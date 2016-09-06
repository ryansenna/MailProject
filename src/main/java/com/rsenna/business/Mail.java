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
    private String emailCC1;// list of emails
    private String emailCC2;// list of emails.

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

    public Mail(Mail m, String emailCC1, String emailCC2) {
        this.smtpServerName = m.getSmtpServerName();
        this.imapServerName = m.getImapServerName();
        this.emailSend = m.getEmailSend();
        this.emailSendPwd = m.getEmailSendPwd();
        this.emailReceive = m.getEmailReceive();
        this.emailReceivePwd = m.getEmailReceivePwd();

        this.emailCC1 = emailCC1;
        this.emailCC2 = emailCC2;
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
    public void sendEmail(String subject, String content) {

        //VALIDATE BOTH PARAMS HERE.
        // Create am SMTP server object
        SmtpServer<SmtpSslServer> smtpServer = SmtpSslServer
                .create(smtpServerName)
                .authenticateWith(emailSend, emailSendPwd);

        // Display Java Mail debug conversation with the server
        smtpServer.debug(true);

        // Using the fluent style of coding create a plain text message
        Email email = Email.create().from(emailSend)
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
     * *********** REVIEW THIS METHOD ************** Example with CC field and
     * alternative to fluent style for adding CC
     */
    public void sendEmailWithCC(String subject, String content) {

        // VALIDATE SUBJECT HERE.
        //VALIDATE CONTENT HERE.
        // Create am SMTP server object
        SmtpServer<SmtpSslServer> smtpServer = SmtpSslServer
                .create(smtpServerName)
                .authenticateWith(emailSend, emailSendPwd);

        // Display Java Mail debug conversation with the server
        smtpServer.debug(true);

        // Using the fluent style requires EmailMessage
        Email email = Email.create().from(emailSend)
                .to(emailReceive)
                .cc(new EmailAddress[]{new EmailAddress(emailCC1),
            new EmailAddress(emailCC2)})
                .subject(subject).addText(content);

        // Display all the cc addresses
        MailAddress[] s = email.getCc();
        for (MailAddress ma : s) {
            log.info("cc: " + ma.getEmail());
        }

        // A session is the object responsible for communicating with the server
        SendMailSession session = smtpServer.createSession();

        // Like a file we open the session, send the message and close the
        // session
        session.open();
        session.sendMail(email);
        session.close();
    }

    /**
     * Standard receive routine for Jodd We use an ImapSSLServer. The class
     * KenImapSslServer extends ImalSslServer and adds a property to display the
     * mail debug conversation
     */
  public void receiveEmail() {

        // Create am IMAP server object
        ImapSslServer imapSslServer = new ImapSslServer(imapServerName,
                emailReceive, emailReceivePwd);

        // Display the converstaion between the application and the imap server
        imapSslServer.setProperty("mail.debug", "true");

        // A session is the object responsible for communicating with the server
        ReceiveMailSession session = imapSslServer.createSession();
        session.open();

        // We only want messages that have not be read yet.
        // Messages that are delivered are then marked as read on the server
        ReceivedEmail[] emails = session.receiveEmailAndMarkSeen(EmailFilter
                .filter().flag(Flags.Flag.SEEN, false));

        // If there are any emails loop through them displaying their contents
        // and saving the attachments.
        if (emails != null) {
            for (ReceivedEmail email : emails) {
                log.info("\n\n===[" + email.getMessageNumber() + "]===");
                // common info
                log.info("FLAG: %1$h\n", email.getFlags());
                log.info("FROM:" + email.getFrom());
                log.info("TO:" + email.getTo()[0]);
                log.info("SUBJECT:" + email.getSubject());
                log.info("PRIORITY:" + email.getPriority());
                log.info("SENT DATE:" + email.getSentDate());
                log.info("RECEIVED DATE: " + email.getReceiveDate());

                // Messages may be multi-part so they are stored in an array
                List<EmailMessage> messages = email.getAllMessages();
                for (EmailMessage msg : messages) {
                    log.info("------");
                    log.info(msg.getEncoding());
                    log.info(msg.getMimeType());
                    log.info(msg.getContent());
                }

                // There may be multiple arrays so they are stored in an array
                List<EmailAttachment> attachments = email.getAttachments();
                if (attachments != null) {
                    log.info("+++++");
                    for (EmailAttachment attachment : attachments) {
                        log.info("name: " + attachment.getName());
                        log.info("cid: " + attachment.getContentId());
                        log.info("size: " + attachment.getSize());
                        // Write the file to disk
                        // Location hard coded in this example
                        attachment.writeToFile(new File(attachmentFolder, attachment.getName()));
                    }
                }
            }
        }
        session.close();
    }

    /**
     * Here we create an email message that contains html, embedded image, and
     * an attachment
     *
     * @throws Exception In case we don't find the file to attach/embed
     */
    public void sendWithEmbedded(String subject, String content, String fileEmbeddedPath) throws Exception {

        // Create am SMTP server object
        SmtpServer<SmtpSslServer> smtpServer = SmtpSslServer
                .create(smtpServerName).authenticateWith(emailSend,
                emailSendPwd);

        smtpServer.debug(true);

        // Using the fluent style of coding create an html message
        Email email = Email.create().from(emailSend)
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
            String fileAttachmentPath) throws Exception {

        // Create am SMTP server object
        SmtpServer<SmtpSslServer> smtpServer = SmtpSslServer
                .create(smtpServerName).authenticateWith(emailSend,
                emailSendPwd);

        smtpServer.debug(true);

        // Using the fluent style of coding create an html message
        Email email = Email.create().from(emailSend)
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
    public void sendWithEmbeddedAndAttachment(String subject, String content,
            String fileAttachmentPath, String fileEmbededPath) throws Exception {

        // Create am SMTP server object
        SmtpServer<SmtpSslServer> smtpServer = SmtpSslServer
                .create(smtpServerName).authenticateWith(emailSend,
                emailSendPwd);

        smtpServer.debug(true);

        // Using the fluent style of coding create an html message
        Email email = Email.create().from(emailSend)
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
