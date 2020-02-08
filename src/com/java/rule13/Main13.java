package com.java.rule13;

public class Main13 {
    public static void main(String[] args){
        Car car = new Car();
        car.tires[0] = new Tire("max",3);
        car.run();
    }
}
