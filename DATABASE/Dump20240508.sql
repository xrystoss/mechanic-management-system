-- MySQL dump 10.13  Distrib 8.0.33, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: mechanic
-- ------------------------------------------------------
-- Server version	8.0.33

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `mobile_phone` varchar(20) NOT NULL,
  `surname` varchar(50) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `customer_type` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`mobile_phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES ('11223344','','asdfghjmn','Citizen'),('12345678','fff','ddd','Company'),('12345699','yyyyy','pooo','Citizen'),('98765432','','dfgh','Citizen'),('99885678','','rrr','Citizen'),('99887765','k','l','Citizen'),('998877664','jjj','Pooo','Citizen');
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `maintenance_log`
--

DROP TABLE IF EXISTS `maintenance_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `maintenance_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `date` varchar(10) DEFAULT NULL,
  `description` text,
  `parts_used` text,
  `licence_plate` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `maintenance_log`
--

LOCK TABLES `maintenance_log` WRITE;
/*!40000 ALTER TABLE `maintenance_log` DISABLE KEYS */;
INSERT INTO `maintenance_log` VALUES (1,'02/01/2002','efrdg','',NULL),(2,'20/11/2002','dfghjkl;','tygjhkiuify',NULL),(3,'12/12/22','def','1,1\n2,7',NULL),(4,'','asdfg','3,1',NULL),(5,'01/05/2024','asdfg','3,9',NULL),(6,'14/05/2024','','1,1\n3,2',NULL),(7,'01/05/2024','dfghj','1,1',NULL),(8,'15/05/2024','asd','8,1',NULL),(9,'07/05/2024','','','kkk8884'),(10,'07/05/2024','123456','','KLZ876'),(11,'08/05/2024','WSEFTGHUKI','','KLZ876');
/*!40000 ALTER TABLE `maintenance_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parts`
--

DROP TABLE IF EXISTS `parts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parts` (
  `code` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `category` varchar(100) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `availability` int DEFAULT NULL,
  `description` text,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parts`
--

LOCK TABLES `parts` WRITE;
/*!40000 ALTER TABLE `parts` DISABLE KEYS */;
INSERT INTO `parts` VALUES (1,'3223','Engine',2.00,0,'3rrrrrr'),(2,'ddd','Brakes',56.00,3,'55'),(3,'ddd','Engine',44.55,12,'lllllllllllllllllllllllllll'),(4,'dddyufhjxfc','Cooling Systems',56.00,4,'3'),(5,'wsdfg','Engine',23.00,1,''),(6,'dsfgthj','Engine',5.00,1,''),(7,'324567','Engine',0.00,1,''),(8,'f','Engine',12.00,0,'');
/*!40000 ALTER TABLE `parts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `payment_details`
--

DROP TABLE IF EXISTS `payment_details`;
/*!50001 DROP VIEW IF EXISTS `payment_details`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `payment_details` AS SELECT 
 1 AS `entry_date`,
 1 AS `total_amount`,
 1 AS `amount_paid`,
 1 AS `licence_plate`,
 1 AS `mobile_phone`,
 1 AS `name`,
 1 AS `surname`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `payment_id` int NOT NULL AUTO_INCREMENT,
  `entry_date` varchar(10) DEFAULT NULL,
  `total_amount` decimal(10,2) DEFAULT NULL,
  `amount_paid` decimal(10,2) DEFAULT NULL,
  `licence_plate` varchar(20) DEFAULT NULL,
  `mobile_phone` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`payment_id`),
  KEY `licence_plate` (`licence_plate`),
  KEY `payments_ibfk_2` (`mobile_phone`),
  CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`licence_plate`) REFERENCES `vehicles` (`licence_plate`),
  CONSTRAINT `payments_ibfk_2` FOREIGN KEY (`mobile_phone`) REFERENCES `customers` (`mobile_phone`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (3,'30/04/2024',2000.00,10.00,'fff','99887765'),(5,'01/05/2024',500.00,10.00,'ppp555','11223344'),(6,'1/1/20',50.00,40.00,'fff','99887765'),(7,'1/1/2019',500.00,250.00,'fff','99887765'),(8,'07/05/2024',677.00,45.00,'kkk8884','998877664');
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vehicles`
--

DROP TABLE IF EXISTS `vehicles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vehicles` (
  `mobile_phone` varchar(20) DEFAULT NULL,
  `licence_plate` varchar(20) DEFAULT NULL,
  `brand` varchar(50) DEFAULT NULL,
  `model` varchar(50) DEFAULT NULL,
  `year_of_manufacture` int DEFAULT NULL,
  `vehicle_mileage` int DEFAULT NULL,
  KEY `idx_licence_plate` (`licence_plate`),
  KEY `vehicles_ibfk_1` (`mobile_phone`),
  CONSTRAINT `vehicles_ibfk_1` FOREIGN KEY (`mobile_phone`) REFERENCES `customers` (`mobile_phone`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vehicles`
--

LOCK TABLES `vehicles` WRITE;
/*!40000 ALTER TABLE `vehicles` DISABLE KEYS */;
INSERT INTO `vehicles` VALUES ('998877664','KLZ876','DEMIO','LLL',2018,55555),('998877664','kkk8884','bmw','mm',123,456),('99887765','fff','fgg','motorcycle',2333,123),('12345678','kkk555','','car',2000,225),('11223344','ppp555','lll','sss',2222,44),('11223344','cderfv','asd','asda',88,123);
/*!40000 ALTER TABLE `vehicles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Final view structure for view `payment_details`
--

/*!50001 DROP VIEW IF EXISTS `payment_details`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `payment_details` AS select `p`.`entry_date` AS `entry_date`,`p`.`total_amount` AS `total_amount`,`p`.`amount_paid` AS `amount_paid`,`p`.`licence_plate` AS `licence_plate`,`p`.`mobile_phone` AS `mobile_phone`,`c`.`name` AS `name`,`c`.`surname` AS `surname` from ((`payments` `p` join `vehicles` `v` on((`p`.`licence_plate` = `v`.`licence_plate`))) join `customers` `c` on((`v`.`mobile_phone` = `c`.`mobile_phone`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-05-08 16:30:51
