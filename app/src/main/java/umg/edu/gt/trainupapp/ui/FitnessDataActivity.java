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
import umg.edu.gt.trainupapp.utils.PrefsUtils;

/**
 * FitnessDataActivity
 * Captura nivel, objetivo y días disponibles. Requiere mínimo 1 día; recomienda 3+.
 */
public class FitnessDataActivity extends AppCompatActivity {

    private RadioGroup rgLevel, rgGoal;
    private CheckBox cbMo, cbTu, cbWe, cbTh, cbFr, cbSa, cbSu;

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
        Button btnContinue = findViewById(R.id.btnContinue);

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
        JSONObject json = new JSONObject();
        try {
            json.put("level", getTextFromChecked(rgLevel));
            json.put("goal", getTextFromChecked(rgGoal));
            JSONArray days = new JSONArray();
            if (cbMo.isChecked()) days.put(getString(R.string.days_monday));
            if (cbTu.isChecked()) days.put(getString(R.string.days_tuesday));
            if (cbWe.isChecked()) days.put(getString(R.string.days_wednesday));
            if (cbTh.isChecked()) days.put(getString(R.string.days_thursday));
            if (cbFr.isChecked()) days.put(getString(R.string.days_friday));
            if (cbSa.isChecked()) days.put(getString(R.string.days_saturday));
            if (cbSu.isChecked()) days.put(getString(R.string.days_sunday));
            json.put("days", days);
        } catch (JSONException ignored) {}
        PrefsUtils.saveJson(this, "fitness_data", json);

        try {
            startActivity(new android.content.Intent().setClassName(getPackageName(), getPackageName()+".ui.EquipmentActivity"));
        } catch (android.content.ActivityNotFoundException ignored) {}
    }

    private String getTextFromChecked(RadioGroup rg) {
        int id = rg.getCheckedRadioButtonId();
        if (id == -1) return "";
        android.widget.RadioButton rb = findViewById(id);
        return rb.getText().toString();
    }
}
