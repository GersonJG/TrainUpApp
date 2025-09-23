package umg.edu.gt.trainupapp.data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import umg.edu.gt.trainupapp.data.database.entity.EquipmentEntity;

@Dao
public interface EquipmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEquipment(EquipmentEntity equipment);

    @Update
    void updateEquipment(EquipmentEntity equipment);

    @Query("SELECT * FROM equipment WHERE userId = :userId LIMIT 1")
    EquipmentEntity getEquipment(int userId);
}

