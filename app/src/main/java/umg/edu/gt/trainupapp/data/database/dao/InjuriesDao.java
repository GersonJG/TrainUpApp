package umg.edu.gt.trainupapp.data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import umg.edu.gt.trainupapp.data.database.entity.InjuriesEntity;

@Dao
public interface InjuriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertInjuries(InjuriesEntity injuries);

    @Update
    void updateInjuries(InjuriesEntity injuries);

    @Query("SELECT * FROM injuries WHERE userId = :userId LIMIT 1")
    InjuriesEntity getInjuries(int userId);
}

