package com.upn.appincidenciasupn_jm2.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.upn.appincidenciasupn_jm2.Entidades.Incidencia;
import com.upn.appincidenciasupn_jm2.R;

import java.util.List;

public class IncidenciaAdapter extends RecyclerView.Adapter<IncidenciaAdapter.IncidenciaViewHolder> {

    private Context context;
    private List<Incidencia> listaIncidencias;
    private OnItemClickListener listener;

    // Interfaz para manejar clics en el botón "Ver detalle"
    public interface OnItemClickListener {
        void onVerDetalleClick(Incidencia incidencia);
    }

    // Constructor
    public IncidenciaAdapter(Context context, List<Incidencia> listaIncidencias, OnItemClickListener listener) {
        this.context = context;
        this.listaIncidencias = listaIncidencias;
        this.listener = listener;
    }

    @NonNull
    @Override
    public IncidenciaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_incidencia, parent, false);
        return new IncidenciaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncidenciaViewHolder holder, int position) {
        Incidencia incidencia = listaIncidencias.get(position);

        holder.tvCategoria.setText(incidencia.getCategoria());
        holder.tvSubcategoria.setText(incidencia.getSubcategoria());
        holder.tvPrioridad.setText(incidencia.getPrioridad());
        holder.tvNombreUsuario.setText( incidencia.getNombreUsuario());
        holder.tvEstado.setText( incidencia.getEstado());

        // Click en botón Ver detalle
        holder.btnVerDetalle.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVerDetalleClick(incidencia);
            }
        });
    }


    @Override
    public int getItemCount() {
        return listaIncidencias.size();
    }

    public static class IncidenciaViewHolder extends RecyclerView.ViewHolder {

        TextView tvCategoria, tvSubcategoria, tvPrioridad, tvNombreUsuario, tvEstado;
        Button btnVerDetalle;

        public IncidenciaViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCategoria = itemView.findViewById(R.id.tvCategoria);
            tvSubcategoria = itemView.findViewById(R.id.tvSubcategoria);
            tvPrioridad = itemView.findViewById(R.id.tvPrioridad);
            tvNombreUsuario = itemView.findViewById(R.id.tvNombreUsuario);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            btnVerDetalle = itemView.findViewById(R.id.btnVerDetalle);
        }
    }
}
