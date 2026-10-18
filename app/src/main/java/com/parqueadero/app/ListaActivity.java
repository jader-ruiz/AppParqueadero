package com.parqueadero.app;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.Calendar;

public class ListaActivity extends AppCompatActivity {

    private VehiculoAdapter adapter;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        RecyclerView rvVehiculos = findViewById(R.id.rvVehiculos);
        rvVehiculos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VehiculoAdapter();
        rvVehiculos.setAdapter(adapter);

        // Cuando toquen un vehículo, abrimos el pop-up
        adapter.setOnItemClickListener(this::mostrarDialogoEdicion);

        viewModel.getListaVehiculos().observe(this, lista -> adapter.setRegistros(lista));
        viewModel.cargarVehiculosAdentro();

        findViewById(R.id.btnFiltroTodos).setOnClickListener(v -> adapter.aplicarFiltro("TODOS"));
        findViewById(R.id.btnFiltroDia).setOnClickListener(v -> adapter.aplicarFiltro("DÍA"));
        findViewById(R.id.btnFiltroMensual).setOnClickListener(v -> adapter.aplicarFiltro("MENSUALIDAD"));
        findViewById(R.id.btnFiltroVencido).setOnClickListener(v -> adapter.aplicarFiltro("VENCIDO"));
        findViewById(R.id.btnVolverLista).setOnClickListener(v -> finish());
    }

    private void mostrarDialogoEdicion(Registro registro) {
        View view = android.view.LayoutInflater.from(this).inflate(R.layout.dialog_editar_registro, null);

        android.widget.EditText etPlaca = view.findViewById(R.id.etEditarPlaca);
        android.widget.EditText etTelefono = view.findViewById(R.id.etEditarTelefono);
        android.widget.TextView tvFecha = view.findViewById(R.id.tvEditarFecha);
        android.widget.Button btnCancelar = view.findViewById(R.id.btnCancelarEdicion);
        android.widget.Button btnGuardar = view.findViewById(R.id.btnGuardarEdicion);

        etPlaca.setText(registro.placa);
        etTelefono.setText(registro.telefono);

        if (registro.tipo.equals("DÍA")) {
            etTelefono.setVisibility(View.GONE);
            tvFecha.setVisibility(View.GONE);
        }

        final java.time.LocalDate[] fechaInicioSeleccionada = {java.time.LocalDate.now()};

        if (registro.tipo.equals("MENSUALIDAD")) {
            try {
                // Extraemos el vencimiento de la base de datos y le restamos 1 mes para mostrar el Inicio
                String fechaString = registro.detalleFecha.replace("Vence: ", "").trim();
                java.time.LocalDate fechaVencimientoActual = java.time.LocalDate.parse(fechaString);
                fechaInicioSeleccionada[0] = fechaVencimientoActual.minusMonths(1);
            } catch (Exception e) {}
            tvFecha.setText("INICIO: " + fechaInicioSeleccionada[0].toString() + " (Tocar)");
        }

        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create();

        tvFecha.setOnClickListener(v -> {
            // Hacemos que el calendario se abra en la fecha de inicio calculada, no en la de hoy
            int year = fechaInicioSeleccionada[0].getYear();
            int month = fechaInicioSeleccionada[0].getMonthValue() - 1;
            int day = fechaInicioSeleccionada[0].getDayOfMonth();

            new android.app.DatePickerDialog(this, (vista, y, m, d) -> {
                fechaInicioSeleccionada[0] = java.time.LocalDate.of(y, m + 1, d);
                tvFecha.setText("INICIO: " + fechaInicioSeleccionada[0].toString() + " (Tocar)");
            }, year, month, day).show();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {
            String placaNueva = etPlaca.getText().toString().trim();
            if (placaNueva.isEmpty()) {
                android.widget.Toast.makeText(this, "La placa no puede estar vacía", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            // LA MAGIA: Al guardar, tomamos el inicio elegido y le sumamos 1 mes automáticamente
            java.time.LocalDate fechaVencimientoCalculada = fechaInicioSeleccionada[0].plusMonths(1);

            viewModel.actualizarRegistro(
                    registro.placa,
                    placaNueva,
                    etTelefono.getText().toString(),
                    fechaVencimientoCalculada,
                    registro.tipo
            );
            android.widget.Toast.makeText(this, "Registro actualizado", android.widget.Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        android.widget.Button btnEliminar = view.findViewById(R.id.btnEliminarEdicion);

        btnEliminar.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Advertencia Crítica")
                    .setMessage("¿Estás seguro de que quieres borrar el registro de la placa " + registro.placa + " del sistema? Esta acción no se puede deshacer.")
                    .setPositiveButton("SÍ, BORRAR", (d, w) -> {
                        viewModel.eliminarRegistro(registro.placa, registro.tipo);
                        android.widget.Toast.makeText(this, "Placa eliminada definitivamente", android.widget.Toast.LENGTH_SHORT).show();
                        dialog.dismiss(); // Cierra el pop-up
                    })
                    .setNegativeButton("CANCELAR", null)
                    .show();
        });

        dialog.show();
    }
}