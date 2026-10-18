package com.parqueadero.app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import java.time.LocalDate;
import java.util.Calendar;

public class MensualidadActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private LocalDate fechaSeleccionada = LocalDate.now(); // Hoy por defecto

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensualidad);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        EditText etPlaca = findViewById(R.id.etPlacaMensual);
        EditText etTelefono = findViewById(R.id.etTelefono);
        TextView tvFecha = findViewById(R.id.tvFechaInicio);
        Button btnGuardar = findViewById(R.id.btnGuardarMensual);
        Button btnVolver = findViewById(R.id.btnVolverMensual);

        tvFecha.setText("INICIO: " + fechaSeleccionada.toString() + " (Tocar)");

        // Abrir calendario al tocar la fecha
        tvFecha.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                fechaSeleccionada = LocalDate.of(year, month + 1, dayOfMonth);
                tvFecha.setText("INICIO: " + fechaSeleccionada.toString() + " (Tocar)");
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnGuardar.setOnClickListener(v -> {
            String placa = etPlaca.getText().toString().trim();
            String tel = etTelefono.getText().toString().trim();

            if (placa.isEmpty()) {
                Toast.makeText(this, "Escribe la placa primero", Toast.LENGTH_SHORT).show();
                return;
            }

            // Consultamos al cerebro si hay una mensualidad activa ANTES de guardar
            viewModel.verificarMensualidadActiva(placa, (activa, fechaVence) -> {
                runOnUiThread(() -> {
                    if (activa) {
                        // Si está activa, lanzamos la advertencia
                        new android.app.AlertDialog.Builder(this)
                                .setTitle("Mensualidad Activa")
                                .setMessage("La placa " + placa + " ya tiene un mes pagado y vigente hasta el " + fechaVence + ". ¿Deseas sobreescribir este pago y renovarlo de todos modos?")
                                .setPositiveButton("SÍ, RENOVAR", (dialog, which) -> {
                                    viewModel.registrarNuevaMensualidad(placa, tel, fechaSeleccionada);
                                    Toast.makeText(this, "Renovación forzada guardada", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .setNegativeButton("CANCELAR", null)
                                .show();
                    } else {
                        // Si no está activa o es nuevo, guardamos directo
                        viewModel.registrarNuevaMensualidad(placa, tel, fechaSeleccionada);
                        Toast.makeText(this, "Mensualidad guardada exitosamente", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            });
        });

        btnVolver.setOnClickListener(v -> finish());
    }
}