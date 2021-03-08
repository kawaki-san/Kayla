package com.rtkay.kayla.api.reddit.oAuth;

import com.rtkay.kayla.App;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

public class Credentials {
    private String userName, password, clientID, clientSecret, userAgent;

    public Credentials() {
        String fileName = "credentials.properties";
        URL resource = App.class.getResource("api/reddit/" + fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            try {
                File credentials = new File(resource.toURI());
                String line;
                int n = 1;
                FileReader reader=new FileReader(credentials);
                Properties p=new Properties();
                p.load(reader);
                this.userAgent = p.getProperty("userAgent");
                this.password = p.getProperty("password");
                this.userName = p.getProperty("username");
                this.clientID = p.getProperty("clientID");
                this.clientSecret = p.getProperty("clientSecret");
            } catch (URISyntaxException | FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Credentials(String userName, String password, String clientID, String clientSecret, String userAgent) {
        this.userName = userName;
        this.password = password;
        this.clientID = clientID;
        this.clientSecret = clientSecret;
        this.userAgent = userAgent;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getClientID() {
        return clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getUserAgent() {
        return userAgent;
    }
}
