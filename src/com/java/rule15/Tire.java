package com.java.rule15;

final class Tire {
    private final int maxRotation;
    private final String location;

    public Tire(String location,int maxRotation){
        this.location = location;
        this.maxRotation = maxRotation;
    }

    public Tire roll(){
        if(0 < maxRotation){
            System.out.println(location+" Tire 수명: "+(maxRotation-1)+"회");
            return new Tire(this.location,this.maxRotation-1);
        }
        else{
            System.out.println("*** "+location+" Tire 펑크 ***");
            return new Tire(this.location,0);
        }
    }
}
