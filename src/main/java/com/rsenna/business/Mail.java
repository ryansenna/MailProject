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
public class Mail {

    // Real programmers use logging
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    // These must be updated to your email accounts
    private final String smtpServerName; // must take when the user types in the login page.
    private final String imapServerName; // "" 
    private final String emailSend; // must take in the user login.
    private final String emailSendPwd; // must take in the user login.
    private final String emailReceive; // must take in the "to" field
    private final String emailReceivePwd;// test purposes.
    private final String emailCC1 = "";// list of emails
    private final String emailCC2 = "";// list of emails.

    private String subject = "";// subject of the message sent.
    private String content = "";// content of the message sent.

    // You will need a folder with this name or change it to another
    // existing folder
    private final String attachmentFolder = "C:\\Temp\\Attach\\";

    public Mail(String smtpServerName, String imapServerName, String emailSend,
            String emailSendPwd, String emailReceive, String emailReceivePwd) {
        
        this.smtpServerName = smtpServerName;
        this.imapServerName = imapServerName;
        this.emailSend = emailSend;
        this.emailSendPwd = emailSendPwd;
        this.emailReceive = emailReceive;
        this.emailReceivePwd = emailReceivePwd;

    }

    public void setContent(String content) {
        this.content = content;
    }

    /// ************** LIST OF GETTERS ***************
    public String getSmtpServerName() {
        if (smtpServerName.isEmpty()) {
            throw new IllegalArgumentException("The smtp server name is not specified.");
        }
        return smtpServerName;
    }

    public String getImapServerName() {
        if (imapServerName.isEmpty()) {
            throw new IllegalArgumentException("The imap server name is not specified.");
        }
        return imapServerName;
    }

    public String getEmailSend() {
        if (emailSend.isEmpty()) {
            throw new IllegalArgumentException("The sender email is not specified.");
        }
        return emailSend;
    }

    public String getEmailSendPwd() {

        if (emailSendPwd.isEmpty()) {
            throw new IllegalArgumentException("The sender email psword is not specified.");
        }
        return emailSendPwd;
    }

    public String getEmailReceive() {
        if (emailReceive.isEmpty()) {
            throw new IllegalArgumentException("The receive email is not specified.");
        }
        return emailReceive;
    }

    public String getEmailReceivePwd() {

        if (emailReceivePwd.isEmpty()) {
            throw new IllegalArgumentException("The receive email psword is not specified.");
        }
        return emailReceivePwd;
    }

    /**
     * This method is where the different uses of Jodd are exercised
     */
    public void perform() {

    }

    /**
     * Standard send routine using Jodd. Jodd knows about GMail so no need to
     * include port information
     */
    public void sendEmail() {

        // Create am SMTP server object
        SmtpServer<SmtpSslServer> smtpServer = SmtpSslServer
                .create(smtpServerName)
                .authenticateWith(emailSend, emailSendPwd);

        // Display Java Mail debug conversation with the server
        smtpServer.debug(true);

        // Using the fluent style of coding create a plain text message
        Email email = Email.create().from(emailSend)
                .to(emailReceive)
                .subject(getSubject()).addText(getContent());

        // A session is the object responsible for communicating with the server
        SendMailSession session = smtpServer.createSession();

        // Like a file we open the session, send the message and close the
        // session
        session.open();
        session.sendMail(email);
        session.close();
    }

    /**
     * This Method will get the subject field from the user.
     *
     * @return subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * This method will get the Text field from the user.
     *
     * @return plainTxt
     */
    public String getContent() {
        return content;
    }

    /**
     * Whatever i receive from outside application will be put here.
     *
     * @param subject the subject of the email.
     */
    public void setSubject(String subject) {
        if (subject.length() > 78) {
            throw new IllegalArgumentException("The Length must be not above 78.");
        }
        this.subject = subject;
    }

    /**
     * Example with CC field and alternative to fluent style for adding CC
     */
    public void sendEmailWithCC() {

    }

    /**
     * Standard receive routine for Jodd We use an ImapSSLServer. The class
     * KenImapSslServer extends ImalSslServer and adds a property to display the
     * mail debug conversation
     */
    public void receiveEmail() {

    }

    /**
     * Here we create an email message that contains html, embedded image, and
     * an attachment
     *
     * @throws Exception In case we don't find the file to attach/embed
     */
    public void sendWithEmbeddedAndAttachment() throws Exception {

    }

}
