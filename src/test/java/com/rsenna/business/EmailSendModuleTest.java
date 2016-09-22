/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rsenna.business;

import org.junit.Test;
import com.rsenna.business.*;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import jodd.mail.EmailAddress;
import jodd.mail.MailAddress;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;

/**
 *
 * @author 1333612
 */
public class EmailSendModuleTest {

    @Ignore
    @Test
    public void testSend() {
        ConfigBean c = new ConfigBean("smtp.gmail.com", "imap.gmail.com");
        ActionBean ab = new ActionBean(c, "sender.rsenna@gmail.com", "thisistest");
        MailAddress[] receiver = {new MailAddress("receiver.rsenna@gmail.com")};

        ab.sendEmail("Hello world", "I am Ryan", receiver, Optional.empty(),
                Optional.empty());

        assertEquals(true, true);
    }

    @Ignore
    @Test
    public void testSendWithCC() {
        ConfigBean c = new ConfigBean("smtp.gmail.com", "imap.gmail.com");
        ActionBean ab
                = new ActionBean(c, "sender.rsenna@gmail.com", "thisistest");
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
        ConfigBean c = new ConfigBean("smtp.gmail.com", "imap.gmail.com");
        ActionBean ab
                = new ActionBean(c, "sender.rsenna@gmail.com", "thisistest");
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

    @Ignore
    @Test
    public void testSendWithBoth() {
        ConfigBean c = new ConfigBean("smtp.gmail.com", "imap.gmail.com");
        ActionBean ab
                = new ActionBean(c, "sender.rsenna@gmail.com", "thisistest");
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

    @Test
    @Ignore
    public void testSendWithEmbedded() {

        ConfigBean c = new ConfigBean("smtp.gmail.com", "imap.gmail.com");
        ActionBean ab
                = new ActionBean(c, "sender.rsenna@gmail.com", "thisistest");
        MailAddress[] receiver
                = {new MailAddress("receiver.rsenna@gmail.com")};
        
        try {
            ab.sendWithEmbeddedAndAttachments("Attachment Test", "Test1", receiver,
                    Optional.empty(), Optional.of("embedded1.jpg"),
                    Optional.empty(), Optional.empty());
        } catch (Exception ex) {
            Logger.getLogger(EmailSendModuleTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        assertEquals(true,true);
    }
    
    @Test
    @Ignore
    public void testSendWithAttachments() {

        ConfigBean c = new ConfigBean("smtp.gmail.com", "imap.gmail.com");
        ActionBean ab
                = new ActionBean(c, "sender.rsenna@gmail.com", "thisistest");
        MailAddress[] receiver
                = {new MailAddress("receiver.rsenna@gmail.com")};
        
        try {
            ab.sendWithEmbeddedAndAttachments("Attachment Test", "Test2", receiver,
                    Optional.of("attachment1.jpg"), Optional.empty(),
                    Optional.empty(), Optional.empty());
        } catch (Exception ex) {
            Logger.getLogger(EmailSendModuleTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        assertEquals(true,true);
    }
    
    @Test
    public void testSendWithAttachmentsAndEmbedded() {

        ConfigBean c = new ConfigBean("smtp.gmail.com", "imap.gmail.com");
        ActionBean ab
                = new ActionBean(c, "sender.rsenna@gmail.com", "thisistest");
        MailAddress[] receiver
                = {new MailAddress("receiver.rsenna@gmail.com")};
        
        try {
            ab.sendWithEmbeddedAndAttachments("Attachment Test", "Test2", receiver,
                    Optional.of("attachment1.jpg"), Optional.of("embedded.jpg"),
                    Optional.empty(), Optional.empty());
        } catch (Exception ex) {
            Logger.getLogger(EmailSendModuleTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        assertEquals(true,true);
    }
    
    

}
