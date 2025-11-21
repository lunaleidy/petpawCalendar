package com.example.petpawcalendar;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.petpawcalendar.network.ApiClient;
import com.example.petpawcalendar.network.ApiService;
import com.example.petpawcalendar.network.dto.ResetConfirmarRequest;
import com.example.petpawcalendar.network.dto.ResponseModel;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetConfirmarActivity extends AppCompatActivity {

    private EditText edtEmailResetConf;
    private EditText edtCodigoResetConf;
    private EditText edtNuevaClaveResetConf;
    private EditText edtConfirmarClaveResetConf;
    private Button btnConfirmarReset;
    private LinearLayout btnAtrasResetConf;
    private ImageView imgToggleNuevaClave;
    private ImageView imgToggleConfirmarClave;

    private boolean nuevaVisible = false;
    private boolean confirmarVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_confirmar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias
        edtEmailResetConf = findViewById(R.id.edtEmailResetConf);
        edtCodigoResetConf = findViewById(R.id.edtCodigoResetConf);
        edtNuevaClaveResetConf = findViewById(R.id.edtNuevaClaveResetConf);
        edtConfirmarClaveResetConf = findViewById(R.id.edtConfirmarClaveResetConf);
        btnConfirmarReset = findViewById(R.id.btnConfirmarReset);
        btnAtrasResetConf = findViewById(R.id.btnAtrasResetConf);
        imgToggleNuevaClave = findViewById(R.id.imgToggleNuevaClave);
        imgToggleConfirmarClave = findViewById(R.id.imgToggleConfirmarClave);

        // Prefill de email si viene de la pantalla anterior
        String email = getIntent().getStringExtra("email");
        if (email != null) {
            edtEmailResetConf.setText(email);
        }

        // Atrás
        btnAtrasResetConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Toggle nueva clave
        imgToggleNuevaClave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePasswordVisibility(edtNuevaClaveResetConf);
                nuevaVisible = !nuevaVisible;
            }
        });

        // Toggle confirmar clave
        imgToggleConfirmarClave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePasswordVisibility(edtConfirmarClaveResetConf);
                confirmarVisible = !confirmarVisible;
            }
        });

        // Botón confirmar
        btnConfirmarReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmarReset();
            }
        });
    }

    private void togglePasswordVisibility(EditText editText) {
        if (editText.getTransformationMethod() instanceof PasswordTransformationMethod) {
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        editText.setSelection(editText.getText().length());
    }

    private void confirmarReset() {
        final String correo = edtEmailResetConf.getText().toString().trim().toLowerCase(Locale.ROOT);
        final String codigo = edtCodigoResetConf.getText().toString().trim().replaceAll("\\s", "");
        final String nuevaClave = edtNuevaClaveResetConf.getText().toString().trim();
        final String confirmarClave = edtConfirmarClaveResetConf.getText().toString().trim();

        // Validaciones básicas
        if (TextUtils.isEmpty(correo)) {
            edtEmailResetConf.setError("El email es obligatorio");
            edtEmailResetConf.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            edtEmailResetConf.setError("Email no válido");
            edtEmailResetConf.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(codigo)) {
            edtCodigoResetConf.setError("El código es obligatorio");
            edtCodigoResetConf.requestFocus();
            return;
        }

        if (!codigo.matches("\\d{6}")) {   // 6 dígitos, ajusta si tu código tiene otra longitud
            edtCodigoResetConf.setError("El código debe tener 6 números sin espacios");
            edtCodigoResetConf.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(nuevaClave)) {
            edtNuevaClaveResetConf.setError("La nueva contraseña es obligatoria");
            edtNuevaClaveResetConf.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(confirmarClave)) {
            edtConfirmarClaveResetConf.setError("Debes confirmar la contraseña");
            edtConfirmarClaveResetConf.requestFocus();
            return;
        }

        if (nuevaClave.contains(" ") || confirmarClave.contains(" ")) {
            edtNuevaClaveResetConf.setError("La contraseña no puede contener espacios");
            edtNuevaClaveResetConf.requestFocus();
            return;
        }

        // Reglas de seguridad: mínimo 6, una letra y un número
        if (nuevaClave.length() < 6) {
            edtNuevaClaveResetConf.setError("Debe tener al menos 6 caracteres");
            edtNuevaClaveResetConf.requestFocus();
            return;
        }
        if (!nuevaClave.matches(".*[A-Za-z].*")) {
            edtNuevaClaveResetConf.setError("Debe incluir al menos una letra");
            edtNuevaClaveResetConf.requestFocus();
            return;
        }
        if (!nuevaClave.matches(".*[0-9].*")) {
            edtNuevaClaveResetConf.setError("Debe incluir al menos un número");
            edtNuevaClaveResetConf.requestFocus();
            return;
        }
        if (!nuevaClave.equals(confirmarClave)) {
            edtConfirmarClaveResetConf.setError("Las contraseñas no coinciden");
            edtConfirmarClaveResetConf.requestFocus();
            return;
        }

        btnConfirmarReset.setEnabled(false);

        ApiService api = ApiClient.getApiService();
        ResetConfirmarRequest body = new ResetConfirmarRequest(correo, codigo, nuevaClave);

        Call<ResponseModel> call = api.resetConfirmar(body);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                btnConfirmarReset.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    ResponseModel resp = response.body();
                    Toast.makeText(ResetConfirmarActivity.this,
                            resp.getMessage(),
                            Toast.LENGTH_LONG).show();

                    if (resp.getSuccess() == 0) {
                        // Contraseña cambiada correctamente -> ir a Login
                        Intent intent = new Intent(ResetConfirmarActivity.this, LoginActivity.class);
                        intent.putExtra("email", correo);
                        startActivity(intent);
                        finish();
                    } else {
                        // Código inválido u otro problema -> sugerir solicitar uno nuevo
                        // Opcional: abrir de nuevo la pantalla de solicitud
                        if (resp.getMessage() != null &&
                                resp.getMessage().toLowerCase().contains("código")) {

                            Intent i = new Intent(ResetConfirmarActivity.this,
                                    ResetSolicitarActivity.class);
                            i.putExtra("email", correo);
                            startActivity(i);
                            finish();
                        }
                    }

                } else {
                    Toast.makeText(ResetConfirmarActivity.this,
                            "Error: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                btnConfirmarReset.setEnabled(true);
                Toast.makeText(ResetConfirmarActivity.this,
                        "Fallo de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}