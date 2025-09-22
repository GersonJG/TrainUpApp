package umg.edu.gt.trainupapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import umg.edu.gt.trainupapp.R;

/**
 * IntroFeatureAdapter
 * Adaptador simple para mostrar tarjetas horizontales con ícono y texto en IntroActivity.
 */
public class IntroFeatureAdapter extends RecyclerView.Adapter<IntroFeatureAdapter.VH> {

    private int[] icons = new int[0];
    private int[] titles = new int[0];

    public void submit(int[] icons, int[] titles) {
        this.icons = icons != null ? icons : new int[0];
        this.titles = titles != null ? titles : new int[0];
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_intro_feature, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (position < icons.length && position < titles.length) {
            holder.icon.setImageResource(icons[position]);
            holder.title.setText(titles[position]);
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(icons.length, titles.length);
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        VH(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon); // ImageView del ícono
            title = itemView.findViewById(R.id.title); // Texto del título
        }
    }
}

