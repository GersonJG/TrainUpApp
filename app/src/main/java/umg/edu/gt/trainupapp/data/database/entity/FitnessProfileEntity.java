package umg.edu.gt.trainupapp.data.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * FitnessProfileEntity: datos de nivel, objetivo y días seleccionados (como JSON string).
 * userId es PK y referencia lógica a UserEntity.id.
 */
@Entity(tableName = "fitness_profile")
public class FitnessProfileEntity {
    @PrimaryKey
    public int userId; // Relación con UserEntity

    public String experienceLevel; // Principiante, Intermedio, Avanzado
    public String goal; // Perder peso, Ganar músculo, etc.
    public String availableDays; // JSON string con días seleccionados
}

