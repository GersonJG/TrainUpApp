package umg.edu.gt.trainupapp.data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import umg.edu.gt.trainupapp.data.database.entity.UserEntity;

@Dao
public interface UserDao {
    /** Inserta usuario y retorna id generado. */
    @Insert
    long insertUser(UserEntity user);

    @Update
    void updateUser(UserEntity user);

    @Query("SELECT * FROM user_data WHERE id = :userId LIMIT 1")
    UserEntity getUser(int userId);

    @Query("SELECT * FROM user_data ORDER BY id DESC LIMIT 1")
    UserEntity getLatestUser();
}

