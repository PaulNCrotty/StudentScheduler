package edu.wgu.android.studentscheduler.entity;


import androidx.room.ColumnInfo;

public class Address {

    @ColumnInfo(name="post_code")
    private int postalCode;
    private String street;
    private String state;
    private String city;

}
