/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rsenna.business;

import org.junit.Test;
import com.rsenna.business.*;
import java.util.Optional;
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
                = { new MailAddress("railanderson@gmail.com")};
        MailAddress[] bcc
                = {new MailAddress("receiver.rsenna@gmail.com"),
                    new MailAddress("railanderson@gmail.com")
                };

        ab.sendEmail("Hello world", "I am Ryan", receiver, Optional.of(cc),
                Optional.of(bcc));

        assertEquals(true, true);
    }

}
