package umg.edu.gt.trainupapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import umg.edu.gt.trainupapp.R;
import umg.edu.gt.trainupapp.data.database.entity.FitnessProfileEntity;
import umg.edu.gt.trainupapp.data.repository.UserDataRepository;
import umg.edu.gt.trainupapp.utils.PrefsUtils;

/**
 * FitnessDataActivity
 * Captura nivel, objetivo y días disponibles.
 * - Guarda datos en Room (FitnessProfileEntity) usando el userId recibido por Intent.
 * - Mantiene guardado en Prefs de forma opcional para compatibilidad temporal.
 */
public class FitnessDataActivity extends AppCompatActivity {

    private RadioGroup rgLevel, rgGoal;
    private CheckBox cbMo, cbTu, cbWe, cbTh, cbFr, cbSa, cbSu;
    private Button btnContinue;

    private UserDataRepository repository;
    private int userId; // userId pasado desde PersonalDataActivity

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fitness_data);

        rgLevel = findViewById(R.id.rgLevel);
        rgGoal = findViewById(R.id.rgGoal);
        cbMo = findViewById(R.id.cbMo);
        cbTu = findViewById(R.id.cbTu);
        cbWe = findViewById(R.id.cbWe);
        cbTh = findViewById(R.id.cbTh);
        cbFr = findViewById(R.id.cbFr);
        cbSa = findViewById(R.id.cbSa);
        cbSu = findViewById(R.id.cbSu);
        btnContinue = findViewById(R.id.btnContinue);

        repository = new UserDataRepository(this);
        userId = getIntent().getIntExtra("user_id", -1);

        btnContinue.setOnClickListener(v -> onContinue());
    }

    private void onContinue() {
        if (rgLevel.getCheckedRadioButtonId() == -1 || rgGoal.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, R.string.error_required_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        int daysCount = (cbMo.isChecked()?1:0) + (cbTu.isChecked()?1:0) + (cbWe.isChecked()?1:0) +
                (cbTh.isChecked()?1:0) + (cbFr.isChecked()?1:0) + (cbSa.isChecked()?1:0) + (cbSu.isChecked()?1:0);
        if (daysCount < 1) {
            Toast.makeText(this, R.string.error_required_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        if (daysCount < 3) {
            Toast.makeText(this, R.string.days_hint_min3, Toast.LENGTH_LONG).show();
        }

        // Asegurar userId válido antes de guardar
        if (userId <= 0) {
            repository.getLatestUserId(uid -> {
                userId = uid;
                if (userId <= 0) {
                    Toast.makeText(this, R.string.error_required_fields, Toast.LENGTH_SHORT).show();
                } else {
                    saveAndGo();
                }
            });
        } else {
            saveAndGo();
        }
    }

    private void saveAndGo() {
        // Construir JSON de días
        JSONArray days = new JSONArray();
        if (cbMo.isChecked()) days.put(getString(R.string.days_monday));
        if (cbTu.isChecked()) days.put(getString(R.string.days_tuesday));
        if (cbWe.isChecked()) days.put(getString(R.string.days_wednesday));
        if (cbTh.isChecked()) days.put(getString(R.string.days_thursday));
        if (cbFr.isChecked()) days.put(getString(R.string.days_friday));
        if (cbSa.isChecked()) days.put(getString(R.string.days_saturday));
        if (cbSu.isChecked()) days.put(getString(R.string.days_sunday));

        // Guardar en Room
        FitnessProfileEntity entity = new FitnessProfileEntity();
        entity.userId = userId;
        entity.experienceLevel = getTextFromChecked(rgLevel);
        entity.goal = getTextFromChecked(rgGoal);
        entity.availableDays = days.toString();

        repository.upsertFitness(entity, () -> {
            // Guardado legacy opcional
            JSONObject json = new JSONObject();
            try {
                json.put("level", entity.experienceLevel);
                json.put("goal", entity.goal);
                json.put("days", days);
            } catch (JSONException ignored) {}
            PrefsUtils.saveJson(this, "fitness_data", json);

            // Navegar a EquipmentActivity con userId
            try {
                android.content.Intent intent = new android.content.Intent()
                        .setClassName(getPackageName(), getPackageName()+".ui.EquipmentActivity");
                intent.putExtra("user_id", userId);
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException ignored) {}
        });
    }

    private String getTextFromChecked(RadioGroup rg) {
        int id = rg.getCheckedRadioButtonId();
        if (id == -1) return "";
        android.widget.RadioButton rb = findViewById(id);
        return rb.getText().toString();
    }
}
