/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rsenna.business;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 *
 * @author 1333612
 */
public class TestRunner {
    
    public static void main(String[] args)
    {
        Result r = JUnitCore.runClasses(EmailSendModule.class);
        
              for (Failure failure : r.getFailures()) {
         System.out.println(failure.toString());
      }
		
      System.out.println(r.wasSuccessful());
    }
}
