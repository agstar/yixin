package com.imooc.yixin;

import java.util.ArrayList;

public class Hexadecimal {


    /**
     * 10进制转其他进制
     */
    public static String conversion(long number, int hex) {
        if (number == 0) {
            return "0";
        }
        Stack<Long> stack = new Stack<>(100);
        while (number != 0) {
            long mod = number % hex;
            number = number / hex;
            stack.push(mod);
        }
        ArrayList<Long> list = stack.popAll();
        StringBuilder sb = new StringBuilder();
        for (Long aLong : list) {
            if (aLong >= 10L) {
                char c = (char) ('a' + aLong - 10);
                sb.append(c);
            } else {
                sb.append(aLong);
            }
        }
        return sb.toString();
    }


    public static void main(String[] args) {
        System.out.println("1348转为2进制为：" + Hexadecimal.conversion(1348, 2));
        System.out.println("1348转为8进制为：" + Hexadecimal.conversion(1348, 8));
        System.out.println("1348转为16进制为：" + Hexadecimal.conversion(1348, 16));
        System.out.println("234211转为2进制为：" + Hexadecimal.conversion(23411, 2));
        System.out.println("234211转为8进制为：" + Hexadecimal.conversion(23411, 8));
        System.out.println("234211转为16进制为：" + Hexadecimal.conversion(23411, 16));
        System.out.println("0转为2进制为：" + Hexadecimal.conversion(0, 2));
        System.out.println("0转为8进制为：" + Hexadecimal.conversion(0, 8));
        System.out.println("0转为16进制为：" + Hexadecimal.conversion(0, 16));
    }

}
