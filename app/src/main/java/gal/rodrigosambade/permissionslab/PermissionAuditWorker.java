package gal.rodrigosambade.permissionslab;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class PermissionAuditWorker extends Worker {
    public PermissionAuditWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        PermissionHistoryDb db = new PermissionHistoryDb(getApplicationContext());
        StringBuilder summary = new StringBuilder("Auditoría: ");
        for (PermissionScenario scenario : PermissionScenario.values()) {
            summary.append(scenario.name())
                    .append('=')
                    .append(PermissionState.evaluate(getApplicationContext(), scenario))
                    .append("; ");
        }
        db.add(summary.toString());
        db.close();
        return Result.success();
    }
}
