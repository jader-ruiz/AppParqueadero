package com.parqueadero.app.data.repository;

import android.app.Application;
import com.parqueadero.app.data.local.Estancia;
import com.parqueadero.app.data.local.Mensualidad;
import com.parqueadero.app.data.local.ParqueaderoDao;
import com.parqueadero.app.data.local.ParqueaderoDatabase;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParqueaderoRepository {

    private ParqueaderoDao dao;
    private ExecutorService executor;

    public interface EstadoCallback { void onResultado(EstadoVehiculo estado, String mensajeExtra); }
    public interface ListaUnificadaCallback { void onResultado(java.util.List<com.parqueadero.app.Registro> lista); }
    public interface VerificacionCallback { void onResultado(boolean activa, String fechaVencimiento); }

    public ParqueaderoRepository(Application application) {
        ParqueaderoDatabase db = ParqueaderoDatabase.getInstance(application);
        this.dao = db.parqueaderoDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    // LÓGICA NUEVA: Solo informa si la suscripción o el pase es válido, sin importar si está adentro o afuera
    public void consultarEstadoPlaca(String placa, EstadoCallback callback) {
        executor.execute(() -> {
            Mensualidad mensualidad = dao.buscarMensualidad(placa);
            if (mensualidad != null) {
                if (mensualidad.getFechaVencimiento().isBefore(LocalDate.now())) {
                    callback.onResultado(EstadoVehiculo.ROJO_VENCIDO, "MENSUALIDAD VENCIDA (Venció el: " + mensualidad.getFechaVencimiento() + ")");
                } else {
                    callback.onResultado(EstadoVehiculo.VERDE_AL_DIA, "MENSUALIDAD VIGENTE (Vence el: " + mensualidad.getFechaVencimiento() + ")");
                }
                return;
            }

            Estancia ultimaEstancia = dao.buscarUltimaEstancia(placa);
            if (ultimaEstancia != null && !ultimaEstancia.isEsMensualidad()) {
                if (ultimaEstancia.getVencimientoDia().isBefore(LocalDateTime.now())) {
                    callback.onResultado(EstadoVehiculo.ROJO_VENCIDO, "PASE DE DÍA VENCIDO (Cumplió 24h)");
                } else {
                    callback.onResultado(EstadoVehiculo.VERDE_AL_DIA, "PASE DE DÍA VIGENTE (24h activas)");
                }
                return;
            }

            callback.onResultado(EstadoVehiculo.NO_REGISTRADO, "NO REGISTRADO O SIN PASES");
        });
    }

    public void registrarIngresoInteligente(String placa, EstadoCallback callback) {
        executor.execute(() -> {
            Mensualidad m = dao.buscarMensualidad(placa);
            if (m != null && !m.getFechaVencimiento().isBefore(LocalDate.now())) {
                callback.onResultado(EstadoVehiculo.VERDE_AL_DIA, "TIENE MENSUALIDAD (No es necesario cobrar día)");
                return;
            }
            Estancia est = new Estancia(placa, false, LocalDateTime.now());
            dao.registrarEntrada(est);
            callback.onResultado(EstadoVehiculo.VERDE_AL_DIA, "PASE DE DÍA (24H) GUARDADO EXITOSAMENTE");
        });
    }

    public void registrarMensualidad(String placa, String telefono, LocalDate fechaInicio, EstadoCallback callback) {
        executor.execute(() -> {
            Mensualidad m = dao.buscarMensualidad(placa);
            if (m != null) {
                m.setFechaVencimiento(fechaInicio.plusMonths(1));
                m.setTelefono(telefono);
                dao.guardarMensualidad(m);
            } else {
                Mensualidad nueva = new Mensualidad(placa, fechaInicio.plusMonths(1), telefono);
                dao.guardarMensualidad(nueva);
            }
            callback.onResultado(EstadoVehiculo.VERDE_AL_DIA, "MENSUALIDAD GUARDADA. Vence: " + fechaInicio.plusMonths(1));
        });
    }

    public void verificarMensualidadActiva(String placa, VerificacionCallback callback) {
        executor.execute(() -> {
            Mensualidad m = dao.buscarMensualidad(placa);
            if (m != null && !m.getFechaVencimiento().isBefore(LocalDate.now())) {
                callback.onResultado(true, m.getFechaVencimiento().toString());
            } else {
                callback.onResultado(false, "");
            }
        });
    }

    public void obtenerTodosLosRegistros(ListaUnificadaCallback callback) {
        executor.execute(() -> {
            java.util.List<Mensualidad> mensualidades = dao.obtenerTodasMensualidades();
            java.util.List<Estancia> estancias = dao.obtenerTodasEstancias();
            java.util.List<com.parqueadero.app.Registro> listaUnificada = new java.util.ArrayList<>();

            for(Mensualidad m : mensualidades) {
                String estado = m.getFechaVencimiento().isBefore(LocalDate.now()) ? "VENCIDO" : "VIGENTE";
                listaUnificada.add(new com.parqueadero.app.Registro(m.getPlaca(), "MENSUALIDAD", estado, "Vence: " + m.getFechaVencimiento(), m.getTelefono()));
            }
            for(Estancia e : estancias) {
                if (!e.isEsMensualidad()) {
                    String estado = e.getVencimientoDia().isBefore(LocalDateTime.now()) ? "VENCIDO" : "VIGENTE";
                    java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yy hh:mm a");
                    listaUnificada.add(new com.parqueadero.app.Registro(e.getPlaca(), "DÍA", estado, "Ingreso: " + e.getHoraEntrada().format(fmt), ""));
                }
            }
            callback.onResultado(listaUnificada);
        });
    }

    public void actualizarRegistro(String placaAntigua, String placaNueva, String telefono, LocalDate nuevaFecha, String tipo) {
        executor.execute(() -> {
            if (tipo.equals("MENSUALIDAD")) {
                Mensualidad m = dao.buscarMensualidad(placaAntigua);
                if (m != null) {
                    dao.eliminarMensualidad(m);
                    Mensualidad actualizada = new Mensualidad(placaNueva.toUpperCase(), nuevaFecha, telefono);
                    dao.guardarMensualidad(actualizada);
                }
            } else if (tipo.equals("DÍA")) {
                Estancia e = dao.buscarUltimaEstancia(placaAntigua);
                if (e != null) {
                    e.setPlaca(placaNueva.toUpperCase());
                    dao.actualizarEstancia(e);
                }
            }
        });
    }

    public void eliminarRegistro(String placa, String tipo) {
        executor.execute(() -> {
            if (tipo.equals("MENSUALIDAD")) {
                dao.eliminarMensualidadPorPlaca(placa);
            } else if (tipo.equals("DÍA")) {
                dao.eliminarEstanciasPorPlaca(placa);
            }
        });
    }
}