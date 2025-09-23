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
import umg.edu.gt.trainupapp.data.database.entity.InjuriesEntity;
import umg.edu.gt.trainupapp.data.repository.UserDataRepository;
import umg.edu.gt.trainupapp.utils.PrefsUtils;

/**
 * InjuriesActivity
 * Permite seleccionar lesiones comunes u "Otra". Opción "No tengo lesiones" anula el resto.
 * Migra almacenamiento a Room (InjuriesEntity) y mantiene guardado opcional en Prefs.
 */
public class InjuriesActivity extends AppCompatActivity {

    private CheckBox cbBack, cbKnees, cbHip, cbShoulder, cbWrist, cbNeck, cbNone;
    private EditText etOther;
    private Button btnContinue;

    private UserDataRepository repository;
    private int userId;

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
        btnContinue = findViewById(R.id.btnContinue);

        repository = new UserDataRepository(this);
        userId = getIntent().getIntExtra("user_id", -1);

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
        boolean noneChecked = cbNone.isChecked();
        JSONArray list = new JSONArray();
        try {
            if (noneChecked) {
                json.put("none", true);
            } else {
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

        // Asegurar userId válido
        if (userId <= 0) {
            repository.getLatestUserId(uid -> {
                userId = uid;
                persistAndGo(json, noneChecked, list);
            });
        } else {
            persistAndGo(json, noneChecked, list);
        }
    }

    private void persistAndGo(JSONObject json, boolean noneChecked, JSONArray list) {
        // Guardar en Room
        InjuriesEntity entity = new InjuriesEntity();
        entity.userId = userId;
        entity.hasInjuries = !noneChecked;
        entity.injuriesList = noneChecked ? "[]" : list.toString();

        repository.upsertInjuries(entity, () -> {
            // Guardado legacy opcional
            PrefsUtils.saveJson(this, "injuries_data", json);

            // Navegar a PreferencesActivity con userId
            try {
                android.content.Intent intent = new android.content.Intent()
                        .setClassName(getPackageName(), getPackageName()+".ui.PreferencesActivity");
                intent.putExtra("user_id", userId);
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException ignored) {}
        });
    }
}
