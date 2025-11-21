package com.example.petpawcalendar;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petpawcalendar.network.ApiClient;
import com.example.petpawcalendar.network.ApiService;
import com.example.petpawcalendar.network.dto.LoginRequest;
import com.example.petpawcalendar.network.dto.ResponseModel;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    // Views
    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnEntrar;
    private LinearLayout btnAtras;
    private TextView txtRegistrarse;
    private TextView txtOlvidarPassword;
    private ImageView imgTogglePassword;
    private boolean passwordVisible = false;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1) Referencias a las vistas
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnEntrar = findViewById(R.id.btnEntrar);
        btnAtras = findViewById(R.id.btnAtras);
        txtRegistrarse = findViewById(R.id.txtRegistrarse);
        txtOlvidarPassword = findViewById(R.id.txtOlvidarPassword);
        imgTogglePassword = findViewById(R.id.imgTogglePassword);

        // 2) Instancia del servicio de API
        apiService = ApiClient.getApiService();

        // >>> PREFILL EMAIL SI VIENE DE OTRA PANTALLA <<<
        String emailDesdeReset = getIntent().getStringExtra("email");
        if (emailDesdeReset != null && !emailDesdeReset.isEmpty()) {
            edtEmail.setText(emailDesdeReset);
        }

        // 3) Botón Atrás -> volver a la pantalla anterior (MainActivity)
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Simplemente cerramos esta Activity
                finish();
            }
        });

        // 4) Texto "Registrarse" -> ir a RegistroActivity
        txtRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });

        // 5) Texto "¿Olvidaste tu contraseña?"
        txtOlvidarPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correoActual = edtEmail.getText().toString().trim();

                Intent intent = new Intent(LoginActivity.this, ResetSolicitarActivity.class);
                intent.putExtra("email", correoActual);
                startActivity(intent);
            }
        });

        // 6) Botón Entrar -> intentar login
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentarLogin();
            }
        });

        imgTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (passwordVisible) {
                    // Volver a ocultar
                    edtPassword.setInputType(
                            android.text.InputType.TYPE_CLASS_TEXT
                                    | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                    );
                    passwordVisible = false;

                    // (opcional) cambiar icono cuando esté oculto
                    imgTogglePassword.setImageResource(android.R.drawable.ic_menu_view);

                } else {
                    // Mostrar caracteres
                    edtPassword.setInputType(
                            android.text.InputType.TYPE_CLASS_TEXT
                                    | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    );
                    passwordVisible = true;

                    // (opcional) icono diferente cuando se ve la contraseña
                    imgTogglePassword.setImageResource(android.R.drawable.ic_menu_view);
                }

                // Mantener el cursor al final
                edtPassword.setSelection(edtPassword.getText().length());
            }
        });

    }

    private void intentarLogin() {
        String correo = edtEmail.getText().toString().trim().toLowerCase(Locale.ROOT);
        String clave = edtPassword.getText().toString().trim();

        // Validación básica
        if (correo.isEmpty()) {
            edtEmail.setError("Ingresa tu correo");
            edtEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            edtEmail.setError("Formato de correo no válido");
            edtEmail.requestFocus();
            return;
        }
        if (clave.isEmpty()) {
            edtPassword.setError("Ingresa tu contraseña");
            edtPassword.requestFocus();
            return;
        }

        // Deshabilitamos el botón para evitar doble click
        btnEntrar.setEnabled(false);

        // Construimos el body del login
        LoginRequest request = new LoginRequest(correo, clave);

        Call<ResponseModel<String>> call = apiService.login(request);
        call.enqueue(new Callback<ResponseModel<String>>() {
            @Override
            public void onResponse(Call<ResponseModel<String>> call,
                                   Response<ResponseModel<String>> response) {

                btnEntrar.setEnabled(true);

                if (!response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this,
                            "Error del servidor: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                ResponseModel<String> body = response.body();
                if (body == null) {
                    Toast.makeText(LoginActivity.this,
                            "Respuesta vacía del servidor",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (body.getSuccess() == 0) {
                    // AHORA getData() YA ES String
                    String token = body.getData();

                    guardarToken(token);

                    Toast.makeText(LoginActivity.this,
                            "Login correcto",
                            Toast.LENGTH_SHORT).show();

                    irAlMenu();
                } else {
                    Toast.makeText(LoginActivity.this,
                            body.getMessage(),
                            Toast.LENGTH_LONG).show();

                    if (body.getMessage() != null &&
                            body.getMessage().toLowerCase().contains("no activada")) {

                        Intent i = new Intent(LoginActivity.this, ActivarCuentaActivity.class);
                        i.putExtra("email", correo);
                        startActivity(i);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseModel<String>> call, Throwable t) {
                btnEntrar.setEnabled(true);
                Toast.makeText(LoginActivity.this,
                        "Fallo de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarToken(String token) {
        SharedPreferences prefs =
                getSharedPreferences("auth_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("jwt_token", token);
        editor.apply();
    }

    private void irAlMenu() {
        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
        // si no quieres que el usuario vuelva al login con “atrás”:
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}