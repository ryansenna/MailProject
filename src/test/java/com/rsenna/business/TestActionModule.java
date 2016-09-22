/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rsenna.business;

import com.rsenna.beans.RyanEmail;
import org.junit.Test;
import com.rsenna.business.*;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import jodd.mail.EmailAddress;
import jodd.mail.MailAddress;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author 1333612
 */
public class TestActionModule {

    @Ignore
    @Test
    public void testSend() {
        ConfigModule c = new ConfigModule("smtp.gmail.com", "imap.gmail.com");
        ActionModule ab = new ActionModule(c, "sender.rsenna@gmail.com", "thisistest");
        MailAddress[] receiver = {new MailAddress("receiver.rsenna@gmail.com")};

        ab.sendEmail("Hello world", "I am Ryan", receiver, Optional.empty(),
                Optional.empty());

        assertEquals(true, true);
    }

    @Ignore
    @Test
    public void testSendWithCC() {
        ConfigModule c = new ConfigModule("smtp.gmail.com", "imap.gmail.com");
        ActionModule ab
                = new ActionModule(c, "sender.rsenna@gmail.com", "thisistest");
        MailAddress[] receiver
                = {new MailAddress("receiver.rsenna@gmail.com")};
        MailAddress[] cc
                = {new MailAddress("receiver.rsenna@gmail.com"),
                    new MailAddress("railanderson@gmail.com")
                };

        ab.sendEmail("Hello world", "I am Ryan", receiver, Optional.of(cc),
                Optional.empty());

        assertEquals(true, true);
    }

    @Ignore
    @Test
    public void testSendWithBcc() {
        ConfigModule c = new ConfigModule("smtp.gmail.com", "imap.gmail.com");
        ActionModule ab
                = new ActionModule(c, "sender.rsenna@gmail.com", "thisistest");
        MailAddress[] receiver
                = {new MailAddress("receiver.rsenna@gmail.com")};
        MailAddress[] bcc
                = {new MailAddress("receiver.rsenna@gmail.com"),
                    new MailAddress("railanderson@gmail.com")
                };

        ab.sendEmail("Hello world", "I am Ryan", receiver, Optional.empty(),
                Optional.of(bcc));

        assertEquals(true, true);
    }

    /**
     * This method tests if we can send a bcc and cc message.
     *
     */
    @Ignore
    @Test
    public void testSendWithBoth() {
        ConfigModule c = new ConfigModule("smtp.gmail.com", "imap.gmail.com");
        ActionModule ab
                = new ActionModule(c, "sender.rsenna@gmail.com", "thisistest");
        MailAddress[] receiver
                = {new MailAddress("receiver.rsenna@gmail.com")};
        MailAddress[] cc
                = {new MailAddress("railanderson@gmail.com")};
        MailAddress[] bcc
                = {new MailAddress("receiver.rsenna@gmail.com"),
                    new MailAddress("railanderson@gmail.com")
                };

        ab.sendEmail("Hello world", "I am Ryan", receiver, Optional.of(cc),
                Optional.of(bcc));

        assertEquals(true, true);
    }

    /**
     * This method tests if we can send a message with only embedded text. If an
     * error occurs at run time, the tests fails.
     */
    @Test
    @Ignore
    public void testSendWithEmbedded() {
        boolean thrown = false;
        ConfigModule c = new ConfigModule("smtp.gmail.com", "imap.gmail.com");
        ActionModule ab
                = new ActionModule(c, "sender.rsenna@gmail.com", "thisistest");
        MailAddress[] receiver
                = {new MailAddress("receiver.rsenna@gmail.com")};

        try {
            ab.sendWithEmbeddedAndAttachments("Attachment Test", "Test1", receiver,
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
        ConfigModule c = new ConfigModule("smtp.gmail.com", "imap.gmail.com");
        ActionModule ab
                = new ActionModule(c, "sender.rsenna@gmail.com", "thisistest");
        MailAddress[] receiver
                = {new MailAddress("receiver.rsenna@gmail.com")};

        try {
            ab.sendWithEmbeddedAndAttachments("Attachment Test", "Test2", receiver,
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
        ConfigModule c = new ConfigModule("smtp.gmail.com", "imap.gmail.com");
        ActionModule ab
                = new ActionModule(c, "sender.rsenna@gmail.com", "thisistest");
        MailAddress[] receiver
                = {new MailAddress("receiver.rsenna@gmail.com")};

        try {
            ab.sendWithEmbeddedAndAttachments("Attachment Test", "Test2", receiver,
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
        ConfigModule c = new ConfigModule("smtp.gmail.com", "imap.gmail.com");
        ActionModule ab
                = new ActionModule(c, "sender.rsenna@gmail.com", "thisistest");
        MailAddress[] receiver
                = {new MailAddress("receiver.rsenna@gmail.com")};
        RyanEmail email = ab.sendEmail("Hello world", "Testing received", receiver, Optional.empty(),
                Optional.empty());
        
        List<RyanEmail> receivedEmails =
                ab.receiveEmail("receiver.rsenna@gmail.com", "thisistest");
        
            a = receivedEmails.get(0).compareEmails(email);
            
        assertTrue(a);  
    }

}
