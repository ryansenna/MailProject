/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business;

import beans.RyanEmail;
import org.junit.Test;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import jodd.mail.MailAddress;
import org.junit.Ignore;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import properties.ConfigProperty;

/**
 *
 * @author 1333612
 */
public class TestActionModule {
    
    private ConfigProperty c;
    public TestActionModule(){
        c = new ConfigProperty();
        c.setUserEmailAddress("sender.rsenna@gmail.com");
        c.setPassword("thisistest");
        c.setSmtpServerName("smtp.gmail.com");
        c.setImapServerName("imap.gmail.com");
    }

    @Ignore
    @Test
    public void testSend() {

        boolean thrown = false;
        ActionModule ab = new ActionModule(c);

        MailAddress[] receiver = {new MailAddress("receiver.rsenna@gmail.com")};
        RyanEmail sentEmail = new RyanEmail();
        RyanEmail receivedEmail = null;

        try {
            sentEmail = ab.sendEmail("Hello world", "I am Ryan", receiver, Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.empty());
            receivedEmail = ab.receiveEmail().get(0);
        } catch (Exception ex) {
            thrown = true;
            Logger.getLogger(TestActionModule.class.getName()).log(Level.SEVERE, null, ex);
        }
        //RyanEmail receivedEmail = ab.receiveEmail().get(0);
        if (!sentEmail.compareEmails(receivedEmail)) {
            //thrown = true;
        }

        assertFalse(thrown);
    }

    @Ignore
    @Test
    public void testSendWithCC() {

        boolean thrown = false;
        ActionModule ab
                = new ActionModule(c);
        RyanEmail sentEmail = new RyanEmail();
        MailAddress[] receiver
                = {new MailAddress("receiver.rsenna@gmail.com")};
        MailAddress[] cc
                = {new MailAddress("receiver.rsenna@gmail.com"),
                    new MailAddress("railanderson@gmail.com")
                };

        try {
            sentEmail = ab.sendEmail("Hello world", "I am Ryan",
                    receiver, Optional.empty(),
                    Optional.empty(), Optional.of(cc), Optional.empty());
        } catch (Exception ex) {
            thrown = true;
            Logger.getLogger(TestActionModule.class.getName()).log(Level.SEVERE, null, ex);
        }

        assertFalse(thrown);
    }

    @Ignore
    @Test
    public void testSendWithBcc() {

        boolean thrown = false;
        ActionModule ab
                = new ActionModule(c);

        RyanEmail sentEmail = new RyanEmail();

        MailAddress[] receiver
                = {new MailAddress("receiver.rsenna@gmail.com")};
        MailAddress[] bcc
                = {new MailAddress("receiver.rsenna@gmail.com"),
                    new MailAddress("railanderson@gmail.com")
                };

        try {
            sentEmail = ab.sendEmail("Hello world", "I am Ryan", receiver, Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.of(bcc));
        } catch (Exception ex) {
            thrown = true;
            Logger.getLogger(TestActionModule.class.getName()).log(Level.SEVERE, null, ex);
        }

        assertFalse(thrown);
    }

    /**
     * This method tests if we can send a bcc and cc message.
     *
     */
    @Ignore
    @Test
    public void testSendWithBoth() {
        boolean thrown = false;;
        ActionModule ab
                = new ActionModule(c);

        RyanEmail sentEmail = new RyanEmail();

        MailAddress[] receiver
                = {new MailAddress("receiver.rsenna@gmail.com")};
        MailAddress[] cc
                = {new MailAddress("railanderson@gmail.com")};
        MailAddress[] bcc
                = {new MailAddress("receiver.rsenna@gmail.com"),
                    new MailAddress("railanderson@gmail.com")
                };

        try {
            sentEmail = ab.sendEmail("Hello world", "I am Ryan", receiver,
                    Optional.empty(), Optional.empty(), Optional.of(cc),
                    Optional.of(bcc));
        } catch (Exception ex) {
            thrown = true;
            Logger.getLogger(TestActionModule.class.getName()).log(Level.SEVERE, null, ex);
        }

        assertFalse(thrown);
    }

