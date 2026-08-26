package gal.rodrigosambade.permissionslab;

import android.Manifest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class PermissionPolicyTest {
    @Test public void notificationsStartAtApi33() {
        assertTrue(PermissionPolicy.permissionsFor(PermissionScenario.NOTIFICATIONS, 32).isEmpty());
        assertEquals(
                Manifest.permission.POST_NOTIFICATIONS,
                PermissionPolicy.permissionsFor(PermissionScenario.NOTIFICATIONS, 33).get(0));
    }

    @Test public void localNetworkStartsAtApi37() {
        assertTrue(PermissionPolicy.permissionsFor(PermissionScenario.LOCAL_NETWORK, 36).isEmpty());
        assertEquals(
                Manifest.permission.ACCESS_LOCAL_NETWORK,
                PermissionPolicy.permissionsFor(PermissionScenario.LOCAL_NETWORK, 37).get(0));
    }

    @Test public void bluetoothSplitsAtApi31() {
        assertEquals(
                Manifest.permission.ACCESS_FINE_LOCATION,
                PermissionPolicy.permissionsFor(PermissionScenario.BLUETOOTH, 30).get(0));
        assertEquals(
                Manifest.permission.BLUETOOTH_SCAN,
                PermissionPolicy.permissionsFor(PermissionScenario.BLUETOOTH, 31).get(0));
    }
}
