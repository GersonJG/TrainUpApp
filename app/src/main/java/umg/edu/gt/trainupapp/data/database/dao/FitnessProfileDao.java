package umg.edu.gt.trainupapp.data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import umg.edu.gt.trainupapp.data.database.entity.FitnessProfileEntity;

@Dao
public interface FitnessProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFitnessProfile(FitnessProfileEntity profile);

    @Update
    void updateFitnessProfile(FitnessProfileEntity profile);

    @Query("SELECT * FROM fitness_profile WHERE userId = :userId LIMIT 1")
    FitnessProfileEntity getFitnessProfile(int userId);
}

