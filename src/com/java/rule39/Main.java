package com.java.rule39;

import java.util.Date;

public class Main {
    public static void main(String[] args){
        Date start = new Date();
        Date end = new Date();
        Period period = new Period(start,end);

        period.end().setYear(78);
        System.out.println(period.start());
        System.out.println(period.end());
    }
}
