package gal.rodrigosambade.permissionslab;

import android.app.AlarmManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import java.util.List;

final class PermissionState {
    private PermissionState() {}

    static String evaluate(Context context, PermissionScenario scenario) {
        if (scenario == PermissionScenario.PHOTO_PICKER) {
            return "No requiere permiso general de galería";
        }
        if (scenario == PermissionScenario.EXACT_ALARM) {
            if (Build.VERSION.SDK_INT < 31) return "No requiere acceso especial en esta API";
            AlarmManager alarm = context.getSystemService(AlarmManager.class);
            return alarm != null && alarm.canScheduleExactAlarms()
                    ? "Acceso especial concedido"
                    : "Acceso especial no concedido";
        }
        List<String> permissions = PermissionPolicy.permissionsFor(scenario, Build.VERSION.SDK_INT);
        if (permissions.isEmpty()) {
            return Build.VERSION.SDK_INT < 23
                    ? "Modelo legacy: concedido al instalar"
                    : "No existe runtime permission para esta API";
        }
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return "Falta: " + shortName(permission);
            }
        }
        return "Concedido";
    }

    static String shortName(String permission) {
        int dot = permission.lastIndexOf('.');
        return dot >= 0 ? permission.substring(dot + 1) : permission;
    }
}
