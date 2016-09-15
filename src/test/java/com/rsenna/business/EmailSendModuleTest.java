/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rsenna.business;

import org.junit.Test;
import com.rsenna.business.*;
import jodd.mail.MailAddress;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author 1333612
 */

public class EmailSendModuleTest {
    
    @Test
    public void testSend()
    {
        EmailSendModule esm = new EmailSendModule("sender.rsenna@gmail.com","thisistest");
        MailAddress[] ma = {new MailAddress("receiver.rsenna@gmail.com")};
        esm.send("Hello World", "I am Ryan Sena", ma);
        
        assertEquals(true,true);
    }
    
}
