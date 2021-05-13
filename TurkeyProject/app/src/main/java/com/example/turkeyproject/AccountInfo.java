package com.example.turkeyproject;

public class AccountInfo {
    private int uID;
    private String account, doctor_name;

    public void init (int id ,String acc, String dn) {
        this.uID=id;
        this.account=acc;
        this.doctor_name=dn;
    }

    public int getUserID() {
        return this.uID;
    }
    public String getAccount(){ return this.account;}
    public String getDoctorName(){
        return this.doctor_name;
    }
}
