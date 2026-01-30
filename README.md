# PetPaw Calendar – Frontend Android

Aplicación Android en Java que consume el backend REST de PetPaw Calendar.
Permite gestionar usuarios, mascotas y eventos de calendario.

## Requisitos

- Android Studio (Giraffe o superior)
- JDK 17
- Dispositivo/emulador con Android 13 (API 33) o superior
- Backend de PetPaw Calendar ejecutándose (por defecto en `http://localhost:8080`)

## 1. Abrir el proyecto

1. En Android Studio, seleccionar **File > Open**.
2. Elegir la carpeta del proyecto `PetPawCalendar`.
3. Esperar a que Gradle sincronice las dependencias (están definidas en `build.gradle` y `libs.versions.toml`).

No es necesario añadir librerías manualmente:  
Retrofit, OkHttp, Glide, Lottie, RecyclerView y CardView ya están configuradas.

## 2. Configurar la URL del backend

En la clase donde se crea Retrofit (por ejemplo `ApiClient`, `RetrofitInstance` o similar):

```java
private static final String BASE_URL = "[http://10.0.2.2:8080/](http://10.0.2.2:8080/)"; // para emulador Android
// Si se usa dispositivo físico, sustituir por la IP del PC
```

## 📺 Demostración de la aplicación
https://youtu.be/6HHadcy1LfA