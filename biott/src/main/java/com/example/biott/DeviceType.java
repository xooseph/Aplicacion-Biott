package com.example.biott;

public enum DeviceType {
    INDUCTIONSTOVE("Parrilla de Induccion", 0);

    private final String text;
    private final int resource;

    DeviceType(String text, int resource) {
        this.text = text;
        this.resource = resource;
    }

    public String getText() {
        return text;
    }

    public static String[] getNames() {
        String[] resultado = new String[DeviceType.values().length];
        for (DeviceType tipo : DeviceType.values()) {
            resultado[tipo.ordinal()] = tipo.text;
        }
        return resultado;
    }
}