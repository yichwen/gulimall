CREATE DATABASE IF NOT EXISTS `gulimall_oms` DEFAULT CHARACTER SET utf8mb4;

use `gulimall_oms`;

--
-- Table structure for table `oms_order`
--

DROP TABLE IF EXISTS `oms_order`;

CREATE TABLE `oms_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) DEFAULT NULL,
  `order_sn` char(64) DEFAULT NULL,
  `coupon_id` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `member_username` varchar(200) DEFAULT NULL,
  `total_amount` decimal(18,4) DEFAULT NULL,
  `pay_amount` decimal(18,4) DEFAULT NULL,
  `freight_amount` decimal(18,4) DEFAULT NULL,
  `promotion_amount` decimal(18,4) DEFAULT NULL,
  `integration_amount` decimal(18,4) DEFAULT NULL,
  `coupon_amount` decimal(18,4) DEFAULT NULL,
  `discount_amount` decimal(18,4) DEFAULT NULL,
  `pay_type` tinyint(4) DEFAULT NULL,
  `source_type` tinyint(4) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `delivery_company` varchar(64) DEFAULT NULL,
  `delivery_sn` varchar(64) DEFAULT NULL,
  `auto_confirm_day` int(11) DEFAULT NULL,
  `integration` int(11) DEFAULT NULL,
  `growth` int(11) DEFAULT NULL,
  `bill_type` tinyint(4) DEFAULT NULL,
  `bill_header` varchar(255) DEFAULT NULL,
  `bill_content` varchar(255) DEFAULT NULL,
  `bill_receiver_phone` varchar(32) DEFAULT NULL,
  `bill_receiver_email` varchar(64) DEFAULT NULL,
  `receiver_name` varchar(100) DEFAULT NULL,
  `receiver_phone` varchar(32) DEFAULT NULL,
  `receiver_post_code` varchar(32) DEFAULT NULL,
  `receiver_province` varchar(32) DEFAULT NULL,
  `receiver_city` varchar(32) DEFAULT NULL,
  `receiver_region` varchar(32) DEFAULT NULL,
  `receiver_detail_address` varchar(200) DEFAULT NULL,
  `note` varchar(500) DEFAULT NULL,
  `confirm_status` tinyint(4) DEFAULT NULL,
  `delete_status` tinyint(4) DEFAULT NULL,
  `use_integration` int(11) DEFAULT NULL,
  `payment_time` datetime DEFAULT NULL,
  `delivery_time` datetime DEFAULT NULL,
  `receive_time` datetime DEFAULT NULL,
  `comment_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `oms_order_item`
--

DROP TABLE IF EXISTS `oms_order_item`;

CREATE TABLE `oms_order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) DEFAULT NULL,
  `order_sn` char(64) DEFAULT NULL,
  `spu_id` bigint(20) DEFAULT NULL,
  `spu_name` varchar(255) DEFAULT NULL,
  `spu_pic` varchar(500) DEFAULT NULL,
  `spu_brand` varchar(200) DEFAULT NULL,
  `category_id` bigint(20) DEFAULT NULL,
  `sku_id` bigint(20) DEFAULT NULL,
  `sku_name` varchar(255) DEFAULT NULL,
  `sku_pic` varchar(500) DEFAULT NULL,
  `sku_price` decimal(18,4) DEFAULT NULL,
  `sku_quantity` int(11) DEFAULT NULL,
  `sku_attrs_vals` varchar(500) DEFAULT NULL,
  `promotion_amount` decimal(18,4) DEFAULT NULL,
  `coupon_amount` decimal(18,4) DEFAULT NULL,
  `integration_amount` decimal(18,4) DEFAULT NULL,
  `real_amount` decimal(18,4) DEFAULT NULL,
  `gift_integration` int(11) DEFAULT NULL,
  `gift_growth` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `oms_order_operate_history`
--

DROP TABLE IF EXISTS `oms_order_operate_history`;

CREATE TABLE `oms_order_operate_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) DEFAULT NULL,
  `operate_man` varchar(100) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `order_status` tinyint(4) DEFAULT NULL,
  `note` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `oms_order_return_apply`
--

DROP TABLE IF EXISTS `oms_order_return_apply`;

CREATE TABLE `oms_order_return_apply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) DEFAULT NULL,
  `sku_id` bigint(20) DEFAULT NULL,
  `order_sn` char(32) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `member_username` varchar(64) DEFAULT NULL,
  `return_amount` decimal(18,4) DEFAULT NULL,
  `return_name` varchar(100) DEFAULT NULL,
  `return_phone` varchar(20) DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL,
  `handle_time` datetime DEFAULT NULL,
  `sku_img` varchar(500) DEFAULT NULL,
  `sku_name` varchar(200) DEFAULT NULL,
  `sku_brand` varchar(200) DEFAULT NULL,
  `sku_attrs_vals` varchar(500) DEFAULT NULL,
  `sku_count` int(11) DEFAULT NULL,
  `sku_price` decimal(18,4) DEFAULT NULL,
  `sku_real_price` decimal(18,4) DEFAULT NULL,
  `reason` varchar(200) DEFAULT NULL,
  `description述` varchar(500) DEFAULT NULL,
  `desc_pics` varchar(2000) DEFAULT NULL,
  `handle_note` varchar(500) DEFAULT NULL,
  `handle_man` varchar(200) DEFAULT NULL,
  `receive_man` varchar(100) DEFAULT NULL,
  `receive_time` datetime DEFAULT NULL,
  `receive_note` varchar(500) DEFAULT NULL,
  `receive_phone` varchar(20) DEFAULT NULL,
  `company_address` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `oms_order_return_reason`
--

DROP TABLE IF EXISTS `oms_order_return_reason`;

CREATE TABLE `oms_order_return_reason` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `oms_order_setting`
--

DROP TABLE IF EXISTS `oms_order_setting`;

CREATE TABLE `oms_order_setting` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `flash_order_overtime` int(11) DEFAULT NULL,
  `normal_order_overtime` int(11) DEFAULT NULL,
  `confirm_overtime` int(11) DEFAULT NULL,
  `finish_overtime` int(11) DEFAULT NULL,
  `comment_overtime` int(11) DEFAULT NULL,
  `member_level` tinyint(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `oms_payment_info`
--

DROP TABLE IF EXISTS `oms_payment_info`;

CREATE TABLE `oms_payment_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_sn` char(64) DEFAULT NULL,
  `order_id` bigint(20) DEFAULT NULL,
  `alipay_trade_no` varchar(50) DEFAULT NULL,
  `total_amount` decimal(18,4) DEFAULT NULL,
  `subject` varchar(200) DEFAULT NULL,
  `payment_status` varchar(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `confirm_time` datetime DEFAULT NULL,
  `callback_content` varchar(4000) DEFAULT NULL,
  `callback_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_order_sn` (`order_sn`),
  UNIQUE KEY `idx_alipay_trade_no` (`alipay_trade_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `oms_refund_info`
--

DROP TABLE IF EXISTS `oms_refund_info`;

CREATE TABLE `oms_refund_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_return_id` bigint(20) DEFAULT NULL,
  `refund` decimal(18,4) DEFAULT NULL,
  `refund_sn` varchar(64) DEFAULT NULL,
  `refund_status` tinyint(1) DEFAULT NULL,
  `refund_channel` tinyint(4) DEFAULT NULL,
  `refund_content` varchar(5000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
