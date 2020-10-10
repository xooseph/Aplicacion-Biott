package com.example.biott;

public enum FunctionType {
    HERVIR("Hervir", 0),
    FREIR("Freir",1),
    HEAT("",2),
    SOPA("",3),
    AGUA("",4),
    LECHE("",5);

    private final String text;
    private final int resource;

    FunctionType(String text, int resource) {
        this.text = text;
        this.resource = resource;
    }

    public String getText() {
        return text;
    }
    public int getResource() {
        return resource;
    }

    public static String[] getNames() {
        String[] resultado = new String[FunctionType.values().length];
        for (FunctionType tipo : FunctionType.values()) {
            resultado[tipo.ordinal()] = tipo.text;
        }
        return resultado;
    }
}
