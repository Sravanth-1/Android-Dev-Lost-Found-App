package com.example.lostandfoundapp;

public class ReadWriteUserDetails {
    public String FullName, RollNo, PhoneNumber, Email;

    public ReadWriteUserDetails(String textFullName, String textRollNo, String textPhoneNUmber, String textEmail) {
        this.FullName = textFullName;
        this.RollNo = textRollNo;
        this.PhoneNumber = textPhoneNUmber;
        this.Email = textEmail;
    }
}
