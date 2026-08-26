package gal.rodrigosambade.permissionslab;

import android.Manifest;
import android.app.AlarmManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements PermissionAdapter.Callback {
    private PermissionAdapter adapter;
    private PermissionViewModel viewModel;
    private PermissionScenario pendingScenario;
    private ImageView preview;
    private Uri pendingPhotoUri;
    private MediaRecorder recorder;

    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), this::onPermissionsResult);

    private final ActivityResultLauncher<PickVisualMediaRequest> photoPicker =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    preview.setImageURI(uri);
                    logEvent("Photo Picker devolvió " + uri);
                } else {
                    viewModel.post("Selección cancelada");
                }
            });

    private final ActivityResultLauncher<Uri> takePicture =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), ok -> {
                if (ok && pendingPhotoUri != null) {
                    preview.setImageURI(pendingPhotoUri);
                    logEvent("Foto capturada correctamente");
                } else {
                    viewModel.post("Captura cancelada");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView sdk = findViewById(R.id.tvSdk);
        TextView event = findViewById(R.id.tvEvent);
        preview = findViewById(R.id.imagePreview);
        RecyclerView recycler = findViewById(R.id.recyclerPermissions);
        Button audit = findViewById(R.id.btnAudit);
        Button history = findViewById(R.id.btnHistory);
        Button settings = findViewById(R.id.btnSettings);

        sdk.setText("SDK dispositivo: " + Build.VERSION.SDK_INT +
                " · targetSdk: " + getApplicationInfo().targetSdkVersion);

        viewModel = new ViewModelProvider(this).get(PermissionViewModel.class);
        viewModel.event().observe(this, event::setText);

        adapter = new PermissionAdapter(this);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        audit.setOnClickListener(v -> {
            WorkManager.getInstance(this).enqueue(
                    new OneTimeWorkRequest.Builder(PermissionAuditWorker.class).build());
            viewModel.post("Auditoría programada con WorkManager");
        });

        history.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
        settings.setOnClickListener(v -> openAppSettings());

        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        if (adapter == null) return;
        List<PermissionItem> items = new ArrayList<>();
        for (PermissionScenario scenario : PermissionScenario.values()) {
            List<String> permissions = PermissionPolicy.permissionsFor(scenario, Build.VERSION.SDK_INT);
            String rendered = permissions.isEmpty()
                    ? "ninguno"
                    : permissions.stream()
                        .map(PermissionState::shortName)
                        .collect(Collectors.joining(", "));
            items.add(new PermissionItem(
                    scenario,
                    rendered,
                    PermissionState.evaluate(this, scenario)));
        }
        adapter.submit(items);
    }

    @Override
    public void onRequest(PermissionScenario scenario) {
        pendingScenario = scenario;

        if (scenario == PermissionScenario.EXACT_ALARM) {
            requestExactAlarmAccess();
            return;
        }

        List<String> permissions = PermissionPolicy.permissionsFor(scenario, Build.VERSION.SDK_INT);
        if (permissions.isEmpty()) {
            viewModel.post(Build.VERSION.SDK_INT < 23
                    ? "En esta API el modelo es legacy: no existe diálogo runtime."
                    : "Este caso no requiere runtime permission en esta API.");
            return;
        }

        boolean rationale = false;
        for (String permission : permissions) {
            rationale |= shouldShowRequestPermissionRationale(permission);
        }

        if (rationale) {
            new AlertDialog.Builder(this)
                    .setTitle("Por qué se solicita")
                    .setMessage(scenario.description + "\n\nPuedes cancelar y seguir usando el resto del laboratorio.")
                    .setNegativeButton("Cancelar", null)
                    .setPositiveButton("Continuar", (d, which) ->
                            permissionLauncher.launch(permissions.toArray(new String[0])))
                    .show();
        } else {
            permissionLauncher.launch(permissions.toArray(new String[0]));
        }
    }

    @Override
    public void onDemo(PermissionScenario scenario) {
        if (!isGrantedForDemo(scenario)) {
            viewModel.post("La función no está disponible con el estado actual. Solicita el permiso o acceso.");
            return;
        }

        switch (scenario) {
            case CAMERA:
                capturePhoto();
                break;
            case MICROPHONE:
                recordThreeSeconds();
                break;
            case LOCATION:
                showLastLocation();
                break;
            case BLUETOOTH:
                showBluetoothDevices();
                break;
            case NOTIFICATIONS:
                DemoNotification.send(this);
                logEvent("Notificación de demostración solicitada");
                break;
            case MEDIA_LIBRARY:
            case PHOTO_PICKER:
                launchPhotoPicker();
                break;
            case LOCAL_NETWORK:
                probeLocalNetwork();
                break;
            case EXACT_ALARM:
                requestExactAlarmAccess();
                break;
        }
    }

    private boolean isGrantedForDemo(PermissionScenario scenario) {
        String state = PermissionState.evaluate(this, scenario);
        return state.startsWith("Concedido")
                || state.startsWith("No requiere")
                || state.startsWith("Modelo legacy")
                || state.startsWith("Acceso especial concedido");
    }

    private void onPermissionsResult(Map<String, Boolean> result) {
        boolean allGranted = !result.isEmpty();
        for (boolean granted : result.values()) allGranted &= granted;
        String event = (pendingScenario == null ? "Permisos" : pendingScenario.title)
                + ": " + (allGranted ? "concedidos" : "concesión parcial/denegada");
        logEvent(event);
        refresh();
    }

    private void launchPhotoPicker() {
        photoPicker.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void capturePhoto() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (dir == null) {
            viewModel.post("No hay directorio de imágenes disponible");
            return;
        }
        File file = new File(dir, "permission_lab_photo.jpg");
        pendingPhotoUri = FileProvider.getUriForFile(this, getPackageName() + ".files", file);
        takePicture.launch(pendingPhotoUri);
    }

    @SuppressWarnings("deprecation")
    private void recordThreeSeconds() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (dir == null) {
            viewModel.post("No hay directorio de audio disponible");
            return;
        }
        File out = new File(dir, "permission_lab_audio.m4a");
        recorder = Build.VERSION.SDK_INT >= 31 ? new MediaRecorder(this) : new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(out.getAbsolutePath());
        try {
            recorder.prepare();
            recorder.start();
            viewModel.post("Grabando 3 segundos…");
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                try {
                    if (recorder != null) recorder.stop();
                    logEvent("Micrograbación guardada en " + out.getName());
                } catch (RuntimeException e) {
                    viewModel.post("La grabación no produjo un archivo válido");
                } finally {
                    releaseRecorder();
                }
            }, 3000);
        } catch (IOException | RuntimeException e) {
            viewModel.post("No se pudo iniciar MediaRecorder: " + e.getClass().getSimpleName());
            releaseRecorder();
        }
    }

    private void showLastLocation() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (lm == null) return;
        try {
            Location best = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (best == null) best = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (best == null) {
                viewModel.post("No hay última ubicación disponible todavía");
            } else {
                logEvent("Ubicación aproximada: " + best.getLatitude() + ", " + best.getLongitude());
            }
        } catch (SecurityException e) {
            viewModel.post("Ubicación revocada mientras se usaba");
        }
    }

    private void showBluetoothDevices() {
        BluetoothManager manager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetooth = manager == null ? null : manager.getAdapter();
        if (bluetooth == null) {
            viewModel.post("Bluetooth no disponible");
            return;
        }
        try {
            int count = bluetooth.getBondedDevices().size();
            logEvent("Dispositivos Bluetooth vinculados: " + count);
        } catch (SecurityException e) {
            viewModel.post("BLUETOOTH_CONNECT no está concedido");
        }
    }

    private void probeLocalNetwork() {
        viewModel.post("Probando http://10.0.2.2:8000/ …");
        new Thread(() -> {
            String message;
            try {
                HttpURLConnection connection =
                        (HttpURLConnection) new URL("http://10.0.2.2:8000/").openConnection();
                connection.setConnectTimeout(1500);
                connection.setReadTimeout(1500);
                connection.setRequestMethod("HEAD");
                int code = connection.getResponseCode();
                message = "Red local accesible, HTTP " + code;
                connection.disconnect();
            } catch (Exception e) {
                message = "Sin respuesta de 10.0.2.2:8000 (" + e.getClass().getSimpleName() + ")";
            }
            String finalMessage = message;
            runOnUiThread(() -> logEvent(finalMessage));
        }).start();
    }

    private void requestExactAlarmAccess() {
        if (Build.VERSION.SDK_INT < 31) {
            viewModel.post("En esta API no existe este panel de acceso especial");
            return;
        }
        AlarmManager alarm = getSystemService(AlarmManager.class);
        if (alarm != null && alarm.canScheduleExactAlarms()) {
            viewModel.post("Las alarmas exactas ya están permitidas");
            return;
        }
        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    private void openAppSettings() {
        startActivity(new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName())));
    }

    private void logEvent(String message) {
        viewModel.post(message);
        try (PermissionHistoryDb db = new PermissionHistoryDb(this)) {
            db.add(message);
        }
    }

    private void releaseRecorder() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }

    @Override
    protected void onStop() {
        releaseRecorder();
        super.onStop();
    }
}
