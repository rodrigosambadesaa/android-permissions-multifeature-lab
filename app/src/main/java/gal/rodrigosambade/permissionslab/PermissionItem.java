package gal.rodrigosambade.permissionslab;

final class PermissionItem {
    final PermissionScenario scenario;
    final String permissions;
    final String state;

    PermissionItem(PermissionScenario scenario, String permissions, String state) {
        this.scenario = scenario;
        this.permissions = permissions;
        this.state = state;
    }
}
