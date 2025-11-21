package com.example.petpawcalendar;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petpawcalendar.network.*;
import com.example.petpawcalendar.network.dto.*;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReenviarActivacionActivity extends AppCompatActivity {

    private EditText edtEmailReenviar;
    private Button btnEnviarCodigoReenviar;
    private LinearLayout btnAtrasReenviar;
    private TextView txtIrActivarDesdeReenviar;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reenviar_activacion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias
        edtEmailReenviar = findViewById(R.id.edtEmailReenviar);
        btnEnviarCodigoReenviar = findViewById(R.id.btnEnviarCodigoReenviar);
        btnAtrasReenviar = findViewById(R.id.btnAtrasReenviar);
        txtIrActivarDesdeReenviar = findViewById(R.id.txtIrActivarDesdeReenviar);

        apiService = ApiClient.getApiService();

        // Prefill de email si viene desde ActivarCuenta o Login/Registro
        String emailPrefill = getIntent().getStringExtra("email");
        if (emailPrefill != null && !emailPrefill.isEmpty()) {
            edtEmailReenviar.setText(emailPrefill);
        }

        // Botón atrás: volver a ActivarCuentaActivity
        btnAtrasReenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailActual = edtEmailReenviar.getText().toString().trim();
                Intent i = new Intent(ReenviarActivacionActivity.this, ActivarCuentaActivity.class);
                if (!emailActual.isEmpty()) {
                    i.putExtra("email", emailActual);
                }
                startActivity(i);
                finish();
            }
        });

        // Texto "Activar mi cuenta"
        txtIrActivarDesdeReenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailActual = edtEmailReenviar.getText().toString().trim();
                Intent i = new Intent(ReenviarActivacionActivity.this, ActivarCuentaActivity.class);
                if (!emailActual.isEmpty()) {
                    i.putExtra("email", emailActual);
                }
                startActivity(i);
                finish();
            }
        });

        // Botón "Enviar código"
        btnEnviarCodigoReenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reenviarCodigo();
            }
        });
    }

    private void reenviarCodigo() {
        final String correo = edtEmailReenviar.getText().toString()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (TextUtils.isEmpty(correo)) {
            edtEmailReenviar.setError("El email es obligatorio");
            edtEmailReenviar.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            edtEmailReenviar.setError("Email no válido");
            edtEmailReenviar.requestFocus();
            return;
        }

        btnEnviarCodigoReenviar.setEnabled(false);

        ReenviarActivacionRequest body = new ReenviarActivacionRequest(correo);
        Call<ResponseModel> call = apiService.reenviarActivacion(body);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                btnEnviarCodigoReenviar.setEnabled(true);

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(ReenviarActivacionActivity.this,
                            "Error: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                ResponseModel resp = response.body();
                Toast.makeText(ReenviarActivacionActivity.this,
                        resp.getMessage(),
                        Toast.LENGTH_LONG).show();

                // Si success == 0, reenviado OK -> volvemos a ActivarCuenta con el correo relleno
                if (resp.getSuccess() == 0) {
                    Intent i = new Intent(ReenviarActivacionActivity.this, ActivarCuentaActivity.class);
                    i.putExtra("email", correo);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                btnEnviarCodigoReenviar.setEnabled(true);
                Toast.makeText(ReenviarActivacionActivity.this,
                        "Fallo de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}