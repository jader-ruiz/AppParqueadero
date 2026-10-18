package com.parqueadero.app.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface ParqueaderoDao {

    // --- ÓRDENES PARA MENSUALIDADES ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void guardarMensualidad(Mensualidad mensualidad);

    @Query("SELECT * FROM mensualidades WHERE placa = :placa LIMIT 1")
    Mensualidad buscarMensualidad(String placa);


    // --- ÓRDENES PARA EL REGISTRO POR DÍA ---
    @Insert
    void registrarEntrada(Estancia estancia);

    @Update
    void actualizarEstancia(Estancia estancia);

    @Query("SELECT * FROM estancias WHERE estaAdentro = 1 ORDER BY horaEntrada DESC")
    List<Estancia> obtenerVehiculosAdentro();

    @Query("SELECT * FROM estancias WHERE placa = :placa AND estaAdentro = 1 LIMIT 1")
    Estancia buscarEstanciaActiva(String placa);

    @Query("SELECT * FROM mensualidades")
    List<Mensualidad> obtenerTodasMensualidades();

    @Query("SELECT * FROM estancias")
    List<Estancia> obtenerTodasEstancias();

    @androidx.room.Delete
    void eliminarMensualidad(Mensualidad mensualidad);

    @Query("DELETE FROM mensualidades WHERE placa = :placa")
    void eliminarMensualidadPorPlaca(String placa);

    @Query("DELETE FROM estancias WHERE placa = :placa")
    void eliminarEstanciasPorPlaca(String placa);

    @Query("SELECT * FROM estancias WHERE placa = :placa ORDER BY horaEntrada DESC LIMIT 1")
    Estancia buscarUltimaEstancia(String placa);
}