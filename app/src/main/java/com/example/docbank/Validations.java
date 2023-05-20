package com.example.docbank;

public  class Validations {
    public static  String vehicleno="^[A-Z]{2}[ -][0-9]{1,2}(?: [A-Z])?(?: [A-Z]*)? [0-9]{4}$";
    public  static  String email="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public  static String mobile="(0/91)?[6-9][0-9]{9}";
    public static String text="[a-z A-Z\\\\s]+";
    public static String digit=  "\\\\d+";
}
