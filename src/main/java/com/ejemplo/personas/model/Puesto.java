package com.ejemplo.personas.model;

public enum Puesto {
    ANALISTA("Analista"),
    DESARROLLADOR("Desarrollador"),
    SOPORTE("Soporte"),
    JEFATURA("Jefatura");

    private final String etiqueta;

    Puesto(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}