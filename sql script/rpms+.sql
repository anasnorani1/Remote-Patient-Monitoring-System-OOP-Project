-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: healthcare_system
-- ------------------------------------------------------
-- Server version	8.0.42

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
DROP SCHEMA IF EXISTS healthcare_system;
CREATE SCHEMA healthcare_system;
USE healthcare_system;

--
-- Table structure for table `admins`
--

DROP TABLE IF EXISTS `admins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admins` (
  `admin_id` int NOT NULL AUTO_INCREMENT,
  `cnic` varchar(20) NOT NULL,
  `password` varchar(100) NOT NULL,
  PRIMARY KEY (`admin_id`),
  UNIQUE KEY `cnic` (`cnic`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admins`
--

LOCK TABLES `admins` WRITE;
/*!40000 ALTER TABLE `admins` DISABLE KEYS */;
INSERT INTO `admins` VALUES (1,'36601-4673227-1','admin123'),(2,'36601-2425190-3','1234'),(3,'36601-2425190-5','1234');
/*!40000 ALTER TABLE `admins` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `appointment_requests`
--

DROP TABLE IF EXISTS `appointment_requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointment_requests` (
  `id` int NOT NULL AUTO_INCREMENT,
  `patient_id` int DEFAULT NULL,
  `doctor_id` int DEFAULT NULL,
  `requested_date` date DEFAULT NULL,
  `requested_time` time DEFAULT NULL,
  `status` varchar(20) DEFAULT 'Requested',
  PRIMARY KEY (`id`),
  KEY `patient_id` (`patient_id`),
  KEY `doctor_id` (`doctor_id`),
  CONSTRAINT `appointment_requests_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`),
  CONSTRAINT `appointment_requests_ibfk_2` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`doctor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointment_requests`
--

LOCK TABLES `appointment_requests` WRITE;
/*!40000 ALTER TABLE `appointment_requests` DISABLE KEYS */;
INSERT INTO `appointment_requests` VALUES (12,21,12,'2025-05-10','10:00:00','Approved'),(13,21,12,'2025-05-10','11:00:00','Approved'),(14,21,12,'2025-05-13','10:00:00','Rejected'),(15,21,12,'2025-05-12','11:00:00','Rejected'),(16,22,13,'2025-05-15','14:00:00','Approved'),(17,21,13,'2025-05-14','15:00:00','Approved');
/*!40000 ALTER TABLE `appointment_requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `appointments`
--

DROP TABLE IF EXISTS `appointments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `patient_id` int DEFAULT NULL,
  `doctor_id` int DEFAULT NULL,
  `date` date DEFAULT NULL,
  `time` varchar(20) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `feedback` text,
  PRIMARY KEY (`id`),
  KEY `fk_doctor` (`doctor_id`),
  KEY `fk_patient` (`patient_id`),
  CONSTRAINT `appointments_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`),
  CONSTRAINT `appointments_ibfk_2` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`doctor_id`),
  CONSTRAINT `fk_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`doctor_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_patient` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointments`
--

LOCK TABLES `appointments` WRITE;
/*!40000 ALTER TABLE `appointments` DISABLE KEYS */;
INSERT INTO `appointments` VALUES (10,21,12,'2025-05-10','10:00:00','Scheduled',NULL),(11,21,12,'2025-05-10','11:00:00','Scheduled',NULL),(12,22,13,'2025-05-15','14:00:00','Scheduled',NULL),(13,21,13,'2025-05-14','15:00:00','Scheduled',NULL);
/*!40000 ALTER TABLE `appointments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctors`
--

DROP TABLE IF EXISTS `doctors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctors` (
  `doctor_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone_number` varchar(20) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `specialization` varchar(100) DEFAULT NULL,
  `medical_degree` varchar(100) DEFAULT NULL,
  `years_of_experience` int DEFAULT NULL,
  `department` varchar(100) DEFAULT NULL,
  `consultation_fee` decimal(10,2) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `cnic` varchar(15) DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `username` varchar(100) DEFAULT NULL,
  `password_hash` text,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`doctor_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctors`
--

LOCK TABLES `doctors` WRITE;
/*!40000 ALTER TABLE `doctors` DISABLE KEYS */;
INSERT INTO `doctors` VALUES (12,'Anas Norani','anorani.bsds24seecs@seecs.edu.pk','03208068311','NUST','Neurology','MBBS',2,'Neurology Department',1000.00,'2006-07-07','36601-4673227-1','Male','36601-4673227-1',NULL,'0320'),(13,'Muneeb Ahmad Saqib','malikanasbrw@gmail.com','03208068311','Nust','Cardiology','MBBS',2,'Cardiology Department',1000.00,'2003-06-10','36601-1555244-2','Male','36601-1555244-2',NULL,'1234');
/*!40000 ALTER TABLE `doctors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `emergency_alerts`
--

DROP TABLE IF EXISTS `emergency_alerts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `emergency_alerts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `patient_id` int DEFAULT NULL,
  `doctor_id` int DEFAULT NULL,
  `trigger_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `patient_name` varchar(100) DEFAULT NULL,
  `doctor_name` varchar(100) DEFAULT NULL,
  `doctor_email` varchar(100) DEFAULT NULL,
  `status` varchar(20) DEFAULT 'Triggered',
  `details` text,
  `alert_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `patient_id` (`patient_id`),
  KEY `doctor_id` (`doctor_id`),
  CONSTRAINT `emergency_alerts_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`),
  CONSTRAINT `emergency_alerts_ibfk_2` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`doctor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `emergency_alerts`
--

LOCK TABLES `emergency_alerts` WRITE;
/*!40000 ALTER TABLE `emergency_alerts` DISABLE KEYS */;
INSERT INTO `emergency_alerts` VALUES (8,21,12,'2025-05-09 08:04:52','Norani Anas','Anas Norani','anorani.bsds24seecs@seecs.edu.pk','Triggered','The Patient is in trouble	\n','2025-05-09 08:04:52'),(9,21,12,'2025-05-09 08:11:27','Norani Anas','Anas Norani','anorani.bsds24seecs@seecs.edu.pk','Triggered','the patient is not feeling well','2025-05-09 08:11:27'),(10,21,12,'2025-05-11 07:36:10','Norani Anas','Anas Norani','anorani.bsds24seecs@seecs.edu.pk','Triggered','the patient needs attention','2025-05-11 07:36:10'),(11,22,13,'2025-05-14 08:22:50','Anas Norani','Muneeb Ahmad Saqib','malikanasbrw@gmail.com','Triggered','The patient is in trouble','2025-05-14 08:22:50'),(12,21,12,'2025-05-14 08:37:46','Norani Anas','Muneeb Ahmad Saqib','malikanasbrw@gmail.com','Triggered','The patient feels suffocated','2025-05-14 08:37:46');
/*!40000 ALTER TABLE `emergency_alerts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feedback`
--

DROP TABLE IF EXISTS `feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedback` (
  `feedback_id` int NOT NULL AUTO_INCREMENT,
  `doctor_id` int DEFAULT NULL,
  `patient_id` int DEFAULT NULL,
  `feedback` text,
  `feedback_datetime` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`feedback_id`),
  KEY `fk_feedback_patient` (`patient_id`),
  KEY `feedback_ibfk_1` (`doctor_id`),
  CONSTRAINT `feedback_ibfk_1` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`doctor_id`) ON DELETE CASCADE,
  CONSTRAINT `feedback_ibfk_2` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`),
  CONSTRAINT `fk_feedback_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`doctor_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_feedback_patient` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedback`
