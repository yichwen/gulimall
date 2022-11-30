package io.dao.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import io.dao.common.to.SkuEsModel;
import io.dao.gulimall.search.config.GulimallElasticSearchConfig;
import io.dao.gulimall.search.constant.EsConstant;
import io.dao.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        // 建立索引
        // 建立映射关系

        // 批量保存数据
        BulkRequest request = new BulkRequest();
        for (SkuEsModel skuEsModel : skuEsModels) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(skuEsModel.getSkuId().toString());
            String s = JSON.toJSONString(skuEsModel);
            indexRequest.source(s, XContentType.JSON);
            request.add(indexRequest);
        }
        BulkResponse bulk = client.bulk(request, GulimallElasticSearchConfig.COMMON_OPTIONS);

        // TODO: 如果批量错误
        boolean b = bulk.hasFailures();
        List<String> collect = Arrays.stream(bulk.getItems())
                .map(BulkItemResponse::getId)
                .collect(Collectors.toList());
        log.info("商品上架完成：{}", collect);

        return !b;
    }

}
