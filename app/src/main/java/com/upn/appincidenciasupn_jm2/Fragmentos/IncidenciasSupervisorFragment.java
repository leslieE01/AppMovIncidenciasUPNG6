package com.upn.appincidenciasupn_jm2.Fragmentos;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.upn.appincidenciasupn_jm2.Actividades.LoginActivity;
import com.upn.appincidenciasupn_jm2.Adaptadores.IncidenciaAdapter;
import com.upn.appincidenciasupn_jm2.Entidades.Incidencia;
import com.upn.appincidenciasupn_jm2.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IncidenciasSupervisorFragment extends Fragment {

    private RecyclerView recyclerView;
    private IncidenciaAdapter adapter;
    private List<Incidencia> listaIncidencias;
    private FirebaseFirestore db;
    private Spinner spinnerFiltro;

    public IncidenciasSupervisorFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_incidencias_supervisor, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewIncidencias);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listaIncidencias = new ArrayList<>();
        adapter = new IncidenciaAdapter(getContext(), listaIncidencias, incidencia -> {
            Fragment fragment = IncidenciaDetalleFragment.newInstance(incidencia.getId(), "supervisor");
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        spinnerFiltro = view.findViewById(R.id.spinnerFiltro);

        // Opciones de filtro
        List<String> opcionesFiltro = Arrays.asList(
                "Mostrar todo",
                "Asignado a Supervisor",
                "Asignado a Técnico",
                "En Proceso",
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

        // Listener para cambios en el spinner
        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                cargarIncidenciasSupervisor();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Carga inicial
        cargarIncidenciasSupervisor();



        //cerrar sesion
        ImageButton settingsButton = view.findViewById(R.id.settingsImageButton);

        //Menu
        settingsButton.setOnClickListener(v -> {
            Context wrapper = new ContextThemeWrapper(requireContext(), R.style.CustomPopupMenu);
            PopupMenu popupMenu = new PopupMenu(wrapper, v);
            popupMenu.inflate(R.menu.popup_menu);

            // Ocultar la opción "Historial de incidencias"
            popupMenu.getMenu().findItem(R.id.action_historial).setVisible(false);

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

        //Filtro de incidencias de spinner
        ImageView imgFiltro = view.findViewById(R.id.imgFiltro);
        Spinner spinnerFiltro = view.findViewById(R.id.spinnerFiltro);

        imgFiltro.setOnClickListener(v -> {
            spinnerFiltro.performClick();  // Simula clic en el Spinner
        });

        return view;
    }

    private void cargarIncidenciasSupervisor() {
        String supervisorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String filtroSeleccionado = (String) spinnerFiltro.getSelectedItem();
        CollectionReference incidenciasRef = db.collection("reportes");

        Query query = incidenciasRef.whereEqualTo("supervisorId", supervisorId);

        switch (filtroSeleccionado) {
            case "Asignado a Supervisor":
                query = query.whereEqualTo("estado", "Asignado a Supervisor");
                break;
            case "Asignado a Técnico":
                query = query.whereEqualTo("estado", "Asignado a Técnico");
                break;
            case "En Proceso":
                query = query.whereEqualTo("estado", "En Proceso");
                break;
            case "Finalizado":
                query = query.whereEqualTo("estado", "Finalizado");
                break;
            case "Fecha más reciente":
                query = query.orderBy("fechaReporte", Query.Direction.DESCENDING);
                break;
            case "Fecha más antigua":
                query = query.orderBy("fechaReporte", Query.Direction.ASCENDING);
                break;
            case "Mostrar todo":
            default:
                // ya está solo filtrado por supervisorId
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
