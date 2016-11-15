package business;

import beans.RyanEmail;
import interfaces.Mailer;
import java.io.File;
import java.sql.SQLException;
import java.util.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.mail.Flags;
import jodd.mail.EmailAttachment;
import jodd.mail.EmailFilter;
import jodd.mail.ImapSslServer;
import jodd.mail.MailAddress;
import jodd.mail.ReceiveMailSession;
import jodd.mail.ReceivedEmail;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;
import jodd.mail.SmtpSslServer;
import jodd.util.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.EmailDAO;
import properties.ConfigProperty;

/**
 * The class ActionModule is a business class with the purpose of creating,
 * sending and receiving e-mails.
 *
 * @author Railanderson "Ryan" Sena
 * @version 1.0
 */
public class ActionModule implements Mailer {

    private ConfigProperty c;
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    public ActionModule(ConfigProperty c) {
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
            email = createEmailWithCCAndBCC(subject, content, receiveEmail, cc, bcc);
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
     * This method will send an email but receiving an email as a parameter
     * and it returns an email for database purposes.
     * @param email
     * @return
     * @throws Exception 
     */
    public RyanEmail sendEmail(RyanEmail email) throws Exception {
        //Create SMTP Server
        SmtpServer<SmtpSslServer> smtpServer
                = c.configSmtpServer();

        // set folder to sent
        email.setFolder("sent");
        // set date
        email.setSentDate((Date) Timestamp.valueOf(LocalDateTime.now()));
        
        email.setFrom(new MailAddress(c.getUserEmailAddress()));

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
    public ArrayList<RyanEmail> receiveEmail()throws SQLException {
        
        EmailDAO edao = new EmailDAO(c);
        // Create am IMAP server object
        ImapSslServer imapSslServer = c.configImapServer();
        
        // Display the converstaion between the application and the imap server
        //imapSslServer.setProperty("mail.debug", "true");
        //Create a List of emails
        ArrayList<RyanEmail> receivedEmails = new ArrayList<RyanEmail>();
        // A session is the object responsible for communicating with the server
        ReceiveMailSession session = imapSslServer.createSession();
        log.error("TELL ME WHO IS THIS VARIABLE" + c.getImapServerName());
        session.open();

        ReceivedEmail[] emails = session.receiveEmail();
        //close the session
        session.close();

        List<RyanEmail> em = convertToRyanEmail(emails);
        // CONVERT RECEIVED EMAILS TO RYAN EMAILS.
        for(int i = 0; i < em.size(); i++){
            receivedEmails.add(em.get(i));
            edao.create(em.get(i));
        } 
        
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
        int emailMessageLenght = 0;

        //loop through all the Received emails
        for (int i = 0; i < receivedEmails.length; i++) {
            Timestamp ts = new Timestamp(receivedEmails[i].getReceiveDate().getTime());
            ryanEmail.setFrom(receivedEmails[i].getFrom());
            ryanEmail.setTo(receivedEmails[i].getTo());
            ryanEmail.setCc(receivedEmails[i].getCc());
            ryanEmail.setBcc(receivedEmails[i].getBcc());
            ryanEmail.setSentDate(receivedEmails[i].getSentDate());
            ryanEmail.setRcvDate(ts);
            ryanEmail.setSubject(receivedEmails[i].getSubject());
            ryanEmail.setAttachedMessages(receivedEmails[i].getAttachedMessages());
            ryanEmail.setAttachments(receivedEmails[i].getAttachments());
            ryanEmail.setMessageNumber(receivedEmails[i].getMessageNumber());
            ryanEmail.setFlags(receivedEmails[i].getFlags());
            ryanEmail.setFolder("inbox");

            // add all the messages from the received Email.
            emailMessageLenght = receivedEmails[i].getAllMessages().size();
            for (int j = 0; j < emailMessageLenght; j++) {
                ryanEmail.addMessage(receivedEmails[i].getAllMessages().get(j));
            }
            myEmails.add(ryanEmail);
            ryanEmail = new RyanEmail();
        }
        //return my list of emails
        return myEmails;
    }

    @Override
    public ConfigProperty getConfigBean() {
        return c;
    }

    /**
     * This Method creates and returns a nude Email, without CC, BCC,
     * Attachments, or EmbeddedAttachments.
     *
     * @param subject
     * @param content
     * @param receiveEmail
     * @return
     */
    private RyanEmail createEmail(String subject, String content,
            MailAddress[] receiveEmail) {
        //EmailAddress sendAddress = new EmailAddress(sendEmail);
        MailAddress sending = new MailAddress(c.getUserEmailAddress());

        //MailAddress[] receiving = copy(receiveEmail);
        RyanEmail email = new RyanEmail();
        email.setFrom(sending);
        email.setTo(receiveEmail);
        email.setSubject(subject);
        email.addMessage(content, MimeTypes.MIME_TEXT_PLAIN);
        email.setSentDate((Date) Timestamp.valueOf(LocalDateTime.now()));
        return email;

    }

    /**
     * This method creates an Email with cc and bcc.
     *
     * @param subject
     * @param content
     * @param receiveEmail
     * @param cc
     * @param bcc
     * @return
     */
    private RyanEmail createEmailWithCCAndBCC(String subject, String content,
            MailAddress[] receiveEmail, Optional<MailAddress[]> cc,
            Optional<MailAddress[]> bcc) {

        MailAddress sending = new MailAddress(c.getUserEmailAddress());

        RyanEmail email = new RyanEmail();
        email.setFrom(sending);
        email.setTo(receiveEmail);
        email.setCc(cc.get());
        email.setBcc(bcc.get());
        email.setSubject(subject);
        email.addMessage(content, MimeTypes.MIME_TEXT_PLAIN);
        email.setSentDate((Date) Timestamp.valueOf(LocalDateTime.now()));

        return email;
    }

    /**
     * This method creates an email with Bcc only.
     *
     * @param subject the subject of the email
     * @param content the content of the email
     * @param receiveEmail
     * @param bcc
     * @return
     */
    private RyanEmail createEmailWithBcc(String subject, String content,
            MailAddress[] receiveEmail, Optional<MailAddress[]> bcc) {

        MailAddress sending = new MailAddress(c.getUserEmailAddress());

        RyanEmail email = new RyanEmail();

        email.setFrom(sending);
        email.setTo(receiveEmail);
        email.setBcc(bcc.get());
        email.setSubject(subject);
        email.addMessage(content, MimeTypes.MIME_TEXT_PLAIN);
        email.setSentDate((Date) Timestamp.valueOf(LocalDateTime.now()));

        return email;
    }

    /**
     * This method creates an email with cc only.
     *
     * @param subject
     * @param content
     * @param receiveEmail
     * @param cc
     * @return
     */
    private RyanEmail createEmailWithCc(String subject, String content,
            MailAddress[] receiveEmail, Optional<MailAddress[]> cc) {

        MailAddress sending = new MailAddress(c.getUserEmailAddress());

        RyanEmail email = new RyanEmail();
        email.setFrom(sending);
        email.setTo(receiveEmail);
        email.setCc(cc.get());
        email.setSubject(subject);
        email.addMessage(content, MimeTypes.MIME_TEXT_PLAIN);
        email.setSentDate((Date) Timestamp.valueOf(LocalDateTime.now()));

        return email;
    }

    private RyanEmail createEmailWithEmbedded(RyanEmail e, String embedded) throws Exception {

        //setting up the embedded image.
        String html = "<html><META http-equiv=Content-Type "
                + "content=\"text/html; charset=utf-8\">"
                + "<body><img src='cid:" + embedded + "'>"
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
