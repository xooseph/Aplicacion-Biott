package com.example.biott;

import java.util.Vector;

public class Function {
    private String name;
    private FunctionType type;

    public Function(){

    }


    public Function(String name,FunctionType type){
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FunctionType getType() {
        return type;
    }

    public void setType(FunctionType type) {
        this.type = type;
    }

    public static Vector<Function> demoFunction() {
        Vector<Function> functions = new Vector<Function>();

        functions.add(new Function(" Hervir Agua", FunctionType.HERVIR));
        functions.add(new Function("Freir",FunctionType.FREIR));
        return functions;
    }
}
