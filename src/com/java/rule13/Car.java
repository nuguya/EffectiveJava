package com.java.rule13;

public class Car {
    public Tire frontLeftTire = new Tire("앞왼쪽", 6);
    public Tire frontRightTire = new Tire("앞오른쪽",2);
    public Tire backLeftTire = new Tire("뒤왼쪽",3);
    private final Tire backRightTire = new Tire("뒤오른쪽",4);
    public static final Tire tires[] = {new Tire("앞",3),new Tire("앞",3),new Tire("앞",3),new Tire("앞",3)};

    int run(){
        System.out.println("자동차가 달립니다");
        if(frontLeftTire.roll()==false) return 1;
        if(frontRightTire.roll()==false) return 2;
        if(backLeftTire.roll()==false) return 3;
        if(backRightTire.roll()==false) return 4;
        return 0;
    }
}
