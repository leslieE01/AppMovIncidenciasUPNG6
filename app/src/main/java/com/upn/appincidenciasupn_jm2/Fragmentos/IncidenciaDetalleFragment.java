package com.upn.appincidenciasupn_jm2.Fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.upn.appincidenciasupn_jm2.R;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class IncidenciaDetalleFragment extends Fragment {

    private TextView tvCategoria, tvSubcategoria, tvDescripcionPrioridad, tvPrioridad, tvDescripcion, tvEstado, tvUbicacion,
            tvNombreUsuario, tvFechaReporte, tvFechaAsignacionTecnico, tvFechaAtencion, tvObservaciones, tvNombreTecnico, tvMediaUrl;
    private TextView tvFechaAsignacionSupervisor, tvNombreSupervisor;
    private Button btnAsignar, btnCambiarEstado;
    private Spinner spinnerEstado;
    private String rol;

    private FirebaseFirestore db;
    private String idDocumento;

    public IncidenciaDetalleFragment() {}

    public static IncidenciaDetalleFragment newInstance(String idDocumento, String rol) {
        IncidenciaDetalleFragment fragment = new IncidenciaDetalleFragment();
        Bundle args = new Bundle();
        args.putString("idDocumento", idDocumento);
        args.putString("rol", rol);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            idDocumento = getArguments().getString("idDocumento");
            rol = getArguments().getString("rol");
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
        tvFechaAsignacionTecnico = view.findViewById(R.id.tvFechaAsignacionTecnico);
        tvFechaAtencion = view.findViewById(R.id.tvFechaAtencion);
        tvObservaciones = view.findViewById(R.id.tvObservaciones);
        tvNombreTecnico = view.findViewById(R.id.tvNombreTecnico);
        tvMediaUrl = view.findViewById(R.id.tvMediaUrl);
        tvFechaAsignacionSupervisor = view.findViewById(R.id.tvFechaAsignacionSupervisor);
        tvNombreSupervisor = view.findViewById(R.id.tvNombreSupervisor);

        // Botón de regreso
        ImageButton backButton = view.findViewById(R.id.BackImageButton);
        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        // Inicializar botón asignar
        btnAsignar = view.findViewById(R.id.btnAsignar);

        // Inicializar Spinner y botón cambiar estado
        spinnerEstado = view.findViewById(R.id.spinnerEstado);
        btnCambiarEstado = view.findViewById(R.id.btnCambiarEstado);

        // Configurar opciones de estado según el rol
        configurarOpcionesSpinner();

        // Mostrar botón asignar según rol
        if ("administrador".equals(rol)) {
            btnAsignar.setVisibility(View.VISIBLE);
            btnAsignar.setOnClickListener(v -> {
                if (idDocumento != null) {
                    AsignarSupervisorFragment fragment = AsignarSupervisorFragment.newInstance(idDocumento);
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainerView, fragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Toast.makeText(requireContext(), "ID de reporte no disponible", Toast.LENGTH_SHORT).show();
                }
            });
        } else if ("supervisor".equals(rol)) {
            btnAsignar.setVisibility(View.VISIBLE);
            btnAsignar.setOnClickListener(v -> {
                if (idDocumento != null) {
                    AsignarTecnicoFragment fragment = AsignarTecnicoFragment.newInstance(idDocumento);
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainerView, fragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Toast.makeText(requireContext(), "ID de reporte no disponible", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            btnAsignar.setVisibility(View.GONE);
        }

        // Acción para cambiar estado
        btnCambiarEstado.setOnClickListener(v -> {
            if (idDocumento != null) {
                String nuevoEstado = spinnerEstado.getSelectedItem().toString();

                Map<String, Object> actualizaciones = new HashMap<>();
                actualizaciones.put("estado", nuevoEstado);

                if ("administrador".equals(rol)) {
                    if ("Pendiente".equals(nuevoEstado)) {
                        actualizaciones.put("nombreSupervisor", "");
                        actualizaciones.put("fechaAsignacionSupervisor", null);
                        actualizaciones.put("nombreTecnico", "");
                        actualizaciones.put("fechaAsignacionTecnico", null);
                        actualizaciones.put("fechaAtencion", null);
                        actualizaciones.put("observaciones", "");
                    }
                } else if ("supervisor".equals(rol)) {
                    if ("Asignado a Supervisor".equals(nuevoEstado)) {
                        actualizaciones.put("nombreTecnico", "");
                        actualizaciones.put("fechaAsignacionTecnico", null);
                        actualizaciones.put("fechaAtencion", null);
                        actualizaciones.put("observaciones", "");
                    }
                } else if ("tecnico".equals(rol)) {
                    if ("Finalizado".equals(nuevoEstado)) {
                        actualizaciones.put("fechaAtencion", new Timestamp(new Date()));
                    }
                }

                db.collection("reportes").document(idDocumento)
                        .update(actualizaciones)
                        .addOnSuccessListener(unused -> {
                            tvEstado.setText(nuevoEstado);
                            Toast.makeText(requireContext(), "Estado actualizado a: " + nuevoEstado, Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error al actualizar estado", Toast.LENGTH_SHORT).show());
            }
        });




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

    private void configurarOpcionesSpinner() {
        List<String> opcionesEstado;

        if ("administrador".equals(rol)) {
            opcionesEstado = Arrays.asList("Pendiente", "Finalizado");
        } else if ("supervisor".equals(rol)) {
            opcionesEstado = Arrays.asList("Asignado a Supervisor", "Finalizado");
        } else if ("tecnico".equals(rol)) {
            opcionesEstado = Arrays.asList("En Proceso", "Finalizado");
        } else {
            opcionesEstado = Arrays.asList("Pendiente", "Finalizado");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, opcionesEstado);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapter);
    }

    private void cargarDatosIncidencia(DocumentSnapshot doc) {
        SimpleDateFormat sdf = new SimpleDateFormat("d 'de' MMMM 'de' yyyy, HH:mm", new Locale("es", "PE"));
        sdf.setTimeZone(TimeZone.getTimeZone("America/Lima"));

        tvCategoria.setText(doc.getString("categoria"));
        tvSubcategoria.setText(doc.getString("subcategoria"));
        tvDescripcionPrioridad.setText(doc.getString("descripcionPrioridad"));
        tvPrioridad.setText(doc.getString("prioridad"));
        tvDescripcion.setText(doc.getString("descripcionIncidencia"));
        tvEstado.setText(doc.getString("estado"));
        tvUbicacion.setText(doc.getString("ubicacion"));
        tvNombreUsuario.setText(doc.getString("nombreUsuario"));

        if (doc.contains("fechaAsignacionSupervisor") && doc.getTimestamp("fechaAsignacionSupervisor") != null) {
            Date fechaAsignacionSupervisor = doc.getTimestamp("fechaAsignacionSupervisor").toDate();
            tvFechaAsignacionSupervisor.setText(sdf.format(fechaAsignacionSupervisor));
        } else {
            tvFechaAsignacionSupervisor.setText("Sin asignar");
        }

        tvNombreSupervisor.setText((doc.getString("nombreSupervisor") != null && !doc.getString("nombreSupervisor").isEmpty()
                ? doc.getString("nombreSupervisor") : "Sin asignar"));

        if (doc.contains("fechaReporte") && doc.getTimestamp("fechaReporte") != null) {
            Date fechaReporte = doc.getTimestamp("fechaReporte").toDate();
            tvFechaReporte.setText(sdf.format(fechaReporte));
        } else {
            tvFechaReporte.setText("No disponible");
        }

        if (doc.contains("fechaAsignacionTecnico") && doc.getTimestamp("fechaAsignacionTecnico") != null) {
            Date fechaAsignacionTecnico = doc.getTimestamp("fechaAsignacionTecnico").toDate();
            tvFechaAsignacionTecnico.setText(sdf.format(fechaAsignacionTecnico));
        } else {
            tvFechaAsignacionTecnico.setText("No asignado");
        }

        if (doc.contains("fechaAtencion") && doc.getTimestamp("fechaAtencion") != null) {
            Date fechaAtencion = doc.getTimestamp("fechaAtencion").toDate();
            tvFechaAtencion.setText(sdf.format(fechaAtencion));
        } else {
            tvFechaAtencion.setText("No atendido");
        }

        tvObservaciones.setText((doc.getString("observaciones") != null ? doc.getString("observaciones") : "Sin observaciones"));
        tvNombreTecnico.setText((doc.getString("nombreTecnico") != null && !doc.getString("nombreTecnico").isEmpty() ? doc.getString("nombreTecnico") : "No asignado"));
        tvMediaUrl.setText((doc.getString("mediaUrl") != null ? doc.getString("mediaUrl") : "No disponible"));
    }
}
