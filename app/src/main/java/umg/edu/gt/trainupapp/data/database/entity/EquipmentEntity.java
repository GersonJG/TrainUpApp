package umg.edu.gt.trainupapp.data.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * EquipmentEntity: lugar de entrenamiento y equipamiento disponible (JSON string).
 */
@Entity(tableName = "equipment")
public class EquipmentEntity {
    @PrimaryKey
    public int userId; // Relación con UserEntity

    public String trainingLocation; // Casa, Gimnasio, etc.
    public String availableEquipment; // JSON string con equipamiento
}
