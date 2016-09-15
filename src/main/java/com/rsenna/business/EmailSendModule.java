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

import com.rsenna.business.RyanEmail;
import jodd.io.FileUtil;
import jodd.mail.att.ByteArrayAttachment;
import jodd.mail.att.FileAttachment;
import jodd.util.MimeTypes;

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
    
    private RyanEmail email;
    private EmailMessage textMessage;

    // You will need a folder with this name or change it to another
    // existing folder
    private final String attachmentFolder = "C:\\Temp\\Attach\\";

    public EmailSendModule(String sendEmailAddress, String sendEmailPassword) {
        setSmtpServerName(sendEmailAddress);
        setEmailAddress(sendEmailAddress);
        setEmailPwd(sendEmailPassword);
    }
    
   public static void create()
   {
       
   }

    /// ************** LIST OF GETTERS ***************
    public String getSmtpServerName() {

        if (smtpServerName.isEmpty()) {
            throw new IllegalArgumentException("The smtp server name is not specified.");
        }
        return smtpServerName;
    }

    public String getEmailSend() {
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
                + sendEmailAddress.substring(sendEmailAddress.indexOf("@")+1);
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
    public RyanEmail send(String subject, String content, MailAddress[] receiveEmail) {

        //VALIDATE BOTH PARAMS HERE.
        // Create am SMTP server object
        SmtpServer<SmtpSslServer> smtpServer = SmtpSslServer
                .create(smtpServerName)
                .authenticateWith(emailAddress, emailPwd);

        // Display Java Mail debug conversation with the server
        smtpServer.debug(true);

        textMessage = new EmailMessage(content, MimeTypes.MIME_TEXT_PLAIN);
       MailAddress sendAddress = new MailAddress(getEmailSend());
        email = new RyanEmail();
        //create the email with the values.
        email.setFrom(sendAddress);
        email.setTo(receiveEmail);
        email.setSubject(subject);
        email.addMessage(textMessage);
       

        // A session is the object responsible for communicating with the server
        SendMailSession session = smtpServer.createSession();

        // Like a file we open the session, send the message and close the
        // session
        session.open();
        session.sendMail(email);
        session.close();
        
        return email;
    }

    /**
     * Here we create an email message that contains html, embedded image, and
     * an attachment
     *
     * @throws Exception In case we don't find the file to attach/embed
     */
    public RyanEmail sendWithEmbedded(String subject, String content,
            String fileEmbeddedPath, MailAddress[] emailReceive) throws Exception {

        // Create am SMTP server object
        SmtpServer<SmtpSslServer> smtpServer = SmtpSslServer
                .create(smtpServerName).authenticateWith(emailAddress,
                emailPwd);

        smtpServer.debug(true);

        // Using the fluent style of coding create an html message
        
        email = new RyanEmail();
        MailAddress sendAddress = new MailAddress(getEmailSend());
        //setting up the embedded image.
        String html = "<html><META http-equiv=Content-Type "
                        + "content=\"text/html; charset=utf-8\">"
                        + "<body><img src="+fileEmbeddedPath+">"
                        + "</body></html>";
        
        textMessage = new EmailMessage(content, MimeTypes.MIME_TEXT_PLAIN);
        
        EmailMessage htmlMessage = new EmailMessage(html, MimeTypes.MIME_TEXT_HTML);
        
        EmailAttachment embeddedAttachment = new ByteArrayAttachment(
        FileUtil.readBytes(fileEmbeddedPath),fileEmbeddedPath,fileEmbeddedPath,fileEmbeddedPath); // possible erros here.
        
        embeddedAttachment.setEmbeddedMessage(htmlMessage);
        
        //setting up the email.
        email.setFrom(sendAddress);
        email.setTo(emailReceive);
        email.setSubject(subject);
        email.addMessage(textMessage);
        email.addMessage(htmlMessage);
        email.attach(embeddedAttachment);
        // A session is the object responsible for communicating with the server
        SendMailSession session = smtpServer.createSession();

        // Like a file we open the session, send the mesage and close the
        // session
        session.open();
        session.sendMail(email);
        session.close();
        
        return email;

    }

    public RyanEmail sendWithAttachment(String subject, String content,
            MailAddress[] emailReceive, String fileAttachmentPath) throws Exception {

        // Create am SMTP server object
        SmtpServer<SmtpSslServer> smtpServer = SmtpSslServer
                .create(smtpServerName).authenticateWith(emailAddress,
                emailPwd);

        smtpServer.debug(true);
        
        email = new RyanEmail();
        //set up the message
        textMessage = new EmailMessage(content, MimeTypes.MIME_TEXT_PLAIN);
        //set up the attachment
        EmailAttachment attachment = 
                new FileAttachment(new File(fileAttachmentPath),
                fileAttachmentPath,fileAttachmentPath);// possible errors
        //setting up the email
        MailAddress sendAddress = new MailAddress(getEmailSend());
        email.setFrom(sendAddress);
        email.setTo(emailReceive);
        email.setSubject(subject);
        email.addMessage(textMessage);
        email.attach(attachment);
        
        
        
        // Using the fluent style of coding create an html message
        //Email email = Email.create().from(emailAddress)
        //        .to(emailReceive)
        //        .subject(subject)
        //        .addText(content)
       //         .attach(EmailAttachment.attachment().file(fileAttachmentPath));

        // A session is the object responsible for communicating with the server
        SendMailSession session = smtpServer.createSession();

        // Like a file we open the session, send the mesage and close the
        // session
        session.open();
        session.sendMail(email);
        session.close();
        
        return email;
    }

    /**
     * Here we create an email message that contains html, embedded image, and
     * an attachment
     *
     * @throws Exception In case we don't find the file to attach/embed
     */
    public RyanEmail sendWithEmbeddedAndAttachment(String subject,
            MailAddress[] emailReceive, String content,
            String fileAttachmentPath, String fileEmbeddedPath) throws Exception {

        // Create am SMTP server object
        SmtpServer<SmtpSslServer> smtpServer = SmtpSslServer
                .create(smtpServerName).authenticateWith(emailAddress,
                emailPwd);

        smtpServer.debug(true);
        
        email = new RyanEmail();
        MailAddress sendAddress = new MailAddress(getEmailSend());
        //set up the message
        textMessage = new EmailMessage(content, MimeTypes.MIME_TEXT_PLAIN);
        //setting up the embedded image.
        String html = "<html><META http-equiv=Content-Type "
                        + "content=\"text/html; charset=utf-8\">"
                        + "<body><img src="+fileEmbeddedPath+">"
                        + "</body></html>";
        
        EmailMessage htmlMessage = new EmailMessage(html, MimeTypes.MIME_TEXT_HTML);
        
        EmailAttachment embeddedAttachment = new ByteArrayAttachment(
        FileUtil.readBytes(fileEmbeddedPath),fileEmbeddedPath,fileEmbeddedPath,fileEmbeddedPath); // possible erros here.
       
        embeddedAttachment.setEmbeddedMessage(htmlMessage);
        
        //setting up the Attachment
                //set up the attachment
        EmailAttachment attachment = 
                new FileAttachment(new File(fileAttachmentPath),
                fileAttachmentPath,fileAttachmentPath);// possible errors
        
        //set up the email
        email.setFrom(sendAddress);
        email.setTo(emailReceive);
        email.setSubject(subject);
        email.addMessage(textMessage);
        email.addMessage(htmlMessage);
        email.attach(embeddedAttachment);
        email.attach(attachment);
        
        // Using the fluent style of coding create an html message
       // Email email = Email.create().from(emailAddress)
       //         .to(emailReceive)
       //         .subject(subject)
       //         .addText(content)
      //          .embed(EmailAttachment.attachment()
     //                   .bytes(new File(fileEmbededPath)))
      //         .attach(EmailAttachment.attachment().file(fileAttachmentPath));

        // A session is the object responsible for communicating with the server
        SendMailSession session = smtpServer.createSession();

        // Like a file we open the session, send the mesage and close the
        // session
        session.open();
        session.sendMail(email);
        session.close();
        
        return email;
    }

}
