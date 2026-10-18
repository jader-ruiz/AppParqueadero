package com.parqueadero.app;

public class Registro {
    public String placa;
    public String tipo; // "MENSUALIDAD" o "DÍA"
    public String estado; // "VIGENTE" o "VENCIDO"
    public String detalleFecha;
    public String telefono;

    public Registro(String placa, String tipo, String estado, String detalleFecha, String telefono) {
        this.placa = placa;
        this.tipo = tipo;
        this.estado = estado;
        this.detalleFecha = detalleFecha;
        this.telefono = telefono;
    }
}