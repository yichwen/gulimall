CREATE DATABASE IF NOT EXISTS `gulimall_ums` DEFAULT CHARACTER SET utf8mb4;

USE `gulimall_ums`;

--
-- Table structure for table `ums_growth_change_history`
--
DROP TABLE IF EXISTS `ums_growth_change_history`;
CREATE TABLE `ums_growth_change_history` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`member_id` bigint(20) DEFAULT NULL,
`create_time` datetime DEFAULT NULL,
`change_count` int(11) DEFAULT NULL,
`note` varchar(0) DEFAULT NULL,
`source_type` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `ums_integration_change_history`
--
DROP TABLE IF EXISTS `ums_integration_change_history`;
CREATE TABLE `ums_integration_change_history` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`member_id` bigint(20) DEFAULT NULL,
`create_time` datetime DEFAULT NULL,
`change_count` int(11) DEFAULT NULL,
`note` varchar(255) DEFAULT NULL,
`source_tyoe` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `ums_member`
--
DROP TABLE IF EXISTS `ums_member`;
CREATE TABLE `ums_member` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`level_id` bigint(20) DEFAULT NULL,
`username` char(64) DEFAULT NULL,
`password` varchar(64) DEFAULT NULL,
`nickname` varchar(64) DEFAULT NULL,
`mobile` varchar(20) DEFAULT NULL,
`email` varchar(64) DEFAULT NULL,
`header` varchar(500) DEFAULT NULL,
`gender` tinyint(4) DEFAULT NULL,
`birth` date DEFAULT NULL,
`city` varchar(500) DEFAULT NULL,
`job` varchar(255) DEFAULT NULL,
`sign` varchar(255) DEFAULT NULL,
`source_type` tinyint(4) DEFAULT NULL,
`integration` int(11) DEFAULT NULL,
`growth` int(11) DEFAULT NULL,
`status` tinyint(4) DEFAULT NULL,
`create_time` datetime DEFAULT NULL,
`social_uid` varchar(255) DEFAULT NULL,
`access_token` varchar(255) DEFAULT NULL,
`expires_in` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `ums_member_collect_spu`
--
DROP TABLE IF EXISTS `ums_member_collect_spu`;
CREATE TABLE `ums_member_collect_spu` (
`id` bigint(20) NOT NULL,
`member_id` bigint(20) DEFAULT NULL,
`spu_id` bigint(20) DEFAULT NULL,
`spu_name` varchar(500) DEFAULT NULL,
`spu_img` varchar(500) DEFAULT NULL,
`create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `ums_member_collect_subject`
--
DROP TABLE IF EXISTS `ums_member_collect_subject`;
CREATE TABLE `ums_member_collect_subject` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`subject_id` bigint(20) DEFAULT NULL,
`subject_name` varchar(255) DEFAULT NULL,
`subject_img` varchar(500) DEFAULT NULL,
`subject_urll` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `ums_member_level`
--
DROP TABLE IF EXISTS `ums_member_level`;
CREATE TABLE `ums_member_level` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`name` varchar(100) DEFAULT NULL,
`growth_point` int(11) DEFAULT NULL,
`default_status` tinyint(4) DEFAULT NULL,
`free_freight_point` decimal(18,4) DEFAULT NULL,
`comment_growth_point` int(11) DEFAULT NULL,
`priviledge_free_freight` tinyint(4) DEFAULT NULL,
`priviledge_member_price` tinyint(4) DEFAULT NULL,
`priviledge_birthday` tinyint(4) DEFAULT NULL,
`note` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `ums_member_login_log`
--
DROP TABLE IF EXISTS `ums_member_login_log`;
CREATE TABLE `ums_member_login_log` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`member_id` bigint(20) DEFAULT NULL,
`create_time` datetime DEFAULT NULL,
`ip` varchar(64) DEFAULT NULL,
`city` varchar(64) DEFAULT NULL,
`login_type` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `ums_member_receive_address`
--
DROP TABLE IF EXISTS `ums_member_receive_address`;
CREATE TABLE `ums_member_receive_address` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`member_id` bigint(20) DEFAULT NULL,
`name` varchar(255) DEFAULT NULL,
`phone` varchar(64) DEFAULT NULL,
`post_code` varchar(64) DEFAULT NULL,
`province` varchar(100) DEFAULT NULL,
`city` varchar(100) DEFAULT NULL,
`region` varchar(100) DEFAULT NULL,
`detail_address` varchar(255) DEFAULT NULL,
`areacode` varchar(15) DEFAULT NULL,
`default_status` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `ums_member_statistics_info`
--
DROP TABLE IF EXISTS `ums_member_statistics_info`;
CREATE TABLE `ums_member_statistics_info` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`member_id` bigint(20) DEFAULT NULL,
`consume_amount` decimal(18,4) DEFAULT NULL,
`coupon_amount` decimal(18,4) DEFAULT NULL,
`order_count` int(11) DEFAULT NULL,
`coupon_count` int(11) DEFAULT NULL,
`comment_count` int(11) DEFAULT NULL,
`return_order_count` int(11) DEFAULT NULL,
`login_count` int(11) DEFAULT NULL,
`attend_count` int(11) DEFAULT NULL,
`fans_count` int(11) DEFAULT NULL,
`collect_product_count` int(11) DEFAULT NULL,
`collect_subject_count` int(11) DEFAULT NULL,
`collect_comment_count` int(11) DEFAULT NULL,
`invite_friend_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;