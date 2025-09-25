package umg.edu.gt.trainupapp.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import umg.edu.gt.trainupapp.data.database.converters.ExerciseConverters;
import umg.edu.gt.trainupapp.data.database.dao.EquipmentDao;
import umg.edu.gt.trainupapp.data.database.dao.FitnessProfileDao;
import umg.edu.gt.trainupapp.data.database.dao.InjuriesDao;
import umg.edu.gt.trainupapp.data.database.dao.UserDao;
// DAOs de ejercicios
import umg.edu.gt.trainupapp.data.database.dao.ExerciseDao;
import umg.edu.gt.trainupapp.data.database.dao.ExerciseCategoryDao;
import umg.edu.gt.trainupapp.data.database.dao.MuscleDao;
import umg.edu.gt.trainupapp.data.database.dao.ExerciseEquipmentDao;

import umg.edu.gt.trainupapp.data.database.entity.EquipmentEntity; // Onboarding user equipment (per-user)
import umg.edu.gt.trainupapp.data.database.entity.FitnessProfileEntity;
import umg.edu.gt.trainupapp.data.database.entity.InjuriesEntity;
import umg.edu.gt.trainupapp.data.database.entity.UserEntity;
// Entidades de ejercicios (catálogos y ejercicios)
import umg.edu.gt.trainupapp.data.database.entity.ExerciseEntity;
import umg.edu.gt.trainupapp.data.database.entity.ExerciseCategoryEntity;
import umg.edu.gt.trainupapp.data.database.entity.MuscleEntity;
import umg.edu.gt.trainupapp.data.database.entity.ExerciseEquipmentEntity;

/**
 * TrainUpDatabase
 * Base de datos unificada que contiene:
 * - Entidades del onboarding del usuario
 * - Catálogos e información de ejercicios obtenidos de la API wger
 * Se emplea un TypeConverter (ExerciseConverters) para listas de enteros.
 * VERSION 3: Se añaden tablas de ejercicios (ExerciseEntity, ExerciseCategoryEntity,
 * MuscleEntity, ExerciseEquipmentEntity). Se usa fallbackToDestructiveMigration mientras
 * no se definan migraciones explícitas (pendiente para producción).
 */
@Database(
        entities = {
                // Onboarding
                UserEntity.class,
                FitnessProfileEntity.class,
                EquipmentEntity.class,
                InjuriesEntity.class,
                // Ejercicios
                ExerciseEntity.class,
                ExerciseCategoryEntity.class,
                MuscleEntity.class,
                ExerciseEquipmentEntity.class
        },
        version = 3,
        exportSchema = false
)
@TypeConverters({ExerciseConverters.class})
public abstract class TrainUpDatabase extends RoomDatabase {
    // DAOs Onboarding
    public abstract UserDao userDao();
    public abstract FitnessProfileDao fitnessProfileDao();
    public abstract EquipmentDao equipmentDao();
    public abstract InjuriesDao injuriesDao();

    // DAOs Ejercicios
    public abstract ExerciseDao exerciseDao();
    public abstract ExerciseCategoryDao exerciseCategoryDao();
    public abstract MuscleDao muscleDao();
    public abstract ExerciseEquipmentDao exerciseEquipmentDao();

    private static volatile TrainUpDatabase INSTANCE;

    public static TrainUpDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TrainUpDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    TrainUpDatabase.class,
                                    "trainup_database"
                            )
                            // Desarrollo: destrucción en caso de migración incompatible.
                            // PRODUCCIÓN: Implementar Migration adecuada.
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
