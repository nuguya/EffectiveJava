package com.java.rule13;

public class Tire {
    public int maxRotation;
    public int accumulatedRoation;
    public String location;

    public Tire(String location,int maxRotation){
        this.location = location;
        this.maxRotation = maxRotation;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public boolean roll(){
        ++accumulatedRoation;
        if(accumulatedRoation < maxRotation){
            System.out.println(location+" Tire 수명: "+(maxRotation-accumulatedRoation)+"회");
            return true;
        }
        else{
            System.out.println("*** "+location+" Tire 펑크 ***");
            return false;
        }
    }
}
