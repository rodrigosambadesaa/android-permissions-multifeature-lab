package gal.rodrigosambade.permissionslab;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

final class PermissionAdapter extends RecyclerView.Adapter<PermissionAdapter.Holder> {
    interface Callback {
        void onRequest(PermissionScenario scenario);
        void onDemo(PermissionScenario scenario);
    }

    private final Callback callback;
    private final List<PermissionItem> items = new ArrayList<>();

    PermissionAdapter(Callback callback) {
        this.callback = callback;
    }

    void submit(List<PermissionItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_permission, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        PermissionItem item = items.get(position);
        h.title.setText(item.scenario.title);
        h.description.setText(item.scenario.description);
        h.permissions.setText("Permisos efectivos: " + item.permissions);
        h.state.setText("Estado: " + item.state);
        h.request.setText(PermissionPolicy.isSpecialAccess(item.scenario) ? "Abrir acceso" : "Solicitar");
        h.request.setEnabled(!PermissionPolicy.isPermissionlessAlternative(item.scenario));
        h.request.setOnClickListener(v -> callback.onRequest(item.scenario));
        h.demo.setOnClickListener(v -> callback.onDemo(item.scenario));
    }

    @Override public int getItemCount() {
        return items.size();
    }

    static final class Holder extends RecyclerView.ViewHolder {
        final TextView title, description, permissions, state;
        final Button request, demo;

        Holder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            description = itemView.findViewById(R.id.tvDescription);
            permissions = itemView.findViewById(R.id.tvPermissions);
            state = itemView.findViewById(R.id.tvState);
            request = itemView.findViewById(R.id.btnRequest);
            demo = itemView.findViewById(R.id.btnDemo);
        }
    }
}
