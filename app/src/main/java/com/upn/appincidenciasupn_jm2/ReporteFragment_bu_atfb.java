package com.upn.appincidenciasupn_jm2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;

public class ReporteFragment_bu_atfb extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private TextView tvNombreUsuario, tvCorreoUsuario, tvTelefonoUsuario, tvDescripcionPrioridad;
    private Spinner spinnerCategoria, spinnerSubcategoria, spinnerPrioridad;
    private EditText etDescripcion, etUbicacion;
    private Button btnSeleccionarImagen, btnEnviarIncidencia;
    private ImageView imagePreview;
    private Uri imageUri;

    public ReporteFragment_bu_atfb() {
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
        tvNombreUsuario.setText("Juan Pérez");
        tvCorreoUsuario.setText("juan.perez@ejemplo.com");
        tvTelefonoUsuario.setText("987654321");

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
                // Aquí luego agregamos código para subir a Firebase
                Toast.makeText(requireContext(), "Reporte enviado correctamente", Toast.LENGTH_SHORT).show();
            }
        });
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
