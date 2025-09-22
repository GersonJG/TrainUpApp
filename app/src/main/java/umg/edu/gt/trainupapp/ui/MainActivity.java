package umg.edu.gt.trainupapp.ui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import umg.edu.gt.trainupapp.R;

/**
 * MainActivity (mockup):
 * - Contiene BottomNavigation con 4 pestañas.
 * - Carga fragments visuales sin funcionalidad real.
 */
public class MainActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar); // Título dinámico
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav); // Navegación inferior

        // Nota: con AGP 8+ R genera IDs no-final (no constantes). En Java, switch requiere constantes.
        // Por ello se usa if/else en lugar de switch para evitar "constant expression required".
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                setTitleAndFragment(getString(R.string.nav_home), new umg.edu.gt.trainupapp.ui.fragments.HomeFragment());
                return true;
            } else if (id == R.id.nav_routine) {
                setTitleAndFragment(getString(R.string.nav_routine), new umg.edu.gt.trainupapp.ui.fragments.RoutineFragment());
                return true;
            } else if (id == R.id.nav_summary) {
                setTitleAndFragment(getString(R.string.nav_summary), new umg.edu.gt.trainupapp.ui.fragments.SummaryFragment());
                return true;
            } else if (id == R.id.nav_profile) {
                setTitleAndFragment(getString(R.string.nav_profile), new umg.edu.gt.trainupapp.ui.fragments.ProfileFragment());
                return true;
            }
            return false;
        });

        // Pestaña por defecto: Inicio
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    private void setTitleAndFragment(@NonNull String title, @NonNull Fragment fragment) {
        toolbar.setTitle(title);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
