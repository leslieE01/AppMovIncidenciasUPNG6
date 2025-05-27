package com.upn.appincidenciasupn_jm2.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.upn.appincidenciasupn_jm2.Entidades.Supervisor;
import com.upn.appincidenciasupn_jm2.R;

import java.util.List;

public class SupervisorAdapter extends RecyclerView.Adapter<SupervisorAdapter.SupervisorViewHolder> {

    private List<Supervisor> listaSupervisores;
    private OnSupervisorClickListener listener;

    public interface OnSupervisorClickListener {
        void onAsignarClick(Supervisor supervisor);
    }

    public SupervisorAdapter(List<Supervisor> listaSupervisores, OnSupervisorClickListener listener) {
        this.listaSupervisores = listaSupervisores;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SupervisorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_supervisor, parent, false);
        return new SupervisorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SupervisorViewHolder holder, int position) {
        Supervisor supervisor = listaSupervisores.get(position);
        holder.tvNombre.setText(supervisor.getNombre());
        holder.tvCorreo.setText(supervisor.getCorreo());
        holder.tvTelefono.setText(supervisor.getTelefono());

        holder.btnAsignar.setOnClickListener(v -> listener.onAsignarClick(supervisor));
    }

    @Override
    public int getItemCount() {
        return listaSupervisores.size();
    }

    public static class SupervisorViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCorreo, tvTelefono;
        Button btnAsignar;

        public SupervisorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreSupervisor);
            tvCorreo = itemView.findViewById(R.id.tvCorreoSupervisor);
            tvTelefono = itemView.findViewById(R.id.tvTelefonoSupervisor);
            btnAsignar = itemView.findViewById(R.id.btnAsignar);
        }
    }
}

