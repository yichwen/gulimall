package io.dao.gulimall.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author yichwenlim
 * @email yichwenlim@gmail.com
 * @date 2020-10-09 15:35:31
 */
@Data
@TableName("sms_coupon_history")
public class CouponHistoryEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long id;
	/**
	 * 
	 */
	private Long couponId;
	/**
	 * 
	 */
	private Long memberId;
	/**
	 * 
	 */
	private String memberNickName;
	/**
	 * 
	 */
	private Integer getType;
	/**
	 * 
	 */
	private Date createTime;
	/**
	 * 
	 */
	private Integer useType;
	/**
	 * 
	 */
	private Date useTime;
	/**
	 * 
	 */
	private Long orderId;
	/**
	 * 
	 */
	private Long orderSn;

}
