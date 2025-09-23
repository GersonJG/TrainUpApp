package umg.edu.gt.trainupapp.data.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * UserEntity: tabla principal para datos personales del usuario.
 * - id autogenerado como PK
 * - name, age, height(cm), weight(kg), gender
 * - imc e imcCategory almacenan el resultado del cálculo de IMC
 */
@Entity(tableName = "user_data")
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public int age;
    public float height; // cm
    public float weight; // kg
    public String gender;
    public float imc;
    public String imcCategory;
}

