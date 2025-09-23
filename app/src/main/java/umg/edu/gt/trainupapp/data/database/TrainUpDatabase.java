package umg.edu.gt.trainupapp.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import umg.edu.gt.trainupapp.data.database.dao.EquipmentDao;
import umg.edu.gt.trainupapp.data.database.dao.FitnessProfileDao;
import umg.edu.gt.trainupapp.data.database.dao.InjuriesDao;
import umg.edu.gt.trainupapp.data.database.dao.UserDao;
import umg.edu.gt.trainupapp.data.database.entity.EquipmentEntity;
import umg.edu.gt.trainupapp.data.database.entity.FitnessProfileEntity;
import umg.edu.gt.trainupapp.data.database.entity.InjuriesEntity;
import umg.edu.gt.trainupapp.data.database.entity.UserEntity;

/**
 * TrainUpDatabase: instancia Room que contiene entidades del onboarding.
 */
@Database(
        entities = {UserEntity.class, FitnessProfileEntity.class, EquipmentEntity.class, InjuriesEntity.class},
        version = 1,
        exportSchema = false
)
public abstract class TrainUpDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract FitnessProfileDao fitnessProfileDao();
    public abstract EquipmentDao equipmentDao();
    public abstract InjuriesDao injuriesDao();

    private static volatile TrainUpDatabase INSTANCE;

    public static TrainUpDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TrainUpDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            TrainUpDatabase.class,
                            "trainup_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
