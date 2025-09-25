package umg.edu.gt.trainupapp.data.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * ExerciseEquipmentEntity
 * -----------------------
 * Catálogo de equipamiento proveniente del endpoint /equipment/ de wger.
 * Se mantiene separado de la tabla de onboarding (EquipmentEntity - table: equipment) para
 * evitar conflicto de nombres y propósitos. Esta tabla almacena el universo de equipos
 * disponibles para ejercicios, permitiendo filtrado y mapeo futuro.
 */
@Entity(tableName = "exercise_equipment_items")
public class ExerciseEquipmentEntity {
    @PrimaryKey
    public int id; // ID del equipo según wger

    @NonNull
    public String name; // Nombre del equipo (en español si la API lo provee o traducido)

    public ExerciseEquipmentEntity(int id, @NonNull String name) {
        this.id = id;
        this.name = name;
    }
}

