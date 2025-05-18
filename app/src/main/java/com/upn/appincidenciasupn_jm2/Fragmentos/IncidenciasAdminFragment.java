package com.upn.appincidenciasupn_jm2.Fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.upn.appincidenciasupn_jm2.Entidades.Incidencia;
import com.upn.appincidenciasupn_jm2.Adaptadores.IncidenciaAdapter;
import com.upn.appincidenciasupn_jm2.R;

import java.util.ArrayList;
import java.util.List;

public class IncidenciasAdminFragment extends Fragment {

    private RecyclerView recyclerView;
    private IncidenciaAdapter adapter;
    private List<Incidencia> listaIncidencias;
    private FirebaseFirestore db;

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
            Fragment fragment = IncidenciaDetalleFragment.newInstance(incidencia.getId());
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        cargarIncidencias();

        return view;
    }

    private void cargarIncidencias() {
        CollectionReference incidenciasRef = db.collection("reportes");

        incidenciasRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getContext(), "Error al cargar incidencias.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (value != null) {
                    listaIncidencias.clear();

                    for (QueryDocumentSnapshot doc : value) {
                        Incidencia incidencia = doc.toObject(Incidencia.class);
                        incidencia.setId(doc.getId());
                        listaIncidencias.add(incidencia);
                    }

                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}
