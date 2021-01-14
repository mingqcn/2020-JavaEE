-- MySQL dump 10.13  Distrib 8.0.22, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: oomall
-- ------------------------------------------------------
-- Server version	8.0.22

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `auth_new_user`
--

DROP TABLE IF EXISTS `auth_new_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auth_new_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_name` varchar(32) DEFAULT NULL,
  `mobile` varchar(128) DEFAULT NULL,
  `email` varchar(128) DEFAULT NULL,
  `name` varchar(32) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `open_id` varchar(128) DEFAULT NULL,
  `depart_id` bigint DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `password` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `auth_new_user_user_name_uindex` (`user_name`),
  UNIQUE KEY `auth_new_user_mobile_uindex` (`mobile`),
  UNIQUE KEY `auth_new_user_email_uindex` (`email`),
  UNIQUE KEY `auth_new_user_open_id_uindex` (`open_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `auth_privilege`
--

DROP TABLE IF EXISTS `auth_privilege`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auth_privilege` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL,
  `url` varchar(512) DEFAULT NULL,
  `request_type` tinyint DEFAULT NULL,
  `signature` varchar(256) DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `auth_role`
--

DROP TABLE IF EXISTS `auth_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auth_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL COMMENT '角色名称',
  `creator_id` bigint NOT NULL COMMENT '创建者',
  `descr` varchar(500) DEFAULT NULL COMMENT '角色描述',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  `depart_id` bigint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `auth_role_name_uindex` (`name`),
  KEY `auth_role_creator_id_index` (`creator_id`)
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8mb4 COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `auth_role_privilege`
--

DROP TABLE IF EXISTS `auth_role_privilege`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auth_role_privilege` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint DEFAULT NULL,
  `privilege_id` bigint DEFAULT NULL,
  `creator_id` bigint DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `signature` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `auth_role_privilege_role_id_index` (`role_id`),
  KEY `auth_role_privilege_creator_id_index` (`creator_id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `auth_user`
--

DROP TABLE IF EXISTS `auth_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auth_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_name` varchar(32) NOT NULL COMMENT '用户名',
  `password` varchar(128) NOT NULL,
  `mobile` varchar(128) DEFAULT NULL,
  `mobile_verified` tinyint DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `email_verified` tinyint DEFAULT NULL,
  `name` varchar(128) DEFAULT NULL COMMENT 'y用户真实姓名',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `last_login_time` datetime DEFAULT NULL,
  `last_login_ip` varchar(63) DEFAULT NULL,
  `open_id` varchar(128) DEFAULT NULL COMMENT '用于第三方登录',
  `state` tinyint DEFAULT NULL COMMENT '用户状态',
  `depart_id` bigint DEFAULT NULL COMMENT '用户部门，0 平台',
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  `signature` varchar(500) DEFAULT NULL COMMENT '对user_name,password,mobile,email,open_id,depart_id签名',
  `creator_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `auth_user_user_name_uindex` (`user_name`),
  UNIQUE KEY `auth_user_open_id_uindex` (`open_id`),
  UNIQUE KEY `auth_user_mobile_uindex` (`mobile`),
  UNIQUE KEY `auth_user_email_uindex` (`email`),
  KEY `auth_user_depart_id_index` (`depart_id`),
  KEY `auth_user_creator_id_index` (`creator_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17330 DEFAULT CHARSET=utf8mb4 COMMENT='用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `auth_user_proxy`
--

DROP TABLE IF EXISTS `auth_user_proxy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auth_user_proxy` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_a_id` bigint DEFAULT NULL,
  `user_b_id` bigint DEFAULT NULL,
  `begin_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `signature` varchar(256) DEFAULT NULL,
  `valid` tinyint NOT NULL DEFAULT '1',
  `depart_id` bigint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `auth_user_proxy_user_a_id_valid_index` (`user_a_id`,`valid`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `auth_user_role`
--

DROP TABLE IF EXISTS `auth_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auth_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `creator_id` bigint DEFAULT NULL,
  `signature` varchar(256) DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `auth_user_role_role_id_index` (`role_id`),
  KEY `auth_user_role_creator_id_index` (`creator_id`),
  KEY `auth_user_role_user_id_index` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-11-18  9:02:53
