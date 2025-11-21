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

    // Endpoint de listar las mascotas del usuario
    @GET("mascotas")
    Call<ResponseModel<List<MascotaRequest>>> listarMascotas(
            @Header("Authorization") String authHeader
    );

    // Endpoint de eliminar mascotas del usuario
    @DELETE("mascotas/{id}")
    Call<ResponseModel<String>> eliminarMascota(
            @Header("Authorization") String authHeader,
            @Path("id") Integer idMascota
    );

    // Endpoint de obtener el perfil del usuario
    @GET("usuarios/perfil")
    Call<ResponseModel<UsuarioPerfilRequest>> obtenerPerfil(
            @Header("Authorization") String authHeader
    );

    // Endpoint de listar los tipos de actividades
    @GET("actividades/tipos")
    Call<ResponseModel<List<TipoActividadRequest>>> listarTiposActividad(
            @Header("Authorization") String authHeader
    );

    // Endpoint de crear una actividad nueva
    @POST("/actividades")
    Call<ResponseModel<ActividadRequest>> crearActividad(
            @Header("Authorization") String authHeader,
            @Body ActividadRequest actividad
    );

    // Endpoint de eliminar la cuenta de usuario
    @HTTP(method = "DELETE", path = "auth/cuenta", hasBody = true)
    Call<ResponseModel<Void>> eliminarCuenta(
            @Header("Authorization") String authHeader,
            @Body BorrarCuentaRequest dto
    );

    // Endoint de crear mascota
    @POST("/mascotas")
    Call<ResponseModel<MascotaRequest>> crearMascota(
            @Header("Authorization") String authHeader,
            @Body MascotaRequest body
    );

    // Endpoint de subir foto de mascota
    @Multipart
    @PUT("/mascotas/{id}/foto")
    Call<ResponseModel<String>> subirFotoMascota(
            @Header("Authorization") String authHeader,
            @Path("id") int idMascota,
            @Part MultipartBody.Part file
    );

}
