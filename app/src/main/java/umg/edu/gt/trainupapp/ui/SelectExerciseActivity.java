package umg.edu.gt.trainupapp.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import umg.edu.gt.trainupapp.R;
import umg.edu.gt.trainupapp.data.database.TrainUpDatabase;
import umg.edu.gt.trainupapp.data.database.dao.ExerciseDao;
import umg.edu.gt.trainupapp.data.database.dao.MuscleDao;
import umg.edu.gt.trainupapp.data.database.entity.ExerciseEntity;
import umg.edu.gt.trainupapp.data.database.entity.MuscleEntity;
import umg.edu.gt.trainupapp.ui.adapters.ExerciseSelectAdapter;

/**
 * SelectExerciseActivity
 * ----------------------
 * Pantalla para seleccionar ejercicios (mockup). Características actuales:
 *  - Carga lista completa de ejercicios desde Room (ejercises) y músculos (para mostrar nombre del primero principal o secundario).
 *  - Búsqueda local in-memory (contiene, case-insensitive) sobre nombre.
 *  - Filtros (equipamiento / músculos) se muestran como chips deshabilitados (placeholder para fase futura).
 *  - Selección visual de items (cambia background) sin persistencia aún.
 *  - Botón "Crear" muestra Toast (rutina creada mockup) y finaliza.
 *  - Botón "Cancelar" cierra la Activity.
 * Limitaciones: no hay paginación, no se guardan selecciones, no hay filtros avanzados.
 */
public class SelectExerciseActivity extends AppCompatActivity {

    private EditText etSearch;
    private TextView tvNoResults;
    private RecyclerView rvExercises;
    private ExerciseSelectAdapter adapter;

    private final List<ExerciseEntity> fullList = new ArrayList<>();
    private final List<ExerciseEntity> filteredList = new ArrayList<>();
    private final Map<Integer, String> muscleNameMap = new HashMap<>();
    private final Set<Integer> selectedIds = new HashSet<>();

    private ExecutorService executor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_exercise);

        executor = Executors.newSingleThreadExecutor();

        etSearch = findViewById(R.id.etSearch);
        tvNoResults = findViewById(R.id.tvNoResults);
        rvExercises = findViewById(R.id.rvExercises);

        // Toolbar acciones
        TextView btnCancel = findViewById(R.id.btnCancel);
        TextView btnCreate = findViewById(R.id.btnCreate);
        btnCancel.setOnClickListener(v -> finish());
        btnCreate.setOnClickListener(v -> {
            Toast.makeText(this, R.string.toast_routine_created, Toast.LENGTH_SHORT).show();
            finish();
        });

        rvExercises.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExerciseSelectAdapter(filteredList, muscleNameMap, selectedIds, this::onExerciseClicked);
        rvExercises.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filter(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });

        loadData();
    }

    private void loadData() {
        executor.execute(() -> {
            TrainUpDatabase db = TrainUpDatabase.getDatabase(getApplicationContext());
            ExerciseDao exerciseDao = db.exerciseDao();
            MuscleDao muscleDao = db.muscleDao();
            List<ExerciseEntity> exercises = exerciseDao.getAllExercises();
            List<MuscleEntity> muscles = muscleDao.getAllMuscles();

            muscleNameMap.clear();
            for (MuscleEntity m : muscles) {
                muscleNameMap.put(m.id, m.name);
            }

            fullList.clear();
            fullList.addAll(exercises);
            filteredList.clear();
            filteredList.addAll(exercises);

            runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
                updateNoResults();
            });
        });
    }

    private void filter(String query) {
        String q = query.trim().toLowerCase();
        filteredList.clear();
        if (q.isEmpty()) {
            filteredList.addAll(fullList);
        } else {
            for (ExerciseEntity e : fullList) {
                if (e.name != null && e.name.toLowerCase().contains(q)) {
                    filteredList.add(e);
                }
            }
        }
        adapter.notifyDataSetChanged();
        updateNoResults();
    }

    private void updateNoResults() {
        tvNoResults.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void onExerciseClicked(ExerciseEntity entity) {
        if (selectedIds.contains(entity.id)) {
            selectedIds.remove(entity.id);
        } else {
            selectedIds.add(entity.id);
        }
        adapter.notifyDataSetChanged();
    }
}

