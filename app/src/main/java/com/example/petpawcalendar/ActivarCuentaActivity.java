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

public class ActivarCuentaActivity extends AppCompatActivity {

    private EditText edtEmailActivar;
    private EditText edtCodigoActivar;
    private Button btnActivarCuenta;
    private LinearLayout btnAtrasActivar;
    private TextView txtReenviarCodigo;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_activar_cuenta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias
        edtEmailActivar = findViewById(R.id.edtEmailActivar);
        edtCodigoActivar = findViewById(R.id.edtCodigoActivar);
        btnActivarCuenta = findViewById(R.id.btnActivarCuenta);
        btnAtrasActivar = findViewById(R.id.btnAtrasActivar);
        txtReenviarCodigo = findViewById(R.id.txtReenviarCodigo);

        apiService = ApiClient.getApiService();

        // Prefill de email (desde Registro o desde Login)
        String emailPrefill = getIntent().getStringExtra("email");
        if (emailPrefill != null && !emailPrefill.isEmpty()) {
            edtEmailActivar.setText(emailPrefill);
        }

        // Botón Atrás -> volver al Login
        btnAtrasActivar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ActivarCuentaActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        // Botón Activar cuenta
        btnActivarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activarCuenta();
            }
        });

        // Texto "Reenviar código"
        txtReenviarCodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ActivarCuentaActivity.this, ReenviarActivacionActivity.class);

                // si ya hay algo escrito en el email, lo mandamos para rellenarlo allí
                String emailActual = edtEmailActivar.getText().toString().trim();
                if (!emailActual.isEmpty()) {
                    i.putExtra("email", emailActual);
                }

                startActivity(i);
            }
        });
    }

    private void activarCuenta() {
        String correo = edtEmailActivar.getText().toString()
                .trim()
                .toLowerCase(Locale.ROOT);
        String codigo = edtCodigoActivar.getText().toString().trim();

        // Validaciones
        if (TextUtils.isEmpty(correo)) {
            edtEmailActivar.setError("El email es obligatorio");
            edtEmailActivar.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            edtEmailActivar.setError("Email no válido");
            edtEmailActivar.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(codigo)) {
            edtCodigoActivar.setError("El código es obligatorio");
            edtCodigoActivar.requestFocus();
            return;
        }
        if (codigo.contains(" ")) {
            edtCodigoActivar.setError("El código no puede tener espacios");
            edtCodigoActivar.requestFocus();
            return;
        }
        if (codigo.length() != 6) {
            edtCodigoActivar.setError("El código debe tener 6 dígitos");
            edtCodigoActivar.requestFocus();
            return;
        }

        btnActivarCuenta.setEnabled(false);

        ActivarCuentaRequest body = new ActivarCuentaRequest(correo, codigo);
        Call<ResponseModel> call = apiService.activarCuenta(body);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                btnActivarCuenta.setEnabled(true);

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(ActivarCuentaActivity.this,
                            "Error del servidor: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                ResponseModel resp = response.body();
                Toast.makeText(ActivarCuentaActivity.this,
                        resp.getMessage(),
                        Toast.LENGTH_LONG).show();

                if (resp.getSuccess() == 0) {
                    // Cuenta activada -> ir a Login con el correo ya rellenado
                    Intent i = new Intent(ActivarCuentaActivity.this, LoginActivity.class);
                    i.putExtra("email", correo);
                    startActivity(i);
                    finish();
                } else {
                    // Código inválido / correo no coincide
                    if (resp.getMessage() != null &&
                            resp.getMessage().toLowerCase().contains("código")) {
                        edtCodigoActivar.setError(resp.getMessage());
                        edtCodigoActivar.requestFocus();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                btnActivarCuenta.setEnabled(true);
                Toast.makeText(ActivarCuentaActivity.this,
                        "Fallo de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}