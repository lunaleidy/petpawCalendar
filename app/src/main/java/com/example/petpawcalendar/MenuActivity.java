package com.example.petpawcalendar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petpawcalendar.network.ApiClient;
import com.example.petpawcalendar.network.ApiService;
import com.example.petpawcalendar.network.dto.MascotaRequest;
import com.example.petpawcalendar.network.dto.ResponseModel;
import com.example.petpawcalendar.network.dto.UsuarioPerfilRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity {

    // Constantes para tabs
    private static final int TAB_INICIO = 0;
    private static final int TAB_CALENDARIO = 1;
    private static final int TAB_AJUSTES = 2;

    private TextView txtNombreUsuarioMenu;
    private ImageView imgAvatarUsuario;
    private LinearLayout btnNuevaMascota;

    private LinearLayout tabCalendario;
    private LinearLayout tabAjustes;
    private LinearLayout tabInicio;

    private TextView txtTabInicio;
    private TextView txtTabCalendario;
    private TextView txtTabAjustes;

    private TextView txtEmptyMascotas;
    private TextView txtEmptyAddMascota;
    private RecyclerView rvMascotas;

    private MascotaAdapter adapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        apiService = ApiClient.getApiService();

        // Referencias UI
        txtNombreUsuarioMenu = findViewById(R.id.txtNombreUsuarioMenu);
        imgAvatarUsuario = findViewById(R.id.imgAvatarUsuario);
        btnNuevaMascota = findViewById(R.id.btnNuevaMascota);

        tabInicio = findViewById(R.id.tabInicio);
        tabCalendario = findViewById(R.id.tabCalendario);
        tabAjustes = findViewById(R.id.tabAjustes);

        txtTabInicio = findViewById(R.id.txtTabInicio);
        txtTabCalendario = findViewById(R.id.txtTabCalendario);
        txtTabAjustes = findViewById(R.id.txtTabAjustes);

        txtEmptyMascotas = findViewById(R.id.txtEmptyMascotas);
        txtEmptyAddMascota = findViewById(R.id.txtEmptyAddMascota);
        rvMascotas = findViewById(R.id.rvMascotas);


        // RecyclerView
        adapter = new MascotaAdapter(this, new MascotaAdapter.OnMascotaClickListener() {
            @Override
            public void onMascotaClick(MascotaRequest mascota) {
                Intent i = new Intent(MenuActivity.this, PerfilMascotaActivity.class);
                i.putExtra("idMascota", mascota.getId());
                startActivity(i);
            }

            @Override
            public void onEliminarClick(MascotaRequest mascota) {
                confirmarEliminarMascota(mascota);
            }

        });

        rvMascotas.setLayoutManager(new LinearLayoutManager(this));
        rvMascotas.setAdapter(adapter);

        // Botón Nueva Mascota (LinearLayout clicable)
        btnNuevaMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this, NuevaMascotaActivity.class);
                startActivity(i);
            }
        });

        txtEmptyAddMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this, NuevaMascotaActivity.class);
                startActivity(i);
            }
        });

        // Tabs inferiores
        tabInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarTab(TAB_INICIO);
                // Ya estás en esta pantalla; podrías hacer scroll al principio si quieres
                rvMascotas.smoothScrollToPosition(0);
            }
        });

        tabCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarTab(TAB_CALENDARIO);
                Intent i = new Intent(MenuActivity.this, CalendarioActivity.class);
                startActivity(i);
            }
        });

        tabAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarTab(TAB_AJUSTES);
                Intent i = new Intent(MenuActivity.this, AjustesActivity.class);
                startActivity(i);
            }
        });

        // Al entrar, Inicio seleccionado
        seleccionarTab(TAB_INICIO);

        // Cargar perfil del usuario (nombre + foto)
        cargarPerfilUsuario();

        // Cargar mascotas
        cargarMascotas();

    }

    private void seleccionarTab(int tab) {
        // Colores
        String colorVerde = "#48C9B0";
        String colorTextoNormal = "#333333";
        String fondoSeleccionado = "#E5E8E8";
        String fondoNormal = "#00000000"; // transparente

        // Inicio
        if (tab == TAB_INICIO) {
            tabInicio.setBackgroundColor(Color.parseColor(fondoSeleccionado));
            txtTabInicio.setTextColor(Color.parseColor(colorVerde));
        } else {
            tabInicio.setBackgroundColor(Color.parseColor(fondoNormal));
            txtTabInicio.setTextColor(Color.parseColor(colorTextoNormal));
        }

        // Calendario
        if (tab == TAB_CALENDARIO) {
            tabCalendario.setBackgroundColor(Color.parseColor(fondoSeleccionado));
            txtTabCalendario.setTextColor(Color.parseColor(colorVerde));
        } else {
            tabCalendario.setBackgroundColor(Color.parseColor(fondoNormal));
            txtTabCalendario.setTextColor(Color.parseColor(colorTextoNormal));
        }

        // Ajustes
        if (tab == TAB_AJUSTES) {
            tabAjustes.setBackgroundColor(Color.parseColor(fondoSeleccionado));
            txtTabAjustes.setTextColor(Color.parseColor(colorVerde));
        } else {
            tabAjustes.setBackgroundColor(Color.parseColor(fondoNormal));
            txtTabAjustes.setTextColor(Color.parseColor(colorTextoNormal));
        }
    }

    private void cargarMascotas() {

        // 1) Leer el token guardado en SharedPreferences
        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null || token.isEmpty()) {
            // No hay token => volver al login o mostrar error
            mostrarErrorToast(getString(R.string.menu_sin_token));
            // Opcional: ir al login
            Intent i = new Intent(MenuActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
            return;
        }

        // 2) Llamar a la API enviando "Bearer <token>"
        String authHeader = "Bearer " + token;
        Call<ResponseModel<List<MascotaRequest>>> call = apiService.listarMascotas(authHeader);

        call.enqueue(new Callback<ResponseModel<List<MascotaRequest>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseModel<List<MascotaRequest>>> call,
                                   @NonNull Response<ResponseModel<List<MascotaRequest>>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    mostrarErrorToast(
                            getString(R.string.menu_error_cargar_mascotas_codigo, response.code())
                    );
                    actualizarEstadoLista(null);
                    return;
                }

                ResponseModel<List<MascotaRequest>> resp = response.body();
                if (resp.getSuccess() == 0) {
                    List<MascotaRequest> lista = resp.getData();
                    adapter.setLista(lista);
                    actualizarEstadoLista(lista);
                } else {
                    mostrarErrorToast(resp.getMessage());
                    actualizarEstadoLista(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseModel<List<MascotaRequest>>> call,
                                  @NonNull Throwable t) {
                mostrarErrorToast(
                        getString(R.string.menu_fallo_conexion, t.getMessage())
                );
                actualizarEstadoLista(null);
            }
        });
    }

    private void cargarPerfilUsuario() {
        // 1) Leer token de SharedPreferences
        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null || token.isEmpty()) {
            // No hay token => nombre genérico y avatar por defecto
            txtNombreUsuarioMenu.setText(getString(R.string.menu_usuario_nombre));
            return;
        }

        String authHeader = "Bearer " + token;

        Call<ResponseModel<UsuarioPerfilRequest>> call =
                apiService.obtenerPerfil(authHeader);

        call.enqueue(new Callback<ResponseModel<UsuarioPerfilRequest>>() {
            @Override
            public void onResponse(
                    @NonNull Call<ResponseModel<UsuarioPerfilRequest>> call,
                    @NonNull Response<ResponseModel<UsuarioPerfilRequest>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    // Si falla, mostramos el nombre genérico
                    txtNombreUsuarioMenu.setText(getString(R.string.menu_usuario_nombre));
                    return;
                }

                ResponseModel<UsuarioPerfilRequest> resp = response.body();
                if (resp.getSuccess() != 0) {
                    // Error lógico desde el backend
                    txtNombreUsuarioMenu.setText(getString(R.string.menu_usuario_nombre));
                    return;
                }

                UsuarioPerfilRequest datos = resp.getData();
                if (datos == null) {
                    txtNombreUsuarioMenu.setText(getString(R.string.menu_usuario_nombre));
                    return;
                }

                // 2) Nombre: solo la primera palabra
                String nombreCompleto = datos.getNombreCompleto();
                String primerNombre = extraerPrimerNombre(nombreCompleto);
                if (primerNombre == null || primerNombre.length() == 0) {
                    primerNombre = getString(R.string.menu_usuario_nombre);
                }
                txtNombreUsuarioMenu.setText(primerNombre);

                // 3) Foto
                String avatarUrl = normalizarUrl(datos.getFotoUrl());
                if (avatarUrl != null && avatarUrl.length() > 0) {
                    Glide.with(MenuActivity.this)
                            .load(avatarUrl)
                            .placeholder(android.R.drawable.ic_menu_myplaces)
                            .circleCrop()
                            .into(imgAvatarUsuario);
                }

            }

            @Override
            public void onFailure(
                    @NonNull Call<ResponseModel<UsuarioPerfilRequest>> call,
                    @NonNull Throwable t) {

                // Si hay fallo de red, simplemente dejamos el nombre genérico
                txtNombreUsuarioMenu.setText(getString(R.string.menu_usuario_nombre));
            }
        });
    }

    private void confirmarEliminarMascota(MascotaRequest mascota) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(R.string.menu_eliminar_titulo)
                .setMessage(getString(R.string.menu_eliminar_mascota_pregunta, mascota.getNombre()))
                .setPositiveButton(R.string.menu_eliminar_confirmar, (dialog, which) -> {
                    eliminarMascotaEnServidor(mascota);
                })
                .setNegativeButton(R.string.menu_eliminar_cancelar, null)
                .show();
    }

    private void eliminarMascotaEnServidor(MascotaRequest mascota) {
        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null || token.isEmpty()) {
            mostrarErrorToast(getString(R.string.menu_sin_token));
            return;
        }

        String authHeader = "Bearer " + token;

        Call<ResponseModel<String>> call =
                apiService.eliminarMascota(authHeader, mascota.getId());

        call.enqueue(new Callback<ResponseModel<String>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseModel<String>> call,
                                   @NonNull Response<ResponseModel<String>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    mostrarErrorToast(getString(R.string.menu_error_eliminar_mascota, response.code()));
                    return;
                }

                ResponseModel<String> resp = response.body();
                if (resp.getSuccess() == 0) {
                    Toast.makeText(MenuActivity.this,
                            getString(R.string.menu_eliminar_ok, mascota.getNombre()),
                            Toast.LENGTH_SHORT).show();

                    // Recargar lista (simple)
                    cargarMascotas();
                } else {
                    mostrarErrorToast(resp.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseModel<String>> call,
                                  @NonNull Throwable t) {
                mostrarErrorToast(getString(R.string.menu_fallo_conexion, t.getMessage()));
            }
        });
    }

    private String extraerPrimerNombre(String nombreCompleto) {
        if (nombreCompleto == null) {
            return "";
        }

        String limpio = nombreCompleto.trim();
        if (limpio.length() == 0) {
            return "";
        }

        int indiceEspacio = limpio.indexOf(' ');
        if (indiceEspacio == -1) {
            // No tiene espacios, devolvemos todo
            return limpio;
        } else {
            // Desde 0 hasta el primer espacio
            return limpio.substring(0, indiceEspacio);
        }
    }

    private void actualizarEstadoLista(List<MascotaRequest> lista) {
        if (lista == null || lista.isEmpty()) {
            txtEmptyMascotas.setVisibility(View.VISIBLE);
            txtEmptyAddMascota.setVisibility(View.VISIBLE);
            rvMascotas.setVisibility(View.GONE);
        } else {
            txtEmptyMascotas.setVisibility(View.GONE);
            txtEmptyAddMascota.setVisibility(View.GONE);
            rvMascotas.setVisibility(View.VISIBLE);
        }
    }

    private String normalizarUrl(String url) {
        if (url == null) return null;

        // Cambia solo si viene con localhost
        return url.replace("http://localhost:8080", "http://10.0.2.2:8080");
    }

    private void mostrarErrorToast(String msg) {
        Toast.makeText(MenuActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // No hacemos nada para bloquear el botón "Atrás" del sistema
    }

}