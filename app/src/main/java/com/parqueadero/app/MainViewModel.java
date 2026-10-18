package com.parqueadero.app;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.parqueadero.app.data.repository.EstadoVehiculo;
import com.parqueadero.app.data.repository.ParqueaderoRepository;

public class MainViewModel extends AndroidViewModel {

    private ParqueaderoRepository repository;

    private MutableLiveData<EstadoVehiculo> estadoSemaforo = new MutableLiveData<>();
    private MutableLiveData<String> mensajePantalla = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new ParqueaderoRepository(application);
    }

    public LiveData<EstadoVehiculo> getEstadoSemaforo() {
        return estadoSemaforo;
    }

    public LiveData<String> getMensajePantalla() {
        return mensajePantalla;
    }

    public void buscarPlaca(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            mensajePantalla.postValue("Por favor ingrese una placa válida");
            return;
        }

        repository.consultarEstadoPlaca(placa, (estado, mensajeExtra) -> {
            estadoSemaforo.postValue(estado);
            mensajePantalla.postValue(mensajeExtra);
        });
    }

    public void registrarIngresoInteligente(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            mensajePantalla.postValue("Por favor ingrese una placa");
            return;
        }

        repository.registrarIngresoInteligente(placa, (estado, mensajeExtra) -> {
            estadoSemaforo.postValue(estado);
            mensajePantalla.postValue(mensajeExtra);
        });
    }


    // "Caja" que guardará la lista de vehículos para que la pantalla la observe
    private MutableLiveData<java.util.List<Registro>> listaVehiculos = new MutableLiveData<>();

    public LiveData<java.util.List<Registro>> getListaVehiculos() { return listaVehiculos; }

    public void cargarVehiculosAdentro() {
        repository.obtenerTodosLosRegistros(lista -> listaVehiculos.postValue(lista));
    }

    public void registrarNuevaMensualidad(String placa, String telefono, java.time.LocalDate fechaInicio) {
        if (placa == null || placa.trim().isEmpty()) {
            mensajePantalla.postValue("La placa es obligatoria");
            return;
        }
        repository.registrarMensualidad(placa, telefono, fechaInicio, (estado, mensajeExtra) -> {
            estadoSemaforo.postValue(estado);
            mensajePantalla.postValue(mensajeExtra);
        });
    }

    public void actualizarRegistro(String placaAntigua, String placaNueva, String telefono, java.time.LocalDate nuevaFecha, String tipo) {
        repository.actualizarRegistro(placaAntigua, placaNueva, telefono, nuevaFecha, tipo);
        // Recargamos la lista para que la pantalla vea los cambios de inmediato
        cargarVehiculosAdentro();
    }

    public void verificarMensualidadActiva(String placa, ParqueaderoRepository.VerificacionCallback callback) {
        repository.verificarMensualidadActiva(placa, callback);
    }

    public void eliminarRegistro(String placa, String tipo) {
        repository.eliminarRegistro(placa, tipo);
        cargarVehiculosAdentro(); // Refresca la lista inmediatamente
    }
}