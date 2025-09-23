package umg.edu.gt.trainupapp.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import umg.edu.gt.trainupapp.R;
import umg.edu.gt.trainupapp.data.database.entity.UserEntity;
import umg.edu.gt.trainupapp.data.repository.UserDataRepository;
import umg.edu.gt.trainupapp.utils.PrefsUtils;

/**
 * PersonalDataActivity
 * Captura datos personales del usuario, calcula IMC y guarda en Room (UserEntity).
 * Nota: Mantiene PrefsUtils solo para compatibilidad mínima/temporal, pero la fuente de verdad es Room.
 */
public class PersonalDataActivity extends AppCompatActivity {

    private EditText etName, etAge, etHeight, etWeight; // Campos de texto
    private RadioGroup rgGender;                        // Selección de género
    private UserDataRepository repository;              // Acceso a Room

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personal_data);

        // findViewById: enlazar vistas con ids del layout
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        rgGender = findViewById(R.id.rgGender);
        Button btnContinue = findViewById(R.id.btnContinue);

        // Inicializar repositorio de datos (Room)
        repository = new UserDataRepository(this);

        // Filtros/inputs
        etAge.setInputType(InputType.TYPE_CLASS_NUMBER);
        etHeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etWeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        btnContinue.setOnClickListener(v -> onContinue());
    }

    /**
     * Valida, calcula IMC, muestra diálogo y guarda datos en Room; luego navega a FitnessDataActivity.
     */
    private void onContinue() {
        String name = etName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();
        int genderId = rgGender.getCheckedRadioButtonId();

        if (name.isEmpty() || ageStr.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty() || genderId == -1) {
            showError(getString(R.string.error_required_fields));
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            float height = Float.parseFloat(heightStr); // cm
            float weight = Float.parseFloat(weightStr); // kg
            if (age < 12 || age > 120 || height < 100 || height > 250 || weight < 30 || weight > 300) {
                showError(getString(R.string.error_invalid_ranges));
                return;
            }

            float bmi = (float) (weight / Math.pow(height / 100f, 2));
            String category;
            if (bmi < 18.5f) category = getString(R.string.bmi_underweight);
            else if (bmi < 25f) category = getString(R.string.bmi_normal);
            else if (bmi < 30f) category = getString(R.string.bmi_overweight);
            else category = getString(R.string.bmi_obesity);

            // Diálogo mostrando IMC
            new AlertDialog.Builder(this)
                    .setTitle(R.string.bmi_title)
                    .setMessage(String.format(Locale.getDefault(), "IMC: %.1f\n%s", bmi, category))
                    .setPositiveButton(R.string.button_continue, (d, w) -> {
                        String gender = ((RadioButton) findViewById(genderId)).getText().toString();

                        // 1) Guardar en Room (UserEntity)
                        UserEntity entity = new UserEntity();
                        entity.name = name;
                        entity.age = age;
                        entity.height = height;
                        entity.weight = weight;
                        entity.gender = gender;
                        entity.imc = bmi;
                        entity.imcCategory = category;

                        repository.insertUser(entity, userId -> {
                            // 2) Guardado legacy opcional (compatibilidad)
                            JSONObject json = new JSONObject();
                            try {
                                json.put("name", name);
                                json.put("age", age);
                                json.put("heightCm", height);
                                json.put("weightKg", weight);
                                json.put("gender", gender);
                                json.put("bmi", bmi);
                                json.put("bmiCategory", category);
                            } catch (JSONException ignored) {}
                            PrefsUtils.saveJson(this, "user_data", json);

                            // 3) Navegar a FitnessDataActivity pasando userId
                            try {
                                android.content.Intent intent = new android.content.Intent()
                                        .setClassName(getPackageName(), getPackageName()+".ui.FitnessDataActivity");
                                intent.putExtra("user_id", userId);
                                startActivity(intent);
                            } catch (android.content.ActivityNotFoundException ignored) {}
                        });
                    })
                    .setCancelable(false)
                    .show();
        } catch (NumberFormatException e) {
            showError(getString(R.string.error_invalid_ranges));
        }
    }

    /** Muestra un error simple en un diálogo. */
    private void showError(String msg) {
        new AlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
