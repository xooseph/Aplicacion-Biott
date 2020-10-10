package com.example.biott;

import java.util.ArrayList;
import java.util.List;

public class Functions {
    protected static List<Function> functionVector = demoFunction();

    public Functions(){
        functionVector = demoFunction();
    }

    static Function element(int id) {
        return functionVector.get(id);
    }

    static void add(Function function) {
        functionVector.add(function);
    }

    static int newFunction() {
        Function function = new Function();
        functionVector.add(function);
        return functionVector.size() - 1;
    }

    public static void erase(int id) {
        functionVector.remove(id);
    }

    public static int size() {
        return functionVector.size();
    }

    public static ArrayList<Function> demoFunction() {
        ArrayList<Function> functions = new ArrayList<Function>();
        return functions;
    }
}
