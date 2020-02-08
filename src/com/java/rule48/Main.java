package com.java.rule48;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args){
        final BigDecimal TEN_CENTS = new BigDecimal(".10");

        int itemsBought = 0;
        BigDecimal funds = new BigDecimal("1.00");
        for(BigDecimal price = TEN_CENTS;funds.compareTo(price)>=0;price = price.add(TEN_CENTS)){
            funds = funds.subtract(price);
            itemsBought++;
        }
        System.out.println(itemsBought);
        System.out.println(funds);
    }
}
