package com.example.petpawcalendar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PoliticaPrivacidadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_politica_privacidad);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // === Botón atrás en la barra superior ===
        LinearLayout btnAtras = findViewById(R.id.btnAtrasPolitica);
        btnAtras.setOnClickListener(v -> {
            // Cerrar esta Activity y volver a la anterior (Registro)
            finish();
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Para que el botón físico/sistema también haga lo mismo
        finish();
    }
}