package com.java.rule38;

import java.util.AbstractList;
import java.util.List;

public class Exam {
    static List<Integer> intArrayAsList(final int[] a){
//        if(a == null){
//            throw new NullPointerException();
//        }

        return new AbstractList<Integer>() {
            @Override
            public Integer get(int index) {
                return a[index];
            }

            public Integer set(int i,Integer val){
                int oldVal = a[i];
                a[i] = val;
                return oldVal;
            }

            @Override
            public int size() {
                return 1;
            }
        };
    }
}


