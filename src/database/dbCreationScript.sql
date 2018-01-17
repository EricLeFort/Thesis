USE advol;

CREATE TABLE IF NOT EXISTS `Highschool`(
	`highschoolID` INT(10) unsigned NOT NULL,
	`board` INT(10) unsigned,
	`region` INT(10) unsigned,
	`county` INT(10) unsigned,

	PRIMARY KEY (`highschoolID`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `Course`(
	`courseCode` VARCHAR(15) NOT NULL,
	`credits` INT(10) NOT NULL,
	`isRequired` BOOLEAN,

	PRIMARY KEY (`courseCode`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `University`(
	`universityID` INT(10) unsigned NOT NULL,
	`universityName` VARCHAR(255),

	PRIMARY KEY (`universityID`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `Applicant`(
	`applicantID` BIGINT(19) unsigned NOT NULL,
	`applicationYear` INT(10) unsigned NOT NULL,
	`highschoolID` INT(10) unsigned,
	`yearsInSchool` TINYINT(1) unsigned NOT NULL,
	`numCourses` TINYINT(2) unsigned NOT NULL,
	`credits` SMALLINT(4) unsigned NOT NULL,
	`currentAvg` SMALLINT(4) unsigned NOT NULL,
	`totalAvg` SMALLINT(4) unsigned NOT NULL,
	`sex` CHAR(1) NOT NULL DEFAULT 'U',					-- M (male), F (female), U (unknown), N (not applicable)
	`postalCode` CHAR(3),
	`resCountry` INT(10) unsigned,
	`resProvince` INT(10) unsigned,
	`resCounty` INT(10) unsigned,
	`resRegion` INT(10) unsigned,
	`citCountry` INT(10) unsigned,
	`citRegion` INT(10) unsigned,
	`birthYear` TINYINT(3) unsigned,
	`tongue` TINYINT(1) unsigned,
	`status` TINYINT(1) unsigned,

	PRIMARY KEY (`applicantID`),
	FOREIGN KEY (`highschoolID`) REFERENCES Highschool(`highschoolID`) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `Program`(
	`universityID` INT(10) unsigned NOT NULL,
	`programCode` VARCHAR(255) NOT NULL,
	`programGroup` INT(10) unsigned NOT NULL,

	CONSTRAINT pk_program PRIMARY KEY (`universityID`, `programCode`),
	FOREIGN KEY (`universityID`) REFERENCES University(`universityID`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `Confirmation`(
	`applicantID` BIGINT(19) unsigned NOT NULL,
	`universityID` INT(10) unsigned NOT NULL,
	`programCode` VARCHAR(255) NOT NULL,
	`yearLevel` TINYINT(1) unsigned NOT NULL,
	`enrollTerm` CHAR(1) NOT NULL,						-- F (Fall), W (Winter)
	`confirmationChoice` TINYINT(2) unsigned NOT NULL,

	FOREIGN KEY (`applicantID`) REFERENCES Applicant(`applicantID`) ON UPDATE CASCADE ON DELETE CASCADE,
	PRIMARY KEY (`applicantID`),
	CONSTRAINT fk_confirmation_program FOREIGN KEY (`universityID`, `programCode`) REFERENCES Program(`universityID`, `programCode`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `Registration`(
	`applicantID` BIGINT(19) unsigned NOT NULL,
	`universityID` INT(10) unsigned NOT NULL,
	`programCode` VARCHAR(255) NOT NULL,
	`yearLevel` TINYINT(1) unsigned NOT NULL,
	`enrollTerm` CHAR(1) NOT NULL,						-- F (Fall), W (Winter)
	`registrationChoice` TINYINT(2) unsigned NOT NULL,

	PRIMARY KEY (`applicantID`),
	FOREIGN KEY (`applicantID`) REFERENCES Applicant(`applicantID`) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT fk_registration_program FOREIGN KEY (`universityID`, `programCode`) REFERENCES Program(`universityID`, `programCode`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `Grade`(
	`applicantID` BIGINT(19) unsigned NOT NULL,
	`courseCode` VARCHAR(15) NOT NULL,
	`grade` INT(10),

	CONSTRAINT pk_grade PRIMARY KEY (`applicantID`, `courseCode`),
	FOREIGN KEY (`applicantID`) REFERENCES Applicant(`applicantID`) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (`courseCode`) REFERENCES Course(`courseCode`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `Application`(
	`applicantID` BIGINT(19) unsigned NOT NULL,
	`programCode` VARCHAR(255) NOT NULL,
	`universityID` INT(10) unsigned NOT NULL,
	`applicationChoice` TINYINT(2) unsigned NOT NULL,
	`fulltime` BOOLEAN NOT NULL,
	`enrollTerm` CHAR(1) NOT NULL,						-- F (Fall), W (Winter)
	`major` INT(10) unsigned,
	`coop` BOOLEAN NOT NULL,
	`yearLevel` TINYINT(1) unsigned,
	`accepted` BOOLEAN NOT NULL,
	`isConfirmed` BOOLEAN NOT NULL,
	`isRegistered` BOOLEAN NOT NULL,

	CONSTRAINT pk_application PRIMARY KEY (`applicantID`, `programCode`),
	FOREIGN KEY (`applicantID`) REFERENCES Applicant(`applicantID`) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT fk_application_program FOREIGN KEY (`universityID`, `programCode`) REFERENCES Program(`universityID`, `programCode`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `Offer`(
	`applicantID` BIGINT(19) unsigned NOT NULL,
	`programCode` VARCHAR(255) NOT NULL,
	`universityID` INT(10) unsigned NOT NULL,
	`enrollTerm` CHAR(1) NOT NULL,						-- F (Fall), W (Winter)
	`yearLevel` TINYINT(1) unsigned NOT NULL,

	CONSTRAINT pk_Offer PRIMARY KEY (`applicantID`, `programCode`),
	FOREIGN KEY (`applicantID`) REFERENCES Applicant(`applicantID`) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT fk_offer_program FOREIGN KEY (`universityID`, `programCode`) REFERENCES Program(`universityID`, `programCode`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;