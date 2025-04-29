package com.upn.appincidenciasupn_jm2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cargar ReporteFragment al iniciar
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, new ReporteFragment())
                .commit();
    }
}
