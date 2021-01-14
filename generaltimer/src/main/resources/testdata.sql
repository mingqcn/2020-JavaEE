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
-- Table structure for table `timer_task`
--

LOCK TABLES `timer_task` WRITE;
/*!40000 ALTER TABLE `advertisement` DISABLE KEYS */;
INSERT INTO `oomall`.`timer_task` (`id`, `begin_time`, `topic`, `tag`, `bean_name`, `method_name`, `return_type_name`, `period`, `gmt_create`, `gmt_modified`, `sender_name`) VALUES (1, '2020/12/06 09:00:00', 'test', 'test', 'cn.edu.xmu.timer.util.TaskFactory', 'test', 'void', 0, '2020/12/03 10:00', NULL, 'task1');
INSERT INTO `oomall`.`timer_task` (`id`, `begin_time`, `topic`, `tag`, `bean_name`, `method_name`, `return_type_name`, `period`, `gmt_create`, `gmt_modified`, `sender_name`) VALUES (2, '2020/12/06 10:00:00', 'test', 'test', 'cn.edu.xmu.timer.util.TaskFactory', 'test', 'void', 8, '2020/12/03 10:00', NULL, 'task2');
INSERT INTO `oomall`.`timer_task` (`id`, `begin_time`, `topic`, `tag`, `bean_name`, `method_name`, `return_type_name`, `period`, `gmt_create`, `gmt_modified`, `sender_name`) VALUES (3, '2020/07/05 10:00:00', 'test', 'test', 'cn.edu.xmu.timer.util.TaskFactory', 'test', 'void', 7, '2020/12/03 10:00', NULL, 'task3');
INSERT INTO `oomall`.`timer_task` (`id`, `begin_time`, `topic`, `tag`, `bean_name`, `method_name`, `return_type_name`, `period`, `gmt_create`, `gmt_modified`, `sender_name`) VALUES (4, '2020/11/05 15:00:00', 'test', 'test', 'cn.edu.xmu.timer.util.TaskFactory', 'test', 'void', 7, '2020/12/03 10:00', NULL, 'task4');
INSERT INTO `oomall`.`timer_task` (`id`, `begin_time`, `topic`, `tag`, `bean_name`, `method_name`, `return_type_name`, `period`, `gmt_create`, `gmt_modified`, `sender_name`) VALUES (5, '2020/05/06 04:00:00', 'test', 'test', 'cn.edu.xmu.timer.util.TaskFactory', 'test', 'void', 9, '2020/12/03 10:00', NULL, 'task5');
INSERT INTO `oomall`.`timer_task` (`id`, `begin_time`, `topic`, `tag`, `bean_name`, `method_name`, `return_type_name`, `period`, `gmt_create`, `gmt_modified`, `sender_name`) VALUES (6, '2020/05/09 04:00:00', 'test', 'test', 'cn.edu.xmu.timer.util.TaskFactory', 'test', 'void', 9, '2020/12/03 10:00', NULL, 'task6');
INSERT INTO `oomall`.`timer_task` (`id`, `begin_time`, `topic`, `tag`, `bean_name`, `method_name`, `return_type_name`, `period`, `gmt_create`, `gmt_modified`, `sender_name`) VALUES (7, '2020/05/06 15:00:00', 'test', 'test', 'cn.edu.xmu.timer.util.TaskFactory', 'test', 'void', 9, '2020/12/03 10:00', NULL, 'task7');
INSERT INTO `oomall`.`timer_task` (`id`, `begin_time`, `topic`, `tag`, `bean_name`, `method_name`, `return_type_name`, `period`, `gmt_create`, `gmt_modified`, `sender_name`) VALUES (8, '2019/12/06 04:00:00', 'test', 'test', 'cn.edu.xmu.timer.util.TaskFactory', 'test', 'void', 10, '2020/12/03 10:00', NULL, 'task8');
INSERT INTO `oomall`.`timer_task` (`id`, `begin_time`, `topic`, `tag`, `bean_name`, `method_name`, `return_type_name`, `period`, `gmt_create`, `gmt_modified`, `sender_name`) VALUES (9, '2019/05/06 04:00:00', 'test', 'test', 'cn.edu.xmu.timer.util.TaskFactory', 'test', 'void', 10, '2020/12/03 10:00', NULL, 'task9');
INSERT INTO `oomall`.`timer_task` (`id`, `begin_time`, `topic`, `tag`, `bean_name`, `method_name`, `return_type_name`, `period`, `gmt_create`, `gmt_modified`, `sender_name`) VALUES (10, '2019/12/03 04:00:00', 'test', 'test', 'cn.edu.xmu.timer.util.TaskFactory', 'test', 'void', 10, '2020/12/03 10:00', NULL, 'task10');
INSERT INTO `oomall`.`timer_task` (`id`, `begin_time`, `topic`, `tag`, `bean_name`, `method_name`, `return_type_name`, `period`, `gmt_create`, `gmt_modified`, `sender_name`) VALUES (11, '2019/12/06 16:00:00', 'test', 'test', 'cn.edu.xmu.timer.util.TaskFactory', 'test', 'void', 10, '2020/12/03 10:00', NULL, 'task11');

/*!40000 ALTER TABLE `advertisement` ENABLE KEYS */;
UNLOCK TABLES;

/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-12-07 13:27:16
