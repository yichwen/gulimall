package io.dao.gulimall.order.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author yichwenlim
 * @email yichwenlim@gmail.com
 * @date 2020-10-09 16:11:24
 */
@Data
@TableName("oms_order_return_apply")
public class OrderReturnApplyEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long id;
	/**
	 * 
	 */
	private Long orderId;
	/**
	 * 
	 */
	private Long skuId;
	/**
	 * 
	 */
	private String orderSn;
	/**
	 * 
	 */
	private Date createTime;
	/**
	 * 
	 */
	private String memberUsername;
	/**
	 * 
	 */
	private BigDecimal returnAmount;
	/**
	 * 
	 */
	private String returnName;
	/**
	 * 
	 */
	private String returnPhone;
	/**
	 * 
	 */
	private Integer status;
	/**
	 * 
	 */
	private Date handleTime;
	/**
	 * 
	 */
	private String skuImg;
	/**
	 * 
	 */
	private String skuName;
	/**
	 * 
	 */
	private String skuBrand;
	/**
	 * 
	 */
	private String skuAttrsVals;
	/**
	 * 
	 */
	private Integer skuCount;
	/**
	 * 
	 */
	private BigDecimal skuPrice;
	/**
	 * 
	 */
	private BigDecimal skuRealPrice;
	/**
	 * 
	 */
	private String reason;
	/**
	 * 
	 */
	private String description述;
	/**
	 * 
	 */
	private String descPics;
	/**
	 * 
	 */
	private String handleNote;
	/**
	 * 
	 */
	private String handleMan;
	/**
	 * 
	 */
	private String receiveMan;
	/**
	 * 
	 */
	private Date receiveTime;
	/**
	 * 
	 */
	private String receiveNote;
	/**
	 * 
	 */
	private String receivePhone;
	/**
	 * 
	 */
	private String companyAddress;

}
