package com.parqueadero.app.data.repository;

public enum EstadoVehiculo {
    VERDE_AL_DIA,   // Mensualidad vigente o pase de 24h dentro del tiempo
    ROJO_VENCIDO,   // Mensualidad expirada o pasaron más de 24h
    NO_REGISTRADO   // Placa nueva, no está parqueada ni tiene mensualidad
}