package io.dao.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author yichwenlim
 * @email yichwenlim@gmail.com
 * @date 2020-10-09 13:08:08
 */
@Data
@TableName("pms_attr_group")
public class AttrGroupEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId
	private Long attrGroupId;
	private String attrGroupName;
	private Integer sort;
	private String descript;
	private String icon;
	private Long catelogId;
	@TableField(exist = false)
	private Long[] catelogIds;
}
