/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import beans.RyanEmail;
import business.ActionModule;
import business.ConfigModule;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import jodd.mail.MailAddress;
import jodd.util.MimeTypes;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import properties.ConfigProperty;

/**
 *
 * @author 1333612
 */
public class TestEmailDAO {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private EmailDAO em;
    private ConfigProperty c;

    public TestEmailDAO() {

        c = new ConfigProperty();
        c.setUserEmailAddress("sender.rsenna@gmail.com");
        c.setPassword("thisistest");
        c.setSmtpServerName("smtp.gmail.com");
        c.setImapServerName("imap.gmail.com");
        
        c.setUrl("jdbc:mysql://waldo2.dawsoncollege.qc.ca:3306/CS1333612");
        c.setDbUsername("CS1333612");
        c.setDbPass("secrefer");

        try {
            em = new EmailDAO(c);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        
    }

    /**
     * This method will recreate the Database from the SQL file in it.
     */
    @Ignore
    public void recreateDB() {

        try {
            EmailDAO e = new EmailDAO(c);
            e.buildDB();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

    }

    /**
     * This method will test the creation of records in my database.
     */
    @Test
    @Ignore
    public void testCreate() {
        boolean thrown = false;
        int r = 0;
        ActionModule ab = new ActionModule(c);
        MailAddress[] receiver = {new MailAddress("receiver.rsenna@gmail.com")};
        RyanEmail sentEmail = new RyanEmail();
        try {
            sentEmail = ab.sendEmail("Hello world", "I am Ryan", receiver, Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.empty());

            r = em.create(sentEmail); //magic line
        } catch (Exception e) {
            thrown = true;
            e.printStackTrace();
            log.error(e.getMessage());
        }

        assertEquals("test create: ", 4, r);
    }

    /**
     * This method will test the creation of an email record that contains cc
     * which affects the number of rows affected in the db.
     */
    @Test
    @Ignore
    public void testCreateWithCc() {
        boolean thrown = false;
        int r = 0;
        ActionModule ab = new ActionModule(c);
        MailAddress[] receiver = {new MailAddress("receiver.rsenna@gmail.com")};
        MailAddress[] cc
                = {new MailAddress("receiver.rsenna@gmail.com"),
                    new MailAddress("railanderson@gmail.com")
                };

        RyanEmail sentEmail = new RyanEmail();
        try {
            sentEmail = ab.sendEmail("Hello world", "I am Ryan",
                    receiver, Optional.empty(),
                    Optional.empty(), Optional.of(cc), Optional.empty());

            r = em.create(sentEmail); //magic line
        } catch (Exception e) {
            thrown = true;
            e.printStackTrace();
            log.error(e.getMessage());
        }

        assertEquals("Test with CC: ", r, 6);
    }

    /**
     * This method will test the creation of an email record that contains bcc
     * which affects the number of rows affected in the db.
     */
    @Test
    @Ignore
    public void testCreateWithBcc() {
        boolean thrown = false;
        int r = 0;
        ActionModule ab = new ActionModule(c);
        MailAddress[] receiver = {new MailAddress("receiver.rsenna@gmail.com")};
        MailAddress[] bcc
                = {new MailAddress("receiver.rsenna@gmail.com"),
                    new MailAddress("railanderson@gmail.com")
                };

        RyanEmail sentEmail = new RyanEmail();
        try {
            sentEmail = ab.sendEmail("Hello world", "I am Ryan", receiver, Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.of(bcc));

            r = em.create(sentEmail); //magic line
        } catch (Exception e) {
            thrown = true;
            e.printStackTrace();
            log.error(e.getMessage());
        }

        assertEquals("Test Bcc: ", r, 6);
    }

    /**
     * This method will test the creation of an email record that contains
     * attachments which affects the number of rows affected in the db.
     */
    @Test
    @Ignore
    public void testCreateWithAttachments() {
        boolean thrown = false;
        int r = 0;
        ActionModule ab = new ActionModule(c);
        MailAddress[] receiver = {new MailAddress("receiver.rsenna@gmail.com")};

        RyanEmail sentEmail = new RyanEmail();
        try {
            sentEmail = ab.sendEmail("Attachment Test", "Test2", receiver,
                    Optional.of("attachment1.jpg"), Optional.empty(),
                    Optional.empty(), Optional.empty());
            r = em.create(sentEmail); //magic line
        } catch (Exception e) {
            thrown = true;
            e.printStackTrace();
            log.error(e.getMessage());
        }

        assertEquals("Test with Attachments: ", r, 5);
    }

    /**
     * This method tests the deletion of a record in the database.
     */
    @Test
    @Ignore
    public void testDelete() {
        int rowAff = 0;
        try {
            rowAff = em.delete(1);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        assertEquals(1, rowAff);
    }

    /**
     * This method tests retrieving a particular record from the database.
     */
    @Test
    @Ignore
    public void testFind() {
        boolean b = false;

        RyanEmail emailTest1 = new RyanEmail();

        emailTest1.setFrom(new MailAddress("sender.rsenna@gmail.com"));
        emailTest1.setTo(new MailAddress("receiver.rsenna@gmail.com"));
        emailTest1.setSubject("Hello");
        emailTest1.setSentDate(Timestamp.valueOf(LocalDateTime.now()));
        emailTest1.setRcvDate(Timestamp.valueOf(LocalDateTime.now()));
        emailTest1.addMessage("Hi how are you", MimeTypes.MIME_TEXT_PLAIN);

        try {
            RyanEmail emailTest2 = em.findEmail(5);
            b = emailTest1.compareEmails(emailTest2);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        //assert if they are equal.
        assertTrue(b);
    }

    /**
     * This method tests retrieving all the records from the database.
     */
    @Test
    @Ignore
    public void testFindAll() {
        boolean a = false;
        List<RyanEmail> ema = new ArrayList<RyanEmail>();
        try {
            ema = em.findAll();
        } catch (Exception e) {
            a = true;
            log.error(e.getMessage());
        }
        assertEquals("testFindAll: ", 4, ema.size());
    }
   
}
