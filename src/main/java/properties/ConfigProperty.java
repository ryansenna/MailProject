/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package properties;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author 1333612
 */
public class ConfigProperty {

    private final StringProperty userEmailAddress;
    private final StringProperty password;
    private final StringProperty smtpServerName;
    private final StringProperty imapServerName;
    private final StringProperty dbUsername;
    private final StringProperty dbPass;

    public ConfigProperty() {
        super();
        this.userEmailAddress = new SimpleStringProperty("");
        this.password = new SimpleStringProperty("");
        this.imapServerName = new SimpleStringProperty("");
        this.smtpServerName = new SimpleStringProperty("");
        this.dbUsername = new SimpleStringProperty("");
        this.dbPass = new SimpleStringProperty("");
    }

    public String getUserEmailAddress() {
        return userEmailAddress.get();
    }

    public String getPassword() {
        return password.get();
    }

    public String getSmtpServerName() {
        return smtpServerName.get();
    }

    public String getImapServerName() {
        return imapServerName.get();
    }

    public String getDbUsername() {
        return dbUsername.get();
    }

    public String getDbPass() {
        return dbPass.get();
    }

    public void setUserEmailAddress(String s) {
        userEmailAddress.set(s);
    }

    public void setPassword(String s) {
        password.set(s);
    }

    public void setSmtpServerName(String s) {
        smtpServerName.set(s);
    }

    public void setImapServerName(String s) {
        imapServerName.set(s);
    }

    public void setDbUsername(String s) {
        dbUsername.set(s);
    }

    public void setDbPass(String s) {
        dbPass.set(s);
    }

    public StringProperty userEmailAddress() {
        return userEmailAddress;
    }

    public StringProperty password() {
        return password;
    }

    public StringProperty smtpServerName() {
        return smtpServerName;
    }

    public StringProperty imapServerName() {
        return imapServerName;
    }

    public StringProperty dbUsername() {
        return dbUsername;
    }

    public StringProperty dbPass() {
        return dbPass;
    }

    @Override
    public String toString() {
        return "MailConfigProperties\n{\t" + "\n\tuserEmailAddress=" + userEmailAddress.get() + "\n\tpassword=" + password.get() + "\n}";
    }

}
