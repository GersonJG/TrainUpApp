package umg.edu.gt.trainupapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;
import java.util.Set;

import umg.edu.gt.trainupapp.R;
import umg.edu.gt.trainupapp.data.database.entity.ExerciseEntity;

/**
 * ExerciseSelectAdapter
 * ---------------------
 * Adapter para mostrar la lista de ejercicios descargados. Características:
 *  - Muestra inicial del nombre dentro de un círculo (placeholder) en lugar de imagen real.
 *  - Muestra nombre del ejercicio y el primer músculo principal; si no existe, primer músculo secundario.
 *  - Admite selección visual (selected background) administrada por un Set externo (selectedIds).
 *  - Callback onItemClick para manejar la lógica de selección en la Activity.
 *
 * No usa librerías de carga de imágenes en esta fase (requisito del proyecto).
 */
public class ExerciseSelectAdapter extends RecyclerView.Adapter<ExerciseSelectAdapter.VH> {

    public interface OnExerciseClickListener { void onClick(ExerciseEntity entity); }

    private final List<ExerciseEntity> data;
    private final Map<Integer, String> muscleNameMap;
    private final Set<Integer> selectedIds;
    private final OnExerciseClickListener listener;

    public ExerciseSelectAdapter(List<ExerciseEntity> data,
                                 Map<Integer, String> muscleNameMap,
                                 Set<Integer> selectedIds,
                                 OnExerciseClickListener listener) {
        this.data = data;
        this.muscleNameMap = muscleNameMap;
        this.selectedIds = selectedIds;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise_select, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ExerciseEntity e = data.get(position);
        // Nombre del ejercicio (fallback cadena vacía para evitar null en UI)
        String name = (e.name == null ? "" : e.name.trim());
        h.tvName.setText(name);

        // Inicial (placeholder circular). Si no hay nombre, se usa '?'
        String initial = name.isEmpty() ? "?" : name.substring(0, 1).toUpperCase();
        h.tvInitial.setText(initial);

        // Carga de imagen con Glide si hay URL; si hay imagen ocultamos la inicial para no superponer
        if (e.imageUrl != null && !e.imageUrl.trim().isEmpty()) {
            h.tvInitial.setVisibility(View.GONE);
            try {
                Glide.with(h.ivImage.getContext())
                        .load(e.imageUrl)
                        .centerCrop()
                        .placeholder(R.drawable.bg_placeholder_circle)
                        .error(R.drawable.bg_placeholder_circle)
                        .into(h.ivImage);
            } catch (Exception ex) {
                // Fallback: mostrar inicial si Glide falla
                h.tvInitial.setVisibility(View.VISIBLE);
            }
        } else {
            // No hay URL: mostrar sólo placeholder circular + inicial
            h.ivImage.setImageDrawable(null); // Mantener fondo circular definido en layout
            h.tvInitial.setVisibility(View.VISIBLE);
        }

        // Obtener primer músculo principal, si no hay usar secundario; si tampoco hay, mostrar '—'
        String muscleLabel = "";
        if (e.muscles != null && !e.muscles.isEmpty()) {
            Integer id = e.muscles.get(0);
            muscleLabel = muscleNameMap.get(id);
        }
        if ((muscleLabel == null || muscleLabel.isEmpty()) && e.musclesSecondary != null && !e.musclesSecondary.isEmpty()) {
            Integer id2 = e.musclesSecondary.get(0);
            muscleLabel = muscleNameMap.get(id2);
        }
        if (muscleLabel == null || muscleLabel.trim().isEmpty()) {
            muscleLabel = "—"; // Fallback visual cuando no hay datos disponibles
        }
        h.tvMuscle.setText(muscleLabel);

        boolean selected = selectedIds.contains(e.id);
        h.container.setSelected(selected);
        h.ivCheck.setVisibility(selected ? View.VISIBLE : View.GONE);

        h.container.setOnClickListener(v -> listener.onClick(e));
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        View container; TextView tvInitial; TextView tvName; TextView tvMuscle; ImageView ivCheck; ImageView ivImage;
        VH(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.containerItem);
            tvInitial = itemView.findViewById(R.id.tvInitial);
            tvName = itemView.findViewById(R.id.tvName);
            tvMuscle = itemView.findViewById(R.id.tvMuscle);
            ivCheck = itemView.findViewById(R.id.ivCheck);
            ivImage = itemView.findViewById(R.id.ivImage);
        }
    }
}