    /**
     * This method tests if we can send a message with only embedded text. If an
     * error occurs at run time, the tests fails.
     */
    @Test
    @Ignore
    public void testSendWithEmbedded() {
        boolean thrown = false;
        ActionModule ab
                = new ActionModule(c);
        RyanEmail sentEmail = new RyanEmail();
        MailAddress[] receiver
                = {new MailAddress("receiver.rsenna@gmail.com")};

        try {
            sentEmail = ab.sendEmail("Attachment Test",
                    "Test1", receiver,
                    Optional.empty(), Optional.of("embedded1.jpg"),
                    Optional.empty(), Optional.empty());
        } catch (Exception ex) {
            thrown = true;
            Logger.getLogger(TestActionModule.class.getName()).log(Level.SEVERE, null, ex);
        }

        assertFalse(thrown);
    }

    /**
     * This method tests if we can send a message with only attachments. If an
     * error occurs at run time, the tests fails.
     */
    @Test
    @Ignore
    public void testSendWithAttachments() {
        boolean thrown = false;
        ActionModule ab
                = new ActionModule(c);

        RyanEmail sentEmail = new RyanEmail();
        MailAddress[] receiver
                = {new MailAddress("receiver.rsenna@gmail.com")};

        try {
            ab.sendEmail("Attachment Test", "Test2", receiver,
                    Optional.of("attachment1.jpg"), Optional.empty(),
                    Optional.empty(), Optional.empty());
        } catch (Exception ex) {
            thrown = true;
            Logger.getLogger(TestActionModule.class.getName()).log(Level.SEVERE, null, ex);
        }

        assertFalse(thrown);
    }

    /**
     * This method tests if we can send a message with attachments and embedded.
     * If an error occurs at run time, the tests fails.
     */
    @Test
    @Ignore
    public void testSendWithAttachmentsAndEmbedded() {

        boolean thrown = false;
        ActionModule ab
                = new ActionModule(c);

        RyanEmail sentEmail = new RyanEmail();
        MailAddress[] receiver
                = {new MailAddress("receiver.rsenna@gmail.com")};

        try {
            ab.sendEmail("Attachment Test", "Test2", receiver,
                    Optional.of("attachment1.jpg"), Optional.of("embedded1.jpg"),
                    Optional.empty(), Optional.empty());
        } catch (Exception ex) {
            thrown = true;
            Logger.getLogger(TestActionModule.class.getName()).log(Level.SEVERE, null, ex);
        }

        assertFalse(thrown);
    }

    /**
     * This method tests if the received email is in fact the one sent. If they
     * are different, the tests fails
     */
    @Test
    @Ignore
    public void testReceiveEmails() {

        boolean a = false;
        ActionModule ab
                = new ActionModule(c);
        MailAddress[] receiver
                = {new MailAddress("receiver.rsenna@gmail.com")};
        RyanEmail email = new RyanEmail();
        List<RyanEmail> receivedEmails = null;
        try {
            email = ab.sendEmail("Hello world", "Testing received", receiver,
                    Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.empty());
            receivedEmails
                    = ab.receiveEmail();
        } catch (Exception ex) {
            Logger.getLogger(TestActionModule.class.getName()).log(Level.SEVERE, null, ex);
        }

        a = receivedEmails.get(0).compareEmails(email);

        assertTrue(a);
    }

    @Test
    @Ignore
    public void testRetrievingAndSaving() {
        c.setUrl("jdbc:mysql://waldo2.dawsoncollege.qc.ca:3306/CS1333612");
        c.setDbUsername("CS1333612");
        c.setDbPass("secrefer");
        boolean b = false;
        ActionModule a = new ActionModule(c);
        try {
            List<RyanEmail> emails = a.receiveEmail();
        } catch (Exception e) {
            e.printStackTrace();
            b = true;
        }

        assertFalse(b);
    }

}
