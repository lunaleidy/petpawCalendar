package com.example.petpawcalendar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.petpawcalendar.network.ApiClient;
import com.example.petpawcalendar.network.ApiService;
import com.example.petpawcalendar.network.dto.MascotaRequest;
import com.example.petpawcalendar.network.dto.ResponseModel;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.math.BigDecimal;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NuevaMascotaActivity extends AppCompatActivity {

    // Código para el intent de seleccionar foto
    private static final int REQUEST_FOTO_GALERIA = 1001;

    // Vistas del top bar
    private LinearLayout btnAtrasNuevaMascota;
    private TextView txtTituloNuevaMascota;

    // Vistas de foto
    private ImageView imgFotoMascota;
    private ImageButton btnCambiarFotoMascota;
    private ImageButton btnBorrarFotoMascota;

    // Campos del formulario
    private EditText edtNombreMascota;
    private EditText edtEspecieMascota;
    private EditText edtRazaMascota;
    private LinearLayout layoutFechaNacimiento;
    private TextView txtFechaNacimiento;
    private Spinner spSexoMascota;
    private TextView txtEdadMascota;
    private EditText edtPesoMascota;

    // Botones inferiores
    private Button btnCancelarMascota;
    private Button btnGuardarMascota;

    // Variables de apoyo
    private ApiService apiService;
    private Calendar calendarNacimiento;
    private boolean fechaElegida = false;
    private Uri uriFotoSeleccionada = null;

    private SimpleDateFormat sdfIsoDia =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat sdfVisibleFecha =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nueva_mascota);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Inicializamos el servicio de API y el calendario
        apiService = ApiClient.getApiService();
        calendarNacimiento = Calendar.getInstance();

        // Vinculamos todas las vistas
        vincularVistas();

        // Configuramos el spinner de sexo
        configurarSpinnerSexo();

        // Configuramos los listeners de botones
        configurarEventos();
    }

    // Vinculamos cada control de la interfaz con su id
    private void vincularVistas() {
        btnAtrasNuevaMascota = findViewById(R.id.btnAtrasNuevaMascota);
        txtTituloNuevaMascota = findViewById(R.id.txtTituloNuevaMascota);

        imgFotoMascota = findViewById(R.id.imgFotoMascota);
        btnCambiarFotoMascota = findViewById(R.id.btnCambiarFotoMascota);
        btnBorrarFotoMascota = findViewById(R.id.btnBorrarFotoMascota);

        edtNombreMascota = findViewById(R.id.edtNombreMascota);
        edtEspecieMascota = findViewById(R.id.edtEspecieMascota);
        edtRazaMascota = findViewById(R.id.edtRazaMascota);

        layoutFechaNacimiento = findViewById(R.id.layoutFechaNacimiento);
        txtFechaNacimiento = findViewById(R.id.txtFechaNacimiento);
        spSexoMascota = findViewById(R.id.spSexoMascota);
        txtEdadMascota = findViewById(R.id.txtEdadMascota);
        edtPesoMascota = findViewById(R.id.edtPesoMascota);

        btnCancelarMascota = findViewById(R.id.btnCancelarMascota);
        btnGuardarMascota = findViewById(R.id.btnGuardarMascota);
    }

    // Cargamos las opciones del spinner de sexo
    private void configurarSpinnerSexo() {
        String[] opcionesSexo = new String[]{
                getString(R.string.nueva_mascota_sexo_placeholder),
                getString(R.string.nueva_mascota_sexo_f),
                getString(R.string.nueva_mascota_sexo_m),
                getString(R.string.nueva_mascota_sexo_o)
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                opcionesSexo
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSexoMascota.setAdapter(adapter);
    }

    // Definimos lo que hace cada botón de la pantalla
    private void configurarEventos() {

        // Botón atrás de la barra superior
        btnAtrasNuevaMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irAlMenuInicio();
            }
        });

        // Botón Cancelar del formulario
        btnCancelarMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irAlMenuInicio();
            }
        });

        // Al pulsar en la fecha, abrimos el DatePicker
        layoutFechaNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePicker();
            }
        });

        // Botón para elegir una foto desde la galería
        btnCambiarFotoMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirGaleriaParaFoto();
            }
        });

        // Botón para quitar la foto y volver a la imagen por defecto
        btnBorrarFotoMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uriFotoSeleccionada = null;
                imgFotoMascota.setImageResource(android.R.drawable.ic_menu_myplaces);
            }
        });

        // Botón para guardar la mascota
        btnGuardarMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarMascota();
            }
        });
    }

    // Mostramos el selector de fecha de nacimiento
    private void mostrarDatePicker() {
        int year = calendarNacimiento.get(Calendar.YEAR);
        int month = calendarNacimiento.get(Calendar.MONTH);
        int day = calendarNacimiento.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dlg = new DatePickerDialog(
                NuevaMascotaActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int y, int m, int d) {
                        calendarNacimiento.set(Calendar.YEAR, y);
                        calendarNacimiento.set(Calendar.MONTH, m);
                        calendarNacimiento.set(Calendar.DAY_OF_MONTH, d);
                        txtFechaNacimiento.setText(
                                sdfVisibleFecha.format(calendarNacimiento.getTime()));
                        fechaElegida = true;
                        actualizarEdad();
                    }
                },
                year, month, day
        );
        dlg.show();
    }

    // Calculamos y mostramos la edad aproximada en años
    private void actualizarEdad() {
        Calendar hoy = Calendar.getInstance();
        int anios = hoy.get(Calendar.YEAR) - calendarNacimiento.get(Calendar.YEAR);

        int mesHoy = hoy.get(Calendar.MONTH);
        int mesNac = calendarNacimiento.get(Calendar.MONTH);
        int diaHoy = hoy.get(Calendar.DAY_OF_MONTH);
        int diaNac = calendarNacimiento.get(Calendar.DAY_OF_MONTH);

        // Ajustamos si aún no ha cumplido años este año
        if (mesHoy < mesNac || (mesHoy == mesNac && diaHoy < diaNac)) {
            anios--;
        }
        if (anios < 0) {
            anios = 0;
        }

        String textoEdad = getString(R.string.nueva_mascota_edad_formato, anios);
        txtEdadMascota.setText(textoEdad);
    }

    // Abrimos la galería para que el usuario elija una foto
    private void abrirGaleriaParaFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_FOTO_GALERIA);
    }

    // Recibimos el resultado de la selección de la foto
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FOTO_GALERIA && resultCode == RESULT_OK && data != null) {
            uriFotoSeleccionada = data.getData();
            if (uriFotoSeleccionada != null) {
                try {
                    ContentResolver cr = getContentResolver();
                    InputStream inputStream = cr.openInputStream(uriFotoSeleccionada);
                    imgFotoMascota.setImageBitmap(BitmapFactory.decodeStream(inputStream));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this,
                            getString(R.string.nueva_mascota_error_foto),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Validamos los datos y llamamos al backend para crear la mascota
    private void guardarMascota() {
        String nombre = edtNombreMascota.getText().toString().trim();
        String especie = edtEspecieMascota.getText().toString().trim();
        String raza = edtRazaMascota.getText().toString().trim();
        String pesoTexto = edtPesoMascota.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this,
                    getString(R.string.nueva_mascota_error_nombre),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Sexo: F / M / O (Otro)
        String sexo = null;
        int posSexo = spSexoMascota.getSelectedItemPosition();
        if (posSexo == 1) {
            sexo = "F";
        } else if (posSexo == 2) {
            sexo = "M";
        } else if (posSexo == 3) {
            sexo = "O";
        }

        Double pesoKg = null;
        if (!pesoTexto.isEmpty()) {
            try {
                pesoKg = Double.parseDouble(pesoTexto);
            } catch (NumberFormatException e) {
                Toast.makeText(this,
                        getString(R.string.nueva_mascota_error_peso),
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String fechaNacIso = null;
        if (fechaElegida) {
            fechaNacIso = sdfIsoDia.format(calendarNacimiento.getTime());
        }

        // Recuperamos el token para la llamada protegida
        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null || token.isEmpty()) {
            Toast.makeText(this,
                    getString(R.string.menu_sin_token),
                    Toast.LENGTH_SHORT).show();
            irALogin();
            return;
        }
        String authHeader = "Bearer " + token;

        // Armamos el objeto MascotaRequest que espera el backend
        MascotaRequest body = new MascotaRequest();
        body.setNombre(nombre);
        body.setEspecie(especie.isEmpty() ? null : especie);
        body.setRaza(raza.isEmpty() ? null : raza);
        body.setSexo(sexo);
        if (pesoKg != null) {
            body.setPesoKg(BigDecimal.valueOf(pesoKg));
        } else {
            body.setPesoKg(null);
        }
        body.setFechaNacimiento(fechaNacIso);

        // Llamamos al endpoint POST /mascotas
        apiService.crearMascota(authHeader, body)
                .enqueue(new Callback<ResponseModel<MascotaRequest>>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseModel<MascotaRequest>> call,
                                           @NonNull Response<ResponseModel<MascotaRequest>> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(NuevaMascotaActivity.this,
                                    getString(R.string.nueva_mascota_error_guardar),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ResponseModel<MascotaRequest> resp = response.body();
                        if (resp.getSuccess() != 0 || resp.getData() == null) {
                            // Si hay mensaje de error del backend, lo mostramos
                            String msg = resp.getMessage();
                            if (msg == null || msg.isEmpty()) {
                                msg = getString(R.string.nueva_mascota_error_guardar);
                            }
                            Toast.makeText(NuevaMascotaActivity.this, msg, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Mascota creada en backend
                        MascotaRequest mascotaCreada = resp.getData();
                        int idMascota = mascotaCreada.getId();

                        // Si no se seleccionó foto, terminamos aquí
                        if (uriFotoSeleccionada == null) {
                            mostrarYVolverAlMenu();
                        } else {
                            // Si hay foto, subimos la imagen al endpoint /mascotas/{id}/foto
                            subirFotoMascota(authHeader, idMascota);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseModel<MascotaRequest>> call,
                                          @NonNull Throwable t) {
                        Toast.makeText(NuevaMascotaActivity.this,
                                getString(R.string.nueva_mascota_error_guardar),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Subimos la foto elegida al servidor
    private void subirFotoMascota(String authHeader, int idMascota) {

        try {
            ContentResolver cr = getContentResolver();
            InputStream inputStream = cr.openInputStream(uriFotoSeleccionada);
            byte[] bytes = new byte[inputStream.available()];
            int read = inputStream.read(bytes);
            inputStream.close();

            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("image/*"), bytes);

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("file", "mascota.jpg", requestFile);

            apiService.subirFotoMascota(authHeader, idMascota, body)
                    .enqueue(new Callback<ResponseModel<String>>() {
                        @Override
                        public void onResponse(@NonNull Call<ResponseModel<String>> call,
                                               @NonNull Response<ResponseModel<String>> response) {
                            // No es crítico el resultado, sólo avisamos éxito general
                            mostrarYVolverAlMenu();
                        }

                        @Override
                        public void onFailure(@NonNull Call<ResponseModel<String>> call,
                                              @NonNull Throwable t) {
                            // Si falla la foto, igual la mascota ya está creada
                            mostrarYVolverAlMenu();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            mostrarYVolverAlMenu();
        }
    }

    // Mostramos el mensaje de éxito y volvemos siempre a MenuActivity
    private void mostrarYVolverAlMenu() {
        Toast.makeText(NuevaMascotaActivity.this,
                getString(R.string.nueva_mascota_ok),
                Toast.LENGTH_SHORT).show();

        irAlMenuInicio();
    }

    // Abrimos el menú principal y limpiamos el historial
    private void irAlMenuInicio() {
        Intent i = new Intent(NuevaMascotaActivity.this, MenuActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();
    }

    // Si no hay token, mandamos al login
    private void irALogin() {
        Intent i = new Intent(NuevaMascotaActivity.this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    // Evitamos que usen el botón físico Atrás
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Toast.makeText(NuevaMascotaActivity.this,
                getString(R.string.nueva_mascota_back_msg),
                Toast.LENGTH_SHORT).show();
    }
}