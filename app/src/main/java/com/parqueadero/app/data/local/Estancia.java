package com.parqueadero.app.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.time.LocalDateTime;

@Entity(tableName = "estancias")
public class Estancia {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String placa;
    private boolean esMensualidad;

    private LocalDateTime horaEntrada;
    private LocalDateTime horaSalida;

    private LocalDateTime vencimientoDia;
    private boolean estaAdentro;

    public Estancia(String placa, boolean esMensualidad, LocalDateTime horaEntrada) {
        this.placa = placa.toUpperCase();
        this.esMensualidad = esMensualidad;
        this.horaEntrada = horaEntrada;
        this.estaAdentro = true;

        // Si es cobro por día, se le suman 24 horas automáticamente
        if (!esMensualidad) {
            this.vencimientoDia = horaEntrada.plusHours(24);
        }
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public boolean isEsMensualidad() { return esMensualidad; }
    public void setEsMensualidad(boolean esMensualidad) { this.esMensualidad = esMensualidad; }

    public LocalDateTime getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(LocalDateTime horaEntrada) { this.horaEntrada = horaEntrada; }

    public LocalDateTime getHoraSalida() { return horaSalida; }
    public void setHoraSalida(LocalDateTime horaSalida) { this.horaSalida = horaSalida; }

    public LocalDateTime getVencimientoDia() { return vencimientoDia; }
    public void setVencimientoDia(LocalDateTime vencimientoDia) { this.vencimientoDia = vencimientoDia; }

    public boolean isEstaAdentro() { return estaAdentro; }
    public void setEstaAdentro(boolean estaAdentro) { this.estaAdentro = estaAdentro; }
}