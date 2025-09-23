package umg.edu.gt.trainupapp.data.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * InjuriesEntity: lista de lesiones como JSON string y flag hasInjuries.
 */
@Entity(tableName = "injuries")
public class InjuriesEntity {
    @PrimaryKey
    public int userId; // Relación con UserEntity

    public String injuriesList; // JSON string con lesiones o vacío si none
    public boolean hasInjuries;
}

