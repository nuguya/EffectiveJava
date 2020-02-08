package com.java.rule39;

import java.util.Date;

public final class Period {
    private final Date start;
    private final Date end;

    public Period(Date start, Date end){
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());

        if(start.compareTo(this.end)>0) {
            throw new IllegalArgumentException(this.start + "after" + this.end);
        }
    }
    public Date start(){
        return start;
    }
    public Date end(){
        return end;
    }
}
