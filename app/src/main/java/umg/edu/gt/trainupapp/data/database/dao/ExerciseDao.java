package umg.edu.gt.trainupapp.data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import umg.edu.gt.trainupapp.data.database.entity.ExerciseEntity;

/**
 * ExerciseDao
 * -----------
 * Acceso a la tabla "exercises" que almacena los ejercicios descargados de la API wger.
 * NOTA sobre filtros por listas (músculos/equipamiento):
 *  - Las listas se almacenan como JSON (ej: [1,2,5]). SQLite sin extensión JSON1 no permite
 *    queries avanzadas; se usa LIKE provisionalmente. Hay riesgo de coincidencias parciales
 *    (ID 1 dentro de 10). Mitigación parcial: patrones con corchetes y comas.
 *  - Fase futura: normalizar relaciones many-to-many con tablas puente e índices.
 */
@Dao
public interface ExerciseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertExercise(ExerciseEntity exercise);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertExercises(List<ExerciseEntity> exercises);

    @Update
    void updateExercise(ExerciseEntity exercise);

    @Query("SELECT * FROM exercises ORDER BY name ASC")
    List<ExerciseEntity> getAllExercises();

    @Query("SELECT * FROM exercises WHERE id = :exerciseId LIMIT 1")
    ExerciseEntity getExerciseById(int exerciseId);

    // Búsqueda por nombre (case-insensitive)
    @Query("SELECT * FROM exercises WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' ORDER BY name ASC")
    List<ExerciseEntity> searchExercisesByName(String query);

    /* Filtros provisionales por ID dentro de arrays JSON serializados.
       Patrones evaluados:
       1) '[id,'  -> al inicio
       2) ',id,'  -> en medio
       3) ',id]'  -> al final
       4) '[id]'  -> único elemento
     */
    @Query("SELECT * FROM exercises WHERE (muscles LIKE '%[' || :muscleId || ',%' OR muscles LIKE '%,' || :muscleId || ',%' OR muscles LIKE '%,' || :muscleId || ']%' OR muscles LIKE '%[' || :muscleId || ']%')")
    List<ExerciseEntity> getExercisesByPrimaryMuscle(int muscleId);

    @Query("SELECT * FROM exercises WHERE (equipment LIKE '%[' || :equipmentId || ',%' OR equipment LIKE '%,' || :equipmentId || ',%' OR equipment LIKE '%,' || :equipmentId || ']%' OR equipment LIKE '%[' || :equipmentId || ']%')")
    List<ExerciseEntity> getExercisesByEquipment(int equipmentId);

    @Query("SELECT * FROM exercises WHERE categoryId = :categoryId ORDER BY name ASC")
    List<ExerciseEntity> getExercisesByCategory(int categoryId);

    @Query("DELETE FROM exercises")
    void deleteAllExercises();

    @Query("SELECT COUNT(*) FROM exercises")
    int getCount();
}
