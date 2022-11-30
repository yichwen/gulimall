package io.dao.gulimall.product.entity;

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
 * @date 2020-10-09 13:08:08
 */
@Data
@TableName("pms_attr")
public class AttrEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@TableId
	private Long attrId;
	private String attrName;
	private Integer searchType;
	private String icon;
	private String valueSelect;
	private Integer attrType;
	private Integer valueType;
	private Long enable;
	private Long catelogId;
	private Integer showDesc;

}
