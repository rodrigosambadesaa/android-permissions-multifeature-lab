package gal.rodrigosambade.permissionslab;

import android.Manifest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class PermissionPolicy {
    private PermissionPolicy() {}

    static List<String> permissionsFor(PermissionScenario scenario, int sdk) {
        List<String> result = new ArrayList<>();
        switch (scenario) {
            case CAMERA:
                if (sdk >= 23) result.add(Manifest.permission.CAMERA);
                break;
            case MICROPHONE:
                if (sdk >= 23) result.add(Manifest.permission.RECORD_AUDIO);
                break;
            case LOCATION:
                if (sdk >= 23) {
                    result.add(Manifest.permission.ACCESS_COARSE_LOCATION);
                    result.add(Manifest.permission.ACCESS_FINE_LOCATION);
                }
                break;
            case BLUETOOTH:
                if (sdk >= 31) {
                    result.add(Manifest.permission.BLUETOOTH_SCAN);
                    result.add(Manifest.permission.BLUETOOTH_CONNECT);
                } else if (sdk >= 23) {
                    result.add(Manifest.permission.ACCESS_FINE_LOCATION);
                }
                break;
            case NOTIFICATIONS:
                if (sdk >= 33) result.add(Manifest.permission.POST_NOTIFICATIONS);
                break;
            case MEDIA_LIBRARY:
                if (sdk >= 34) {
                    result.add(Manifest.permission.READ_MEDIA_IMAGES);
                    result.add(Manifest.permission.READ_MEDIA_VIDEO);
                    result.add(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED);
                } else if (sdk >= 33) {
                    result.add(Manifest.permission.READ_MEDIA_IMAGES);
                    result.add(Manifest.permission.READ_MEDIA_VIDEO);
                } else if (sdk >= 23) {
                    result.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                break;
            case LOCAL_NETWORK:
                if (sdk >= 37) result.add(Manifest.permission.ACCESS_LOCAL_NETWORK);
                break;
            case PHOTO_PICKER:
            case EXACT_ALARM:
                break;
        }
        return Collections.unmodifiableList(result);
    }

    static boolean isSpecialAccess(PermissionScenario scenario) {
        return scenario == PermissionScenario.EXACT_ALARM;
    }

    static boolean isPermissionlessAlternative(PermissionScenario scenario) {
        return scenario == PermissionScenario.PHOTO_PICKER;
    }
}
