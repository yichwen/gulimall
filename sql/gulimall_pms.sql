CREATE DATABASE IF NOT EXISTS `gulimall_pms` DEFAULT CHARACTER SET utf8mb4;

use `gulimall_pms`;

--
-- Table structure for table `pms_attr`
--
DROP TABLE IF EXISTS `pms_attr`;
CREATE TABLE `pms_attr` (
  `attr_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `attr_name` char(30) DEFAULT NULL,
  `search_type` tinyint(4) DEFAULT NULL,
  `icon` varchar(255) DEFAULT NULL,
  `value_select` char(255) DEFAULT NULL,
  `attr_type` tinyint(4) DEFAULT NULL,
  `value_type` tinyint(4) DEFAULT NULL,
  `enable` bigint(20) DEFAULT NULL,
  `catelog_id` bigint(20) DEFAULT NULL,
  `show_desc` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`attr_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `pms_attr_attrgroup_relation`
--
DROP TABLE IF EXISTS `pms_attr_attrgroup_relation`;
CREATE TABLE `pms_attr_attrgroup_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `attr_id` bigint(20) DEFAULT NULL,
  `attr_group_id` bigint(20) DEFAULT NULL,
  `attr_sort` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `pms_attr_group`
--
DROP TABLE IF EXISTS `pms_attr_group`;
CREATE TABLE `pms_attr_group` (
  `attr_group_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `attr_group_name` char(20) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `descript` varchar(255) DEFAULT NULL,
  `icon` varchar(255) DEFAULT NULL,
  `catelog_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`attr_group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `pms_brand`
--
DROP TABLE IF EXISTS `pms_brand`;
CREATE TABLE `pms_brand` (
  `brand_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` char(50) DEFAULT NULL,
  `logo` varchar(2000) DEFAULT NULL,
  `descript` longtext,
  `show_status` tinyint(4) DEFAULT NULL,
  `first_letter` char(1) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  PRIMARY KEY (`brand_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `pms_category`
--
DROP TABLE IF EXISTS `pms_category`;
CREATE TABLE `pms_category` (
  `cat_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` char(50) DEFAULT NULL,
  `parent_cid` bigint(20) DEFAULT NULL,
  `cat_level` int(11) DEFAULT NULL,
  `show_status` tinyint(4) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `icon` char(255) DEFAULT NULL,
  `product_unit` char(50) DEFAULT NULL,
  `product_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`cat_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1433 DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `pms_category_brand_relation`
--
DROP TABLE IF EXISTS `pms_category_brand_relation`;
CREATE TABLE `pms_category_brand_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `brand_id` bigint(20) DEFAULT NULL,
  `catelog_id` bigint(20) DEFAULT NULL,
  `brand_name` varchar(255) DEFAULT NULL,
  `catelog_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `pms_comment_replay`
--
DROP TABLE IF EXISTS `pms_comment_replay`;
CREATE TABLE `pms_comment_replay` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `comment_id` bigint(20) DEFAULT NULL,
  `reply_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `pms_product_attr_value`
--
DROP TABLE IF EXISTS `pms_product_attr_value`;
CREATE TABLE `pms_product_attr_value` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`spu_id` bigint(20) DEFAULT NULL,
`attr_id` bigint(20) DEFAULT NULL,
`attr_name` varchar(200) DEFAULT NULL,
`attr_value` varchar(200) DEFAULT NULL,
`attr_sort` int(11) DEFAULT NULL,
`quick_show` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `pms_sku_images`
--
DROP TABLE IF EXISTS `pms_sku_images`;
CREATE TABLE `pms_sku_images` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`sku_id` bigint(20) DEFAULT NULL,
`img_url` varchar(255) DEFAULT NULL,
`img_sort` int(11) DEFAULT NULL,
`default_img` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `pms_sku_info`
--
DROP TABLE IF EXISTS `pms_sku_info`;
CREATE TABLE `pms_sku_info` (
`sku_id` bigint(20) NOT NULL AUTO_INCREMENT,
`spu_id` bigint(20) DEFAULT NULL,
`sku_name` varchar(255) DEFAULT NULL,
`sku_desc` varchar(2000) DEFAULT NULL,
`catalog_id` bigint(20) DEFAULT NULL,
`brand_id` bigint(20) DEFAULT NULL,
`sku_default_img` varchar(255) DEFAULT NULL,
`sku_title` varchar(255) DEFAULT NULL,
`sku_subtitle` varchar(2000) DEFAULT NULL,
`price` decimal(18,4) DEFAULT NULL,
`sale_count` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`sku_id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `pms_sku_sale_attr_value`
--
DROP TABLE IF EXISTS `pms_sku_sale_attr_value`;
CREATE TABLE `pms_sku_sale_attr_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sku_id` bigint(20) DEFAULT NULL,
  `attr_id` bigint(20) DEFAULT NULL,
  `attr_name` varchar(200) DEFAULT NULL,
  `attr_value` varchar(200) DEFAULT NULL,
  `attr_sort` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `pms_spu_comment`
--
DROP TABLE IF EXISTS `pms_spu_comment`;
CREATE TABLE `pms_spu_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sku_id` bigint(20) DEFAULT NULL,
  `spu_id` bigint(20) DEFAULT NULL,
  `spu_name` varchar(255) DEFAULT NULL,
  `member_nick_name` varchar(255) DEFAULT NULL,
  `star` tinyint(1) DEFAULT NULL,
  `member_ip` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `show_status` tinyint(1) DEFAULT NULL,
  `spu_attributes` varchar(255) DEFAULT NULL,
  `likes_count` int(11) DEFAULT NULL,
  `reply_count` int(11) DEFAULT NULL,
  `resources` varchar(1000) DEFAULT NULL,
  `content` text,
  `member_icon` varchar(255) DEFAULT NULL,
  `comment_type` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `pms_spu_images`
--
DROP TABLE IF EXISTS `pms_spu_images`;
CREATE TABLE `pms_spu_images` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `spu_id` bigint(20) DEFAULT NULL,
  `img_name` varchar(200) DEFAULT NULL,
  `img_url` varchar(255) DEFAULT NULL,
  `img_sort` int(11) DEFAULT NULL,
  `default_img` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `pms_spu_info`
--
DROP TABLE IF EXISTS `pms_spu_info`;
CREATE TABLE `pms_spu_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `spu_name` varchar(200) DEFAULT NULL,
  `spu_description` varchar(1000) DEFAULT NULL,
  `catalog_id` bigint(20) DEFAULT NULL,
  `brand_id` bigint(20) DEFAULT NULL,
  `weight` decimal(18,4) DEFAULT NULL,
  `publish_status` tinyint(4) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `pms_spu_info_desc`
--
DROP TABLE IF EXISTS `pms_spu_info_desc`;
CREATE TABLE `pms_spu_info_desc` (
  `spu_id` bigint(20) NOT NULL,
  `decript` longtext,
  PRIMARY KEY (`spu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;