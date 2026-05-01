package com.livisync.smd_a2.models;

public class User {
    private String uid, name, email, phone, address, country, gender, dob, accountType;

    public User() {}

    public User(String uid, String name, String email, String phone, String address,
                String country, String gender, String dob, String accountType) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.country = country;
        this.gender = gender;
        this.dob = dob;
        this.accountType = accountType;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getCountry() { return country; }
    public String getGender() { return gender; }
    public String getDob() { return dob; }
    public String getAccountType() { return accountType; }
}