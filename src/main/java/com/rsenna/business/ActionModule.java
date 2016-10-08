
package com.rsenna.business;

import com.rsenna.beans.RyanEmail;
import com.rsenna.interfaces.Mailer;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import javax.mail.Flags;
import jodd.io.FileUtil;
import jodd.mail.EmailAddress;
import jodd.mail.EmailAttachment;
import jodd.mail.EmailAttachmentBuilder;
import jodd.mail.EmailFilter;
import jodd.mail.EmailMessage;
import jodd.mail.ImapSslServer;
import jodd.mail.MailAddress;
import jodd.mail.ReceiveMailSession;
import jodd.mail.ReceivedEmail;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;
import jodd.mail.SmtpSslServer;
import jodd.mail.att.ByteArrayAttachment;
import jodd.mail.att.FileAttachment;
import jodd.util.MimeTypes;
import org.joda.time.DateTime;

/**
 * The class ActionModule is a business class with the purpose
 * of creating, sending and receiving e-mails.
 * The class uses Jodd and Joda as long with java libraries to support its
 * final goal.
 *
 * @author Railanderson "Ryan" Sena
 * @version 1.0
 */
public class ActionModule implements Mailer {

    private ConfigModule c;

    public ActionModule(ConfigModule c) {
        this.c = c;
    }

    /**
     * Definition in the interface.
     *
     * @param subject
     * @param content
     * @param receiveEmail
     * @param fileAttachPath
     * @param embeddedPath
     * @param cc
     * @param bcc
     * @return
     * @throws Exception
     */
    @Override
    public RyanEmail sendEmail(String subject,
            String content, MailAddress[] receiveEmail,
            Optional<String> fileAttachPath, Optional<String> embeddedPath,
            Optional<MailAddress[]> cc, Optional<MailAddress[]> bcc) throws Exception {
        //Create SMTP Server
        SmtpServer<SmtpSslServer> smtpServer
                = c.configSmtpServer();



        //Create an Email
        RyanEmail email = new RyanEmail();
        if (cc.isPresent() && bcc.isPresent()) {// create with cc and bcc if exists
            email = createEmailWithBoth(subject, content, receiveEmail, cc, bcc);
        } else if (cc.isPresent()) {// create if cc exists.
            email = createEmailWithCc(subject, content, receiveEmail, cc);
        } else if (bcc.isPresent()) {// create if bcc exists.
            email = createEmailWithBcc(subject, content, receiveEmail, bcc);
        } else {// create a normal email.
            email = createEmail(subject, content, receiveEmail);
        }
        
        //Keep it in the folder Sent
        email.setFolder("Sent");
        // Check if we need to have an Embedded message.
        if (embeddedPath.isPresent()) {
            email = createEmailWithEmbedded(email, embeddedPath.get());
        }
        //Check if we need to have an Attachment.
        if (fileAttachPath.isPresent()) {
            email = createEmailWithAttachment(email, fileAttachPath.get());
        }
        // Display Java Mail debug conversation with the server
        smtpServer.debug(true);

        // A session is the object responsible for communicating with the server
        SendMailSession session = smtpServer.createSession();

        // session
        session.open();
        session.sendMail(email);
        session.close();

        //return the email db purposes.
        return email;

    }

    /**
     * Definition in the interface.
     *
     * @return
     */
    @Override
    public ArrayList<RyanEmail> receiveEmail() {
        
        // Create am IMAP server object
        ImapSslServer imapSslServer = c.configImapServer();

        // Display the converstaion between the application and the imap server
        imapSslServer.setProperty("mail.debug", "true");
        
        //Create a List of emails
        ArrayList<RyanEmail> receivedEmails = new ArrayList<RyanEmail>();
        // A session is the object responsible for communicating with the server
        ReceiveMailSession session = imapSslServer.createSession();
        session.open();
        
        ReceivedEmail[] emails = session.receiveEmailAndMarkSeen(EmailFilter
                .filter().flag(Flags.Flag.SEEN, false));
        //close the session
        session.close();
        
        // CONVERT RECEIVED EMAILS TO RYAN EMAILS.
        receivedEmails = convertToRyanEmail(emails);
        
        //return for db purposes and UI
        return receivedEmails;
    }
    
