package com.example.petpawcalendar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.petpawcalendar.network.ApiClient;
import com.example.petpawcalendar.network.ApiService;
import com.example.petpawcalendar.network.dto.MascotaRequest;
import com.example.petpawcalendar.network.dto.ResponseModel;
import com.example.petpawcalendar.network.dto.TipoActividadRequest;
import com.example.petpawcalendar.network.dto.UsuarioPerfilRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarioActivity extends AppCompatActivity {

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

    private Spinner spMascotas;
    private Spinner spTiposActividad;
    private CalendarView calendarView;

    private LinearLayout btnNuevaActividad;

    private ApiService apiService;

    private List<MascotaRequest> listaMascotas = new ArrayList<>();
    private List<TipoActividadRequest> listaTipos = new ArrayList<>();

    // fecha seleccionada en el calendario
    private long selectedDateMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        apiService = ApiClient.getApiService();

        // Referencias UI comunes
        txtNombreUsuarioMenu = findViewById(R.id.txtNombreUsuarioMenu);
        imgAvatarUsuario = findViewById(R.id.imgAvatarUsuario);
        btnNuevaMascota = findViewById(R.id.btnNuevaMascota);

        tabInicio = findViewById(R.id.tabInicio);
        tabCalendario = findViewById(R.id.tabCalendario);
        tabAjustes = findViewById(R.id.tabAjustes);

        txtTabInicio = findViewById(R.id.txtTabInicio);
        txtTabCalendario = findViewById(R.id.txtTabCalendario);
        txtTabAjustes = findViewById(R.id.txtTabAjustes);

        spMascotas = findViewById(R.id.spMascotas);
        spTiposActividad = findViewById(R.id.spTiposActividad);
        calendarView = findViewById(R.id.calendarView);
        btnNuevaActividad = findViewById(R.id.btnNuevaActividad);

        // Botón Nueva Mascota
        btnNuevaMascota.setOnClickListener(v -> {
            Intent i = new Intent(CalendarioActivity.this, NuevaMascotaActivity.class);
            startActivity(i);
        });

        // Tabs
        tabInicio.setOnClickListener(v -> {
            seleccionarTab(TAB_INICIO);
            Intent i = new Intent(CalendarioActivity.this, MenuActivity.class);
            startActivity(i);
            finish();
        });

        tabCalendario.setOnClickListener(v -> {
            // Ya estamos aquí; solo mantenemos el estado visual
            seleccionarTab(TAB_CALENDARIO);
        });

        tabAjustes.setOnClickListener(v -> {
            seleccionarTab(TAB_AJUSTES);
            Intent i = new Intent(CalendarioActivity.this, AjustesActivity.class);
            startActivity(i);
            finish();
        });

        seleccionarTab(TAB_CALENDARIO);

        // Perfil usuario
        cargarPerfilUsuario();

        // Spinners
        cargarMascotasEnSpinner();
        cargarTiposActividad();

        // ====== FECHA SELECCIONADA DEL CALENDARIO ======
        // Por defecto, la fecha actual que marca el CalendarView
        selectedDateMillis = calendarView.getDate();

        // Cuando el usuario toca un día, actualizamos selectedDateMillis
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar c = Calendar.getInstance();
            c.set(year, month, dayOfMonth, 0, 0, 0);
            c.set(Calendar.MILLISECOND, 0);
            selectedDateMillis = c.getTimeInMillis();
        });

        // Botón "+ Añadir actividad"
        btnNuevaActividad.setOnClickListener(v -> irACrearActividad());
    }

    private void seleccionarTab(int tab) {
        String colorVerde = "#48C9B0";
        String colorTextoNormal = "#333333";
        String fondoSeleccionado = "#E5E8E8";
        String fondoNormal = "#00000000";

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

    // ===== PERFIL USUARIO =====
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
                    Glide.with(CalendarioActivity.this)
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

    // ===== SPINNER MASCOTAS =====
    private void cargarMascotasEnSpinner() {
        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null || token.isEmpty()) {
            mostrarToast(getString(R.string.menu_sin_token));
            irALogin();
            return;
        }

        String authHeader = "Bearer " + token;

        apiService.listarMascotas(authHeader)
                .enqueue(new Callback<ResponseModel<List<MascotaRequest>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseModel<List<MascotaRequest>>> call,
                                           @NonNull Response<ResponseModel<List<MascotaRequest>>> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            mostrarToast(getString(R.string.menu_error_cargar_mascotas_codigo,
                                    response.code()));
                            return;
                        }

                        ResponseModel<List<MascotaRequest>> resp = response.body();
                        if (resp.getSuccess() != 0 || resp.getData() == null) {
                            mostrarToast(resp.getMessage());
                            return;
                        }

                        listaMascotas = resp.getData();

                        // Lista de nombres con placeholder en posición 0
                        List<String> nombres = new ArrayList<>();
                        nombres.add(getString(R.string.cal_placeholder_mascota)); // "Mascota:"
                        for (MascotaRequest m : listaMascotas) {
                            nombres.add(m.getNombre());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                CalendarioActivity.this,
                                android.R.layout.simple_spinner_item,
                                nombres
                        ) {
                            @Override
                            public boolean isEnabled(int position) {
                                // posición 0 deshabilitada
                                return position != 0;
                            }

                            @Override
                            public TextView getDropDownView(int position, android.view.View convertView,
                                                            android.view.ViewGroup parent) {
                                TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
                                if (position == 0) {
                                    tv.setTextColor(Color.GRAY);
                                } else {
                                    tv.setTextColor(Color.BLACK);
                                }
                                return tv;
                            }
                        };
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spMascotas.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseModel<List<MascotaRequest>>> call,
                                          @NonNull Throwable t) {
                        mostrarToast(getString(R.string.menu_fallo_conexion, t.getMessage()));
                    }
                });
    }

    // ===== SPINNER TIPOS ACTIVIDAD =====
    private void cargarTiposActividad() {
        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null || token.isEmpty()) {
            mostrarToast(getString(R.string.menu_sin_token));
            irALogin();
            return;
        }

        String authHeader = "Bearer " + token;

        apiService.listarTiposActividad(authHeader)
                .enqueue(new Callback<ResponseModel<List<TipoActividadRequest>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseModel<List<TipoActividadRequest>>> call,
                                           @NonNull Response<ResponseModel<List<TipoActividadRequest>>> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            mostrarToast("Error al cargar tipos de actividad (" + response.code() + ")");
                            return;
                        }

                        ResponseModel<List<TipoActividadRequest>> resp = response.body();
                        if (resp.getSuccess() != 0 || resp.getData() == null) {
                            mostrarToast(resp.getMessage());
                            return;
                        }

                        listaTipos = resp.getData();

                        List<String> nombres = new ArrayList<>();
                        nombres.add(getString(R.string.cal_placeholder_tipo)); // "Tipo de evento:"
                        for (TipoActividadRequest t : listaTipos) {
                            nombres.add(t.getNombre());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                CalendarioActivity.this,
                                android.R.layout.simple_spinner_item,
                                nombres
                        ) {
                            @Override
                            public boolean isEnabled(int position) {
                                return position != 0;
                            }

                            @Override
                            public TextView getDropDownView(int position, android.view.View convertView,
                                                            android.view.ViewGroup parent) {
                                TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
                                if (position == 0) {
                                    tv.setTextColor(Color.GRAY);
                                } else {
                                    tv.setTextColor(Color.BLACK);
                                }
                                return tv;
                            }
                        };
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spTiposActividad.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseModel<List<TipoActividadRequest>>> call,
                                          @NonNull Throwable t) {
                        mostrarToast(getString(R.string.menu_fallo_conexion, t.getMessage()));
                    }
                });
    }

    // ===== NAVEGAR A CREAR ACTIVIDAD =====
    private void irACrearActividad() {
        if (listaMascotas == null || listaMascotas.isEmpty()) {
            mostrarToast(getString(R.string.cal_error_sin_mascotas));
            return;
        }
        if (listaTipos == null || listaTipos.isEmpty()) {
            mostrarToast(getString(R.string.cal_error_sin_tipos));
            return;
        }

        int posMascotaSpinner = spMascotas.getSelectedItemPosition();
        int posTipoSpinner = spTiposActividad.getSelectedItemPosition();

        // 0 es el placeholder => obligatorio elegir algo > 0
        if (posMascotaSpinner <= 0) {
            mostrarToast(getString(R.string.cal_error_sel_mascota));
            return;
        }
        if (posTipoSpinner <= 0) {
            mostrarToast(getString(R.string.cal_error_sel_tipo));
            return;
        }

        int idxMascota = posMascotaSpinner - 1; // restar placeholder
        int idxTipo = posTipoSpinner - 1;

        MascotaRequest mascotaSel = listaMascotas.get(idxMascota);
        TipoActividadRequest tipoSel = listaTipos.get(idxTipo);

        String fechaIso = getSelectedDateIso();

        Intent i = new Intent(CalendarioActivity.this, CrearActividadActivity.class);
        i.putExtra("idMascota", mascotaSel.getId());
        i.putExtra("idTipoActividad", tipoSel.getId());
        i.putExtra("fechaDia", fechaIso);
        startActivity(i);
    }

    private String getSelectedDateIso() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(selectedDateMillis));
    }

    // ===== Helpers comunes =====
    private String extraerPrimerNombre(String nombreCompleto) {
        if (nombreCompleto == null) return "";
        String limpio = nombreCompleto.trim();
        if (limpio.isEmpty()) return "";

        int idx = limpio.indexOf(' ');
        return (idx == -1) ? limpio : limpio.substring(0, idx);
    }

    private String normalizarUrl(String url) {
        if (url == null) return null;
        return url
                .replace("http://localhost:8080", "http://10.0.2.2:8080")
                .replace("https://localhost:8080", "http://10.0.2.2:8080");
    }

    private void mostrarToast(String msg) {
        Toast.makeText(CalendarioActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void irALogin() {
        Intent i = new Intent(CalendarioActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Bloqueamos el botón atrás del sistema
    }
}