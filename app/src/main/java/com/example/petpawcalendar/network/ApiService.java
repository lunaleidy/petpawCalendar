package com.example.petpawcalendar.network;

import retrofit2.Call;
import retrofit2.http.*;

import com.example.petpawcalendar.network.dto.*;

import java.util.List;

import okhttp3.MultipartBody;

public interface ApiService {

    // Endpoint del login
    @POST("/auth/login")
    Call<ResponseModel<String>> login(@Body LoginRequest body);

    // Endpoint de enviar código para restablecer la contraseña
    @POST("/auth/reset/solicitar")
    Call<ResponseModel> resetSolicitar(@Body ResetSolicitarRequest body);

    // Endpoint de confirmar código y restablecer la contraseña
    @POST("/auth/reset/confirmar")
    Call<ResponseModel> resetConfirmar(@Body ResetConfirmarRequest body);

    // Endpoint de registro de usuario
    @POST("auth/registrar")
    Call<ResponseModel> registrar(@Body RegistroRequest body);

    // Endpoint para activar la cuenta de usuario
    @POST("auth/activar")
    Call<ResponseModel> activarCuenta(@Body ActivarCuentaRequest body);

    // Endpoint para reenviar el código de activación de la cuenta de usuario
    @POST("auth/activar/reenviar")
    Call<ResponseModel> reenviarActivacion(@Body ReenviarActivacionRequest body);

    @GET("mascotas")
    Call<ResponseModel<List<MascotaRequest>>> listarMascotas(
            @Header("Authorization") String authHeader
    );

    @DELETE("mascotas/{id}")
    Call<ResponseModel<String>> eliminarMascota(
            @Header("Authorization") String authHeader,
            @Path("id") Integer idMascota
    );

    @GET("usuarios/perfil")
    Call<ResponseModel<UsuarioPerfilRequest>> obtenerPerfil(
            @Header("Authorization") String authHeader
    );

    @GET("actividades/tipos")
    Call<ResponseModel<List<TipoActividadRequest>>> listarTiposActividad(
            @Header("Authorization") String authHeader
    );

    @POST("/actividades")
    Call<ResponseModel<ActividadRequest>> crearActividad(
            @Header("Authorization") String authHeader,
            @Body ActividadRequest actividad
    );

    @HTTP(method = "DELETE", path = "auth/cuenta", hasBody = true)
    Call<ResponseModel<Void>> eliminarCuenta(
            @Header("Authorization") String authHeader,
            @Body BorrarCuentaRequest dto
    );


}
