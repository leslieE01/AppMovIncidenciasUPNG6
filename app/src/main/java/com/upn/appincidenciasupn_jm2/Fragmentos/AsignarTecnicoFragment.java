package com.upn.appincidenciasupn_jm2.Fragmentos;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.upn.appincidenciasupn_jm2.Adaptadores.SupervisorAdapter;
import com.upn.appincidenciasupn_jm2.Entidades.Supervisor;
import com.upn.appincidenciasupn_jm2.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsignarTecnicoFragment extends Fragment {

    private RecyclerView recyclerView;
    private SupervisorAdapter adapter;
    private List<Supervisor> listaTecnicos;
    private FirebaseFirestore db;
    private String reporteId;
    private String supervisorId;

    public AsignarTecnicoFragment() {}

    public static AsignarTecnicoFragment newInstance(String reporteId) {
        AsignarTecnicoFragment fragment = new AsignarTecnicoFragment();
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
        listaTecnicos = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            reporteId = getArguments().getString("reporteId");
        }

        // Obtener UID del supervisor logueado
        supervisorId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        cargarTecnicos();
    }

    private void cargarTecnicos() {
        db.collection("usuarios")
                .whereEqualTo("rol", "tecnico")
                .whereEqualTo("supervisorId", supervisorId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Supervisor tecnico = new Supervisor(
                                doc.getId(),
                                doc.getString("nombre"),
                                doc.getString("correo"),
                                doc.getString("telefono")
                        );
                        listaTecnicos.add(tecnico);
                    }

                    adapter = new SupervisorAdapter(listaTecnicos, tecnico -> asignarTecnico(tecnico));
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Error cargando técnicos: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void asignarTecnico(Supervisor tecnico) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("tecnicoId", tecnico.getId());
        updates.put("nombreTecnico", tecnico.getNombre());
        updates.put("fechaAsignacionTecnico", Timestamp.now());
        updates.put("estado", "Asignado a Técnico");

        db.collection("reportes").document(reporteId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Técnico asignado correctamente", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack(); // Navega al fragmento anterior
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Error al asignar técnico: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