--

LOCK TABLES `feedback` WRITE;
/*!40000 ALTER TABLE `feedback` DISABLE KEYS */;
INSERT INTO `feedback` VALUES (3,12,21,'take your medicine on time','2025-05-09 13:13:14'),(4,13,22,'check bp daily','2025-05-14 13:24:52'),(5,13,22,'take medications on time','2025-05-14 13:42:02');
/*!40000 ALTER TABLE `feedback` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `messages`
--

DROP TABLE IF EXISTS `messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `messages` (
  `id` int NOT NULL AUTO_INCREMENT,
  `patient_id` int DEFAULT NULL,
  `doctor_email` varchar(100) DEFAULT NULL,
  `message` text,
  `timestamp` datetime DEFAULT NULL,
  `video_call_request` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `messages`
--

LOCK TABLES `messages` WRITE;
/*!40000 ALTER TABLE `messages` DISABLE KEYS */;
INSERT INTO `messages` VALUES (1,9,'anorani.bsds24seecs@seecs.edu.pk','kya haal chal hy','2025-05-02 13:48:10',0),(2,9,'anorani.bsds24seecs@seecs.edu.pk','visdhafbhka fbgjsda','2025-05-02 14:13:05',1),(3,9,'anorani.bsds24seecs@seecs.edu.pk','dajvbsdhkzx z','2025-05-02 14:15:48',1),(4,9,'anorani.bsds24seecs@seecs.edu.pk','alhumdulilah','2025-05-02 14:18:15',0),(5,21,'anorani.bsds24seecs@seecs.edu.pk','asking for video appointment','2025-05-09 13:11:57',1),(6,22,'malikanasbrw@gmail.com','The PAtient is in trouble','2025-05-14 13:23:19',1),(7,21,'malikanasbrw@gmail.com','The patient does not feels well','2025-05-14 13:38:38',1);
/*!40000 ALTER TABLE `messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patients`
--

DROP TABLE IF EXISTS `patients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `patients` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `age` int DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `illness` varchar(255) DEFAULT NULL,
  `contact` varchar(20) DEFAULT NULL,
  `address` text,
  `date_of_birth` date DEFAULT NULL,
  `cnic` varchar(15) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password_hash` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patients`
--

LOCK TABLES `patients` WRITE;
/*!40000 ALTER TABLE `patients` DISABLE KEYS */;
INSERT INTO `patients` VALUES (21,'Norani Anas',18,'Male','Stomatch Issue','03208068311','NUST SEECS','2006-07-07','36601-4673227-1','0320','36601-4673227-1','$2a$10$t8HWoro0uKVq/2TY3qaOnOJB9rKJpB7nKcR4Rjc/IHdFwM50Rh4H.','malikanasbrw@gmail.com'),(22,'Anas Norani',18,'Male','headache','03208068311','NUST','2006-07-07','36601-1505302-1','1234','36601-1505302-1','$2a$10$wkC5aIW3ag9f8AtAHeKwGO069jKYegW/PBWX52dgMkOcjYGCZLLoy','anorani.bsds24seecs@seecs.edu.pk');
/*!40000 ALTER TABLE `patients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prescriptions`
--

DROP TABLE IF EXISTS `prescriptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prescriptions` (
  `prescription_id` int NOT NULL AUTO_INCREMENT,
  `doctor_id` int DEFAULT NULL,
  `patient_id` int DEFAULT NULL,
  `medicine_name` varchar(255) DEFAULT NULL,
  `when_to_take` varchar(50) DEFAULT NULL,
  `before_after` varchar(50) DEFAULT NULL,
  `quantity` varchar(255) DEFAULT NULL,
  `times_per_day` varchar(10) DEFAULT NULL,
  `note` text,
  `appointment_id` int DEFAULT NULL,
  `prescription_date` date DEFAULT NULL,
  PRIMARY KEY (`prescription_id`),
  KEY `patient_id` (`patient_id`),
  KEY `fk_appointment` (`appointment_id`),
  KEY `prescriptions_ibfk_1` (`doctor_id`),
  CONSTRAINT `fk_appointment` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`) ON DELETE CASCADE,
  CONSTRAINT `prescriptions_ibfk_1` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`doctor_id`) ON DELETE CASCADE,
  CONSTRAINT `prescriptions_ibfk_2` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`),
  CONSTRAINT `prescriptions_ibfk_3` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prescriptions`
--

LOCK TABLES `prescriptions` WRITE;
/*!40000 ALTER TABLE `prescriptions` DISABLE KEYS */;
INSERT INTO `prescriptions` VALUES (18,12,21,'nuberol ','Breakfast, Dinner','After','1 tablet','2','take with milk',11,NULL),(19,12,21,'brufen syp','Breakfast, Dinner','After','1 table spoon','2','',11,NULL),(20,13,22,'brufen syp','Breakfast, Dinner','After','1 table spoon','2','',12,NULL),(21,13,21,'klarisef tab 250 mg','Dinner','After','1 tab','1','take with milk',13,NULL);
/*!40000 ALTER TABLE `prescriptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_logs`
--

DROP TABLE IF EXISTS `system_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_logs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `action` varchar(255) DEFAULT NULL,
  `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `details` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=204 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_logs`
--

LOCK TABLES `system_logs` WRITE;
/*!40000 ALTER TABLE `system_logs` DISABLE KEYS */;
INSERT INTO `system_logs` VALUES (1,'Add New Patient','2025-04-28 13:56:25',NULL),(2,'Add New Patient','2025-04-28 14:08:14',NULL),(3,'Add New Patient','2025-04-28 14:10:43','Patient ID: 123, Name: John Doe'),(4,'Add New Doctor','2025-04-28 14:11:48',NULL),(5,'Add New Doctor','2025-04-28 14:16:24',NULL),(6,'Add New Patient','2025-04-28 14:16:32',NULL),(7,'Add New Patient','2025-04-28 14:16:56','Name: Anas, CNIC: 12345-1234567-1, Illness: a'),(8,'View Doctors','2025-04-28 14:25:54',NULL),(9,'View Doctors','2025-04-28 14:25:56',NULL),(10,'View Doctors','2025-04-28 14:25:57',NULL),(11,'View Doctors','2025-04-28 14:26:38',NULL),(12,'View Doctors','2025-04-28 14:31:05',NULL),(13,'View Doctors','2025-04-28 14:38:45',NULL),(14,'Add New Patient','2025-04-28 14:38:51',NULL),(15,'Add New Doctor','2025-04-28 14:38:59',NULL),(16,'View Doctors','2025-04-28 14:40:07',NULL),(17,'View Doctors','2025-04-28 14:53:03',NULL),(18,'View Doctors','2025-04-28 14:53:06',NULL),(19,'View Doctors','2025-04-28 14:53:07',NULL),(20,'View Doctors','2025-04-28 14:53:08',NULL),(21,'View Doctors','2025-04-28 14:53:09',NULL),(22,'Add New Doctor','2025-04-28 15:06:03',NULL),(23,'Add New Patient','2025-04-28 15:06:06',NULL),(24,'View Patients','2025-04-28 15:06:29',NULL),(25,'Add New Doctor','2025-04-28 18:05:34',NULL),(26,'Add New Doctor','2025-04-28 18:17:21',NULL),(27,'Add New Doctor','2025-04-28 18:21:14',NULL),(28,'Add New Doctor','2025-04-28 18:22:42',NULL),(29,'Add New Doctor','2025-04-28 18:24:24',NULL),(30,'Add New Doctor','2025-04-28 18:29:12',NULL),(31,'Add New Doctor','2025-04-28 18:29:14',NULL),(32,'Add New Doctor','2025-04-28 18:29:15',NULL),(33,'Add New Doctor','2025-04-28 18:29:15',NULL),(34,'Add New Doctor','2025-04-28 18:29:16',NULL),(35,'Add New Doctor','2025-04-28 20:15:15',NULL),(36,'Add New Doctor','2025-04-28 20:15:50',NULL),(37,'Add New Doctor','2025-04-28 20:26:20',NULL),(38,'Add New Patient','2025-04-28 20:26:27',NULL),(39,'Add New Doctor','2025-04-28 20:26:33',NULL),(40,'Add New Patient','2025-04-28 20:26:37',NULL),(41,'Add New Patient','2025-04-28 20:27:07',NULL),(42,'Add New Doctor','2025-04-28 20:27:11',NULL),(43,'Add New Patient','2025-04-28 20:27:14',NULL),(44,'View Doctors','2025-04-28 20:37:33',NULL),(45,'View Doctors','2025-04-28 20:46:17',NULL),(46,'View Doctors','2025-04-28 20:46:20',NULL),(47,'View Doctors','2025-04-28 20:46:21',NULL),(48,'View Doctors','2025-04-28 20:46:22',NULL),(49,'View Doctors','2025-04-28 20:46:23',NULL),(50,'View Doctors','2025-04-28 20:46:23',NULL),(51,'View Doctors','2025-04-28 20:46:23',NULL),(52,'View Doctors','2025-04-28 20:46:24',NULL),(53,'View Doctors','2025-04-28 20:46:24',NULL),(54,'View Doctors','2025-04-28 20:46:25',NULL),(55,'View Doctors','2025-04-28 20:46:26',NULL),(56,'View Doctors','2025-04-28 20:46:26',NULL),(57,'View Doctors','2025-04-28 20:46:26',NULL),(58,'Add New Patient','2025-04-30 06:40:54',NULL),(59,'Add New Doctor','2025-04-30 06:40:58',NULL),(60,'Add New Doctor','2025-04-30 06:43:42',NULL),(61,'Add New Doctor','2025-04-30 06:52:06',NULL),(62,'View Patients','2025-04-30 08:11:34',NULL),(63,'View Patients','2025-04-30 08:11:37',NULL),(64,'View Patients','2025-04-30 08:13:55',NULL),(65,'View Patients','2025-04-30 08:15:44',NULL),(66,'View Patients','2025-04-30 08:20:54',NULL),(67,'View Patients','2025-04-30 08:21:12',NULL),(68,'View Patients','2025-04-30 08:23:34',NULL),(69,'View Patients','2025-04-30 08:26:12',NULL),(70,'View Patients','2025-04-30 08:27:33',NULL),(71,'View Patients','2025-04-30 08:33:57',NULL),(72,'View Patients','2025-04-30 08:34:19',NULL),(73,'View Patients','2025-04-30 08:38:56',NULL),(74,'View Patients','2025-04-30 08:39:10',NULL),(75,'View Patients','2025-04-30 08:41:39',NULL),(76,'View Patients','2025-04-30 08:43:22',NULL),(77,'View Patients','2025-04-30 08:47:25',NULL),(78,'Add New Patient','2025-04-30 08:47:47',NULL),(79,'Add New Patient','2025-04-30 08:48:33','Name: Anas Norani, CNIC: 36601-4673227-1, Illness: head ache'),(80,'View Patients','2025-04-30 08:48:36',NULL),(81,'Add New Patient','2025-04-30 08:49:06',NULL),(82,'Add New Patient','2025-04-30 08:49:43','Name: Hanan Majeed, CNIC: 36601-4673227-1, Illness: headache'),(83,'View Patients','2025-04-30 08:49:46',NULL),(84,'View Patients','2025-04-30 08:49:52',NULL),(85,'Add New Patient','2025-04-30 08:52:17',NULL),(86,'Add New Patient','2025-04-30 08:54:17',NULL),(87,'View Patients','2025-04-30 08:54:50',NULL),(88,'Add New Patient','2025-04-30 08:54:53',NULL),(89,'Add New Patient','2025-04-30 08:55:20','Name: anas, CNIC: 12345-6789012-3, Illness: cvvx'),(90,'View Patients','2025-04-30 08:55:30',NULL),(91,'View Patients','2025-04-30 08:58:22',NULL),(92,'Send Email Notification','2025-04-30 09:18:20',NULL),(93,'Send Email Notification','2025-04-30 09:21:57',NULL),(94,'View Patients','2025-04-30 09:23:38',NULL),(95,'Send Email Notification','2025-04-30 09:23:43',NULL),(96,'Send Email Notification','2025-04-30 09:26:02',NULL),(97,'Send Email Notification','2025-04-30 09:27:08',NULL),(98,'Send Email Notification','2025-04-30 12:36:17',NULL),(99,'Send Email Notification','2025-04-30 12:41:58',NULL),(100,'Send Email Notification','2025-04-30 12:44:23',NULL),(101,'Send Email Notification','2025-04-30 12:46:48',NULL),(102,'Add New Patient','2025-04-30 13:31:41',NULL),(103,'Add New Patient','2025-04-30 13:32:32','Name: Anas Norani, CNIC: 36601-4673227-2, Illness: headache'),(104,'View Patients','2025-04-30 14:39:30',NULL),(105,'Add New Doctor','2025-04-30 14:39:35',NULL),(106,'Add New Doctor','2025-04-30 14:45:25',NULL),(107,'Add New Doctor','2025-04-30 14:51:50',NULL),(108,'Add New Doctor','2025-04-30 14:56:55',NULL),(109,'Add New Doctor','2025-04-30 15:01:08',NULL),(110,'Add New Doctor','2025-04-30 15:03:58',NULL),(111,'Add New Patient','2025-05-01 07:38:06',NULL),(112,'Add New Doctor','2025-05-01 07:43:55',NULL),(113,'Add New Patient','2025-05-01 07:43:59',NULL),(114,'View Patients','2025-05-01 07:44:10',NULL),(115,'Add New Patient','2025-05-01 07:44:25',NULL),(116,'Add New Patient','2025-05-01 07:46:39',NULL),(117,'Add New Patient','2025-05-01 07:48:53',NULL),(118,'Add New Patient','2025-05-01 07:49:48','Name: Anas Norani, CNIC: 36601-4673227-1, Illness: ENT'),(119,'View Patients','2025-05-01 07:53:59',NULL),(120,'Add New Patient','2025-05-01 07:54:23',NULL),(121,'Add New Patient','2025-05-01 07:55:28','Name: Anas Norani, CNIC: 36601-4673227-1, Illness: ENT'),(122,'Add New Patient','2025-05-01 08:00:58',NULL),(123,'Add New Patient','2025-05-01 08:03:04','Name: Shiv Mankani, CNIC: 45501-5742488-7, Illness: ENT'),(124,'View Patients','2025-05-01 08:04:12',NULL),(125,'Add New Patient','2025-05-01 08:04:43','Name: Shiv Mankani, CNIC: 45501-5742488-7, Illness: ENT'),(126,'View Patients','2025-05-01 08:05:37',NULL),(127,'Add New Patient','2025-05-01 08:05:51',NULL),(128,'Add New Patient','2025-05-01 08:06:49','Name: Shiv Mankanu, CNIC: 45501-5742488-7, Illness: Head Ache'),(129,'Add New Doctor','2025-05-01 08:22:02',NULL),(130,'Add New Patient','2025-05-01 08:33:08',NULL),(131,'Add New Patient','2025-05-01 08:34:39','Name: Anas Rajpoot, CNIC: 45402-3983008-7, Illness: none'),(132,'Add New Patient','2025-05-01 08:38:50',NULL),(133,'Add New Patient','2025-05-01 08:39:50','Name: Muneeb, CNIC: 36601-2425163-2, Illness: no'),(134,'Manage Appointments','2025-05-01 16:42:49',NULL),(135,'Add New Doctor','2025-05-01 16:48:07',NULL),(136,'Manage Appointments','2025-05-01 16:48:11',NULL),(137,'Manage Appointments','2025-05-01 19:50:46',NULL),(138,'Manage Appointments','2025-05-01 19:50:49',NULL),(139,'Manage Appointments','2025-05-01 19:51:13',NULL),(140,'Manage Appointments','2025-05-01 19:52:38',NULL),(141,'Manage Appointments','2025-05-01 20:23:54',NULL),(142,'Manage Appointments','2025-05-01 20:25:25',NULL),(143,'Manage Appointments','2025-05-01 20:28:51',NULL),(144,'Manage Appointments','2025-05-01 20:33:45',NULL),(145,'Manage Appointments','2025-05-01 20:36:09',NULL),(146,'Manage Appointments','2025-05-01 20:40:02',NULL),(147,'Manage Appointments','2025-05-01 20:44:32',NULL),(148,'Manage Appointments','2025-05-01 20:47:22',NULL),(149,'Add New Patient','2025-05-01 20:47:30',NULL),(150,'Manage Appointments','2025-05-01 20:48:29',NULL),(151,'Add New Patient','2025-05-02 04:12:25',NULL),(152,'Add New Patient','2025-05-02 04:14:03','Name: Sikandar Hussanin, CNIC: 31201-7687780-1, Illness: Head Ache'),(153,'View Patients','2025-05-02 04:16:38',NULL),(154,'Manage Appointments','2025-05-02 04:17:29',NULL),(155,'Add New Patient','2025-05-02 04:26:14',NULL),(156,'Add New Patient','2025-05-02 04:27:36','Name: ie, CNIC: 31104-1293456-7, Illness: ksjz'),(157,'View Patients','2025-05-02 04:27:51',NULL),(158,'Manage Appointments','2025-05-02 04:28:21',NULL),(159,'View Patients','2025-05-02 05:19:48',NULL),(160,'Add New Patient','2025-05-02 05:22:11',NULL),(161,'Add New Patient','2025-05-02 05:23:27','Name: Zayna Qasim, CNIC: 37405-8926258-2, Illness: Head Ache'),(162,'Send Email Notification','2025-05-02 05:25:23',NULL),(163,'View Patients','2025-05-02 05:26:26',NULL),(164,'Manage Appointments','2025-05-02 06:17:28',NULL),(165,'Manage Appointments','2025-05-02 06:18:35',NULL),(166,'Manage Appointments','2025-05-02 06:20:34',NULL),(167,'Add New Patient','2025-05-02 09:26:53',NULL),(168,'Add New Patient','2025-05-02 09:27:59','Name: Tayyab Mumtaz, CNIC: 36601-8192936-7, Illness: headache'),(169,'Manage Appointments','2025-05-02 09:31:36',NULL),(170,'Manage Appointments','2025-05-02 16:26:46',NULL),(171,'Manage Appointments','2025-05-02 16:40:52',NULL),(172,'Manage Appointments','2025-05-02 16:43:50',NULL),(173,'View Patients','2025-05-02 16:50:11',NULL),(174,'View Patients','2025-05-02 16:56:57',NULL),(175,'View Patients','2025-05-02 16:58:52',NULL),(176,'View Patients','2025-05-02 17:00:01',NULL),(177,'View Patients','2025-05-02 17:01:01',NULL),(178,'View Patients','2025-05-02 17:03:44',NULL),(179,'View Patients','2025-05-02 17:07:20',NULL),(180,'View Patients','2025-05-02 17:07:37',NULL),(181,'View Patients','2025-05-02 17:09:56',NULL),(182,'Manage Appointments','2025-05-02 17:10:09',NULL),(183,'Manage Appointments','2025-05-02 17:10:51',NULL),(184,'Manage Appointments','2025-05-02 17:21:13',NULL),(185,'Manage Appointments','2025-05-02 17:25:54',NULL),(186,'Manage Appointments','2025-05-02 17:28:59',NULL),(187,'Add New Doctor','2025-05-02 18:00:12',NULL),(188,'Manage Appointments','2025-05-02 18:03:23',NULL),(189,'Manage Appointments','2025-05-03 14:26:17',NULL),(190,'Manage Appointments','2025-05-03 14:36:24',NULL),(191,'Manage Appointments','2025-05-03 14:39:06',NULL),(192,'Manage Appointments','2025-05-03 14:39:53',NULL),(193,'Manage Appointments','2025-05-03 14:40:18',NULL),(194,'Manage Appointments','2025-05-03 14:48:37',NULL),(195,'Manage Appointments','2025-05-03 14:49:22',NULL),(196,'Manage Appointments','2025-05-03 14:51:14',NULL),(197,'Manage Appointments','2025-05-03 14:52:35',NULL),(198,'Manage Appointments','2025-05-03 14:53:21',NULL),(199,'Manage Appointments','2025-05-03 14:57:04',NULL),(200,'Manage Appointments','2025-05-03 14:57:44',NULL),(201,'Add New Doctor','2025-05-06 06:34:36',NULL),(202,'Add New Patient','2025-05-09 08:00:54','Name: Norani Anas, CNIC: 36601-4673227-1, Illness: Stomatch Issue'),(203,'Add New Patient','2025-05-14 08:19:32','Name: Anas Norani, CNIC: 36601-1505302-1, Illness: headache');
/*!40000 ALTER TABLE `system_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vital_signs`
--

DROP TABLE IF EXISTS `vital_signs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vital_signs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `patient_id` int NOT NULL,
  `heart_rate` int NOT NULL,
  `oxygen_level` int NOT NULL,
  `temperature` double NOT NULL,
  `blood_pressure` varchar(10) NOT NULL,
  `doctor_id` int NOT NULL,
  `timestamp` datetime DEFAULT CURRENT_TIMESTAMP,
  `patient_name` varchar(255) DEFAULT NULL,
  `doctor_name` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `doctor_id` (`doctor_id`),
  CONSTRAINT `vital_signs_ibfk_1` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`doctor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vital_signs`
--

LOCK TABLES `vital_signs` WRITE;
/*!40000 ALTER TABLE `vital_signs` DISABLE KEYS */;
INSERT INTO `vital_signs` VALUES (9,21,72,98,98.6,'120/80',12,'2025-05-09 13:04:26','Norani Anas','Anas Norani (ID: 12)','2025-05-09 08:04:26'),(10,21,75,99,98.5,'120/80',12,'2025-05-09 13:04:26','Norani Anas','Anas Norani (ID: 12)','2025-05-09 08:04:26'),(11,21,10,30,80,'150/100',12,'2025-05-09 13:04:26','Norani Anas','Anas Norani (ID: 12)','2025-05-09 08:04:26'),(12,22,72,98,98.6,'120/80',13,'2025-05-14 13:22:32','Anas Norani','Muneeb Ahmad Saqib (ID: 13)','2025-05-14 08:22:32'),(13,22,75,99,98.5,'120/80',13,'2025-05-14 13:22:32','Anas Norani','Muneeb Ahmad Saqib (ID: 13)','2025-05-14 08:22:32'),(14,21,72,98,98.6,'120/80',12,'2025-05-14 13:35:21','Norani Anas','Anas Norani (ID: 12)','2025-05-14 08:35:21'),(15,21,75,99,98.5,'120/80',12,'2025-05-14 13:35:21','Norani Anas','Anas Norani (ID: 12)','2025-05-14 08:35:21'),(16,21,72,98,98.6,'120/80',12,'2025-05-14 13:36:04','Norani Anas','Anas Norani (ID: 12)','2025-05-14 08:36:04'),(17,21,75,99,98.5,'120/80',12,'2025-05-14 13:36:04','Norani Anas','Anas Norani (ID: 12)','2025-05-14 08:36:04'),(18,21,10,20,30,'150/100',12,'2025-05-14 13:36:04','Norani Anas','Anas Norani (ID: 12)','2025-05-14 08:36:04'),(19,21,72,98,98.6,'120/80',12,'2025-05-15 09:51:42','Norani Anas','Anas Norani (ID: 12)','2025-05-15 04:51:42'),(20,21,75,99,98.5,'120/80',12,'2025-05-15 09:51:42','Norani Anas','Anas Norani (ID: 12)','2025-05-15 04:51:42'),(21,21,10,20,30,'150/100',12,'2025-05-15 09:51:42','Norani Anas','Anas Norani (ID: 12)','2025-05-15 04:51:42'),(22,21,11,11,11,'170/150',12,'2025-05-15 09:51:42','Norani Anas','Anas Norani (ID: 12)','2025-05-15 04:51:42');
/*!40000 ALTER TABLE `vital_signs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'healthcare_system'
--

--
-- Dumping routines for database 'healthcare_system'
--
/*!50003 DROP PROCEDURE IF EXISTS `PromoteEmployee` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `PromoteEmployee`(
    IN emp_id INT,
    IN new_position VARCHAR(100),
    IN new_salary DECIMAL(10,2)
)
BEGIN
    UPDATE employees
    SET position = new_position,
        salary = new_salary
    WHERE id = emp_id;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-18 11:54:08
