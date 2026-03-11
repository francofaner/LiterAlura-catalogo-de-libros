package com.franco.literalura.model;

public enum Idioma {
    ES("es", "Español"),
    EN("en", "Inglés"),
    FR("fr", "Francés"),
    PT("pt", "Portugués");

    private final String codigo;
    private final String descripcion;

    Idioma(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static Idioma desdeCodigo(String codigo) {
        for (Idioma idioma : Idioma.values()) {
            if (idioma.codigo.equalsIgnoreCase(codigo)) {
                return idioma;
            }
        }
        return null;
    }
}