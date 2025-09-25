package umg.edu.gt.trainupapp.data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import umg.edu.gt.trainupapp.data.database.entity.MuscleEntity;

/**
 * MuscleDao
 * ---------
 * Acceso al catálogo de músculos.
 */
@Dao
public interface MuscleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMuscle(MuscleEntity muscle);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMuscles(List<MuscleEntity> muscles);

    @Query("SELECT * FROM muscles ORDER BY name ASC")
    List<MuscleEntity> getAllMuscles();

    @Query("SELECT * FROM muscles WHERE id = :muscleId LIMIT 1")
    MuscleEntity getMuscleById(int muscleId);

    @Query("DELETE FROM muscles")
    void deleteAllMuscles();
}

