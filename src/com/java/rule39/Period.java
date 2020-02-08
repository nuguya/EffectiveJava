package com.java.rule39;

import java.util.Date;

public final class Period {
    private final Date start;
    public  Date end;

    public Period(Date start, Date end){
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());

        if(start.compareTo(this.end)>0) {
            throw new IllegalArgumentException(this.start + "after" + this.end);
        }
    }
    public Date start(){
        return new Date(start.getTime());
    }
    public Date end(){
        return new Date(end.getTime());
    }
}
