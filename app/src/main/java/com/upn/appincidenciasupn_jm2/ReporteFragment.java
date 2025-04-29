package com.upn.appincidenciasupn_jm2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.io.IOException;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;
import java.util.HashMap;
import java.util.Map;


public class ReporteFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private TextView tvNombreUsuario, tvCorreoUsuario, tvTelefonoUsuario, tvDescripcionPrioridad;
    private Spinner spinnerCategoria, spinnerSubcategoria, spinnerPrioridad;
    private EditText etDescripcion, etUbicacion;
    private Button btnSeleccionarImagen, btnEnviarIncidencia;
    private ImageView imagePreview;
    private Uri imageUri;

    public ReporteFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflamos el layout XML
        return inflater.inflate(R.layout.fragment_reporte, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializamos vistas
        tvNombreUsuario = view.findViewById(R.id.tvNombreUsuario);
        tvCorreoUsuario = view.findViewById(R.id.tvCorreoUsuario);
        tvTelefonoUsuario = view.findViewById(R.id.tvTelefonoUsuario);
        spinnerCategoria = view.findViewById(R.id.spinnerCategoria);
        spinnerSubcategoria = view.findViewById(R.id.spinnerSubcategoria);
        spinnerPrioridad = view.findViewById(R.id.spinnerPrioridad);
        etDescripcion = view.findViewById(R.id.etDescripcion);
        etUbicacion = view.findViewById(R.id.etUbicacion);
        tvDescripcionPrioridad = view.findViewById(R.id.tvDescripcionPrioridad);
        btnSeleccionarImagen = view.findViewById(R.id.btnSeleccionarImagen);
        btnEnviarIncidencia = view.findViewById(R.id.btnEnviarIncidencia);


        // Simulación de datos del usuario (luego se puede cargar de Firebase Auth)
        tvNombreUsuario.setText("Richard Sánchez");
        tvCorreoUsuario.setText("richard.sanchez@upn.edu.pe");
        tvTelefonoUsuario.setText("999888777");

        // Adaptadores de prueba (puedes cambiarlos luego)
        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Electricidad", "Plomería", "Infraestructura", "Otros"});

        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(categoriaAdapter);

        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String categoria = parent.getItemAtPosition(position).toString();
                cargarSubcategorias(categoria);
                if (categoria.equals("Otros")) {
                    etDescripcion.setHint("Por favor describe el problema");
                } else {
                    etDescripcion.setHint("Descripción (opcional)");
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        ArrayAdapter<String> prioridadAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Baja", "Media", "Alta", "Emergencia"});

        prioridadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrioridad.setAdapter(prioridadAdapter);

        spinnerPrioridad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String prioridad = parent.getItemAtPosition(position).toString();
                switch (prioridad) {
                    case "Baja":
                        tvDescripcionPrioridad.setText("Puede esperar unos días");
                        break;
                    case "Media":
                        tvDescripcionPrioridad.setText("Se requiere en esta semana");
                        break;
                    case "Alta":
                        tvDescripcionPrioridad.setText("Debe resolverse lo antes posible");
                        break;
                    case "Emergencia":
                        tvDescripcionPrioridad.setText("Peligro inmediato o alto impacto");
                        break;
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnSeleccionarImagen.setOnClickListener(v -> abrirGaleria());

        btnEnviarIncidencia.setOnClickListener(v -> {
            if (validarCampos()) {

                // Obtén los datos ingresados
                String nombreUsuario = tvNombreUsuario.getText().toString();
                String categoria = spinnerCategoria.getSelectedItem().toString();
                String subcategoria = spinnerSubcategoria.getSelectedItem().toString();
                String prioridad = spinnerPrioridad.getSelectedItem().toString();
                String descripcionPrioridad = tvDescripcionPrioridad.getText().toString();
                String descripcion = etDescripcion.getText().toString().trim();
                String ubicacion = etUbicacion.getText().toString().trim();

                // Datos de usuario de ejemplo
                String usuarioId = "usuario123"; // luego lo cambias por FirebaseAuth si quieres

                // Simulación de mediaUrl
                String mediaUrl = (imageUri != null) ? imageUri.toString() : "";

                // Crear el HashMap con todos los campos
                Map<String, Object> reporte = new HashMap<>();
                reporte.put("categoria", categoria);
                reporte.put("subcategoria", subcategoria);
                reporte.put("descripcionIncidencia", descripcion);
                reporte.put("ubicacion", ubicacion);
                reporte.put("prioridad", prioridad);
                reporte.put("descripcionPrioridad", descripcionPrioridad);
                reporte.put("estado", "Pendiente");
                reporte.put("fechaReporte", Timestamp.now());
                reporte.put("fechaAsignacion", null);
                reporte.put("fechaAtencion", null);
                reporte.put("mediaUrl", mediaUrl);
                reporte.put("usuarioId", usuarioId);
                reporte.put("nombreUsuario", nombreUsuario);
                reporte.put("tecnicoId", "");
                reporte.put("nombreTecnico", "");
                reporte.put("observaciones", "");

                // Guardar en Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("reportes")
                        .add(reporte)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(requireContext(), "Reporte enviado correctamente", Toast.LENGTH_SHORT).show();
                            limpiarCampos();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(requireContext(), "Error al enviar reporte: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

    }

    private void limpiarCampos() {
        spinnerCategoria.setSelection(0);
        spinnerSubcategoria.setSelection(0);
        spinnerPrioridad.setSelection(0);
        etDescripcion.setText("");
        etUbicacion.setText("");
        tvDescripcionPrioridad.setText("");
        imagePreview.setImageBitmap(null);
        imagePreview.setVisibility(View.GONE);
        imageUri = null;
    }

    private void cargarSubcategorias(String categoria) {
        String[] subcategorias;
        switch (categoria) {
            case "Electricidad":
                subcategorias = new String[]{"Falla de luz", "Enchufe dañado", "Cables expuestos"};
                break;
            case "Plomería":
                subcategorias = new String[]{"Fuga de agua", "Inodoro dañado", "Tubería rota"};
                break;
            case "Infraestructura":
                subcategorias = new String[]{"Pared dañada", "Techo con goteras", "Ventana rota"};
                break;
            case "Otros":
            default:
                subcategorias = new String[]{"Otro"};
                break;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, subcategorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubcategoria.setAdapter(adapter);
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                imagePreview.setImageBitmap(bitmap);
                imagePreview.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validarCampos() {
        String ubicacion = etUbicacion.getText().toString().trim();
        String categoria = spinnerCategoria.getSelectedItem().toString();
        String descripcion = etDescripcion.getText().toString().trim();

        if (ubicacion.isEmpty()) {
            etUbicacion.setError("La ubicación es obligatoria");
            etUbicacion.requestFocus();
            return false;
        }

        if (categoria.equals("Otros") && descripcion.isEmpty()) {
            etDescripcion.setError("Debes describir la incidencia si elegiste 'Otros'");
            etDescripcion.requestFocus();
            return false;
        }

        return true;
    }
}