        /**
     * This static method takes in Received email array and transforms into a
     * List of Ryan Emails.
     *
     * @param emails
     * @return
     */
    public ArrayList<RyanEmail> convertToRyanEmail(ReceivedEmail[] receivedEmails) {
        //declare types
        ArrayList<RyanEmail> myEmails = new ArrayList<RyanEmail>();
        RyanEmail ryanEmail = new RyanEmail();
       //loop through all the Received emails
        for (int i = 0; i < receivedEmails.length; i++) {
            dt = new DateTime(receivedEmails[i].getSentDate());
            ryanEmail.setFrom(receivedEmails[i].getFrom());
            ryanEmail.setTo(receivedEmails[i].getTo());
            ryanEmail.setCc(receivedEmails[i].getCc());
            ryanEmail.setBcc(receivedEmails[i].getBcc());
            ryanEmail.setRcvDate(receivedEmails[i].getReceiveDate().);
            ryanEmail.setSubject(receivedEmails[i].getSubject());
            ryanEmail.set
            ryanEmail.setAttachments((ArrayList<EmailAttachment>) receivedEmails[i].getAttachments());
            ryanEmail.setFolder("inbox");

            myEmails.add(ryanEmail);
            ryanEmail = new RyanEmail();
        }
        //return my list of emails
        return myEmails;
    }

    @Override
    public ConfigModule getConfigBean() {
        return c;
    }

    private RyanEmail createEmail(String subject, String content,
            MailAddress[] receiveEmail) {
        //EmailAddress sendAddress = new EmailAddress(sendEmail);
        MailAddress sending = new MailAddress(sendEmail);

        //MailAddress[] receiving = copy(receiveEmail);
        RyanEmail email = new RyanEmail();
        DateTime dt = DateTime.now();
        email.setFrom(sending);
        email.setTo(receiveEmail);
        email.setSubject(subject);
        email.addMessage(content, MimeTypes.MIME_TEXT_PLAIN);
        email.setSentDateAndTime(dt);
        return email;

    }

    private RyanEmail createEmailWithBoth(String subject, String content,
            MailAddress[] receiveEmail, Optional<MailAddress[]> cc,
            Optional<MailAddress[]> bcc) {

        MailAddress sending = new MailAddress(sendEmail);

        RyanEmail email = new RyanEmail();
        DateTime dt = DateTime.now();
        
        email.setFrom(sending);
        email.setTo(receiveEmail);
        email.setCc(cc.get());
        email.setBcc(bcc.get());
        email.setSubject(subject);
        email.addMessage(content, MimeTypes.MIME_TEXT_PLAIN);
        email.setSentDateAndTime(dt);

        return email;
    }

    private RyanEmail createEmailWithBcc(String subject, String content,
            MailAddress[] receiveEmail, Optional<MailAddress[]> bcc) {

        MailAddress sending = new MailAddress(sendEmail);

        RyanEmail email = new RyanEmail();
        DateTime dt = DateTime.now();
        
        email.setFrom(sending);
        email.setTo(receiveEmail);
        email.setBcc(bcc.get());
        email.setSubject(subject);
        email.addMessage(content, MimeTypes.MIME_TEXT_PLAIN);
        email.setSentDateAndTime(dt);

        return email;
    }

    private RyanEmail createEmailWithCc(String subject, String content,
            MailAddress[] receiveEmail, Optional<MailAddress[]> cc) {

        MailAddress sending = new MailAddress(sendEmail);

        RyanEmail email = new RyanEmail();
        DateTime dt = DateTime.now();
        email.setFrom(sending);
        email.setTo(receiveEmail);
        email.setCc(cc.get());
        email.setSubject(subject);
        email.addMessage(content, MimeTypes.MIME_TEXT_PLAIN);
        email.setSentDateAndTime(dt);

        return email;
    }

    private RyanEmail createEmailWithEmbedded(RyanEmail e, String embedded) throws Exception {

        //setting up the embedded image.
        String html = "<html><META http-equiv=Content-Type "
                + "content=\"text/html; charset=utf-8\">"
                + "<body><img src='cid:embedded1.jpg'>"
                + "</body></html>";
        e.addHtml(html);
        e.embed(EmailAttachment.attachment()
                .bytes(new File(embedded)));

        return e;
    }

    private RyanEmail createEmailWithAttachment(RyanEmail e,
            String fileAttachmentPath) throws Exception {

        e.attach(EmailAttachment.attachment().file(fileAttachmentPath));
        return e;
    }
}
