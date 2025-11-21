package com.example.petpawcalendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petpawcalendar.network.dto.MascotaRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MascotaAdapter extends RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder> {

    public interface OnMascotaClickListener {
        void onMascotaClick(MascotaRequest mascota);

        void onEliminarClick(MascotaRequest mascota);
    }

    private final Context context;
    private final OnMascotaClickListener listener;
    private List<MascotaRequest> lista;

    public MascotaAdapter(Context context, OnMascotaClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.lista = new ArrayList<>();
    }

    public void setLista(List<MascotaRequest> nuevaLista) {
        if (nuevaLista != null) {
            this.lista = nuevaLista;
        } else {
            this.lista = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MascotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_mascota, parent, false);
        return new MascotaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MascotaViewHolder holder, int position) {
        final MascotaRequest m = lista.get(position);

        // Nombre (sin sexo, como comentaste)
        holder.txtNombre.setText(m.getNombre());

        holder.txtRaza.setText(m.getRaza());
        holder.txtEspecie.setText(m.getEspecie());

        String fechaNacimiento = m.getFechaNacimiento();
        if (fechaNacimiento != null && !fechaNacimiento.isEmpty()) {
            holder.txtFecha.setText(fechaNacimiento);
        } else {
            holder.txtFecha.setText("-");
        }

        BigDecimal peso = m.getPesoKg();
        if (peso != null) {
            holder.txtPeso.setText(peso.toPlainString() + " kg");
        } else {
            holder.txtPeso.setText("- kg");
        }

        // ---- Foto de la mascota ----
        String fotoUrl = normalizarUrl(m.getFotoUrl());

        if (fotoUrl == null || fotoUrl.trim().isEmpty()) {
            holder.imgMascota.setImageResource(android.R.drawable.ic_menu_gallery);
        } else {
            Glide.with(context)
                    .load(fotoUrl)           // <-- usa la URL normalizada
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .circleCrop()
                    .into(holder.imgMascota);
        }

        // Click en el card
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMascotaClick(m);
            }
        });

        // Click en eliminar
        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEliminarClick(m);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class MascotaViewHolder extends RecyclerView.ViewHolder {

        ImageView imgMascota;
        ImageView btnEliminar;
        TextView txtNombre;
        TextView txtRaza;
        TextView txtEspecie;
        TextView txtFecha;
        TextView txtPeso;

        MascotaViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMascota = itemView.findViewById(R.id.imgMascota);
            btnEliminar = itemView.findViewById(R.id.btnEliminarMascota);
            txtNombre = itemView.findViewById(R.id.txtNombreMascota);
            txtRaza = itemView.findViewById(R.id.txtRazaMascota);
            txtEspecie = itemView.findViewById(R.id.txtEspecieChip);
            txtFecha = itemView.findViewById(R.id.txtFechaNacimientoMascota);
            txtPeso = itemView.findViewById(R.id.txtPesoMascota);
        }
    }

    /**
     * Cambia "http://localhost:8080" por "http://10.0.2.2:8080"
     * para que el emulador pueda acceder al backend.
     */
    private String normalizarUrl(String url) {
        if (url == null) return null;

        return url
                .replace("http://localhost:8080", "http://10.0.2.2:8080")
                .replace("https://localhost:8080", "http://10.0.2.2:8080");
    }
}
