package com.example.petpawcalendar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.petpawcalendar.network.ApiClient;
import com.example.petpawcalendar.network.ApiService;
import com.example.petpawcalendar.network.dto.RegistroRequest;
import com.example.petpawcalendar.network.dto.ResponseModel;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroActivity extends AppCompatActivity {

    // Views
    private LinearLayout btnAtrasRegistro;
    private EditText edtNombreUsuario;
    private EditText edtEmailRegistro;
    private EditText edtPasswordRegistro;
    private EditText edtPasswordConfirmar;
    private ImageView imgTogglePasswordRegistro;
    private ImageView imgTogglePasswordConfirmar;
    private CheckBox chkTerminos;
    private Button btnCrearCuenta;
    private TextView txtIrLogin;
    private TextView txtTerminos;

    private boolean passVisible1 = false;
    private boolean passVisible2 = false;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnAtrasRegistro = findViewById(R.id.btnAtrasRegistro);
        edtNombreUsuario = findViewById(R.id.edtNombreUsuario);
        edtEmailRegistro = findViewById(R.id.edtEmailRegistro);
        edtPasswordRegistro = findViewById(R.id.edtPasswordRegistro);
        edtPasswordConfirmar = findViewById(R.id.edtPasswordConfirmar);
        imgTogglePasswordRegistro = findViewById(R.id.imgTogglePasswordRegistro);
        imgTogglePasswordConfirmar = findViewById(R.id.imgTogglePasswordConfirmar);
        chkTerminos = findViewById(R.id.chkTerminos);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        txtIrLogin = findViewById(R.id.txtIrLogin);
        txtTerminos = findViewById(R.id.txtTerminos);

        apiService = ApiClient.getApiService();

        btnAtrasRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // volver a la pantalla anterior
            }
        });

        configurarTextoTerminos();

        imgTogglePasswordRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePassword(edtPasswordRegistro);
                passVisible1 = !passVisible1;
            }
        });


        imgTogglePasswordConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePassword(edtPasswordConfirmar);
                passVisible2 = !passVisible2;
            }
        });

        txtIrLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegistroActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentarRegistro();
            }
        });
    }

    private void configurarTextoTerminos() {
        String fullText = getString(R.string.registro_terminos_frase);
        SpannableString spannable = new SpannableString(fullText);

        String terminosText = "Términos y Condiciones";
        String politicaText = "Política de Privacidad";

        int startTerm = fullText.indexOf(terminosText);
        int endTerm = startTerm + terminosText.length();

        int startPol = fullText.indexOf(politicaText);
        int endPol = startPol + politicaText.length();

        int teal = Color.parseColor("#48C9B0");

        if (startTerm >= 0 && startPol >= 0) {

            ClickableSpan spanTerminos = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(RegistroActivity.this, TerminosActivity.class);
                    startActivity(intent);
                }
            };
            ForegroundColorSpan colorTerm = new ForegroundColorSpan(teal);
            spannable.setSpan(spanTerminos, startTerm, endTerm, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(colorTerm, startTerm, endTerm, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            ClickableSpan spanPolitica = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(RegistroActivity.this, PoliticaPrivacidadActivity.class);
                    startActivity(intent);
                }
            };
            ForegroundColorSpan colorPol = new ForegroundColorSpan(teal);
            spannable.setSpan(spanPolitica, startPol, endPol, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(colorPol, startPol, endPol, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        txtTerminos.setText(spannable);
        txtTerminos.setMovementMethod(LinkMovementMethod.getInstance());
        txtTerminos.setHighlightColor(Color.TRANSPARENT);
    }

    private void togglePassword(EditText editText) {
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();

        if ((editText.getInputType()
                & android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                == android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {

            // Ocultar
            editText.setInputType(
                    android.text.InputType.TYPE_CLASS_TEXT
                            | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            );
        } else {
            // Mostrar
            editText.setInputType(
                    android.text.InputType.TYPE_CLASS_TEXT
                            | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            );
        }

        editText.setSelection(start, end);
    }

    private void intentarRegistro() {

        String nombre = edtNombreUsuario.getText().toString().trim();
        // correo: sin espacios alrededor y en minúsculas
        String correo = edtEmailRegistro.getText().toString()
                .trim()
                .toLowerCase(Locale.ROOT);
        String pass = edtPasswordRegistro.getText().toString().trim();
        String passConfirm = edtPasswordConfirmar.getText().toString().trim();
        boolean acepta = chkTerminos.isChecked();

        // === Validaciones ===
        if (nombre.isEmpty()) {
            edtNombreUsuario.setError("Ingresa tu nombre completo");
            edtNombreUsuario.requestFocus();
            return;
        }

        if (correo.isEmpty()) {
            edtEmailRegistro.setError("Ingresa tu correo");
            edtEmailRegistro.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            edtEmailRegistro.setError("Formato de correo no válido");
            edtEmailRegistro.requestFocus();
            return;
        }

        if (pass.isEmpty()) {
            edtPasswordRegistro.setError("Ingresa una contraseña");
            edtPasswordRegistro.requestFocus();
            return;
        }
        if (pass.contains(" ")) {
            edtPasswordRegistro.setError("La contraseña no puede tener espacios");
            edtPasswordRegistro.requestFocus();
            return;
        }
        if (pass.length() < 6) {
            edtPasswordRegistro.setError("Debe tener al menos 6 caracteres");
            edtPasswordRegistro.requestFocus();
            return;
        }
        if (!pass.matches(".*[A-Za-z].*")) {
            edtPasswordRegistro.setError("Debe incluir al menos una letra");
            edtPasswordRegistro.requestFocus();
            return;
        }
        if (!pass.matches(".*[0-9].*")) {
            edtPasswordRegistro.setError("Debe incluir al menos un número");
            edtPasswordRegistro.requestFocus();
            return;
        }

        if (passConfirm.isEmpty()) {
            edtPasswordConfirmar.setError("Confirma tu contraseña");
            edtPasswordConfirmar.requestFocus();
            return;
        }
        if (!pass.equals(passConfirm)) {
            edtPasswordConfirmar.setError("Las contraseñas no coinciden");
            edtPasswordConfirmar.requestFocus();
            return;
        }

        if (!acepta) {
            Toast.makeText(this,
                    "Debes aceptar los términos y condiciones para registrarte",
                    Toast.LENGTH_LONG).show();
            return;
        }

        btnCrearCuenta.setEnabled(false);

        RegistroRequest body = new RegistroRequest(
                nombre,
                correo,
                pass,
                Boolean.TRUE
        );

        Call<ResponseModel> call = apiService.registrar(body);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                btnCrearCuenta.setEnabled(true);

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(RegistroActivity.this,
                            "Error del servidor: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                ResponseModel resp = response.body();
                Toast.makeText(RegistroActivity.this,
                        resp.getMessage(),
                        Toast.LENGTH_LONG).show();

                if (resp.getSuccess() == 0) {
                    // Registro OK -> ir a ActivarCuentaActivity
                    Intent i = new Intent(RegistroActivity.this, ActivarCuentaActivity.class);
                    i.putExtra("email", correo);
                    startActivity(i);
                    finish();
                } else {
                    // Si hay error de correo ya registrado, marcamos el campo
                    if (resp.getMessage() != null &&
                            resp.getMessage().toLowerCase().contains("correo")) {
                        edtEmailRegistro.setError(resp.getMessage());
                        edtEmailRegistro.requestFocus();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                btnCrearCuenta.setEnabled(true);
                Toast.makeText(RegistroActivity.this,
                        "Fallo de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}