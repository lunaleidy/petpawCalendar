package com.example.petpawcalendar;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.petpawcalendar.network.ApiClient;
import com.example.petpawcalendar.network.ApiService;
import com.example.petpawcalendar.network.dto.BorrarCuentaRequest;
import com.example.petpawcalendar.network.dto.ResponseModel;
import com.example.petpawcalendar.network.dto.UsuarioPerfilRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AjustesActivity extends AppCompatActivity {

    // Constantes para tabs inferiores
    private static final int TAB_INICIO = 0;
    private static final int TAB_CALENDARIO = 1;
    private static final int TAB_AJUSTES = 2;

    // Vistas de la barra superior
    private TextView txtNombreUsuarioMenu;
    private ImageView imgAvatarUsuario;
    private LinearLayout btnNuevaMascota;

    // Vistas de tabs inferiores
    private LinearLayout tabCalendario;
    private LinearLayout tabAjustes;
    private LinearLayout tabInicio;
    private TextView txtTabInicio;
    private TextView txtTabCalendario;
    private TextView txtTabAjustes;

    // Botones principales de la pantalla
    private Button btnEliminarCuenta;
    private Button btnSalirApp;

    // Cliente para peticiones HTTP
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ajustes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializamos el servicio de API
        apiService = ApiClient.getApiService();

        // Vinculamos vistas de la interfaz
        txtNombreUsuarioMenu = findViewById(R.id.txtNombreUsuarioMenu);
        imgAvatarUsuario = findViewById(R.id.imgAvatarUsuario);
        btnNuevaMascota = findViewById(R.id.btnNuevaMascota);

        tabInicio = findViewById(R.id.tabInicio);
        tabCalendario = findViewById(R.id.tabCalendario);
        tabAjustes = findViewById(R.id.tabAjustes);

        txtTabInicio = findViewById(R.id.txtTabInicio);
        txtTabCalendario = findViewById(R.id.txtTabCalendario);
        txtTabAjustes = findViewById(R.id.txtTabAjustes);

        btnEliminarCuenta = findViewById(R.id.btnEliminarCuenta);
        btnSalirApp = findViewById(R.id.btnSalirApp);

        // Botón "Nueva mascota" en la barra superior
        btnNuevaMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AjustesActivity.this, NuevaMascotaActivity.class);
                startActivity(i);
            }
        });

        // Configuramos los tabs inferiores
        configurarTabsInferiores();

        // Marcamos que estamos en la pestaña de Ajustes
        seleccionarTab(TAB_AJUSTES);

        // Cargamos los datos del usuario en la barra superior
        cargarPerfilUsuario();

        // Botón para cerrar sesión (salir de la cuenta)
        btnSalirApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });

        // Botón para eliminar totalmente la cuenta
        btnEliminarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoPedirPassword();
            }
        });
    }

    // Configuramos la navegación de los tabs inferiores
    private void configurarTabsInferiores() {

        tabInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarTab(TAB_INICIO);
                Intent i = new Intent(AjustesActivity.this, MenuActivity.class);
                startActivity(i);
                finish();
            }
        });

        tabCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarTab(TAB_CALENDARIO);
                Intent i = new Intent(AjustesActivity.this, CalendarioActivity.class);
                startActivity(i);
                finish();
            }
        });

        tabAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ya estamos en Ajustes, solo actualizamos colores
                seleccionarTab(TAB_AJUSTES);
            }
        });
    }

    // Cambiamos colores de los tabs según el que esté seleccionado
    private void seleccionarTab(int tab) {
        String colorVerde = "#48C9B0";
        String colorTextoNormal = "#333333";
        String fondoSeleccionado = "#E5E8E8";
        String fondoNormal = "#00000000";

        // Tab Inicio
        if (tab == TAB_INICIO) {
            tabInicio.setBackgroundColor(Color.parseColor(fondoSeleccionado));
            txtTabInicio.setTextColor(Color.parseColor(colorVerde));
        } else {
            tabInicio.setBackgroundColor(Color.parseColor(fondoNormal));
            txtTabInicio.setTextColor(Color.parseColor(colorTextoNormal));
        }

        // Tab Calendario
        if (tab == TAB_CALENDARIO) {
            tabCalendario.setBackgroundColor(Color.parseColor(fondoSeleccionado));
            txtTabCalendario.setTextColor(Color.parseColor(colorVerde));
        } else {
            tabCalendario.setBackgroundColor(Color.parseColor(fondoNormal));
            txtTabCalendario.setTextColor(Color.parseColor(colorTextoNormal));
        }

        // Tab Ajustes
        if (tab == TAB_AJUSTES) {
            tabAjustes.setBackgroundColor(Color.parseColor(fondoSeleccionado));
            txtTabAjustes.setTextColor(Color.parseColor(colorVerde));
        } else {
            tabAjustes.setBackgroundColor(Color.parseColor(fondoNormal));
            txtTabAjustes.setTextColor(Color.parseColor(colorTextoNormal));
        }
    }

    // Obtenemos el nombre y la foto del usuario para la barra superior
    private void cargarPerfilUsuario() {
        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null || token.isEmpty()) {
            txtNombreUsuarioMenu.setText(getString(R.string.menu_usuario_nombre));
            return;
        }

        String authHeader = "Bearer " + token;

        Call<ResponseModel<UsuarioPerfilRequest>> call = apiService.obtenerPerfil(authHeader);
        call.enqueue(new Callback<ResponseModel<UsuarioPerfilRequest>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseModel<UsuarioPerfilRequest>> call,
                                   @NonNull Response<ResponseModel<UsuarioPerfilRequest>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    txtNombreUsuarioMenu.setText(getString(R.string.menu_usuario_nombre));
                    return;
                }

                ResponseModel<UsuarioPerfilRequest> resp = response.body();
                if (resp.getSuccess() != 0 || resp.getData() == null) {
                    txtNombreUsuarioMenu.setText(getString(R.string.menu_usuario_nombre));
                    return;
                }

                UsuarioPerfilRequest datos = resp.getData();
                String primerNombre = extraerPrimerNombre(datos.getNombreCompleto());
                if (primerNombre == null || primerNombre.isEmpty()) {
                    primerNombre = getString(R.string.menu_usuario_nombre);
                }
                txtNombreUsuarioMenu.setText(primerNombre);

                String avatarUrl = normalizarUrl(datos.getFotoUrl());
                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    Glide.with(AjustesActivity.this)
                            .load(avatarUrl)
                            .placeholder(android.R.drawable.ic_menu_myplaces)
                            .circleCrop()
                            .into(imgAvatarUsuario);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseModel<UsuarioPerfilRequest>> call,
                                  @NonNull Throwable t) {
                txtNombreUsuarioMenu.setText(getString(R.string.menu_usuario_nombre));
            }
        });
    }

    // Extraemos solo el primer nombre de un nombre completo
    private String extraerPrimerNombre(String nombreCompleto) {
        if (nombreCompleto == null) {
            return "";
        }

        String limpio = nombreCompleto.trim();
        if (limpio.isEmpty()) {
            return "";
        }

        int idx = limpio.indexOf(' ');
        if (idx == -1) {
            return limpio;
        } else {
            return limpio.substring(0, idx);
        }
    }

    // Adaptamos la URL del backend para el emulador
    private String normalizarUrl(String url) {
        if (url == null) {
            return null;
        }
        return url
                .replace("http://localhost:8080", "http://10.0.2.2:8080")
                .replace("https://localhost:8080", "http://10.0.2.2:8080");
    }

    // Mostramos un Toast corto
    private void mostrarToast(String msg) {
        Toast.makeText(AjustesActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    // Cerrar sesión: limpiamos el token y volvemos al Login
    private void cerrarSesion() {
        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        prefs.edit().remove("jwt_token").apply();

        Intent i = new Intent(AjustesActivity.this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    // Primer diálogo: pedimos la contraseña para confirmar la identidad
    private void mostrarDialogoPedirPassword() {
        final android.widget.EditText edtPassword = new android.widget.EditText(this);
        edtPassword.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT |
                        android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edtPassword.setHint(R.string.ajustes_hint_password);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.ajustes_eliminar_titulo);
        builder.setMessage(R.string.ajustes_eliminar_pide_pass);
        builder.setView(edtPassword);

        builder.setPositiveButton(R.string.ajustes_eliminar_continuar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String pass = edtPassword.getText().toString().trim();
                if (pass.isEmpty()) {
                    mostrarToast(getString(R.string.ajustes_eliminar_error_pass));
                    return;
                }
                mostrarDialogoConfirmarEliminacion(pass);
            }
        });

        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    // Segundo diálogo: confirmamos si realmente desea eliminar la cuenta
    private void mostrarDialogoConfirmarEliminacion(final String password) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.ajustes_eliminar_confirm_titulo);
        builder.setMessage(R.string.ajustes_eliminar_confirm_msg);

        builder.setPositiveButton(R.string.ajustes_eliminar_confirm_aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                llamarEliminarCuenta(password);
            }
        });

        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    // Llamamos al endpoint DELETE /auth/cuenta para borrar definitivamente la cuenta
    private void llamarEliminarCuenta(String password) {
        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null || token.isEmpty()) {
            mostrarToast(getString(R.string.menu_sin_token));
            return;
        }

        String authHeader = "Bearer " + token;
        BorrarCuentaRequest body = new BorrarCuentaRequest(password);

        apiService.eliminarCuenta(authHeader, body)
                .enqueue(new Callback<ResponseModel<Void>>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseModel<Void>> call,
                                           @NonNull Response<ResponseModel<Void>> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            mostrarToast(getString(R.string.ajustes_eliminar_error));
                            return;
                        }

                        ResponseModel<Void> resp = response.body();

                        if (resp.getSuccess() == 0) {
                            // Cuenta eliminada correctamente
                            prefs.edit().remove("jwt_token").apply();
                            mostrarToast(getString(R.string.ajustes_eliminar_ok));

                            Intent i = new Intent(AjustesActivity.this, LoginActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        } else {
                            // Error lógico, por ejemplo "Clave incorrecta"
                            if (resp.getMessage() != null && !resp.getMessage().isEmpty()) {
                                mostrarToast(resp.getMessage());
                            } else {
                                mostrarToast(getString(R.string.ajustes_eliminar_error));
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseModel<Void>> call,
                                          @NonNull Throwable t) {
                        mostrarToast(getString(R.string.ajustes_eliminar_error));
                    }
                });
    }

    // Evitamos que el usuario use el botón físico Atrás
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Toast.makeText(AjustesActivity.this,
                getString(R.string.ajustes_back_msg),
                Toast.LENGTH_SHORT).show();
    }
}