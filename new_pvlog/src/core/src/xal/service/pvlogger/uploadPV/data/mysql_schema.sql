SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

DROP SCHEMA IF EXISTS `new_pvlog` ;
CREATE SCHEMA IF NOT EXISTS `new_pvlog` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
USE `new_pvlog` ;

-- -----------------------------------------------------
-- Table `new_pvlog`.`MACH_SNAPSHOT_PER`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `new_pvlog`.`MACH_SNAPSHOT_PER` ;

CREATE  TABLE IF NOT EXISTS `new_pvlog`.`MACH_SNAPSHOT_PER` (
  `SNAPSHOT_PER` INT NOT NULL ,
  `SNAPSHOT_PER_NAM` VARCHAR(10) NOT NULL ,
  PRIMARY KEY (`SNAPSHOT_PER`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `new_pvlog`.`MACH_SNAPSHOT_RETENT`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `new_pvlog`.`MACH_SNAPSHOT_RETENT` ;

CREATE  TABLE IF NOT EXISTS `new_pvlog`.`MACH_SNAPSHOT_RETENT` (
  `SNAPSHOT_RETENT` INT NOT NULL ,
  `SNAPSHOT_RETENT_NM` VARCHAR(10) NOT NULL ,
  PRIMARY KEY (`SNAPSHOT_RETENT`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `new_pvlog`.`MACH_SNAPSHOT_SVC`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `new_pvlog`.`MACH_SNAPSHOT_SVC` ;

CREATE  TABLE IF NOT EXISTS `new_pvlog`.`MACH_SNAPSHOT_SVC` (
  `SVC_ID` VARCHAR(10) NOT NULL ,
  `SVC_NM` VARCHAR(50) NULL ,
  PRIMARY KEY (`SVC_ID`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `new_pvlog`.`MACH_SNAPSHOT_TYPE`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `new_pvlog`.`MACH_SNAPSHOT_TYPE` ;

CREATE  TABLE IF NOT EXISTS `new_pvlog`.`MACH_SNAPSHOT_TYPE` (
  `SNAPSHOT_TYPE_NM` VARCHAR(120) NOT NULL ,
  `SNAPSHOT_TYPE_DESC` VARCHAR(255) NULL ,
  `SNAPSHOT_PER` INT NULL ,
  `SNAPSHOT_RETENT` INT NULL ,
  `SVC_ID` VARCHAR(10) NULL ,
  INDEX `FK_SNAPSHOT_PER_idx` (`SNAPSHOT_PER` ASC) ,
  INDEX `FK_SNAPSHOT_RETENT_idx` (`SNAPSHOT_RETENT` ASC) ,
  INDEX `FK_SVC_ID_idx` (`SVC_ID` ASC) ,
  PRIMARY KEY (`SNAPSHOT_TYPE_NM`) ,
  CONSTRAINT `FK_SNAPSHOT_PER_a`
    FOREIGN KEY (`SNAPSHOT_PER` )
    REFERENCES `new_pvlog`.`MACH_SNAPSHOT_PER` (`SNAPSHOT_PER` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `FK_SNAPSHOT_RETENT_a`
    FOREIGN KEY (`SNAPSHOT_RETENT` )
    REFERENCES `new_pvlog`.`MACH_SNAPSHOT_RETENT` (`SNAPSHOT_RETENT` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `FK_SVC_ID_a`
    FOREIGN KEY (`SVC_ID` )
    REFERENCES `new_pvlog`.`MACH_SNAPSHOT_SVC` (`SVC_ID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `new_pvlog`.`MACH_SNAPSHOT`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `new_pvlog`.`MACH_SNAPSHOT` ;

CREATE  TABLE IF NOT EXISTS `new_pvlog`.`MACH_SNAPSHOT` (
  `SNAPSHOT_ID` INT NOT NULL AUTO_INCREMENT ,
  `SNAPSHOT_DTE` DATE NULL ,
  `GOLDEN_IND` TINYINT(1) NULL ,
  `SNAPSHOT_TYPE_NM` VARCHAR(120) NULL ,
  `CMNT` VARCHAR(2000) NULL ,
  PRIMARY KEY (`SNAPSHOT_ID`) ,
  INDEX `FK_SNAPSHOT_TYPE_NM_idx` (`SNAPSHOT_TYPE_NM` ASC) ,
  CONSTRAINT `FK_SNAPSHOT_TYPE_NM`
    FOREIGN KEY (`SNAPSHOT_TYPE_NM` )
    REFERENCES `new_pvlog`.`MACH_SNAPSHOT_TYPE` (`SNAPSHOT_TYPE_NM` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `new_pvlog`.`SGNL_REC`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `new_pvlog`.`SGNL_REC` ;

CREATE  TABLE IF NOT EXISTS `new_pvlog`.`SGNL_REC` (
  `SGNL_ID` VARCHAR(45) NOT NULL ,
  `SYSTEM_ID` VARCHAR(45) NULL ,
  `EQUIP_CAT_ID` VARCHAR(45) NULL ,
  `DEVICE_ID` VARCHAR(45) NULL ,
  `RELATIVE_SGNL_ID` VARCHAR(45) NULL ,
  `READBACk_IND` TINYINT(1) NULL ,
  `ACTIVE_IND` TINYINT(1) NULL ,
  PRIMARY KEY (`SGNL_ID`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `new_pvlog`.`MACH_SNAPSHOT_SGNL`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `new_pvlog`.`MACH_SNAPSHOT_SGNL` ;

CREATE  TABLE IF NOT EXISTS `new_pvlog`.`MACH_SNAPSHOT_SGNL` (
  `MACH_SNAPSHOT_SGNL_ID` INT NOT NULL AUTO_INCREMENT ,
  `SNAPSHOT_ID` INT NULL ,
  `SGNL_ID` VARCHAR(45) NULL ,
  `SGNL_TIMESTP` TIMESTAMP NULL ,
  `NANOSECS` INT(9) NULL ,
  `SGNL_VAL` VARCHAR(75) NULL ,
  `SGNL_STAT` DECIMAL(4,0) NULL ,
  `SGNL_SVRTY` DECIMAL(4,0) NULL ,
  PRIMARY KEY (`MACH_SNAPSHOT_SGNL_ID`) ,
  INDEX `FK_SNAPSHOT_ID_idx` (`SNAPSHOT_ID` ASC) ,
  INDEX `FK_SGNL_ID_idx` (`SGNL_ID` ASC) ,
  CONSTRAINT `FK_SNAPSHOT_ID`
    FOREIGN KEY (`SNAPSHOT_ID` )
    REFERENCES `new_pvlog`.`MACH_SNAPSHOT` (`SNAPSHOT_ID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `FK_SGNL_ID`
    FOREIGN KEY (`SGNL_ID` )
    REFERENCES `new_pvlog`.`SGNL_REC` (`SGNL_ID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `new_pvlog`.`MACH_SNAPSHOT_TYPE_SGNL`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `new_pvlog`.`MACH_SNAPSHOT_TYPE_SGNL` ;

CREATE  TABLE IF NOT EXISTS `new_pvlog`.`MACH_SNAPSHOT_TYPE_SGNL` (
  `SNAPSHOT_TYPE_NM` VARCHAR(120) NOT NULL ,
  `SGNL_ID` VARCHAR(45) NOT NULL ,
  INDEX `FK_SGNL_ID_idx` (`SGNL_ID` ASC) ,
  INDEX `SNAPSHOT_TYPE_NM_idx` (`SNAPSHOT_TYPE_NM` ASC) ,
  PRIMARY KEY (`SNAPSHOT_TYPE_NM`, `SGNL_ID`) ,
  CONSTRAINT `FK_SGNL_ID_a`
    FOREIGN KEY (`SGNL_ID` )
    REFERENCES `new_pvlog`.`SGNL_REC` (`SGNL_ID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `FK_SNAPSHOT_TYPE_NM_a`
    FOREIGN KEY (`SNAPSHOT_TYPE_NM` )
    REFERENCES `new_pvlog`.`MACH_SNAPSHOT_TYPE` (`SNAPSHOT_TYPE_NM` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
