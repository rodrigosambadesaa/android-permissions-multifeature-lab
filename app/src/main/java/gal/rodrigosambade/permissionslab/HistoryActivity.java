package gal.rodrigosambade.permissionslab;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ListView list = findViewById(R.id.listHistory);
        try (PermissionHistoryDb db = new PermissionHistoryDb(this)) {
            list.setAdapter(new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    db.latest(100)));
        }
    }
}
