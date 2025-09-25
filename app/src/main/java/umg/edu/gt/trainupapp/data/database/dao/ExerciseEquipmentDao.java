package umg.edu.gt.trainupapp.data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import umg.edu.gt.trainupapp.data.database.entity.ExerciseEquipmentEntity;

/**
 * ExerciseEquipmentDao
 * --------------------
 * Acceso al catálogo de equipamiento utilizado por los ejercicios (distinto del equipamiento
 * seleccionado por el usuario en el onboarding). Permite sincronizar y consultar los nombres
 * de los equipos para mapear los IDs almacenados en ExerciseEntity.equipment.
 */
@Dao
public interface ExerciseEquipmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEquipment(ExerciseEquipmentEntity equipmentItem);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEquipmentList(List<ExerciseEquipmentEntity> equipmentItems);

    @Query("SELECT * FROM exercise_equipment_items ORDER BY name ASC")
    List<ExerciseEquipmentEntity> getAllEquipmentItems();

    @Query("SELECT * FROM exercise_equipment_items WHERE id = :equipmentId LIMIT 1")
    ExerciseEquipmentEntity getEquipmentById(int equipmentId);

    @Query("DELETE FROM exercise_equipment_items")
    void deleteAllEquipmentItems();
}

