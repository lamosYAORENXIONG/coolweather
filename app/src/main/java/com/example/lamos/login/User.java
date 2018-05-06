package com.example.lamos.login;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;

/**
 * Created by lamos on 2018/4/15.
 */

public class User extends DataSupport {
    private String name;
    private String Account;
    private String password;
    private String mail;
    private String weatherId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }


    public String getAccount() {
        return Account;
    }

    public void setAccount(String account) {
        Account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    
    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }



}
