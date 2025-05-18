package com.upn.appincidenciasupn_jm2.Fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.upn.appincidenciasupn_jm2.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class IncidenciaDetalleFragment extends Fragment {

    private TextView tvCategoria, tvSubcategoria, tvDescripcionPrioridad, tvPrioridad, tvDescripcion, tvEstado, tvUbicacion,
            tvNombreUsuario, tvFechaReporte, tvFechaAsignacion, tvFechaAtencion, tvObservaciones, tvNombreTecnico, tvMediaUrl;

    private FirebaseFirestore db;
    private String idDocumento;

    public IncidenciaDetalleFragment() {
        // Constructor vacío requerido
    }

    public static IncidenciaDetalleFragment newInstance(String idDocumento) {
        IncidenciaDetalleFragment fragment = new IncidenciaDetalleFragment();
        Bundle args = new Bundle();
        args.putString("idDocumento", idDocumento);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            idDocumento = getArguments().getString("idDocumento");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_incidencia_detalle, container, false);

        // Inicializar vistas
        tvCategoria = view.findViewById(R.id.tvCategoria);
        tvSubcategoria = view.findViewById(R.id.tvSubcategoria);
        tvDescripcionPrioridad = view.findViewById(R.id.tvDescripcionPrioridad);
        tvPrioridad = view.findViewById(R.id.tvPrioridad);
        tvDescripcion = view.findViewById(R.id.tvDescripcion);
        tvEstado = view.findViewById(R.id.tvEstado);
        tvUbicacion = view.findViewById(R.id.tvUbicacion);
        tvNombreUsuario = view.findViewById(R.id.tvNombreUsuario);
        tvFechaReporte = view.findViewById(R.id.tvFechaReporte);
        tvFechaAsignacion = view.findViewById(R.id.tvFechaAsignacion);
        tvFechaAtencion = view.findViewById(R.id.tvFechaAtencion);
        tvObservaciones = view.findViewById(R.id.tvObservaciones);
        tvNombreTecnico = view.findViewById(R.id.tvNombreTecnico);
        tvMediaUrl = view.findViewById(R.id.tvMediaUrl);


        // Botón de regreso
        ImageButton backButton = view.findViewById(R.id.BackImageButton);
        backButton.setOnClickListener(v -> requireActivity().onBackPressed());  // Regresa a la actividad anterior

        // Cargar datos de Firestore
        if (idDocumento != null) {
            db.collection("reportes").document(idDocumento)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            cargarDatosIncidencia(documentSnapshot);
                        } else {
                            Toast.makeText(getContext(), "No se encontró la incidencia", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        return view;




    }

    private void cargarDatosIncidencia(DocumentSnapshot doc) {
        tvCategoria.setText( doc.getString("categoria"));
        tvSubcategoria.setText( doc.getString("subcategoria"));
        tvDescripcionPrioridad.setText(doc.getString("descripcionPrioridad"));
        tvPrioridad.setText(doc.getString("prioridad"));
        tvDescripcion.setText( doc.getString("descripcionIncidencia"));
        tvEstado.setText(doc.getString("estado"));
        tvUbicacion.setText(doc.getString("ubicacion"));
        tvNombreUsuario.setText(doc.getString("nombreUsuario"));

        // Configuración de formato para las fechas
        SimpleDateFormat sdf = new SimpleDateFormat("d 'de' MMMM 'de' yyyy, HH:mm", new Locale("es", "PE"));
        sdf.setTimeZone(TimeZone.getTimeZone("America/Lima"));

        // Formateamos la fecha de reporte
        if (doc.contains("fechaReporte") && doc.getTimestamp("fechaReporte") != null) {
            Date fechaReporte = doc.getTimestamp("fechaReporte").toDate();
            tvFechaReporte.setText( sdf.format(fechaReporte));
        } else {
            tvFechaReporte.setText("No disponible");
        }

        // Formateamos la fecha de asignación
        if (doc.contains("fechaAsignacion") && doc.getTimestamp("fechaAsignacion") != null) {
            Date fechaAsignacion = doc.getTimestamp("fechaAsignacion").toDate();
            tvFechaAsignacion.setText( sdf.format(fechaAsignacion));
        } else {
            tvFechaAsignacion.setText("No asignado");
        }

        // Formateamos la fecha de atención
        if (doc.contains("fechaAtencion") && doc.getTimestamp("fechaAtencion") != null) {
            Date fechaAtencion = doc.getTimestamp("fechaAtencion").toDate();
            tvFechaAtencion.setText( sdf.format(fechaAtencion));
        } else {
            tvFechaAtencion.setText("No atendido");
        }

        tvObservaciones.setText((doc.getString("observaciones") != null ? doc.getString("observaciones") : "Sin observaciones"));
        tvNombreTecnico.setText((doc.getString("nombreTecnico") != null && !doc.getString("nombreTecnico").isEmpty() ? doc.getString("nombreTecnico") : "No asignado"));
        tvMediaUrl.setText((doc.getString("mediaUrl") != null ? doc.getString("mediaUrl") : "No disponible"));
    }

}
