USE CS1333612;
DROP TABLE IF EXISTS Email;
DROP TABLE IF EXISTS Messages;
DROP TABLE IF EXISTS EmailAddress;
DROP TABLE IF EXISTS Attachments;
DROP TABLE IF EXISTS EmbeddedAttachments;
DROP TABLE IF EXISTS Folder;

CREATE TABLE Email
(
	emailId int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    sendAddressId int,
    receiveAddressId int,
    emailsubject VARCHAR(255),
    messageId int,
    folderId int
);

CREATE TABLE Messages
(
	messageId int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    emailId int,
    attachmentId int,
    embeddedId int,
    emailText TEXT
);

CREATE TABLE EmailAddress
(
	emailAddressId int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    address VARCHAR(255),
    emailType VARCHAR(255)
);

CREATE TABLE Attachments
(
	attachmentId int PRIMARY KEY AUTO_INCREMENT,
    messageFile MEDIUMBLOB
);

CREATE TABLE EmbeddedAttachments
(
	embeddedId int PRIMARY KEY AUTO_INCREMENT,
    htmlTxt TEXT,
    messageFile MEDIUMBLOB
);

CREATE TABLE Folder
(
	folderId int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    folderName VARCHAR(255)
);

INSERT INTO Folder(folderName) VALUES ("inbox");
INSERT INTO Folder(folderName) VALUES ("sent");

ALTER TABLE Email ADD CONSTRAINT fk_sendAddress 
FOREIGN KEY (sendAddressId)
REFERENCES EmailAddress(emailAddressId);

ALTER TABLE Email ADD CONSTRAINT fk_receiveAddress
FOREIGN KEY (receiveAddressId)
REFERENCES EmailAddress(emailAddressId);

ALTER TABLE Email ADD CONSTRAINT fk_messageId
FOREIGN KEY (messageId)
REFERENCES Messages(messageId);

ALTER TABLE Email ADD CONSTRAINT fk_folderId
FOREIGN KEY (folderId)
REFERENCES Folder(folderId);

ALTER TABLE Messages ADD CONSTRAINT fk_emailId
FOREIGN KEY (emailId)
REFERENCES Email(emailId);

ALTER TABLE Messages ADD CONSTRAINT fk_attachmentId
FOREIGN KEY (attachmentId)
REFERENCES Attachments(attachmentId);

ALTER TABLE Messages ADD CONSTRAINT fk_embeddedId
FOREIGN KEY (embeddedId)
REFERENCES EmbeddedAttachments(embeddedId);