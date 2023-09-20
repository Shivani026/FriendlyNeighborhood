package com.example.friendlyneighborhood.Model;

public class UserModel {
    private String name, password, contact, email , type, society , blockFlat;

    public UserModel(String name, String password, String contact, String email, String type, String society, String blockFlat) {
        this.name = name;
        this.password = password;
        this.contact = contact;
        this.email = email;
        this.type = type;
        this.society = society;
        this.blockFlat = blockFlat;
    }

    public UserModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSociety() {
        return society;
    }

    public void setSociety(String society) {
        this.society = society;
    }

    public String getBlockFlat() {
        return blockFlat;
    }

    public void setBlockFlat(String blockFlat) {
        this.blockFlat = blockFlat;
    }
}
