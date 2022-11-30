CREATE DATABASE IF NOT EXISTS `gulimall_sms` DEFAULT CHARACTER SET utf8mb4;

USE `gulimall_sms`;

--
-- Table structure for table `sms_coupon`
--
DROP TABLE IF EXISTS `sms_coupon`;
CREATE TABLE `sms_coupon` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`coupon_type` tinyint(1) DEFAULT NULL,
`coupon_img` varchar(2000) DEFAULT NULL,
`coupon_name` varchar(100) DEFAULT NULL,
`num` int(11) DEFAULT NULL,
`amount` decimal(18,4) DEFAULT NULL,
`per_limit` int(11) DEFAULT NULL,
`min_point` decimal(18,4) DEFAULT NULL,
`start_time` datetime DEFAULT NULL,
`end_time` datetime DEFAULT NULL,
`use_type` tinyint(1) DEFAULT NULL,
`note` varchar(200) DEFAULT NULL,
`publish_count` int(11) DEFAULT NULL,
`use_count` int(11) DEFAULT NULL,
`receive_count` int(11) DEFAULT NULL,
`enable_start_time` datetime DEFAULT NULL,
`enable_end_time` datetime DEFAULT NULL,
`code` varchar(64) DEFAULT NULL,
`member_level` tinyint(1) DEFAULT NULL,
`publish` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `sms_coupon_history`
--
DROP TABLE IF EXISTS `sms_coupon_history`;
CREATE TABLE `sms_coupon_history` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`coupon_id` bigint(20) DEFAULT NULL,
`member_id` bigint(20) DEFAULT NULL,
`member_nick_name` varchar(64) DEFAULT NULL,
`get_type` tinyint(1) DEFAULT NULL,
`create_time` datetime DEFAULT NULL,
`use_type` tinyint(1) DEFAULT NULL,
`use_time` datetime DEFAULT NULL,
`order_id` bigint(20) DEFAULT NULL,
`order_sn` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `sms_coupon_spu_category_relation`
--
DROP TABLE IF EXISTS `sms_coupon_spu_category_relation`;
CREATE TABLE `sms_coupon_spu_category_relation` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`coupon_id` bigint(20) DEFAULT NULL,
`category_id` bigint(20) DEFAULT NULL,
`category_name` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `sms_coupon_spu_relation`
--
DROP TABLE IF EXISTS `sms_coupon_spu_relation`;
CREATE TABLE `sms_coupon_spu_relation` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`coupon_id` bigint(20) DEFAULT NULL,
`spu_id` bigint(20) DEFAULT NULL,
`spu_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `sms_home_adv`
--
DROP TABLE IF EXISTS `sms_home_adv`;
CREATE TABLE `sms_home_adv` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`name` varchar(100) DEFAULT NULL,
`pic` varchar(500) DEFAULT NULL,
`start_time` datetime DEFAULT NULL,
`end_time` datetime DEFAULT NULL,
`status` tinyint(1) DEFAULT NULL,
`click_count` int(11) DEFAULT NULL,
`url` varchar(500) DEFAULT NULL,
`note` varchar(500) DEFAULT NULL,
`sort` int(11) DEFAULT NULL,
`publisher_id` bigint(20) DEFAULT NULL,
`auth_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `sms_home_subject`
--
DROP TABLE IF EXISTS `sms_home_subject`;
CREATE TABLE `sms_home_subject` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`name` varchar(200) DEFAULT NULL,
`title` varchar(255) DEFAULT NULL,
`sub_title` varchar(255) DEFAULT NULL,
`status` tinyint(1) DEFAULT NULL,
`url` varchar(500) DEFAULT NULL,
`sort` int(11) DEFAULT NULL,
`img` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `sms_home_subject_spu`
--
DROP TABLE IF EXISTS `sms_home_subject_spu`;
CREATE TABLE `sms_home_subject_spu` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`name` varchar(200) DEFAULT NULL,
`subject_id` bigint(20) DEFAULT NULL,
`spu_id` bigint(20) DEFAULT NULL,
`sort` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- Table structure for table `sms_member_price`
--

DROP TABLE IF EXISTS `sms_member_price`;
CREATE TABLE `sms_member_price` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`sku_id` bigint(20) DEFAULT NULL,
`member_level_id` bigint(20) DEFAULT NULL,
`member_level_name` varchar(100) DEFAULT NULL,
`member_price` decimal(18,4) DEFAULT NULL,
`add_other` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `sms_seckill_promotion`
--
DROP TABLE IF EXISTS `sms_seckill_promotion`;
CREATE TABLE `sms_seckill_promotion` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`title` varchar(255) DEFAULT NULL,
`start_time` datetime DEFAULT NULL,
`end_time` datetime DEFAULT NULL,
`status` tinyint(4) DEFAULT NULL,
`create_time` datetime DEFAULT NULL,
`user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `sms_seckill_session`
--
DROP TABLE IF EXISTS `sms_seckill_session`;
CREATE TABLE `sms_seckill_session` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`name` varchar(200) DEFAULT NULL,
`start_time` datetime DEFAULT NULL,
`end_time` datetime DEFAULT NULL,
`status` tinyint(1) DEFAULT NULL,
`create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `sms_seckill_sku_notice`
--
DROP TABLE IF EXISTS `sms_seckill_sku_notice`;
CREATE TABLE `sms_seckill_sku_notice` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`member_id` bigint(20) DEFAULT NULL,
`sku_id` bigint(20) DEFAULT NULL,
`session_id` bigint(20) DEFAULT NULL,
`subcribe_time` datetime DEFAULT NULL,
`send_time` datetime DEFAULT NULL,
`notice_type` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `sms_seckill_sku_relation`
--
DROP TABLE IF EXISTS `sms_seckill_sku_relation`;
CREATE TABLE `sms_seckill_sku_relation` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`promotion_id` bigint(20) DEFAULT NULL,
`promotion_session_id` bigint(20) DEFAULT NULL,
`sku_id` bigint(20) DEFAULT NULL,
`seckill_price` decimal(10,0) DEFAULT NULL,
`seckill_count` decimal(10,0) DEFAULT NULL,
`seckill_limit` decimal(10,0) DEFAULT NULL,
`seckill_sort` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `sms_sku_full_reduction`
--
DROP TABLE IF EXISTS `sms_sku_full_reduction`;
CREATE TABLE `sms_sku_full_reduction` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`sku_id` bigint(20) DEFAULT NULL,
`full_price` decimal(18,4) DEFAULT NULL,
`reduce_price` decimal(18,4) DEFAULT NULL,
`add_other` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `sms_sku_ladder`
--
DROP TABLE IF EXISTS `sms_sku_ladder`;
CREATE TABLE `sms_sku_ladder` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`sku_id` bigint(20) DEFAULT NULL,
`full_count` int(11) DEFAULT NULL,
`discount` decimal(4,2) DEFAULT NULL,
`price` decimal(18,4) DEFAULT NULL,
`add_other` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `sms_spu_bounds`
--
DROP TABLE IF EXISTS `sms_spu_bounds`;
CREATE TABLE `sms_spu_bounds` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`spu_id` bigint(20) DEFAULT NULL,
`grow_bounds` decimal(18,4) DEFAULT NULL,
`buy_bounds` decimal(18,4) DEFAULT NULL,
`work` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;