# Android Permissions Multi-Feature Lab

Práctica completa de permisos Android diseñada para estudiar **cómo cambia el modelo entre versiones**
y, al mismo tiempo, integrar APIs y componentes reales de una aplicación.

## Objetivos

La aplicación no pide permisos "porque sí". Cada tarjeta explica:

- qué capacidad protege;
- en qué API cambia el comportamiento;
- qué permiso corresponde a la versión actual;
- si existe una alternativa que evita pedir acceso amplio;
- estado real consultado en cada `onResume()`.

## Matriz incluida

| Caso | Android antiguo | Android moderno |
| --- | --- | --- |
| Cámara | runtime desde API 23 | `CAMERA` durante el uso |
| Micrófono | runtime desde API 23 | `RECORD_AUDIO`; sujeto a restricciones while-in-use |
| Ubicación | runtime desde API 23 | precisa/coarse y comportamiento dependiente de contexto |
| Bluetooth | permisos Bluetooth + ubicación para escaneo | API 31+: `BLUETOOTH_SCAN` / `BLUETOOTH_CONNECT` |
| Notificaciones | sin runtime permission | API 33+: `POST_NOTIFICATIONS` |
| Fotos/vídeos | `READ_EXTERNAL_STORAGE` | API 33+: `READ_MEDIA_*`; API 34+: acceso visual seleccionado |
| Red local | `INTERNET` bastaba para target antiguos | API 37+: `ACCESS_LOCAL_NETWORK` |
| Alarmas exactas | permiso/especial access | se gestiona desde Settings, no como un runtime permission normal |
| Photo Picker | no existía | selector del sistema que evita pedir lectura general de la galería |

## Elementos Android mezclados

- `RecyclerView` con múltiples tipos de estado.
- `ViewModel` + `LiveData`.
- Activity Result API (`RequestMultiplePermissions`, `TakePicture`, `PickVisualMedia`).
- `FileProvider`.
- Cámara y previsualización.
- `MediaRecorder` para una micrograbación.
- `LocationManager`.
- Bluetooth.
- Notification Channels y notificaciones.
- `ACCESS_LOCAL_NETWORK` y prueba HTTP local.
- `SQLiteOpenHelper` para historial.
- `WorkManager` para auditorías de permisos.
- apertura de configuración de la app y de accesos especiales.
- recursos vectoriales locales, sin imágenes externas.

## Compatibilidad

- `minSdk 21`: permite observar el comportamiento anterior a los runtime permissions.
- `compileSdk/targetSdk 37`: incorpora Android 17 y `ACCESS_LOCAL_NETWORK`.
- Java 17 + AndroidX.

## Principios de diseño

1. Pedir acceso solo cuando el usuario activa la función.
2. Mostrar explicación/rationale cuando corresponde.
3. Reconsultar siempre el estado del sistema; no persistir "concedido" como verdad.
4. Degradar funcionalidad sin bloquear toda la aplicación.
5. Preferir selectores del sistema cuando evitan permisos amplios.
6. Separar permisos de ejecución de accesos especiales.

## Pruebas sugeridas

Ejecuta la misma APK en emuladores API 22, 23, 30, 31, 33, 34 y 37 y compara la columna
**Permisos efectivos** y los diálogos del sistema.

> Nota: algunas APIs y permisos no existen en imágenes muy antiguas; el código está protegido por comprobaciones
> de versión para mantener la práctica instalable desde API 21.
