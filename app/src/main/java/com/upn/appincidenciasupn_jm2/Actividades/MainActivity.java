package com.upn.appincidenciasupn_jm2.Actividades;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import com.upn.appincidenciasupn_jm2.Fragmentos.IncidenciasAdminFragment;
import com.upn.appincidenciasupn_jm2.R;
import com.upn.appincidenciasupn_jm2.Fragmentos.ReporteFragment;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            obtenerYMostrarFragmentoPorRol(user.getUid());
        } else {
            Toast.makeText(this, "No hay sesión iniciada", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void obtenerYMostrarFragmentoPorRol(String uid) {
        DocumentReference docRef = db.collection("usuarios").document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String rol = document.getString("rol");
                    if ("usuario".equals(rol)) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainerView, new ReporteFragment())
                                .commit();
                    } else if ("administrador".equals(rol)) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainerView, new IncidenciasAdminFragment())
                                .commit();
                    } else {
                        Toast.makeText(this, "Rol no autorizado", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(this, "No se encontró información del usuario", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Toast.makeText(this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
