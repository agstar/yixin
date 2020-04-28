```
package com.imooc.muxin;

import java.util.ArrayList;

public class Stack<T> {
    public static int size = 10;
    /*栈顶元素索引*/
    private int top = -1;
    private ArrayList<T> data;
    /*栈初始化长度*/
    private int length;

    public Stack(int length) {
        if (length <= 0) {
            throw new RuntimeException("初始化栈长度必须大于0");
        }
        this.length = length;
        this.data = new ArrayList<>(length);
    }

    public Stack() {
        this.length = size;
        this.data = new ArrayList<>(length);
    }

    /**
     * 判断栈是否空
     */
    public boolean isEmpty() {
        return top < 0;
    }

    /**
     * 判断栈是否满
     */
    public boolean isFull() {
        return top == length - 1;
    }

    /**
     * 向栈中去除元素
     */
    public T pop() {
        if (top < 0) {
            throw new RuntimeException("栈已空");
        }
        T t = data.get(top);
        data.remove(top);
        top--;
        return t;
    }

    /**
     * 向栈中插入元素
     */
    public void push(T node) {
        if (top >= length - 1) {
            throw new RuntimeException("栈已满");
        }
        data.add(++top, node);
    }

    /**
     * 打印栈中的内容
     */
    public void printStack() {
        if (top > -1) {
            for (int i = 0; i <= top; i++) {
                System.out.print(data.get(i) + "\t");
            }
            System.out.println();
        } else {
            System.out.println("栈中元素为空");
        }
    }

    public static void main(String[] args) {
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < 10; i++) {
            stack.push(i + 1);
        }
        System.out.println("栈是否满" + stack.isFull());
        stack.printStack();
        for (int i = 0; i < 5; i++) {
            System.out.println(stack.pop());
        }
        System.out.println("栈是否空" + stack.isEmpty());
        stack.printStack();
    }


}
```
