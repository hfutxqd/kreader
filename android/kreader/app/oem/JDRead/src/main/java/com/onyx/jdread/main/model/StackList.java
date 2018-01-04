package com.onyx.jdread.main.model;

import java.util.LinkedList;

/**
 * Created by huxiaomao on 2017/12/9.
 */

public class StackList {
    private LinkedList<String> stack = new LinkedList<>();

    public void push(String childViewName){
        stack.addFirst(childViewName);
    }

    public String peek(){
        return stack.getFirst();
    }


    public String pop(){
        return stack.removeFirst();
    }

    public boolean empty(){
        return stack.isEmpty();
    }

    public String popChildView(){
        if(stack.size() <= 1){
            return stack.peek();
        }
        stack.pop();
        return stack.peek();
    }

    public String toString(){
        return stack.toString();
    }
}