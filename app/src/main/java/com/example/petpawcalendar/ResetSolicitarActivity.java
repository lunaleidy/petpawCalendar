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

import com.example.petpawcalendar.network.ApiClient;
import com.example.petpawcalendar.network.ApiService;
import com.example.petpawcalendar.network.dto.ResetSolicitarRequest;
import com.example.petpawcalendar.network.dto.ResponseModel;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetSolicitarActivity extends AppCompatActivity {

    private EditText edtEmailReset;
    private Button btnEnviarCodigoReset;
    private LinearLayout btnAtrasReset;
    private TextView txtIrLoginDesdeReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_solicitar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias
        edtEmailReset = findViewById(R.id.edtEmailReset);
        btnEnviarCodigoReset = findViewById(R.id.btnEnviarCodigoReset);
        btnAtrasReset = findViewById(R.id.btnAtrasReset);
        txtIrLoginDesdeReset = findViewById(R.id.txtIrLoginDesdeReset);

        // Prefill de email si viene de Login o de ResetConfirmar
        String emailPrefill = getIntent().getStringExtra("email");
        if (emailPrefill != null && !emailPrefill.isEmpty()) {
            edtEmailReset.setText(emailPrefill);
        }

        // Botón atrás
        btnAtrasReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // termina esta activity y regresa a la anterior
            }
        });

        // Texto "Iniciar Sesión"
        txtIrLoginDesdeReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResetSolicitarActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Botón enviar código
        btnEnviarCodigoReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarCodigoReset();
            }
        });
    }

    private void enviarCodigoReset() {
        String email = edtEmailReset.getText().toString().trim().toLowerCase(Locale.ROOT);;

        if (TextUtils.isEmpty(email)) {
            edtEmailReset.setError("El email es obligatorio");
            edtEmailReset.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmailReset.setError("Email no válido");
            edtEmailReset.requestFocus();
            return;
        }

        btnEnviarCodigoReset.setEnabled(false);

        ApiService api = ApiClient.getApiService();
        ResetSolicitarRequest body = new ResetSolicitarRequest(email);

        Call<ResponseModel> call = api.resetSolicitar(body);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                btnEnviarCodigoReset.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    ResponseModel resp = response.body();

                    Toast.makeText(ResetSolicitarActivity.this,
                            resp.getMessage(),
                            Toast.LENGTH_LONG).show();

                    // Si success == 0, el backend dice que envió (o intentó enviar) el código.
                    if (resp.getSuccess() == 0) {
                        Intent i = new Intent(ResetSolicitarActivity.this, ResetConfirmarActivity.class);
                        i.putExtra("email", email);
                        startActivity(i);
                        finish();
                    }

                } else {
                    Toast.makeText(ResetSolicitarActivity.this,
                            "Error: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                btnEnviarCodigoReset.setEnabled(true);
                Toast.makeText(ResetSolicitarActivity.this,
                        "Fallo de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}