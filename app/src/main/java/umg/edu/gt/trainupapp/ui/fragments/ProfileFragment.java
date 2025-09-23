package umg.edu.gt.trainupapp.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import umg.edu.gt.trainupapp.R;
import umg.edu.gt.trainupapp.data.database.entity.EquipmentEntity;
import umg.edu.gt.trainupapp.data.database.entity.FitnessProfileEntity;
import umg.edu.gt.trainupapp.data.database.entity.InjuriesEntity;
import umg.edu.gt.trainupapp.data.database.entity.UserEntity;
import umg.edu.gt.trainupapp.data.repository.UserDataRepository;

/**
 * ProfileFragment: muestra y permite editar los datos del usuario.
 * - Carga automática desde Room (User, Fitness - días, Equipment, Injuries).
 * - Edición con validaciones detalladas y mensajes inline.
 * - Nivel y Objetivo se muestran en solo lectura; Días disponibles editables.
 */
public class ProfileFragment extends Fragment {

    // Vistas lectura
    private TextView tvName, tvAge, tvHeight, tvWeight, tvGender, tvBmi;
    private TextView tvLevel, tvGoal, tvPlace, tvEquipmentList, tvInjuriesList;

    // Vistas edición
    private TextInputLayout tilName, tilAge, tilHeight, tilWeight, tilOtherInjury;
    private TextInputEditText etName, etAge, etHeight, etWeight, etOtherInjury;
    private RadioGroup rgGender, rgPlace;
    private TextView tvGenderError, tvDaysError, tvPlaceError, tvEquipmentError, tvInjuriesError;
    private CheckBox cbMo, cbTu, cbWe, cbTh, cbFr, cbSa, cbSu;
    private CheckBox cbBody, cbDumb, cbBar, cbBands, cbKettlebell, cbMachines, cbPullup, cbTrx, cbBall, cbMat;
    private CheckBox cbBack, cbKnees, cbHip, cbShoulder, cbWrist, cbNeck, cbNone;

    // Grupos para cambiar visibilidad
    private View groupPersonalRead, groupPersonalEdit;
    private View tvPlaceLabel, tvEquipmentLabel, groupEquipment, groupInjuries;

    // Botones
    private Button btnEditInfo, btnSave, btnCancel;

    private UserDataRepository repository;
    private int userId = -1;

