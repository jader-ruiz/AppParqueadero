package com.parqueadero.app;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.parqueadero.app.data.repository.EstadoVehiculo;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;

    private EditText etPlaca;
    private Button btnBuscar, btnIngresoDia, btnSalida;
    private TextView tvMensajeSemaforo;
    private View tarjetaSemaforo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vincularVistas();
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        configurarObservadores();
        configurarBotones();
    }

    private void vincularVistas() {
        etPlaca = findViewById(R.id.etPlaca);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnIngresoDia = findViewById(R.id.btnIngresoDia);
        tvMensajeSemaforo = findViewById(R.id.tvMensajeSemaforo);
        tarjetaSemaforo = findViewById(R.id.tarjetaSemaforo);
    }

    private void configurarBotones() {
        btnBuscar.setOnClickListener(v -> {
            String placa = etPlaca.getText().toString();
            viewModel.buscarPlaca(placa);
        });

        btnIngresoDia.setOnClickListener(v -> {
            String placa = etPlaca.getText().toString();
            viewModel.registrarIngresoInteligente(placa);
            limpiarCampoPlaca();
        });


        Button btnVerLista = findViewById(R.id.btnVerLista);
        btnVerLista.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainActivity.this, ListaActivity.class);
            startActivity(intent);
        });

        // Cuando hace clic en el botón morado de Mensualidades
        Button btnMensualidades = findViewById(R.id.btnMensualidades);
        btnMensualidades.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainActivity.this, MensualidadActivity.class);
            startActivity(intent);
        });
    }

    private void configurarObservadores() {
        viewModel.getMensajePantalla().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String mensaje) {
                tvMensajeSemaforo.setText(mensaje);
            }
        });

        viewModel.getEstadoSemaforo().observe(this, new Observer<EstadoVehiculo>() {
            @Override
            public void onChanged(EstadoVehiculo estado) {
                switch (estado) {
                    case VERDE_AL_DIA:
                        tarjetaSemaforo.setBackgroundColor(Color.parseColor("#4CAF50"));
                        tvMensajeSemaforo.setTextColor(Color.WHITE);
                        break;
                    case ROJO_VENCIDO:
                        tarjetaSemaforo.setBackgroundColor(Color.parseColor("#F44336"));
                        tvMensajeSemaforo.setTextColor(Color.WHITE);
                        break;
                    case NO_REGISTRADO:
                    default:
                        tarjetaSemaforo.setBackgroundColor(Color.parseColor("#E0E0E0"));
                        tvMensajeSemaforo.setTextColor(Color.BLACK);
                        break;
                }
            }
        });
    }

    private void limpiarCampoPlaca() {
        etPlaca.setText("");
        etPlaca.requestFocus();
        Toast.makeText(this, "Acción guardada", Toast.LENGTH_SHORT).show();
    }
}