package io.dao.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import io.dao.common.valid.AddGroup;
import io.dao.common.valid.ListValue;
import io.dao.common.valid.UpdateGroup;
import io.dao.common.valid.UpdateStatusGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * @author yichwenlim
 * @email yichwenlim@gmail.com
 * @date 2020-10-09 13:08:08
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotNull(message = "修改必须指定品牌id", groups = { UpdateGroup.class })
	@Null(message = "新增不能指定id", groups = { AddGroup.class })
	@TableId
	private Long brandId;
	@NotEmpty(message = "品牌名不能为空", groups = { AddGroup.class, UpdateGroup.class })
	private String name;
//	@NotEmpty(groups = { AddGroup.class })
//	@URL(message = "logo必须是一个合法的URL地址", groups = { AddGroup.class, UpdateGroup.class })
	private String logo;
	private String descript;
	// 自定义注解
    @NotNull(groups = { AddGroup.class, UpdateStatusGroup.class })
	@ListValue(value = {0, 1}, groups = { AddGroup.class, UpdateStatusGroup.class})
	private Integer showStatus;
	@NotEmpty(groups = { AddGroup.class })
	@Pattern(regexp = "^[a-zA-Z]$", message = "检索首字母必须是一个字母", groups = { AddGroup.class, UpdateGroup.class })
	private String firstLetter;
	@NotNull(groups = { AddGroup.class })
	@Min(value = 0, message = "排序必须大于等于0", groups = { AddGroup.class, UpdateGroup.class })
	private Integer sort;

}
