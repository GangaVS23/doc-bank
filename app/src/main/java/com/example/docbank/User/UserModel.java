package com.example.docbank.User;

public class UserModel {
    String uid,name,phone,username,password,utype;

    public UserModel(String uid, String name, String phone, String username, String password, String utype) {
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.username = username;
        this.password = password;
        this.utype = utype;
    }

    public String getUtype() {
        return utype;
    }

    public void setUtype(String utype) {
        this.utype = utype;
    }



    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
