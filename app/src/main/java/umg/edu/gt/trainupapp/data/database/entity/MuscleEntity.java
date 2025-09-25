package umg.edu.gt.trainupapp.data.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * MuscleEntity
 * ------------
 * Catálogo de músculos provenientes del endpoint /muscle/ de wger.
 * Campo isFront indica si el músculo aparece en la vista frontal (permitirá filtros visuales futuros).
 */
@Entity(tableName = "muscles")
public class MuscleEntity {
    @PrimaryKey
    public int id;

    @NonNull
    public String name;

    public boolean isFront; // true si pertenece a la vista frontal anatómica

    public MuscleEntity(int id, @NonNull String name, boolean isFront) {
        this.id = id;
        this.name = name;
        this.isFront = isFront;
    }
}

