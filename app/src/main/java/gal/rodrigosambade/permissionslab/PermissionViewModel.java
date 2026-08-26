package gal.rodrigosambade.permissionslab;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PermissionViewModel extends ViewModel {
    private final MutableLiveData<String> event = new MutableLiveData<>("Selecciona una práctica");

    LiveData<String> event() {
        return event;
    }

    void post(String message) {
        event.setValue(message);
    }
}
