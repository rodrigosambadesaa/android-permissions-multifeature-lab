# Enunciado — Laboratorio de permisos Android

Construir una aplicación didáctica que permita comparar el **modelo de permisos Android entre distintas versiones** y comprobarlo mediante funciones reales de la plataforma.

La práctica debe cubrir cámara, micrófono, ubicación, Bluetooth, notificaciones, acceso a fotos/vídeos, red local, alarmas exactas y alternativas como Photo Picker. Para cada capacidad debe explicarse qué permiso corresponde a la versión actual, cuándo cambia su comportamiento, si existe una alternativa menos intrusiva y cuál es el estado real del permiso al volver a la aplicación.

La aplicación integra ejemplos reales de cámara, grabación, ubicación, Bluetooth, notificaciones, red local, WorkManager y un historial local.

## Base de datos

El historial se almacena mediante `SQLiteOpenHelper` en `permission_history.db`. El `.db` se crea en ejecución y no debe subirse como binario; el esquema reproducible se incluye en [`database/schema.sql`](database/schema.sql).
