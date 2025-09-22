package umg.edu.gt.trainupapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

/**
 * MainActivity (stub)
 * Esta clase proviene del template inicial y no se usa como pantalla principal.
 * Antes referenciaba R.id.main (un id que no existe en activity_main), lo que causaba
 * error de compilación. Se elimina el listener de insets para evitar la referencia
 * al id inexistente. La Activity en uso para la navegación es ui.MainActivity.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Nota: Se removió ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), ...)
        // porque activity_main no define un view con id "main". Esta clase queda como stub.
    }
}