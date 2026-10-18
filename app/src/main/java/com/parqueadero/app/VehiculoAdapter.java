package com.parqueadero.app;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class VehiculoAdapter extends RecyclerView.Adapter<VehiculoAdapter.ViewHolder> {

    private List<Registro> listaCompleta = new ArrayList<>();
    private List<Registro> listaFiltrada = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(Registro registro);
    }
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setRegistros(List<Registro> registros) {
        this.listaCompleta = registros;
        this.listaFiltrada = new ArrayList<>(registros);
        notifyDataSetChanged();
    }

    public void aplicarFiltro(String criterio) {
        listaFiltrada.clear();
        if (criterio.equals("TODOS")) {
            listaFiltrada.addAll(listaCompleta);
        } else if (criterio.equals("VENCIDO")) {
            for (Registro r : listaCompleta) if (r.estado.equals("VENCIDO")) listaFiltrada.add(r);
        } else {
            for (Registro r : listaCompleta) if (r.tipo.equals(criterio)) listaFiltrada.add(r);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vehiculo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Registro reg = listaFiltrada.get(position);
        holder.tvPlaca.setText(reg.placa + " (" + reg.tipo + ")");
        holder.tvDetalle.setText("Estado: " + reg.estado + "\n" + reg.detalleFecha);

        // Colores según estado
        holder.tvPlaca.setTextColor(reg.estado.equals("VENCIDO") ? Color.RED : Color.parseColor("#4CAF50"));

        // Preparar el clic para la futura ventana de Edición
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(reg);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaFiltrada.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlaca, tvDetalle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlaca = itemView.findViewById(R.id.tvItemPlaca);
            tvDetalle = itemView.findViewById(R.id.tvItemDetalle);
        }
    }
}