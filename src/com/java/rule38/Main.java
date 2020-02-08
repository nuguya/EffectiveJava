package com.java.rule38;

import com.java.rule38.Exam;
import com.java.rule38.Test;

import java.util.List;

public class Main {
    public static void main(String[] args){
        int a[] = null;

        List<Integer> L = Exam.intArrayAsList(a);

        Test.print(L);
    }
}
