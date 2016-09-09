/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rsenna.business;

import java.io.File;
import java.util.List;

import javax.mail.Flags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.mail.Email;
import jodd.mail.EmailAddress;
import jodd.mail.EmailAttachment;
import jodd.mail.EmailFilter;
import jodd.mail.EmailMessage;
import jodd.mail.ImapSslServer;
import jodd.mail.MailAddress;
import jodd.mail.ReceiveMailSession;
import jodd.mail.ReceivedEmail;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;
import jodd.mail.SmtpSslServer;

/**
 *
 * @author 1333612
 */
public class EmailSendModule {

    // Real programmers use logging
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    // These must be updated to your email accounts
    private String smtpServerName;
    private String emailAddress;
    private String emailPwd;

    // You will need a folder with this name or change it to another
    // existing folder
    private final String attachmentFolder = "C:\\Temp\\Attach\\";

    public EmailSendModule(String sendEmailAddress, String sendEmailPassword) {
        setSmtpServerName(sendEmailAddress);
        setEmailAddress(sendEmailAddress);
        setEmailPwd(sendEmailPassword);
    }

    /// ************** LIST OF GETTERS ***************
    public String getSmtpServerName() {

        if (smtpServerName.isEmpty()) {
            throw new IllegalArgumentException("The smtp server name is not specified.");
        }
        return smtpServerName;
    }

    public String getEmailSend() {
        if (emailAddress.isEmpty()) {
            throw new IllegalArgumentException("The sender email is not specified.");
        }
        return emailAddress;
    }

    public String getEmailSendPwd() {

        if (emailPwd.isEmpty()) {
            throw new IllegalArgumentException("The sender email psword is not specified.");
        }
        return emailPwd;
    }
    
    
    private void setSmtpServerName(String sendEmailAddress) {
        this.smtpServerName = "smtp."
                + sendEmailAddress.substring(sendEmailAddress.indexOf("@"));
    }

    private void setEmailAddress(String sendEmailAddress) {
        this.emailAddress = sendEmailAddress;
    }

    private void setEmailPwd(String sendEmailPassword) {
        this.emailPwd = sendEmailPassword;
    }


    /**
     * Standard send routine using Jodd. Jodd knows about GMail so no need to
     * include port information
     */
    public void send(String subject, String content, String emailReceive) {

        //VALIDATE BOTH PARAMS HERE.
        // Create am SMTP server object
        SmtpServer<SmtpSslServer> smtpServer = SmtpSslServer
                .create(smtpServerName)
                .authenticateWith(emailAddress, emailPwd);

        // Display Java Mail debug conversation with the server
        smtpServer.debug(true);

        // Using the fluent style of coding create a plain text message
        Email email = Email.create().from(emailAddress)
                .to(emailReceive)
                .subject(subject).addText(content);

        // A session is the object responsible for communicating with the server
        SendMailSession session = smtpServer.createSession();

        // Like a file we open the session, send the message and close the
        // session
        session.open();
        session.sendMail(email);
        session.close();
    }

    /**
     * Here we create an email message that contains html, embedded image, and
     * an attachment
     *
     * @throws Exception In case we don't find the file to attach/embed
     */
    public void sendWithEmbedded(String subject, String content,
            String fileEmbeddedPath, String emailReceive) throws Exception {

        // Create am SMTP server object
        SmtpServer<SmtpSslServer> smtpServer = SmtpSslServer
                .create(smtpServerName).authenticateWith(emailAddress,
                emailPwd);

        smtpServer.debug(true);

        // Using the fluent style of coding create an html message
        Email email = Email.create().from(emailAddress)
                .to(emailReceive)
                .subject(subject)
                .addText(content)
                .embed(EmailAttachment.attachment()
                        .bytes(new File(fileEmbeddedPath)));

        // A session is the object responsible for communicating with the server
        SendMailSession session = smtpServer.createSession();

        // Like a file we open the session, send the mesage and close the
        // session
        session.open();
        session.sendMail(email);
        session.close();

    }

    public void sendWithAttachment(String subject, String content,
            String emailReceive, String fileAttachmentPath) throws Exception {

        // Create am SMTP server object
        SmtpServer<SmtpSslServer> smtpServer = SmtpSslServer
                .create(smtpServerName).authenticateWith(emailAddress,
                emailPwd);

        smtpServer.debug(true);

        // Using the fluent style of coding create an html message
        Email email = Email.create().from(emailAddress)
                .to(emailReceive)
                .subject(subject)
                .addText(content)
                .attach(EmailAttachment.attachment().file(fileAttachmentPath));

        // A session is the object responsible for communicating with the server
        SendMailSession session = smtpServer.createSession();

        // Like a file we open the session, send the mesage and close the
        // session
        session.open();
        session.sendMail(email);
        session.close();

    }

    /**
     * Here we create an email message that contains html, embedded image, and
     * an attachment
     *
     * @throws Exception In case we don't find the file to attach/embed
     */
    public void sendWithEmbeddedAndAttachment(String subject,
            String emailReceive, String content,
            String fileAttachmentPath, String fileEmbededPath) throws Exception {

        // Create am SMTP server object
        SmtpServer<SmtpSslServer> smtpServer = SmtpSslServer
                .create(smtpServerName).authenticateWith(emailAddress,
                emailPwd);

        smtpServer.debug(true);

        // Using the fluent style of coding create an html message
        Email email = Email.create().from(emailAddress)
                .to(emailReceive)
                .subject(subject)
                .addText(content)
                .embed(EmailAttachment.attachment()
                        .bytes(new File(fileEmbededPath)))
                .attach(EmailAttachment.attachment().file(fileAttachmentPath));

        // A session is the object responsible for communicating with the server
        SendMailSession session = smtpServer.createSession();

        // Like a file we open the session, send the mesage and close the
        // session
        session.open();
        session.sendMail(email);
        session.close();
    }

}
