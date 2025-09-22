package umg.edu.gt.trainupapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Import explícito de R para facilitar la resolución de recursos
import umg.edu.gt.trainupapp.R;

/**
 * WelcomeActivity
 * Pantalla de bienvenida de TrainUP. Muestra el logo, título, descripción, lista de beneficios
 * y un botón para continuar al onboarding (IntroActivity).
 */
public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Habilita modo Edge-to-Edge para un diseño moderno que usa toda la pantalla
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        // Ajusta paddings para respetar barras del sistema (status/navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.welcome_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias a vistas (documentación):
        TextView tvTitle = findViewById(R.id.tvTitle);            // Título principal
        TextView tvSubtitle = findViewById(R.id.tvSubtitle);      // Subtítulo
        ImageView ivLogo = findViewById(R.id.ivLogo);              // Logo centrado
        TextView tvDescription = findViewById(R.id.tvDescription); // Descripción
        TextView tvBenefit1 = findViewById(R.id.tvBenefit1);       // Beneficio 1
        TextView tvBenefit2 = findViewById(R.id.tvBenefit2);       // Beneficio 2
        TextView tvBenefit3 = findViewById(R.id.tvBenefit3);       // Beneficio 3
        Button btnStart = findViewById(R.id.btnStart);             // Botón "Continuar"

        // Navegación: ir a IntroActivity (paquete ui)
        btnStart.setOnClickListener(v -> {
            String packageName = getPackageName();
            Intent intent = new Intent();
            intent.setClassName(packageName, packageName + ".ui.IntroActivity");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(this, getString(R.string.button_continue) + " – Próximamente", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
