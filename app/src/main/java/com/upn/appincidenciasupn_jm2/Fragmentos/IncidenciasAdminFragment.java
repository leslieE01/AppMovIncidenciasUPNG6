package com.upn.appincidenciasupn_jm2.Fragmentos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.upn.appincidenciasupn_jm2.Actividades.LoginActivity;
import com.upn.appincidenciasupn_jm2.Entidades.Incidencia;
import com.upn.appincidenciasupn_jm2.Adaptadores.IncidenciaAdapter;
import com.upn.appincidenciasupn_jm2.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IncidenciasAdminFragment extends Fragment {

    private RecyclerView recyclerView;
    private IncidenciaAdapter adapter;
    private List<Incidencia> listaIncidencias;
    private FirebaseFirestore db;
    private Spinner spinnerFiltro;

    public IncidenciasAdminFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_incidencias_admin, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewIncidencias);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listaIncidencias = new ArrayList<>();
        adapter = new IncidenciaAdapter(getContext(), listaIncidencias, incidencia -> {
            Fragment fragment = IncidenciaDetalleFragment.newInstance(incidencia.getId(), "administrador");
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setAdapter(adapter);

        spinnerFiltro = view.findViewById(R.id.spinnerFiltro);

        // Opciones para el filtro único
        List<String> opcionesFiltro = Arrays.asList(
                "Mostrar todo",
                "Pendiente",
                "Asignado a Supervisor",
                "Asignado a Técnico",
                "En proceso",
                "Finalizado",
                "Fecha más reciente",
                "Fecha más antigua"
        );

        ArrayAdapter<String> adapterFiltro = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                opcionesFiltro
        );
        adapterFiltro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltro.setAdapter(adapterFiltro);

        // Firebase
        db = FirebaseFirestore.getInstance();

        // Listener para recargar cuando cambie el filtro
        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargarIncidencias();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Carga inicial
        cargarIncidencias();

        //cerrar sesion
        ImageButton settingsButton = view.findViewById(R.id.settingsImageButton);

        settingsButton.setOnClickListener(v -> {
            Context wrapper = new ContextThemeWrapper(requireContext(), R.style.CustomPopupMenu);
            PopupMenu popupMenu = new PopupMenu(wrapper, v);
            popupMenu.inflate(R.menu.popup_menu);

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_logout) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(requireContext(), LoginActivity.class));
                    requireActivity().finish();
                    Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });

        return view;
    }

    private void cargarIncidencias() {
        String filtroSeleccionado = (String) spinnerFiltro.getSelectedItem();
        CollectionReference incidenciasRef = db.collection("reportes");
        Query query = incidenciasRef;

        switch (filtroSeleccionado) {
            case "Pendiente":
                query = incidenciasRef.whereEqualTo("estado", "Pendiente");
                break;
            case "Asignado a Supervisor":
                query = incidenciasRef.whereEqualTo("estado", "Asignado a Supervisor");
                break;
            case "Asignado a Técnico":
                query = incidenciasRef.whereEqualTo("estado", "Asignado a Técnico");
                break;
            case "En Proceso":
                query = incidenciasRef.whereEqualTo("estado", "En Proceso");
                break;
            case "Finalizado":
                query = incidenciasRef.whereEqualTo("estado", "Finalizado");
                break;
            case "Fecha más reciente":
                query = incidenciasRef.orderBy("fechaReporte", Query.Direction.DESCENDING);
                break;
            case "Fecha más antigua":
                query = incidenciasRef.orderBy("fechaReporte", Query.Direction.ASCENDING);
                break;
            case "Mostrar todo":
            default:
                query = incidenciasRef;
                break;
        }


        query.get().addOnSuccessListener(value -> {
            listaIncidencias.clear();
            for (QueryDocumentSnapshot doc : value) {
                Incidencia incidencia = doc.toObject(Incidencia.class);
                incidencia.setId(doc.getId());
                listaIncidencias.add(incidencia);
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error al cargar incidencias.", Toast.LENGTH_SHORT).show();
        });
    }
}
