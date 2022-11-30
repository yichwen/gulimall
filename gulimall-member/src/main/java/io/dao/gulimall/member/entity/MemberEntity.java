package io.dao.gulimall.member.entity;

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
 * @date 2020-10-09 16:09:06
 */
@Data
@TableName("ums_member")
public class MemberEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	@TableId
	private Long id;
	private Long levelId;
	private String username;
	private String password;
	private String nickname;
	private String mobile;
	private String email;
	private String header;
	private Integer gender;
	private Date birth;
	private String city;
	private String job;
	private String sign;
	private Integer sourceType;
	private Integer integration;
	private Integer growth;
	private Integer status;
	private Date createTime;
	private String socialUid;
	private String accessToken;
	private String expiresIn;

}
