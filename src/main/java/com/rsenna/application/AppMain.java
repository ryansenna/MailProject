/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rsenna.application;

import com.rsenna.business.Mail;
import java.util.Scanner;

/**
 *
 * @author 1333612
 */
public class AppMain {

    private static Mail m = new Mail();
    private static String login, pwd = "";
    private static String subject = "";
    private static String content = "";

    public static void main(String[] args) {

        //promptLogin();
        promptSubject();
        promptTxt();
        
        m.setSubject(subject);
        m.setPlainTxt(content);
        
        m.sendEmail();
        System.exit(0);
    }


    private static void promptLogin() {

        Scanner keyboard = new Scanner(System.in);

        System.out.print("Login: ");
        login = keyboard.nextLine();
        keyboard.nextLine();

        System.out.print("Password: ");
        pwd = keyboard.nextLine();

    }

    private static void promptSubject() {
        Scanner keyboard = new Scanner(System.in);

        System.out.print("subject: ");
        subject = keyboard.nextLine();
    }

    private static void promptTxt() {
        Scanner keyboard = new Scanner(System.in);

        System.out.print("Message: ");
        content = keyboard.nextLine();
    }
}
