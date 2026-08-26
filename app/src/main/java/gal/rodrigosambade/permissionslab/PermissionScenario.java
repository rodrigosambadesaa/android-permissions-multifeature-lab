package gal.rodrigosambade.permissionslab;

enum PermissionScenario {
    CAMERA("Cámara", "Captura una foto mediante FileProvider."),
    MICROPHONE("Micrófono", "Graba una muestra de audio durante 3 segundos."),
    LOCATION("Ubicación", "Lee la última ubicación conocida si existe."),
    BLUETOOTH("Bluetooth cercano", "Consulta dispositivos vinculados, con modelo distinto antes/después de API 31."),
    NOTIFICATIONS("Notificaciones", "Publica una notificación de demostración; runtime desde API 33."),
    MEDIA_LIBRARY("Biblioteca multimedia", "Compara permisos legacy, READ_MEDIA_* y acceso parcial."),
    PHOTO_PICKER("Photo Picker sin permiso amplio", "Selecciona una imagen con selector del sistema."),
    LOCAL_NETWORK("Red local", "Prueba una conexión HTTP al host 10.0.2.2; API 37 añade ACCESS_LOCAL_NETWORK."),
    EXACT_ALARM("Alarma exacta (acceso especial)", "Abre el panel de acceso especial; no es un runtime permission normal.");

    final String title;
    final String description;

    PermissionScenario(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
