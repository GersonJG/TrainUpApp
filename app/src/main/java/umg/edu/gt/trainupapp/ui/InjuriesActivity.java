package umg.edu.gt.trainupapp.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import umg.edu.gt.trainupapp.R;
import umg.edu.gt.trainupapp.utils.PrefsUtils;

/**
 * InjuriesActivity
 * Permite seleccionar lesiones comunes u "Otra" (texto). Opción "No tengo lesiones" anula el resto.
 */
public class InjuriesActivity extends AppCompatActivity {

    private CheckBox cbBack, cbKnees, cbHip, cbShoulder, cbWrist, cbNeck, cbNone;
    private EditText etOther;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_injuries);

        cbBack = findViewById(R.id.cbBack);
        cbKnees = findViewById(R.id.cbKnees);
        cbHip = findViewById(R.id.cbHip);
        cbShoulder = findViewById(R.id.cbShoulder);
        cbWrist = findViewById(R.id.cbWrist);
        cbNeck = findViewById(R.id.cbNeck);
        cbNone = findViewById(R.id.cbNone);
        etOther = findViewById(R.id.etOther);
        Button btnContinue = findViewById(R.id.btnContinue);

        cbNone.setOnCheckedChangeListener(this::onNoneToggled);
        btnContinue.setOnClickListener(v -> onContinue());
    }

    private void onNoneToggled(CompoundButton buttonView, boolean isChecked) {
        // Deshabilita/limpia otros si "No tengo lesiones" está activo
        CheckBox[] boxes = new CheckBox[]{cbBack, cbKnees, cbHip, cbShoulder, cbWrist, cbNeck};
        for (CheckBox cb : boxes) {
            cb.setEnabled(!isChecked);
            if (isChecked) cb.setChecked(false);
        }
        etOther.setEnabled(!isChecked);
        if (isChecked) etOther.setText("");
    }

    private void onContinue() {
        JSONObject json = new JSONObject();
        try {
            if (cbNone.isChecked()) {
                json.put("none", true);
            } else {
                JSONArray list = new JSONArray();
                if (cbBack.isChecked()) list.put(getString(R.string.inj_back));
                if (cbKnees.isChecked()) list.put(getString(R.string.inj_knees));
                if (cbHip.isChecked()) list.put(getString(R.string.inj_hip));
                if (cbShoulder.isChecked()) list.put(getString(R.string.inj_shoulder));
                if (cbWrist.isChecked()) list.put(getString(R.string.inj_wrist));
                if (cbNeck.isChecked()) list.put(getString(R.string.inj_neck));
                String other = etOther.getText().toString().trim();
                if (!TextUtils.isEmpty(other)) list.put(other);
                json.put("injuries", list);
            }
        } catch (JSONException ignored) {}

        PrefsUtils.saveJson(this, "injuries_data", json);

        try {
            startActivity(new android.content.Intent().setClassName(getPackageName(), getPackageName()+".ui.PreferencesActivity"));
        } catch (android.content.ActivityNotFoundException ignored) {}
    }
}
