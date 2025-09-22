package umg.edu.gt.trainupapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import umg.edu.gt.trainupapp.R; // Import de R del módulo app

/**
 * IntroActivity
 * Presenta funcionalidades clave de TrainUP mediante una lista horizontal de tarjetas con ícono + texto.
 * Botón "Continuar" navega a PersonalDataActivity.
 */
public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro);

        // Referencias de UI
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvSubtitle = findViewById(R.id.tvSubtitle);
        RecyclerView rvFeatures = findViewById(R.id.rvFeatures);
        Button btnContinue = findViewById(R.id.btnContinue);

        // Configuración RecyclerView horizontal
        rvFeatures.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        IntroFeatureAdapter adapter = new IntroFeatureAdapter();
        rvFeatures.setAdapter(adapter);

        // Carga de items (ícono + título)
        adapter.submit(
                new int[]{
                        R.drawable.ic_target_24,
                        R.drawable.ic_psychology_24,
                        R.drawable.ic_calendar_today_24,
                        R.drawable.ic_health_and_safety_24,
                        R.drawable.ic_trending_up_24,
                        R.drawable.ic_fitness_center_24
                },
                new int[]{
                        R.string.intro_item_objectives,
                        R.string.intro_item_ai,
                        R.string.intro_item_schedule,
                        R.string.intro_item_safe,
                        R.string.intro_item_progress,
                        R.string.intro_item_levels
                }
        );

        // Navegación: PersonalDataActivity
        btnContinue.setOnClickListener(v -> {
            try {
                startActivity(new android.content.Intent().setClassName(getPackageName(), getPackageName()+".ui.PersonalDataActivity"));
            } catch (android.content.ActivityNotFoundException ex) {
                // En caso de no estar aún registrada
            }
        });
    }
}
