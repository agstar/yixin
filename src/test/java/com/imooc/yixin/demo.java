package com.imooc.yixin;

import java.util.Stack;

public class demo {


    public String huiwen(String str) {
        String right = "Right";
        String wrong = "Wrong";
        Stack stack = new Stack<Character>();
        char[] chars = str.toCharArray();
        for (char aChar : chars) {
            stack.push(aChar);
        }
        String ishuiwen = right;
        for (char aChar : chars) {
            Character pop = (Character) stack.pop();
            if (!pop.equals(aChar)) {
                ishuiwen = wrong;
                break;
            }
        }
        return ishuiwen;
    }

}
