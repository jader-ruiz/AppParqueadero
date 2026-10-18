package com.parqueadero.app.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.time.LocalDate;

@Entity(tableName = "mensualidades")
public class Mensualidad {

    @PrimaryKey
    @NonNull
    private String placa;

    private LocalDate fechaVencimiento;
    private String telefono;

    public Mensualidad(@NonNull String placa, LocalDate fechaVencimiento, String telefono) {
        this.placa = placa.toUpperCase();
        this.fechaVencimiento = fechaVencimiento;
        this.telefono = telefono;
    }

    @NonNull
    public String getPlaca() { return placa; }
    public void setPlaca(@NonNull String placa) { this.placa = placa; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}