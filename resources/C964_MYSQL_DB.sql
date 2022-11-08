CREATE SCHEMA `letter_recognition` ;
USE letter_recognition;

DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS letters;
DROP TABLE IF EXISTS sessions;
DROP TABLE IF EXISTS settings;
DROP USER 'c964'@'localhost';

CREATE TABLE IF NOT EXISTS `users` (
  `User_ID` int NOT NULL AUTO_INCREMENT,
  `User_Name` varchar(50) DEFAULT NULL,
  `Password` varchar(256) DEFAULT NULL,
  `Create_Date` datetime DEFAULT NULL,
  `Last_Update` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`User_ID`),
  UNIQUE KEY `User_Name_UNIQUE` (`User_Name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `letter_recognition`.`sessions` (
  `session_ID` INT NOT NULL AUTO_INCREMENT,
  `letter_count` INT NULL,
  `letter_correct` INT NULL,
  `letter_incorrect` INT NULL,
  `Create_Date` datetime DEFAULT NULL,
  `Last_Update` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`session_ID`));



CREATE TABLE `letter_recognition`.`letters` (
  `letter_ID` INT NOT NULL AUTO_INCREMENT,
  `session_ID` INT NOT NULL,
  `current_letter` VARCHAR(2) NULL,
  `predicted_letter` VARCHAR(2) NULL,
  `status` VARCHAR(15) NULL,
  `41_accuracy` DOUBLE NULL,
  `42_accuracy` DOUBLE NULL,
  `43_accuracy` DOUBLE NULL,
  `44_accuracy` DOUBLE NULL,
  `45_accuracy` DOUBLE NULL,
  `46_accuracy` DOUBLE NULL,
  `47_accuracy` DOUBLE NULL,
  `48_accuracy` DOUBLE NULL,
  `49_accuracy` DOUBLE NULL,
  `4a_accuracy` DOUBLE NULL,
  `4b_accuracy` DOUBLE NULL,
  `4c_accuracy` DOUBLE NULL,
  `4d_accuracy` DOUBLE NULL,
  `4e_accuracy` DOUBLE NULL,
  `4f_accuracy` DOUBLE NULL,
  `50_accuracy` DOUBLE NULL,
  `51_accuracy` DOUBLE NULL,
  `52_accuracy` DOUBLE NULL,
  `53_accuracy` DOUBLE NULL,
  `54_accuracy` DOUBLE NULL,
  `55_accuracy` DOUBLE NULL,
  `56_accuracy` DOUBLE NULL,
  `57_accuracy` DOUBLE NULL,
  `58_accuracy` DOUBLE NULL,
  `59_accuracy` DOUBLE NULL,
  `5a_accuracy` DOUBLE NULL,
  `61_accuracy` DOUBLE NULL,
  `62_accuracy` DOUBLE NULL,
  `63_accuracy` DOUBLE NULL,
  `64_accuracy` DOUBLE NULL,
  `65_accuracy` DOUBLE NULL,
  `66_accuracy` DOUBLE NULL,
  `67_accuracy` DOUBLE NULL,
  `68_accuracy` DOUBLE NULL,
  `69_accuracy` DOUBLE NULL,
  `6a_accuracy` DOUBLE NULL,
  `6b_accuracy` DOUBLE NULL,
  `6c_accuracy` DOUBLE NULL,
  `6d_accuracy` DOUBLE NULL,
  `6e_accuracy` DOUBLE NULL,
  `6f_accuracy` DOUBLE NULL,
  `70_accuracy` DOUBLE NULL,
  `71_accuracy` DOUBLE NULL,
  `72_accuracy` DOUBLE NULL,
  `73_accuracy` DOUBLE NULL,
  `74_accuracy` DOUBLE NULL,
  `75_accuracy` DOUBLE NULL,
  `76_accuracy` DOUBLE NULL,
  `77_accuracy` DOUBLE NULL,
  `78_accuracy` DOUBLE NULL,
  `79_accuracy` DOUBLE NULL,
  `7a_accuracy` DOUBLE NULL,
  `Create_Date` datetime DEFAULT NULL,
  `Last_Update` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`letter_ID`),
  INDEX `session_ID_idx` (`session_ID` ASC) VISIBLE,
  CONSTRAINT `session_ID`
    FOREIGN KEY (`session_ID`)
    REFERENCES `letter_recognition`.`sessions` (`session_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);



