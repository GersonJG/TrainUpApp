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
 * SummaryFragment (mockup visual):
 * - Título "Resumen de Rutina"
 * - Bloques: Músculos, Duración, Volumen total, Series completadas
 * - Botón "Guardar rutina" y nota informativa
 */
public class SummaryFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_summary, container, false);
    }
}

