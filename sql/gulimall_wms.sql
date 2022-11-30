CREATE DATABASE IF NOT EXISTS `gulimall_wms` DEFAULT CHARACTER SET utf8mb4;

USE `gulimall_wms`;

--
-- Table structure for table `wms_purchase`
--
DROP TABLE IF EXISTS `wms_purchase`;
CREATE TABLE `wms_purchase` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`assignee_id` bigint(20) DEFAULT NULL,
`assignee_name` varchar(255) DEFAULT NULL,
`phone` char(13) DEFAULT NULL,
`priority` int(4) DEFAULT NULL,
`status` int(4) DEFAULT NULL,
`ware_id` bigint(20) DEFAULT NULL,
`amount` decimal(18,4) DEFAULT NULL,
`create_time` datetime DEFAULT NULL,
`update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `wms_purchase_detail`
--
DROP TABLE IF EXISTS `wms_purchase_detail`;
CREATE TABLE `wms_purchase_detail` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`purchase_id` bigint(20) DEFAULT NULL,
`sku_id` bigint(20) DEFAULT NULL,
`sku_num` int(11) DEFAULT NULL,
`sku_price` decimal(18,4) DEFAULT NULL,
`ware_id` bigint(20) DEFAULT NULL,
`status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `wms_ware_info`
--
DROP TABLE IF EXISTS `wms_ware_info`;
CREATE TABLE `wms_ware_info` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`name` varchar(255) DEFAULT NULL,
`address` varchar(255) DEFAULT NULL,
`areacode` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `wms_ware_order_task`
--
DROP TABLE IF EXISTS `wms_ware_order_task`;
CREATE TABLE `wms_ware_order_task` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`order_id` bigint(20) DEFAULT NULL,
`order_sn` varchar(255) DEFAULT NULL,
`consignee` varchar(100) DEFAULT NULL,
`consignee_tel` char(15) DEFAULT NULL,
`delivery_address` varchar(500) DEFAULT NULL,
`order_comment` varchar(200) DEFAULT NULL,
`payment_way` tinyint(1) DEFAULT NULL,
`task_status` tinyint(2) DEFAULT NULL,
`order_body` varchar(255) DEFAULT NULL,
`tracking_no` char(30) DEFAULT NULL,
`create_time` datetime DEFAULT NULL,
`ware_id` bigint(20) DEFAULT NULL,
`task_comment` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `wms_ware_order_task_detail`
--
DROP TABLE IF EXISTS `wms_ware_order_task_detail`;
CREATE TABLE `wms_ware_order_task_detail` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`sku_id` bigint(20) DEFAULT NULL,
`sku_name` varchar(255) DEFAULT NULL,
`sku_num` int(11) DEFAULT NULL,
`task_id` bigint(20) DEFAULT NULL,
`ware_id` bigint(20) DEFAULT NULL,
`lock_status` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `wms_ware_sku`
--
DROP TABLE IF EXISTS `wms_ware_sku`;
CREATE TABLE `wms_ware_sku` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`sku_id` bigint(20) DEFAULT NULL,
`ware_id` bigint(20) DEFAULT NULL,
`stock` int(11) DEFAULT NULL,
`sku_name` varchar(200) DEFAULT NULL,
`stock_locked` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4;