    private FitnessProfileEntity fitnessCache; // Para preservar nivel/objetivo en upsert

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new UserDataRepository(requireContext());
        bindViews(view);
        setupListeners();
        loadData();
    }

    private void bindViews(View v) {
        // Lectura
        tvName = v.findViewById(R.id.tvName);
        tvAge = v.findViewById(R.id.tvAge);
        tvHeight = v.findViewById(R.id.tvHeight);
        tvWeight = v.findViewById(R.id.tvWeight);
        tvGender = v.findViewById(R.id.tvGender);
        tvBmi = v.findViewById(R.id.tvBmi);
        tvLevel = v.findViewById(R.id.tvLevel);
        tvGoal = v.findViewById(R.id.tvGoal);
        tvPlace = v.findViewById(R.id.tvPlace);
        tvEquipmentList = v.findViewById(R.id.tvEquipmentList);
        tvInjuriesList = v.findViewById(R.id.tvInjuriesList);

        // Edición
        tilName = v.findViewById(R.id.tilName);
        tilAge = v.findViewById(R.id.tilAge);
        tilHeight = v.findViewById(R.id.tilHeight);
        tilWeight = v.findViewById(R.id.tilWeight);
        tilOtherInjury = v.findViewById(R.id.tilOtherInjury);
        etName = v.findViewById(R.id.etName);
        etAge = v.findViewById(R.id.etAge);
        etHeight = v.findViewById(R.id.etHeight);
        etWeight = v.findViewById(R.id.etWeight);
        etOtherInjury = v.findViewById(R.id.etOtherInjury);
        rgGender = v.findViewById(R.id.rgGender);
        rgPlace = v.findViewById(R.id.rgPlace);
        tvGenderError = v.findViewById(R.id.tvGenderError);
        tvDaysError = v.findViewById(R.id.tvDaysError);
        tvPlaceError = v.findViewById(R.id.tvPlaceError);
        tvEquipmentError = v.findViewById(R.id.tvEquipmentError);
        tvInjuriesError = v.findViewById(R.id.tvInjuriesError);

        cbMo = v.findViewById(R.id.cbMo);
        cbTu = v.findViewById(R.id.cbTu);
        cbWe = v.findViewById(R.id.cbWe);
        cbTh = v.findViewById(R.id.cbTh);
        cbFr = v.findViewById(R.id.cbFr);
        cbSa = v.findViewById(R.id.cbSa);
        cbSu = v.findViewById(R.id.cbSu);

        cbBody = v.findViewById(R.id.cbBody);
        cbDumb = v.findViewById(R.id.cbDumb);
        cbBar = v.findViewById(R.id.cbBar);
        cbBands = v.findViewById(R.id.cbBands);
        cbKettlebell = v.findViewById(R.id.cbKettlebell);
        cbMachines = v.findViewById(R.id.cbMachines);
        cbPullup = v.findViewById(R.id.cbPullup);
        cbTrx = v.findViewById(R.id.cbTrx);
        cbBall = v.findViewById(R.id.cbBall);
        cbMat = v.findViewById(R.id.cbMat);

        cbBack = v.findViewById(R.id.cbBack);
        cbKnees = v.findViewById(R.id.cbKnees);
        cbHip = v.findViewById(R.id.cbHip);
        cbShoulder = v.findViewById(R.id.cbShoulder);
        cbWrist = v.findViewById(R.id.cbWrist);
        cbNeck = v.findViewById(R.id.cbNeck);
        cbNone = v.findViewById(R.id.cbNone);

        groupPersonalRead = v.findViewById(R.id.groupPersonalRead);
        groupPersonalEdit = v.findViewById(R.id.groupPersonalEdit);
        tvPlaceLabel = v.findViewById(R.id.tvPlaceLabel);
        tvEquipmentLabel = v.findViewById(R.id.tvEquipmentLabel);
        groupEquipment = v.findViewById(R.id.groupEquipment);
        groupInjuries = v.findViewById(R.id.groupInjuries);

        btnEditInfo = v.findViewById(R.id.btnEditInfo);
        btnSave = v.findViewById(R.id.btnSave);
        btnCancel = v.findViewById(R.id.btnCancel);

        // Filtros de entrada básicos
        etName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
        etAge.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        etHeight.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        etWeight.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        etOtherInjury.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100), blockDangerousCharsFilter});

        // Validación en tiempo real (opcional mínima): limpiar errores cuando cambia
        addClearErrorOnChange(tilName, etName);
        addClearErrorOnChange(tilAge, etAge);
        addClearErrorOnChange(tilHeight, etHeight);
        addClearErrorOnChange(tilWeight, etWeight);
    }

    private void setupListeners() {
        btnEditInfo.setOnClickListener(v -> switchToEditMode(true));
        btnCancel.setOnClickListener(v -> switchToEditMode(false));
        btnSave.setOnClickListener(v -> onSave());

        cbNone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setInjuriesEnabled(!isChecked);
            if (isChecked) {
                clearInjurySelections();
                tilOtherInjury.setError(null);
            }
        });
    }

    private void loadData() {
        // Obtener userId actual o último
        repository.getLatestUserId(uid -> {
            userId = uid;
            if (userId <= 0) return; // Nada que mostrar aún
            // Cargar entidades
            repository.getUserById(userId, this::bindUser);
            repository.getFitnessByUserId(userId, this::bindFitness);
            repository.getEquipmentByUserId(userId, this::bindEquipment);
            repository.getInjuriesByUserId(userId, this::bindInjuries);
        });
    }

    private void bindUser(UserEntity u) {
        if (u == null) return;
        // Lectura
        tvName.setText(nonEmpty(u.name));
        tvAge.setText(u.age > 0 ? String.format(Locale.getDefault(), "%d años", u.age) : "—");
        tvHeight.setText(u.height > 0 ? String.format(Locale.getDefault(), "%.1f cm", u.height) : "—");
        tvWeight.setText(u.weight > 0 ? String.format(Locale.getDefault(), "%.1f kg", u.weight) : "—");
        tvGender.setText(nonEmpty(u.gender));
        if (u.imc > 0) {
            tvBmi.setText(String.format(Locale.getDefault(), "IMC: %.1f (%s)", u.imc, nonEmpty(u.imcCategory)));
        } else {
            tvBmi.setText("IMC: —");
        }

        // Edición
        etName.setText(nonNull(u.name));
        etAge.setText(u.age > 0 ? String.valueOf(u.age) : "");
        etHeight.setText(u.height > 0 ? String.format(Locale.getDefault(), "%.1f", u.height) : "");
        etWeight.setText(u.weight > 0 ? String.format(Locale.getDefault(), "%.1f", u.weight) : "");
        // Género
        if (equalsAny(u.gender, getString(R.string.gender_male))) {
            ((RadioButton) requireView().findViewById(R.id.rbMale)).setChecked(true);
        } else if (equalsAny(u.gender, getString(R.string.gender_female))) {
            ((RadioButton) requireView().findViewById(R.id.rbFemale)).setChecked(true);
        } else if (equalsAny(u.gender, getString(R.string.gender_other))) {
            ((RadioButton) requireView().findViewById(R.id.rbOther)).setChecked(true);
        } else {
            rgGender.clearCheck();
        }
    }

    private void bindFitness(FitnessProfileEntity f) {
        fitnessCache = f; // Guardar cache para preservar nivel/objetivo
        if (f != null) {
            tvLevel.setText(nonEmpty(f.experienceLevel));
            tvGoal.setText(nonEmpty(f.goal));
            // Días
            markDaysFromJson(f.availableDays);
        } else {
            tvLevel.setText("—");
            tvGoal.setText("—");
        }
    }

    private void bindEquipment(EquipmentEntity e) {
        if (e == null) return;
        tvPlace.setText(nonEmpty(e.trainingLocation));
        // Lista de equipos
        tvEquipmentList.setText(joinListFromJson(e.availableEquipment));
        markEquipmentFromJson(e.availableEquipment);
        // Marcar lugar
        if (equalsAny(e.trainingLocation, getString(R.string.place_home))) {
            ((RadioButton) requireView().findViewById(R.id.rbHome)).setChecked(true);
        } else if (equalsAny(e.trainingLocation, getString(R.string.place_gym))) {
            ((RadioButton) requireView().findViewById(R.id.rbGym)).setChecked(true);
        } else if (equalsAny(e.trainingLocation, getString(R.string.place_outdoor))) {
            ((RadioButton) requireView().findViewById(R.id.rbOutdoor)).setChecked(true);
        } else if (equalsAny(e.trainingLocation, getString(R.string.place_mixed))) {
            ((RadioButton) requireView().findViewById(R.id.rbMixed)).setChecked(true);
        } else {
            rgPlace.clearCheck();
        }
    }

    private void bindInjuries(InjuriesEntity i) {
        if (i == null) return;
        if (!i.hasInjuries) {
            tvInjuriesList.setText(getString(R.string.inj_none));
            cbNone.setChecked(true);
        } else {
            cbNone.setChecked(false);
            tvInjuriesList.setText(joinListFromJson(i.injuriesList));
            markInjuriesFromJson(i.injuriesList);
        }
    }

    private void switchToEditMode(boolean edit) {
        groupPersonalRead.setVisibility(edit ? View.GONE : View.VISIBLE);
        groupPersonalEdit.setVisibility(edit ? View.VISIBLE : View.GONE);

        // Fitness: solo días editables, siempre visibles (no ocultamos)
        // Equipamiento: mostrar controles en edición
        int vis = edit ? View.VISIBLE : View.GONE;
        tvPlaceLabel.setVisibility(vis);
        rgPlace.setVisibility(vis);
        tvEquipmentLabel.setVisibility(vis);
        groupEquipment.setVisibility(vis);

        // Lesiones: mostrar controles en edición
        groupInjuries.setVisibility(vis);

        // Botones
        btnEditInfo.setVisibility(edit ? View.GONE : View.VISIBLE);
        btnSave.setVisibility(edit ? View.VISIBLE : View.GONE);
        btnCancel.setVisibility(edit ? View.VISIBLE : View.GONE);

        // Limpiar errores al cambiar modo
        clearAllErrors();
    }

    private void onSave() {
        clearAllErrors();

        // Validar personales
        boolean ok = true;
        String name = safeText(etName);
        if (!name.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü ]{2,50}$")) {
            tilName.setError(getString(R.string.error_name_generic));
            ok = false;
        }
        Integer age = parseIntStrict(safeText(etAge));
        if (age == null || age < 12 || age > 120) {
            tilAge.setError(getString(R.string.error_age_range));
            ok = false;
        }
        Float height = parseOneDecimalFloat(safeText(etHeight));
        if (height == null || height < 100f || height > 250f) {
            tilHeight.setError(getString(R.string.error_height_range));
            ok = false;
        }
        Float weight = parseOneDecimalFloat(safeText(etWeight));
        if (weight == null || weight < 30f || weight > 300f) {
            tilWeight.setError(getString(R.string.error_weight_range));
            ok = false;
        }
        int genderId = rgGender.getCheckedRadioButtonId();
        if (genderId == -1) {
            tvGenderError.setText(getString(R.string.error_select_gender));
            tvGenderError.setVisibility(View.VISIBLE);
            ok = false;
        }

        // Validar días
        int daysCount = countChecked(cbMo, cbTu, cbWe, cbTh, cbFr, cbSa, cbSu);
        if (daysCount < 1) {
            tvDaysError.setText(getString(R.string.error_select_days));
            tvDaysError.setVisibility(View.VISIBLE);
            ok = false;
        }

        // Validar equipamiento
        if (rgPlace.getCheckedRadioButtonId() == -1) {
            tvPlaceError.setText(getString(R.string.error_select_place));
            tvPlaceError.setVisibility(View.VISIBLE);
            ok = false;
        }
        int equipCount = countChecked(cbBody, cbDumb, cbBar, cbBands, cbKettlebell, cbMachines, cbPullup, cbTrx, cbBall, cbMat);
        if (equipCount < 1) {
            tvEquipmentError.setText(getString(R.string.error_select_equipment));
            tvEquipmentError.setVisibility(View.VISIBLE);
            ok = false;
        }

        // Validar lesiones
        boolean none = cbNone.isChecked();
        String other = safeText(etOtherInjury);
        if (!none && !TextUtils.isEmpty(other)) {
            if (!other.matches("^[\\p{L}0-9 .,]{1,100}$")) {
                tilOtherInjury.setError(getString(R.string.error_other_injury));
                ok = false;
            }
        }

        if (!ok) {
            // Enfocar primer error visible
            focusFirstError();
            return;
        }

        // Preparar entidades
        String gender = ((RadioButton) requireView().findViewById(genderId)).getText().toString();
        float bmi = (float) (weight / Math.pow(height / 100f, 2));
        String bmiCat;
        if (bmi < 18.5f) bmiCat = getString(R.string.bmi_underweight);
        else if (bmi < 25f) bmiCat = getString(R.string.bmi_normal);
        else if (bmi < 30f) bmiCat = getString(R.string.bmi_overweight);
        else bmiCat = getString(R.string.bmi_obesity);

        UserEntity u = new UserEntity();
        u.id = userId;
        u.name = name;
        u.age = age;
        u.height = height;
        u.weight = weight;
        u.gender = gender;
        u.imc = round1(bmi);
        u.imcCategory = bmiCat;

        FitnessProfileEntity f = new FitnessProfileEntity();
        f.userId = userId;
        if (fitnessCache != null) {
            f.experienceLevel = nonNull(fitnessCache.experienceLevel);
            f.goal = nonNull(fitnessCache.goal);
        } else {
            f.experienceLevel = "";
            f.goal = "";
        }
        f.availableDays = buildDaysJson();

        EquipmentEntity e = new EquipmentEntity();
        e.userId = userId;
        e.trainingLocation = getTextFromChecked(rgPlace);
        e.availableEquipment = buildEquipmentJson();

        InjuriesEntity inj = new InjuriesEntity();
        inj.userId = userId;
        inj.hasInjuries = !none;
        inj.injuriesList = none ? "[]" : buildInjuriesJson();

        // Guardar: actualizar usuario, upsert demás. Secuencialmente.
        repository.updateUser(u, () -> {
            repository.upsertFitness(f, () -> {
                repository.upsertEquipment(e, () -> {
                    repository.upsertInjuries(inj, () -> {
                        Snackbar.make(requireView(), R.string.msg_data_updated, Snackbar.LENGTH_LONG).show();
                        bindUser(u);
                        // Refrescar lecturas visibles
                        tvLevel.setText(nonEmpty(f.experienceLevel));
                        tvGoal.setText(nonEmpty(f.goal));
                        tvPlace.setText(nonEmpty(e.trainingLocation));
                        tvEquipmentList.setText(joinListFromJson(e.availableEquipment));
                        tvInjuriesList.setText(inj.hasInjuries ? joinListFromJson(inj.injuriesList) : getString(R.string.inj_none));
                        switchToEditMode(false);
                    });
                });
            });
        });
    }

    // Helpers de UI/validación
    private void setInjuriesEnabled(boolean enabled) {
        cbBack.setEnabled(enabled);
        cbKnees.setEnabled(enabled);
        cbHip.setEnabled(enabled);
        cbShoulder.setEnabled(enabled);
        cbWrist.setEnabled(enabled);
        cbNeck.setEnabled(enabled);
        etOtherInjury.setEnabled(enabled);
        if (!enabled) etOtherInjury.setText("");
    }

    private void clearInjurySelections() {
        cbBack.setChecked(false);
        cbKnees.setChecked(false);
        cbHip.setChecked(false);
        cbShoulder.setChecked(false);
        cbWrist.setChecked(false);
        cbNeck.setChecked(false);
    }

    private void clearAllErrors() {
        tilName.setError(null);
        tilAge.setError(null);
        tilHeight.setError(null);
        tilWeight.setError(null);
        tilOtherInjury.setError(null);
        tvGenderError.setText(""); tvGenderError.setVisibility(View.GONE);
        tvDaysError.setText(""); tvDaysError.setVisibility(View.GONE);
        tvPlaceError.setText(""); tvPlaceError.setVisibility(View.GONE);
        tvEquipmentError.setText(""); tvEquipmentError.setVisibility(View.GONE);
        tvInjuriesError.setText(""); tvInjuriesError.setVisibility(View.GONE);
    }

    private void focusFirstError() {
        if (!TextUtils.isEmpty(tilName.getError())) { etName.requestFocus(); return; }
        if (!TextUtils.isEmpty(tilAge.getError())) { etAge.requestFocus(); return; }
        if (!TextUtils.isEmpty(tilHeight.getError())) { etHeight.requestFocus(); return; }
        if (!TextUtils.isEmpty(tilWeight.getError())) { etWeight.requestFocus(); return; }
        if (tvGenderError.getVisibility() == View.VISIBLE) { rgGender.requestFocus(); return; }
        if (tvDaysError.getVisibility() == View.VISIBLE) { cbMo.requestFocus(); return; }
        if (tvPlaceError.getVisibility() == View.VISIBLE) { rgPlace.requestFocus(); return; }
        if (tvEquipmentError.getVisibility() == View.VISIBLE) { cbBody.requestFocus(); return; }
        if (!TextUtils.isEmpty(tilOtherInjury.getError())) { etOtherInjury.requestFocus(); }
    }

    private void addClearErrorOnChange(TextInputLayout til, TextInputEditText et) {
        et.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { til.setError(null); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private int countChecked(CheckBox... boxes) {
        int c = 0; for (CheckBox b : boxes) if (b.isChecked()) c++; return c;
    }

    private String getTextFromChecked(RadioGroup rg) {
        int id = rg.getCheckedRadioButtonId();
        if (id == -1) return "";
        RadioButton rb = requireView().findViewById(id);
        return rb.getText().toString();
    }

    private String buildDaysJson() {
        JSONArray arr = new JSONArray();
        if (cbMo.isChecked()) arr.put(getString(R.string.days_monday));
        if (cbTu.isChecked()) arr.put(getString(R.string.days_tuesday));
        if (cbWe.isChecked()) arr.put(getString(R.string.days_wednesday));
        if (cbTh.isChecked()) arr.put(getString(R.string.days_thursday));
        if (cbFr.isChecked()) arr.put(getString(R.string.days_friday));
        if (cbSa.isChecked()) arr.put(getString(R.string.days_saturday));
        if (cbSu.isChecked()) arr.put(getString(R.string.days_sunday));
        return arr.toString();
    }

    private void markDaysFromJson(String json) {
        try {
            JSONArray arr = json != null ? new JSONArray(json) : null;
            if (arr == null) return;
            List<String> list = new ArrayList<>();
            for (int i=0;i<arr.length();i++) list.add(arr.optString(i));
            cbMo.setChecked(list.contains(getString(R.string.days_monday)));
            cbTu.setChecked(list.contains(getString(R.string.days_tuesday)));
            cbWe.setChecked(list.contains(getString(R.string.days_wednesday)));
            cbTh.setChecked(list.contains(getString(R.string.days_thursday)));
            cbFr.setChecked(list.contains(getString(R.string.days_friday)));
            cbSa.setChecked(list.contains(getString(R.string.days_saturday)));
            cbSu.setChecked(list.contains(getString(R.string.days_sunday)));
        } catch (JSONException ignored) {}
    }

    private String buildEquipmentJson() {
        JSONArray arr = new JSONArray();
        if (cbBody.isChecked()) arr.put(getString(R.string.eq_bodyweight));
        if (cbDumb.isChecked()) arr.put(getString(R.string.eq_dumbbells));
        if (cbBar.isChecked()) arr.put(getString(R.string.eq_barbell));
        if (cbBands.isChecked()) arr.put(getString(R.string.eq_bands));
        if (cbKettlebell.isChecked()) arr.put(getString(R.string.eq_kettlebell));
        if (cbMachines.isChecked()) arr.put(getString(R.string.eq_machines));
        if (cbPullup.isChecked()) arr.put(getString(R.string.eq_pullup));
        if (cbTrx.isChecked()) arr.put(getString(R.string.eq_trx));
        if (cbBall.isChecked()) arr.put(getString(R.string.eq_ball));
        if (cbMat.isChecked()) arr.put(getString(R.string.eq_mat));
        return arr.toString();
    }

    private void markEquipmentFromJson(String json) {
        try {
            JSONArray arr = json != null ? new JSONArray(json) : null;
            if (arr == null) return;
            List<String> list = new ArrayList<>();
            for (int i=0;i<arr.length();i++) list.add(arr.optString(i));
            cbBody.setChecked(list.contains(getString(R.string.eq_bodyweight)));
            cbDumb.setChecked(list.contains(getString(R.string.eq_dumbbells)));
            cbBar.setChecked(list.contains(getString(R.string.eq_barbell)));
            cbBands.setChecked(list.contains(getString(R.string.eq_bands)));
            cbKettlebell.setChecked(list.contains(getString(R.string.eq_kettlebell)));
            cbMachines.setChecked(list.contains(getString(R.string.eq_machines)));
            cbPullup.setChecked(list.contains(getString(R.string.eq_pullup)));
            cbTrx.setChecked(list.contains(getString(R.string.eq_trx)));
            cbBall.setChecked(list.contains(getString(R.string.eq_ball)));
            cbMat.setChecked(list.contains(getString(R.string.eq_mat)));
        } catch (JSONException ignored) {}
    }

    private String buildInjuriesJson() {
        JSONArray arr = new JSONArray();
        if (cbBack.isChecked()) arr.put(getString(R.string.inj_back));
        if (cbKnees.isChecked()) arr.put(getString(R.string.inj_knees));
        if (cbHip.isChecked()) arr.put(getString(R.string.inj_hip));
        if (cbShoulder.isChecked()) arr.put(getString(R.string.inj_shoulder));
        if (cbWrist.isChecked()) arr.put(getString(R.string.inj_wrist));
        if (cbNeck.isChecked()) arr.put(getString(R.string.inj_neck));
        String other = safeText(etOtherInjury);
        if (!TextUtils.isEmpty(other)) arr.put(other);
        return arr.toString();
    }

    private void markInjuriesFromJson(String json) {
        try {
            JSONArray arr = json != null ? new JSONArray(json) : null;
            if (arr == null) return;
            List<String> list = new ArrayList<>();
            for (int i=0;i<arr.length();i++) list.add(arr.optString(i));
            cbBack.setChecked(list.contains(getString(R.string.inj_back)));
            cbKnees.setChecked(list.contains(getString(R.string.inj_knees)));
            cbHip.setChecked(list.contains(getString(R.string.inj_hip)));
            cbShoulder.setChecked(list.contains(getString(R.string.inj_shoulder)));
            cbWrist.setChecked(list.contains(getString(R.string.inj_wrist)));
            cbNeck.setChecked(list.contains(getString(R.string.inj_neck)));
            // other no se marca automáticamente
        } catch (JSONException ignored) {}
    }

    private String joinListFromJson(String json) {
        try {
            JSONArray arr = json != null ? new JSONArray(json) : null;
            if (arr == null) return "—";
            List<String> list = new ArrayList<>();
            for (int i=0;i<arr.length();i++) list.add(arr.optString(i));
            return TextUtils.join(", ", list);
        } catch (JSONException e) {
            return "—";
        }
    }

    private String safeText(TextInputEditText et) { return et.getText() != null ? et.getText().toString().trim() : ""; }
    private String nonNull(String s) { return s == null ? "" : s; }
    private String nonEmpty(String s) { return TextUtils.isEmpty(s) ? "—" : s; }
    private boolean equalsAny(String a, String b) { return a != null && a.equalsIgnoreCase(b); }

    private Integer parseIntStrict(String s) {
        try { if (TextUtils.isEmpty(s)) return null; return Integer.parseInt(s); } catch (Exception e) { return null; }
    }

    private Float parseOneDecimalFloat(String s) {
        if (TextUtils.isEmpty(s)) return null;
        s = s.replace(',', '.');
        // Permitir como máximo 1 decimal
        int dot = s.indexOf('.');
        if (dot >= 0 && s.length() - dot - 1 > 1) return null;
        try { return Float.parseFloat(s); } catch (Exception e) { return null; }
    }

    private float round1(float v) { return Math.round(v * 10f) / 10f; }

    // Filtro que bloquea caracteres peligrosos en "Otra lesión"
    private final InputFilter blockDangerousCharsFilter = (source, start, end, dest, dstart, dend) -> {
        for (int i = start; i < end; i++) {
            char c = source.charAt(i);
            // Permitir solo letras, dígitos, espacio, punto y coma
            if (!(Character.isLetterOrDigit(c) || c == ' ' || c == '.' || c == ',')) {
                return ""; // bloquear cualquier otro
            }
        }
        return null; // aceptar
    };
}
