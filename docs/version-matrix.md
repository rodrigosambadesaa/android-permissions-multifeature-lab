# Matriz didáctica por API

## API 21-22
Los permisos declarados en el manifiesto se conceden al instalar. No existe diálogo de runtime permission.

## API 23
Se introduce el modelo de permisos peligrosos en tiempo de ejecución.

## API 31
Bluetooth cercano se separa de la ubicación con `BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT`
y `BLUETOOTH_ADVERTISE`.

## API 33
`POST_NOTIFICATIONS` pasa a ser permiso de ejecución y el acceso multimedia se divide en
`READ_MEDIA_IMAGES`, `READ_MEDIA_VIDEO` y `READ_MEDIA_AUDIO`.

## API 34
Android 14 introduce acceso parcial a fotos/vídeos y `READ_MEDIA_VISUAL_USER_SELECTED`.
Para muchos casos conviene Photo Picker en lugar de permisos de biblioteca.

## API 37
Apps con target Android 17 deben solicitar `ACCESS_LOCAL_NETWORK` para acceso LAN amplio.

## Nota de seguridad
El estado de permisos se consulta al sistema cada vez que la Activity vuelve a primer plano.
El historial SQLite registra decisiones y auditorías, pero **no se usa como fuente de verdad**.
