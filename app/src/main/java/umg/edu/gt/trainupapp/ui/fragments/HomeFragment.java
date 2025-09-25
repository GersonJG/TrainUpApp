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
 * HomeFragment (mockup visual):
 * - Título, botón "Nueva Rutina"
 * - Secciones visuales: Acciones Rápidas y Entrenamientos Recientes (placeholders)
 */
public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        View btn = root.findViewById(R.id.btnNewRoutine);
        btn.setOnClickListener(v -> {
            try {
                requireContext().startActivity(new android.content.Intent().setClassName(requireContext().getPackageName(), requireContext().getPackageName()+".ui.SelectExerciseActivity"));
            } catch (Exception ignored) {}
        });
        return root;
    }
}
