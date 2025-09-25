package umg.edu.gt.trainupapp.data.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * ExerciseCategoryEntity
 * ----------------------
 * Catálogo de categorías de ejercicios proveniente del endpoint /exercisecategory/ de wger.
 * Se guarda localmente para permitir filtrado offline.
 */
@Entity(tableName = "exercise_categories")
public class ExerciseCategoryEntity {
    @PrimaryKey
    public int id; // ID provisto por la API

    @NonNull
    public String name; // Nombre en español (o traducido)

    public ExerciseCategoryEntity(int id, @NonNull String name) {
        this.id = id;
        this.name = name;
    }
}

