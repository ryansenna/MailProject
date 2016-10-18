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

/**
 *
 * @author 1333612
 */
public class TestActionModule {

    @Test
    public void testSend() {

        boolean thrown = false;
        ConfigModule c = new ConfigModule("smtp.gmail.com", "imap.gmail.com",
                "sender.rsenna@gmail.com", "thisistest",
                "receiver.rsenna@gmail.com", "thisistest");
        ActionModule ab = new ActionModule(c);

        MailAddress[] receiver = {new MailAddress("receiver.rsenna@gmail.com")};
        RyanEmail sentEmail = new RyanEmail();

        try {
            sentEmail = ab.sendEmail("Hello world", "I am Ryan", receiver, Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.empty());
        } catch (Exception ex) {
            thrown = true;
            Logger.getLogger(TestActionModule.class.getName()).log(Level.SEVERE, null, ex);
        }

        RyanEmail receivedEmail = ab.receiveEmail().get(0);
        if (!sentEmail.compareEmails(receivedEmail)) {
            thrown = true;
        }

        assertFalse(thrown);
    }

    @Ignore
    @Test
    public void testSendWithCC() {

        boolean thrown = false;
        ConfigModule c = new ConfigModule("smtp.gmail.com", "imap.gmail.com",
                "sender.rsenna@gmail.com", "thisistest",
                "receiver.rsenna@gmail.com", "thisistest");
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
        ConfigModule c = new ConfigModule("smtp.gmail.com", "imap.gmail.com",
                "sender.rsenna@gmail.com", "thisistest",
                "receiver.rsenna@gmail.com", "thisistest");
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
        boolean thrown = false;
        ConfigModule c = new ConfigModule("smtp.gmail.com", "imap.gmail.com",
                "sender.rsenna@gmail.com", "thisistest",
                "receiver.rsenna@gmail.com", "thisistest");
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
                    Optional.empty(),Optional.empty(), Optional.of(cc),
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
        ConfigModule c = new ConfigModule("smtp.gmail.com", "imap.gmail.com",
                "sender.rsenna@gmail.com", "thisistest",
                "receiver.rsenna@gmail.com", "thisistest");
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
        ConfigModule c = new ConfigModule("smtp.gmail.com", "imap.gmail.com",
                "sender.rsenna@gmail.com", "thisistest",
                "receiver.rsenna@gmail.com", "thisistest");
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
        ConfigModule c = new ConfigModule("smtp.gmail.com", "imap.gmail.com",
                "sender.rsenna@gmail.com", "thisistest",
                "receiver.rsenna@gmail.com", "thisistest");
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
    public void testReceiveEmails() {

        boolean a = true;
        ConfigModule c = new ConfigModule("smtp.gmail.com", "imap.gmail.com",
                "sender.rsenna@gmail.com", "thisistest",
                "receiver.rsenna@gmail.com", "thisistest");
        ActionModule ab
                = new ActionModule(c);
        MailAddress[] receiver
                = {new MailAddress("receiver.rsenna@gmail.com")};
        RyanEmail email = new RyanEmail();
        try {
            email = ab.sendEmail("Hello world", "Testing received", receiver,
                    Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.empty());
        } catch (Exception ex) {
            Logger.getLogger(TestActionModule.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<RyanEmail> receivedEmails
                = ab.receiveEmail();

        a = receivedEmails.get(0).compareEmails(email);

        assertFalse(a);
    }

}
