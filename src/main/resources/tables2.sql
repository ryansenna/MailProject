USE CS1333612;
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS Email;
DROP TABLE IF EXISTS Messages;
DROP TABLE IF EXISTS EmailAddress;
DROP TABLE IF EXISTS Attachments;
DROP TABLE IF EXISTS EmbeddedAttachments;
DROP TABLE IF EXISTS Folder;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE Email
(
    emailId int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    emailsubject VARCHAR(255),
    emailSentDate TIMESTAMP,
    emailRcvdDate TIMESTAMP,
    senderAddress VARCHAR(255)
);

CREATE TABLE Messages
(
    messageId int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    emailId int,
    emailText TEXT
);

CREATE TABLE EmailAddress
(
    emailAddressId int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    address VARCHAR(255),
    emailType VARCHAR(255),
    emailId int
);

CREATE TABLE Attachments
(
    attachmentId int PRIMARY KEY AUTO_INCREMENT,
    messageFile MEDIUMBLOB,
    messageId int
);

CREATE TABLE Folder
(
    folderId int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    folderName VARCHAR(255),
    emailId int
);