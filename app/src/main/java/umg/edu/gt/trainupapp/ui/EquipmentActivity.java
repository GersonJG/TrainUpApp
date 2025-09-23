package umg.edu.gt.trainupapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import umg.edu.gt.trainupapp.R;
import umg.edu.gt.trainupapp.data.database.entity.EquipmentEntity;
import umg.edu.gt.trainupapp.data.repository.UserDataRepository;
import umg.edu.gt.trainupapp.utils.PrefsUtils;

/**
 * EquipmentActivity
 * Captura lugar y equipamiento disponible; guarda en Room (EquipmentEntity) y navega a InjuriesActivity.
 * Mantiene Prefs opcional para compatibilidad.
 */
public class EquipmentActivity extends AppCompatActivity {

    private RadioGroup rgPlace;
    private CheckBox cbBody, cbDumb, cbBar, cbBands, cbKettlebell, cbMachines, cbPullup, cbTrx, cbBall, cbMat;
    private Button btnContinue;

    private UserDataRepository repository;
    private int userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_equipment);

        rgPlace = findViewById(R.id.rgPlace);
        cbBody = findViewById(R.id.cbBody);
        cbDumb = findViewById(R.id.cbDumb);
        cbBar = findViewById(R.id.cbBar);
        cbBands = findViewById(R.id.cbBands);
        cbKettlebell = findViewById(R.id.cbKettlebell);
        cbMachines = findViewById(R.id.cbMachines);
        cbPullup = findViewById(R.id.cbPullup);
        cbTrx = findViewById(R.id.cbTrx);
        cbBall = findViewById(R.id.cbBall);
        cbMat = findViewById(R.id.cbMat);
        btnContinue = findViewById(R.id.btnContinue);

        repository = new UserDataRepository(this);
        userId = getIntent().getIntExtra("user_id", -1);

        btnContinue.setOnClickListener(v -> onContinue());
    }

    private void onContinue() {
        // Construir JSON de equipamiento
        JSONObject json = new JSONObject();
        JSONArray eq = new JSONArray();
        try {
            String place = getTextFromChecked(rgPlace);
            json.put("place", place);
            if (cbBody.isChecked()) eq.put(getString(R.string.eq_bodyweight));
            if (cbDumb.isChecked()) eq.put(getString(R.string.eq_dumbbells));
            if (cbBar.isChecked()) eq.put(getString(R.string.eq_barbell));
            if (cbBands.isChecked()) eq.put(getString(R.string.eq_bands));
            if (cbKettlebell.isChecked()) eq.put(getString(R.string.eq_kettlebell));
            if (cbMachines.isChecked()) eq.put(getString(R.string.eq_machines));
            if (cbPullup.isChecked()) eq.put(getString(R.string.eq_pullup));
            if (cbTrx.isChecked()) eq.put(getString(R.string.eq_trx));
            if (cbBall.isChecked()) eq.put(getString(R.string.eq_ball));
            if (cbMat.isChecked()) eq.put(getString(R.string.eq_mat));
            json.put("equipment", eq);
        } catch (JSONException ignored) {}

        // Asegurar userId válido; si no viene por Intent, usar el último
        if (userId <= 0) {
            repository.getLatestUserId(uid -> {
                userId = uid;
                persistAndGo(json, eq);
            });
        } else {
            persistAndGo(json, eq);
        }
    }

    private void persistAndGo(JSONObject json, JSONArray eq) {
        // Guardar en Room
        EquipmentEntity entity = new EquipmentEntity();
        entity.userId = userId;
        entity.trainingLocation = json.optString("place", "");
        entity.availableEquipment = eq.toString();

        repository.upsertEquipment(entity, () -> {
            // Guardado legacy opcional
            PrefsUtils.saveJson(this, "equipment_data", json);

            // Navegar a InjuriesActivity con userId
            try {
                android.content.Intent intent = new android.content.Intent()
                        .setClassName(getPackageName(), getPackageName()+".ui.InjuriesActivity");
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
