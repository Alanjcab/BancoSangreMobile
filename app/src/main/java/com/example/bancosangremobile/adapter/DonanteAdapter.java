package com.example.bancosangremobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bancosangremobile.R;
import com.example.bancosangremobile.model.Donante;

import java.util.List;

public class DonanteAdapter
        extends RecyclerView.Adapter<DonanteAdapter.DonanteViewHolder> {

    private final List<Donante> donantes;
    private final OnDonanteClickListener listener;

    public interface OnDonanteClickListener {
        void onDonanteClick(Donante donante);
    }

    public DonanteAdapter(List<Donante> donantes,
                          OnDonanteClickListener listener) {
        this.donantes = donantes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DonanteViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_donante, parent, false);

        return new DonanteViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(
            @NonNull DonanteViewHolder holder, int position) {

        Donante donante = donantes.get(position);

        holder.txtNombre.setText(donante.getNombre());
        holder.txtDni.setText("DNI " + donante.getDni());
        holder.txtGrupo.setText(donante.getGrupoSanguineo());
        holder.txtEstado.setText(donante.getEstado());

        //color de la etique dependiendo del estado
        if ("Apto".equalsIgnoreCase(donante.getEstado())) {

            holder.txtEstado.setTextColor(0xFF16875D);
            holder.txtEstado.setBackgroundResource(
                    R.drawable.bg_estado_apto
            );

        } else {

            holder.txtEstado.setTextColor(0xFFD48806);
            holder.txtEstado.setBackgroundResource(
                    R.drawable.bg_home_card
            );
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDonanteClick(donante);
            }
        });
    }

    @Override
    public int getItemCount() {
        return donantes.size();
    }

    static class DonanteViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtNombre;
        TextView txtDni;
        TextView txtGrupo;
        TextView txtEstado;

        public DonanteViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNombre = itemView.findViewById(
                    R.id.txtNombreDonante
            );

            txtDni = itemView.findViewById(
                    R.id.txtDniDonante
            );

            txtGrupo = itemView.findViewById(
                    R.id.txtGrupoSanguineo
            );

            txtEstado = itemView.findViewById(
                    R.id.txtEstadoDonante
            );
        }
    }
}

