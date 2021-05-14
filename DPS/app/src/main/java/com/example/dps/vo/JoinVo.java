package com.example.dps.vo;

public class JoinVo {
    public final String user_id,user_pwd,birth,name,phone_number,serial_no1,serial_no2,gender,email;
    public JoinVo(String user_id, String user_pwd, String birth, String name, String phone_number,  String serial_no1, String serial_no2, String gender, String email) {
        this.user_id = user_id;
        this.user_pwd = user_pwd;
        this.birth = birth;
        this.name = name;
        this.phone_number = phone_number;
        this.serial_no1 = serial_no1;
        this.serial_no2 = serial_no2;
        this.gender = gender;
        this.email = email;
    }
}
