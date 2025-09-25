package umg.edu.gt.trainupapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import umg.edu.gt.trainupapp.R;
import umg.edu.gt.trainupapp.data.api.WgerDataSyncService;
import umg.edu.gt.trainupapp.data.database.TrainUpDatabase;

/**
 * SplashActivity
 * --------------
 * Pantalla de carga mostrada tras el onboarding (botón COMENZAR en PreferencesActivity).
 * Flujo:
 *  - Verifica si ya existen ejercicios en la base local (exerciseDao.getCount()).
 *  - Si existen: muestra mensaje breve y navega directamente a MainActivity (evita re-descarga).
 *  - Si no existen: inicia sincronización completa con WgerDataSyncService.
 *  - En caso de error: muestra mensaje y habilita botón de reintento.
 *
 * NOTA: Diseño simple y limpio preparado para futura animación o branding extendido.
 */
public class SplashActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView tvStatus;
    private View btnRetry;

    private boolean syncing = false;
    private java.util.concurrent.ExecutorService executor; // Ejecuta tareas en background

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.progressBar);
        tvStatus = findViewById(R.id.tvStatus);
        btnRetry = findViewById(R.id.btnRetry);

        btnRetry.setOnClickListener(v -> startSyncIfNeeded(true));

        executor = java.util.concurrent.Executors.newSingleThreadExecutor();

        startSyncIfNeeded(false);
    }

    private void startSyncIfNeeded(boolean forcedRetry) {
        if (syncing) return;
        syncing = true;
        btnRetry.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        executor.execute(() -> {
            TrainUpDatabase db = TrainUpDatabase.getDatabase(getApplicationContext());
            int count = 0;
            try {
                count = db.exerciseDao().getCount();
            } catch (Exception ignored) {}
            final int existing = count;
            runOnUiThread(() -> {
                if (existing > 0 && !forcedRetry) {
                    tvStatus.setText(getString(R.string.splash_sync_existing));
                    navigateToMainDelayed();
                } else {
                    tvStatus.setText(getString(R.string.splash_sync_message));
                    beginSync(db);
                }
            });
        });
    }

    private void beginSync(TrainUpDatabase db) {
        WgerDataSyncService service = new WgerDataSyncService(db);
        service.syncAll(new WgerDataSyncService.SyncCallback() {
            @Override
            public void onSuccess(int totalExercises) {
                Toast.makeText(SplashActivity.this, "Ejercicios sincronizados: " + totalExercises, Toast.LENGTH_SHORT).show();
                navigateToMain();
            }
            @Override
            public void onError(String message, Throwable cause) {
                syncing = false;
                progressBar.setVisibility(View.GONE);
                tvStatus.setText(getString(R.string.splash_error));
                btnRetry.setVisibility(View.VISIBLE);
            }
        });
    }

    private void navigateToMainDelayed() {
        tvStatus.postDelayed(this::navigateToMain, 1000);
    }

    private void navigateToMain() {
        try {
            startActivity(new Intent().setClassName(getPackageName(), getPackageName()+".ui.MainActivity"));
        } catch (Exception ignored) {}
        finish();
    }
}
