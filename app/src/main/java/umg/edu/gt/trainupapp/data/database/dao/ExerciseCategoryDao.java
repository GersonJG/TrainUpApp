package umg.edu.gt.trainupapp.data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import umg.edu.gt.trainupapp.data.database.entity.ExerciseCategoryEntity;

/**
 * ExerciseCategoryDao
 * -------------------
 * Acceso a catálogo de categorías de ejercicios.
 */
@Dao
public interface ExerciseCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategory(ExerciseCategoryEntity category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategories(List<ExerciseCategoryEntity> categories);

    @Query("SELECT * FROM exercise_categories ORDER BY name ASC")
    List<ExerciseCategoryEntity> getAllCategories();

    @Query("SELECT * FROM exercise_categories WHERE id = :categoryId LIMIT 1")
    ExerciseCategoryEntity getCategoryById(int categoryId);

    @Query("DELETE FROM exercise_categories")
    void deleteAllCategories();
}

