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
import umg.edu.gt.trainupapp.utils.PrefsUtils;

/**
 * EquipmentActivity
 * Captura lugar y equipamiento disponible; guarda en SharedPreferences y navega a InjuriesActivity.
 */
public class EquipmentActivity extends AppCompatActivity {

    private RadioGroup rgPlace;
    private CheckBox cbBody, cbDumb, cbBar, cbBands, cbKettlebell, cbMachines, cbPullup, cbTrx, cbBall, cbMat;

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
        Button btnContinue = findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(v -> onContinue());
    }

    private void onContinue() {
        JSONObject json = new JSONObject();
        try {
            String place = getTextFromChecked(rgPlace);
            json.put("place", place);
            JSONArray eq = new JSONArray();
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
        PrefsUtils.saveJson(this, "equipment_data", json);

        try {
            startActivity(new android.content.Intent().setClassName(getPackageName(), getPackageName()+".ui.InjuriesActivity"));
        } catch (android.content.ActivityNotFoundException ignored) {}
    }

    private String getTextFromChecked(RadioGroup rg) {
        int id = rg.getCheckedRadioButtonId();
        if (id == -1) return "";
        android.widget.RadioButton rb = findViewById(id);
        return rb.getText().toString();
    }
}
