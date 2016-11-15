/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileIO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;
import java.nio.file.Path;
import static java.nio.file.Paths.get;
import java.util.Properties;
import properties.ConfigProperty;

/**
 *
 * @author 1333612
 */
public class PropertiesIO {
    
    /**
     * Reads the properties from a file.
     * 
     * @param mailConfig
     * @param path
     * @param propFileName
     * @return
     * @throws IOException 
     */
    public final boolean loadTextProperties(final ConfigProperty mailConfig, final String path, final String propFileName) throws IOException {

        boolean found = false;
        Properties prop = new Properties();

        Path txtFile = get(path, propFileName + ".properties");

        // File must exist
        if (Files.exists(txtFile)) {
            try (InputStream propFileStream = newInputStream(txtFile);) {
                prop.load(propFileStream);
            }
            mailConfig.setPassword(prop.getProperty("password"));
            mailConfig.setUserEmailAddress(prop.getProperty("userEmailAddress"));
            mailConfig.setImapServerName(prop.getProperty("imap"));
            mailConfig.setSmtpServerName(prop.getProperty("smtp"));
            mailConfig.setDbUsername(prop.getProperty("dbUsername"));
            mailConfig.setDbPass(prop.getProperty("dbPass"));

            found = true;
        }
        return found;
    }
    /**
     * Writes the properties to a file.
     * 
     * @param path
     * @param propFileName
     * @param mailConfig
     * @throws IOException 
     */
    public final void writeTextProperties(final String path, final String propFileName, final ConfigProperty mailConfig) throws IOException {

        Properties prop = new Properties();

        prop.setProperty("password", mailConfig.getPassword());
        prop.setProperty("userEmailAddress", mailConfig.getUserEmailAddress());
        prop.setProperty("smtp", mailConfig.getSmtpServerName());
        prop.setProperty("imap", mailConfig.getImapServerName());
        prop.setProperty("dbUsername", mailConfig.getDbUsername());
        prop.setProperty("dbPass", mailConfig.getDbPass());

        Path txtFile = get(path, propFileName + ".properties");

        // Creates the file or if file exists it is truncated to length of zero
        // before writing
        try (OutputStream propFileStream = newOutputStream(txtFile)) {
            prop.store(propFileStream, "SMTP Properties");
        }
    }

}
