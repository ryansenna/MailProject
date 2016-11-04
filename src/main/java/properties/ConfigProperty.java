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

    private final StringProperty userName;
    private final StringProperty userEmailAddress;
    private final StringProperty password;
    private final StringProperty smtpServerName;
    private final StringProperty imapServerName;

    public ConfigProperty() {
        super();
        this.userName = new SimpleStringProperty("");
        this.userEmailAddress = new SimpleStringProperty("");
        this.password = new SimpleStringProperty("");
        this.imapServerName = new SimpleStringProperty("");
        this.smtpServerName = new SimpleStringProperty("");
    }

    public String getUserName() {
        return userName.get();
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

    public void setUserName(String s) {
        userName.set(s);
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

    @Override
    public String toString() {
        return "MailConfigProperties\n{\t" + "userName=" + userName.get() + "\n\tuserEmailAddress=" + userEmailAddress.get() + "\n\tpassword=" + password.get() + "\n}";
    }

}
