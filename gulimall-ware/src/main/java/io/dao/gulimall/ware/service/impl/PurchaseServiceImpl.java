package io.dao.gulimall.ware.service.impl;

import io.dao.common.constant.WareConstant;
import io.dao.gulimall.ware.entity.PurchaseDetailEntity;
import io.dao.gulimall.ware.service.PurchaseDetailService;
import io.dao.gulimall.ware.service.WareSkuService;
import io.dao.gulimall.ware.vo.MergeVo;
import io.dao.gulimall.ware.vo.PurchaseDoneItemVo;
import io.dao.gulimall.ware.vo.PurchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dao.common.utils.PageUtils;
import io.dao.common.utils.Query;

import io.dao.gulimall.ware.dao.PurchaseDao;
import io.dao.gulimall.ware.entity.PurchaseEntity;
import io.dao.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceivePurchase(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
                        .eq("status", 0).or()
                        .eq("status", 1)
        );
        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }

        // TODO: 确认采购单状态是0或1才可以合并

        List<Long> items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;

        List<PurchaseDetailEntity> purchaseDetailEntities = items.stream().map(i -> {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            detailEntity.setId(i);
            detailEntity.setPurchaseId(finalPurchaseId);
            detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return detailEntity;
        }).collect(Collectors.toList());
        this.purchaseDetailService.updateBatchById(purchaseDetailEntities);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);

    }

    @Override
    public void received(List<Long> ids) {
        // 确认当前采购单是新建或已分配
        List<PurchaseEntity> purchaseEntities = ids.stream().map(this::getById)
                .filter(e -> e.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                        e.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode())
                .map(e -> {
                    e.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
                    e.setUpdateTime(new Date());
                    return e;
                }).collect(Collectors.toList());
        // 改变采购单的状态
        this.updateBatchById(purchaseEntities);
        // 改变采购项的状态
        purchaseEntities.stream().map(e -> purchaseDetailService.listDetailByPurchaseId(e.getId())).map(list -> list.stream().map(i -> {
            PurchaseDetailEntity e1 = new PurchaseDetailEntity();
            e1.setId(i.getId());
            e1.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
            return e1;
        }).collect(Collectors.toList())).forEach(updateList -> purchaseDetailService.updateBatchById(updateList));

    }

    @Transactional
    @Override
    public void done(PurchaseDoneVo vo) {
        // 改变采购项的状态
        boolean flag = true;
        List<PurchaseDetailEntity> updateList = new ArrayList<>();
        List<PurchaseDoneItemVo> items = vo.getItems();
        for (PurchaseDoneItemVo item: items) {
            PurchaseDetailEntity entity = new PurchaseDetailEntity();
            if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()) {
                flag = false;
                entity.setStatus(item.getStatus());
            } else {
                entity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                // 将成功采购的进行入库
                PurchaseDetailEntity byId = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(byId.getSkuId(), byId.getWareId(), byId.getSkuNum());
            }
            entity.setId(item.getItemId());
            updateList.add(entity);
        }
        purchaseDetailService.updateBatchById(updateList);

        // 改变采购单的状态
        Long id = vo.getId();
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setStatus(flag ?
                WareConstant.PurchaseStatusEnum.FINISH.getCode() :
                WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        this.updateById(purchaseEntity);

    }

}