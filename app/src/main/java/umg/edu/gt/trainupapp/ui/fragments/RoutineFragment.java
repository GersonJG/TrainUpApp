package umg.edu.gt.trainupapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import umg.edu.gt.trainupapp.R;

/**
 * RoutineFragment (mockup visual):
 * - Título "RUTINA", botón "Finalizar"
 * - Lista visual de ejercicios (placeholders)
 * - Tabla visual (Serie, Volumen, Reps, Estado)
 * - Botones: "+ Agregar serie" y "Chat con IA"
 */
public class RoutineFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_routine, container, false);
    }
}

