/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import beans.RyanEmail;
import business.ActionModule;
import business.ConfigModule;
import java.io.IOException;
import java.util.Optional;
import jodd.mail.MailAddress;
import org.junit.*;
import static org.junit.Assert.assertFalse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 1333612
 */
public class TestEmailDAO {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Test
    public void testCreate() {
        boolean thrown = false;
        ConfigModule c = new ConfigModule("smtp.gmail.com", "imap.gmail.com",
                "sender.rsenna@gmail.com", "thisistest",
                "receiver.rsenna@gmail.com", "thisistest",
                "jdbc:mysql://waldo2.dawsoncollege.qc.ca:3306/CS1333612", "CS1333612", "secrefer");
        ActionModule ab = new ActionModule(c);
        MailAddress[] receiver = {new MailAddress("receiver.rsenna@gmail.com")};
        RyanEmail sentEmail = new RyanEmail();
        try {
            sentEmail = ab.sendEmail("Hello world", "I am Ryan 2", receiver, Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.empty());
            
            EmailDAO edao = new EmailDAO(c);
            
            edao.create(sentEmail); //magic line
        } catch (Exception e) {
            thrown = true;
            e.printStackTrace();
            log.error(e.getMessage());
        }
        
        assertFalse(thrown);
    }
}
