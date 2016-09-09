/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rsenna.business;

import java.io.File;
import java.util.List;
import javax.mail.Flags;
import jodd.mail.EmailAttachment;
import jodd.mail.EmailFilter;
import jodd.mail.EmailMessage;
import jodd.mail.ImapSslServer;
import jodd.mail.ReceiveMailSession;
import jodd.mail.ReceivedEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 1333612
 */
public class EmailReceiveModule {

    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    private String imapServerName;
    private String emailReceive;
    private String emailPwd;

    private final String attachmentFolder = "C:\\Temp\\Attach\\";

    public EmailReceiveModule(String emailReceive, String emailPwd) {

        setImapServerName(emailReceive);
        setEmailReceive(emailReceive);
        setEmailPwd(emailPwd);
    }

    /**
     * GETTERS AND SETTERS
     *
     */
    public String getImapServerName() {
        return imapServerName;
    }

    public String getEmailReceive() {
        return emailReceive;
    }

    public String getEmailPwd() {
        return emailPwd;
    }

    private void setImapServerName(String emailReceive) {
        this.imapServerName = "imap."
                + emailReceive.substring(emailReceive.indexOf("@"));;
    }

    private void setEmailReceive(String emailReceive) {
        this.emailReceive = emailReceive;
    }

    private void setEmailPwd(String emailPwd) {
        this.emailPwd = emailPwd;
    }

    public void receive() {
        // Create an IMAP server object
        ImapSslServer imapSslServer = new ImapSslServer(imapServerName,
                emailReceive, emailPwd);

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

}