CREATE TABLE `letter_recognition`.`settings` (
   `settings_ID` INT NOT NULL AUTO_INCREMENT,
   `41_enabled` TINYINT NULL,
   `42_enabled` TINYINT NULL,
   `43_enabled` TINYINT NULL,
   `44_enabled` TINYINT NULL,
   `45_enabled` TINYINT NULL,
   `46_enabled` TINYINT NULL,
   `47_enabled` TINYINT NULL,
   `48_enabled` TINYINT NULL,
   `49_enabled` TINYINT NULL,
   `4a_enabled` TINYINT NULL,
   `4b_enabled` TINYINT NULL,
   `4c_enabled` TINYINT NULL,
   `4d_enabled` TINYINT NULL,
   `4e_enabled` TINYINT NULL,
   `4f_enabled` TINYINT NULL,
   `50_enabled` TINYINT NULL,
   `51_enabled` TINYINT NULL,
   `52_enabled` TINYINT NULL,
   `53_enabled` TINYINT NULL,
   `54_enabled` TINYINT NULL,
   `55_enabled` TINYINT NULL,
   `56_enabled` TINYINT NULL,
   `57_enabled` TINYINT NULL,
   `58_enabled` TINYINT NULL,
   `59_enabled` TINYINT NULL,
   `5a_enabled` TINYINT NULL,
   `61_enabled` TINYINT NULL,
   `62_enabled` TINYINT NULL,
   `63_enabled` TINYINT NULL,
   `64_enabled` TINYINT NULL,
   `65_enabled` TINYINT NULL,
   `66_enabled` TINYINT NULL,
   `67_enabled` TINYINT NULL,
   `68_enabled` TINYINT NULL,
   `69_enabled` TINYINT NULL,
   `6a_enabled` TINYINT NULL,
   `6b_enabled` TINYINT NULL,
   `6c_enabled` TINYINT NULL,
   `6d_enabled` TINYINT NULL,
   `6e_enabled` TINYINT NULL,
   `6f_enabled` TINYINT NULL,
   `70_enabled` TINYINT NULL,
   `71_enabled` TINYINT NULL,
   `72_enabled` TINYINT NULL,
   `73_enabled` TINYINT NULL,
   `74_enabled` TINYINT NULL,
   `75_enabled` TINYINT NULL,
   `76_enabled` TINYINT NULL,
   `77_enabled` TINYINT NULL,
   `78_enabled` TINYINT NULL,
   `79_enabled` TINYINT NULL,
   `7a_enabled` TINYINT NULL,
   `Create_Date` datetime DEFAULT NULL,
   `Last_Update` timestamp NULL DEFAULT NULL,
   PRIMARY KEY (`settings_ID`));
   
INSERT INTO `settings` (`settings_ID`, `41_enabled`, `42_enabled`, `43_enabled`, `44_enabled`, `45_enabled`, `46_enabled`, `47_enabled`, `48_enabled`, `49_enabled`, `4a_enabled`, `4b_enabled`, `4c_enabled`, `4d_enabled`, `4e_enabled`, `4f_enabled`, `50_enabled`, `51_enabled`, `52_enabled`, `53_enabled`, `54_enabled`, `55_enabled`, `56_enabled`, `57_enabled`, `58_enabled`, `59_enabled`, `5a_enabled`, `61_enabled`, `62_enabled`, `63_enabled`, `64_enabled`, `65_enabled`, `66_enabled`, `67_enabled`, `68_enabled`, `69_enabled`, `6a_enabled`, `6b_enabled`, `6c_enabled`, `6d_enabled`, `6e_enabled`, `6f_enabled`, `70_enabled`, `71_enabled`, `72_enabled`, `73_enabled`, `74_enabled`, `75_enabled`, `76_enabled`, `77_enabled`, `78_enabled`, `79_enabled`, `7a_enabled`, `Create_Date`, `Last_Update`) VALUES ('1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '2022-09-26 23:03:33', '2022-09-26 23:25:42');
INSERT INTO `letter_recognition`.`users` (`User_ID`, `User_Name`, `Password`, `Create_Date`, `Last_Update`) VALUES ('1', 'Admin', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', NOW(), NOW());
CREATE USER 'c964'@'localhost' IDENTIFIED BY 'password';
GRANT INSERT, UPDATE, SELECT on letter_recognition.* TO 'c964'@'localhost' WITH GRANT OPTION;
