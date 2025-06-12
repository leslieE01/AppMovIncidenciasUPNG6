package com.upn.appincidenciasupn_jm2.Fragmentos;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.upn.appincidenciasupn_jm2.Adaptadores.SupervisorAdapter;
import com.upn.appincidenciasupn_jm2.Entidades.Supervisor;
import com.upn.appincidenciasupn_jm2.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsignarSupervisorFragment extends Fragment {

    private RecyclerView recyclerView;
    private SupervisorAdapter adapter;
    private List<Supervisor> listaSupervisores;
    private FirebaseFirestore db;
    private String reporteId;

    public AsignarSupervisorFragment() {}

    public static AsignarSupervisorFragment newInstance(String reporteId) {
        AsignarSupervisorFragment fragment = new AsignarSupervisorFragment();
        Bundle args = new Bundle();
        args.putString("reporteId", reporteId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=  inflater.inflate(R.layout.fragment_asignar_supervisor, container, false);

        // Botón de regreso
        ImageButton backButton = view.findViewById(R.id.BackImageButton);
        backButton.setOnClickListener(v -> requireActivity().onBackPressed());  // Regresa a la actividad anterior

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerSupervisores);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        listaSupervisores = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            reporteId = getArguments().getString("reporteId");
        }

        cargarSupervisores();
    }

    private void cargarSupervisores() {
        db.collection("usuarios")
                .whereEqualTo("rol", "supervisor")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Supervisor supervisor = new Supervisor(
                                doc.getId(),
                                doc.getString("nombre"),
                                doc.getString("correo"),
                                doc.getString("telefono")
                        );
                        listaSupervisores.add(supervisor);
                    }

                    adapter = new SupervisorAdapter(listaSupervisores, supervisor -> asignarSupervisor(supervisor));
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Error cargando supervisores: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void asignarSupervisor(Supervisor supervisor) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("supervisorId", supervisor.getId());
        updates.put("nombreSupervisor", supervisor.getNombre());
        updates.put("fechaAsignacionSupervisor", Timestamp.now());
        updates.put("estado", "Asignado a Supervisor");

        db.collection("reportes").document(reporteId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Supervisor asignado correctamente", Toast.LENGTH_SHORT).show();
                   // Dentro de asignarSupervisor:
                    getParentFragmentManager().popBackStack(); // Navega al fragmento anterior

                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Error al asignar supervisor: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}

