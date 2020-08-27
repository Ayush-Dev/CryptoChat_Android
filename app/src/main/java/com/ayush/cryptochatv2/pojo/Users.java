package com.ayush.cryptochatv2.pojo;

public class Users {

    private String name, email, uid, publicKey;

    public Users(){}

    public Users(String name, String email, String uid, String publicKey) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.publicKey = publicKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
