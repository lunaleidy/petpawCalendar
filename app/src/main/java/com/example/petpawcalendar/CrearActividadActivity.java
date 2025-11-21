package com.example.petpawcalendar;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.petpawcalendar.network.ApiClient;
import com.example.petpawcalendar.network.ApiService;
import com.example.petpawcalendar.network.dto.ActividadRequest;
import com.example.petpawcalendar.network.dto.MascotaRequest;
import com.example.petpawcalendar.network.dto.ResponseModel;
import com.example.petpawcalendar.network.dto.TipoActividadRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearActividadActivity extends AppCompatActivity {

    private ApiService apiService;

    private LinearLayout btnAtras;
    private Spinner spMascota;
    private Spinner spTipo;
    private EditText edtTitulo;
    private TextView txtFecha;
    private TextView txtHora;
    private LinearLayout layoutFecha;
    private LinearLayout layoutHora;
    private EditText edtNotas;
    private RadioGroup rgProgramar;
    private RadioButton rbRepetir;
    private LinearLayout layoutFrecuencia;
    private Spinner spFrecuenciaNumero;
    private Spinner spFrecuenciaUnidad;
    private Button btnCancelar;
    private Button btnGuardar;

    private List<MascotaRequest> listaMascotas = new ArrayList<>();
    private List<TipoActividadRequest> listaTipos = new ArrayList<>();

    private int preselectedMascotaId = -1;
    private int preselectedTipoId = -1;
    private String preselectedFechaDia;

    private Calendar calendarFechaHora;
    private boolean fechaElegida = false;
    private boolean horaElegida = false;

    private final SimpleDateFormat sdfIsoDia =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat sdfIsoDateTime =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private final SimpleDateFormat sdfVisibleFecha =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat sdfVisibleHora =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_actividad);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        apiService = ApiClient.getApiService();
        calendarFechaHora = Calendar.getInstance();

        Intent intent = getIntent();
        preselectedMascotaId = intent.getIntExtra("idMascota", -1);
        preselectedTipoId = intent.getIntExtra("idTipoActividad", -1);
        preselectedFechaDia = intent.getStringExtra("fechaDia"); // yyyy-MM-dd

        // Vinculamos los elementos de la interfaz
        btnAtras = findViewById(R.id.btnAtrasCrearActividad);
        spMascota = findViewById(R.id.spMascotaActividad);
        spTipo = findViewById(R.id.spTipoActividad);
        edtTitulo = findViewById(R.id.edtTituloActividad);
        txtFecha = findViewById(R.id.txtFechaSeleccionada);
        txtHora = findViewById(R.id.txtHoraSeleccionada);
        layoutFecha = findViewById(R.id.layoutFecha);
        layoutHora = findViewById(R.id.layoutHora);
        edtNotas = findViewById(R.id.edtNotasActividad);
        rgProgramar = findViewById(R.id.rgProgramar);
        rbRepetir = findViewById(R.id.rbRepetir);
        layoutFrecuencia = findViewById(R.id.layoutFrecuencia);
        spFrecuenciaNumero = findViewById(R.id.spFrecuenciaNumero);
        spFrecuenciaUnidad = findViewById(R.id.spFrecuenciaUnidad);
        btnCancelar = findViewById(R.id.btnCancelarActividad);
        btnGuardar = findViewById(R.id.btnGuardarActividad);

        // Navegación
        btnCancelar.setOnClickListener(v -> finish());

        // Date/Time pickers
        layoutFecha.setOnClickListener(v -> mostrarDatePicker());
        layoutHora.setOnClickListener(v -> mostrarTimePicker());

        // Frecuencia
        initFrecuenciaSpinners();
        layoutFrecuencia.setVisibility(android.view.View.GONE);

        rgProgramar.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbRepetir) {
                layoutFrecuencia.setVisibility(android.view.View.VISIBLE);
            } else {
                layoutFrecuencia.setVisibility(android.view.View.GONE);
            }
        });

        // Configuramos el botón para ir hacia atrás
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CrearActividadActivity.this, CalendarioActivity.class);
                startActivity(intent);
            }
        });

        // Preseleccionar fecha del intent
        if (preselectedFechaDia != null) {
            try {
                calendarFechaHora.setTime(sdfIsoDia.parse(preselectedFechaDia));
                txtFecha.setText(sdfVisibleFecha.format(calendarFechaHora.getTime()));
                fechaElegida = true;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        cargarMascotas();
        cargarTiposActividad();

        btnGuardar.setOnClickListener(v -> guardarActividad());
    }

    private void initFrecuenciaSpinners() {
        // 00–60, donde 00 es placeholder no válido
        List<String> nums = new ArrayList<>();
        nums.add("00"); // placeholder
        for (int i = 1; i <= 60; i++) {
            nums.add(String.format(Locale.getDefault(), "%02d", i));
        }

        ArrayAdapter<String> numAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                nums
        ) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTextColor(Color.parseColor("#333333"));
                return tv;
            }

            // TEXTO EN EL DESPLEGABLE
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
                tv.setTextColor(position == 0 ? Color.GRAY : Color.BLACK);
                return tv;
            }
        };
        numAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFrecuenciaNumero.setAdapter(numAdapter);

        List<String> unidades = new ArrayList<>();
        unidades.add(getString(R.string.crear_act_frecuencia_unidad_placeholder)); // "Unidad"
        unidades.add("minuto");
        unidades.add("hora");
        unidades.add("día");
        unidades.add("semana");
        unidades.add("mes");
        unidades.add("año");

        ArrayAdapter<String> uniAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                unidades
        ) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTextColor(Color.parseColor("#333333"));
                return tv;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
                tv.setTextColor(position == 0 ? Color.GRAY : Color.BLACK);
                return tv;
            }
        };
        uniAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFrecuenciaUnidad.setAdapter(uniAdapter);
    }

    private void mostrarDatePicker() {
        int year = calendarFechaHora.get(Calendar.YEAR);
        int month = calendarFechaHora.get(Calendar.MONTH);
        int day = calendarFechaHora.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, y, m, d) -> {
            calendarFechaHora.set(Calendar.YEAR, y);
            calendarFechaHora.set(Calendar.MONTH, m);
            calendarFechaHora.set(Calendar.DAY_OF_MONTH, d);
            txtFecha.setText(sdfVisibleFecha.format(calendarFechaHora.getTime()));
            fechaElegida = true;
        }, year, month, day).show();
    }

    private void mostrarTimePicker() {
        int hour = calendarFechaHora.get(Calendar.HOUR_OF_DAY);
        int minute = calendarFechaHora.get(Calendar.MINUTE);

        new TimePickerDialog(this, (view, h, m) -> {
            calendarFechaHora.set(Calendar.HOUR_OF_DAY, h);
            calendarFechaHora.set(Calendar.MINUTE, m);
            txtHora.setText(sdfVisibleHora.format(calendarFechaHora.getTime()));
            horaElegida = true;
        }, hour, minute, true).show();
    }

    private void cargarMascotas() {
        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, getString(R.string.menu_sin_token), Toast.LENGTH_SHORT).show();
            return;
        }

        String authHeader = "Bearer " + token;
        apiService.listarMascotas(authHeader)
                .enqueue(new Callback<ResponseModel<List<MascotaRequest>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseModel<List<MascotaRequest>>> call,
                                           @NonNull Response<ResponseModel<List<MascotaRequest>>> response) {
                        if (!response.isSuccessful() || response.body() == null) return;

                        ResponseModel<List<MascotaRequest>> resp = response.body();
                        if (resp.getSuccess() != 0 || resp.getData() == null) return;

                        listaMascotas = resp.getData();

                        List<String> nombres = new ArrayList<>();
                        nombres.add(getString(R.string.cal_placeholder_mascota));
                        for (MascotaRequest m : listaMascotas) {
                            nombres.add(m.getNombre());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                CrearActividadActivity.this,
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
                                tv.setTextColor(position == 0 ? Color.GRAY : Color.BLACK);
                                return tv;
                            }
                        };
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spMascota.setAdapter(adapter);

                        if (preselectedMascotaId != -1) {
                            for (int i = 0; i < listaMascotas.size(); i++) {
                                if (listaMascotas.get(i).getId() == preselectedMascotaId) {
                                    spMascota.setSelection(i + 1);
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseModel<List<MascotaRequest>>> call,
                                          @NonNull Throwable t) {
                    }
                });
    }

    private void cargarTiposActividad() {
        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null || token.isEmpty()) return;

        String authHeader = "Bearer " + token;
        apiService.listarTiposActividad(authHeader)
                .enqueue(new Callback<ResponseModel<List<TipoActividadRequest>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseModel<List<TipoActividadRequest>>> call,
                                           @NonNull Response<ResponseModel<List<TipoActividadRequest>>> response) {
                        if (!response.isSuccessful() || response.body() == null) return;

                        ResponseModel<List<TipoActividadRequest>> resp = response.body();
                        if (resp.getSuccess() != 0 || resp.getData() == null) return;

                        listaTipos = resp.getData();

                        List<String> nombres = new ArrayList<>();
                        nombres.add(getString(R.string.cal_placeholder_tipo));
                        for (TipoActividadRequest t : listaTipos) {
                            nombres.add(t.getNombre());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                CrearActividadActivity.this,
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
                                tv.setTextColor(position == 0 ? Color.GRAY : Color.BLACK);
                                return tv;
                            }
                        };
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spTipo.setAdapter(adapter);

                        if (preselectedTipoId != -1) {
                            for (int i = 0; i < listaTipos.size(); i++) {
                                if (listaTipos.get(i).getId() == preselectedTipoId) {
                                    spTipo.setSelection(i + 1);
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseModel<List<TipoActividadRequest>>> call,
                                          @NonNull Throwable t) {
                    }
                });
    }

    private void guardarActividad() {
        int posMascota = spMascota.getSelectedItemPosition();
        if (posMascota <= 0) {
            Toast.makeText(this, getString(R.string.crear_act_error_mascota), Toast.LENGTH_SHORT).show();
            return;
        }

        int posTipo = spTipo.getSelectedItemPosition();
        if (posTipo <= 0) {
            Toast.makeText(this, getString(R.string.crear_act_error_tipo), Toast.LENGTH_SHORT).show();
            return;
        }

        String titulo = edtTitulo.getText().toString().trim();
        if (titulo.isEmpty()) {
            Toast.makeText(this, getString(R.string.crear_act_error_titulo), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!fechaElegida) {
            Toast.makeText(this, getString(R.string.crear_act_error_fecha), Toast.LENGTH_SHORT).show();
            return;
        }

        MascotaRequest mascotaSel = listaMascotas.get(posMascota - 1);
        TipoActividadRequest tipoSel = listaTipos.get(posTipo - 1);

        String descripcion = edtNotas.getText().toString().trim();
        String fechaDiaIso = sdfIsoDia.format(calendarFechaHora.getTime());
        String fechaInicioIso = null;
        boolean todoDia = true;

        if (horaElegida) {
            todoDia = false;
            fechaInicioIso = sdfIsoDateTime.format(calendarFechaHora.getTime());
        }

        // Repetición
        String repeticion = "ninguna";
        Integer repeticionCada = null;
        String repeticionHasta = null;

        if (rbRepetir.isChecked()) {
            int posNum = spFrecuenciaNumero.getSelectedItemPosition();
            int posUni = spFrecuenciaUnidad.getSelectedItemPosition();

            if (posNum <= 0 || posUni <= 0) {
                Toast.makeText(this, getString(R.string.crear_act_error_frecuencia), Toast.LENGTH_SHORT).show();
                return;
            }

            repeticionCada = Integer.parseInt(spFrecuenciaNumero.getSelectedItem().toString());

            String unidad = spFrecuenciaUnidad.getSelectedItem().toString().toLowerCase(Locale.ROOT);
            if (unidad.contains("minuto")) repeticion = "MINUTO";
            else if (unidad.contains("hora")) repeticion = "HORA";
            else if (unidad.contains("día") || unidad.contains("dia")) repeticion = "DIA";
            else if (unidad.contains("semana")) repeticion = "SEMANA";
            else if (unidad.contains("mes")) repeticion = "MES";
            else if (unidad.contains("año") || unidad.contains("ano")) repeticion = "ANIO";
            else repeticion = "NINGUNA"; // fallback
        }

        ActividadRequest req = new ActividadRequest();
        req.setMascota(new ActividadRequest.MascotaRef(mascotaSel.getId()));
        req.setTipo(new ActividadRequest.TipoRef(tipoSel.getId()));
        req.setTitulo(titulo);
        req.setDescripcion(descripcion.isEmpty() ? null : descripcion);
        req.setFechaDia(fechaDiaIso);
        req.setFechaInicio(fechaInicioIso);
        req.setTodoDia(todoDia);
        req.setRepeticion(repeticion);
        req.setRepeticionCada(repeticionCada);
        req.setRepeticionHasta(repeticionHasta);

        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, getString(R.string.menu_sin_token), Toast.LENGTH_SHORT).show();
            return;
        }

        String authHeader = "Bearer " + token;

        apiService.crearActividad(authHeader, req)
                .enqueue(new Callback<ResponseModel<ActividadRequest>>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseModel<ActividadRequest>> call,
                                           @NonNull Response<ResponseModel<ActividadRequest>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(CrearActividadActivity.this,
                                    getString(R.string.crear_act_guardado_error),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ResponseModel<ActividadRequest> resp = response.body();
                        if (resp.getSuccess() == 0) {
                            Toast.makeText(CrearActividadActivity.this,
                                    getString(R.string.crear_act_guardado_ok),
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(CrearActividadActivity.this,
                                    resp.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseModel<ActividadRequest>> call,
                                          @NonNull Throwable t) {
                        Toast.makeText(CrearActividadActivity.this,
                                getString(R.string.crear_act_guardado_error),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}