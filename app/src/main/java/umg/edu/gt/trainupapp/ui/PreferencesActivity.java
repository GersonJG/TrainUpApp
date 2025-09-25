package umg.edu.gt.trainupapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import umg.edu.gt.trainupapp.R;
import umg.edu.gt.trainupapp.utils.PrefsUtils;

/**
 * PreferencesActivity
 * Permite elegir el modo de creación de rutina (IA o Manual). En esta fase, el botón COMENZAR
 * siempre avanza a MainActivity (mockup), asumiendo visualmente un modo por defecto sin persistir datos obligatorios.
 */
public class PreferencesActivity extends AppCompatActivity {

    private RadioGroup rgMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_preferences);

        rgMode = findViewById(R.id.rgMode);
        Button btnStart = findViewById(R.id.btnStart);

        btnStart.setOnClickListener(v -> onStartFlow());
    }

    private void onStartFlow() {
        // Mockup: si no hay selección, asumimos "IA" solo visualmente. Avanzamos siempre.
        int id = rgMode.getCheckedRadioButtonId();
        if (id == -1) {
            Toast.makeText(this, R.string.toast_ready, Toast.LENGTH_SHORT).show();
        } else {
            // Opcional: guardar JSON de forma no obligatoria en esta fase mockup
            String mode = ((android.widget.RadioButton) findViewById(id)).getText().toString();
            JSONObject json = new JSONObject();
            try { json.put("routine_mode", mode); } catch (JSONException ignored) {}
            PrefsUtils.saveJson(this, "routine_mode", json);
            Toast.makeText(this, R.string.toast_ready, Toast.LENGTH_SHORT).show();
        }

        try {
            startActivity(new android.content.Intent().setClassName(getPackageName(), getPackageName()+".ui.SplashActivity"));
        } catch (android.content.ActivityNotFoundException ignored) {}
    }
}
