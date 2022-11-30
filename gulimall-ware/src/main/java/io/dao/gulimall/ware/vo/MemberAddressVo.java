package io.dao.gulimall.ware.vo;

import lombok.Data;

@Data
public class MemberAddressVo {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long memberId;

	private String name;

	private String phone;

	private String postCode;

	private String province;

	private String city;

	private String region;

	private String detailAddress;

	private String areacode;

	private Integer defaultStatus;

}
