package io.dao.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author yichwenlim
 * @email yichwenlim@gmail.com
 * @date 2020-10-09 13:08:08
 */
@Data
@TableName("pms_category")
public class CategoryEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@TableId
	private Long catId;
	private String name;
	private Long parentCid;
	private Integer catLevel;
	// 逻辑删除字段，这里的配置将会覆盖全局的配置
	@TableLogic(value = "1", delval = "0")
	private Integer showStatus;
	private Integer sort;
	private String icon;
	private String productUnit;
	private Integer productCount;

	// 自行增加的属性
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@TableField(exist = false)
	private List<CategoryEntity> children;

}
