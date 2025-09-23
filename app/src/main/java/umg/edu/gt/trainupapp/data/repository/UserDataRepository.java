package umg.edu.gt.trainupapp.data.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import umg.edu.gt.trainupapp.data.database.TrainUpDatabase;
import umg.edu.gt.trainupapp.data.database.dao.EquipmentDao;
import umg.edu.gt.trainupapp.data.database.dao.FitnessProfileDao;
import umg.edu.gt.trainupapp.data.database.dao.InjuriesDao;
import umg.edu.gt.trainupapp.data.database.dao.UserDao;
import umg.edu.gt.trainupapp.data.database.entity.EquipmentEntity;
import umg.edu.gt.trainupapp.data.database.entity.FitnessProfileEntity;
import umg.edu.gt.trainupapp.data.database.entity.InjuriesEntity;
import umg.edu.gt.trainupapp.data.database.entity.UserEntity;

/**
 * UserDataRepository: encapsula acceso a Room (DAO) para las pantallas de onboarding y perfil.
 * Todas las operaciones se ejecutan en un hilo de IO y la respuesta vuelve al hilo principal.
 */
public class UserDataRepository {
    private final UserDao userDao;
    private final FitnessProfileDao fitnessDao;
    private final EquipmentDao equipmentDao;
    private final InjuriesDao injuriesDao;

    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());

    public interface IntCallback { void onResult(int value); }
    public interface VoidCallback { void onComplete(); }
    public interface UserCallback { void onResult(UserEntity entity); }
    public interface FitnessCallback { void onResult(FitnessProfileEntity entity); }
    public interface EquipmentCallback { void onResult(EquipmentEntity entity); }
    public interface InjuriesCallback { void onResult(InjuriesEntity entity); }

    public UserDataRepository(Context ctx) {
        TrainUpDatabase db = TrainUpDatabase.getDatabase(ctx);
        userDao = db.userDao();
        fitnessDao = db.fitnessProfileDao();
        equipmentDao = db.equipmentDao();
        injuriesDao = db.injuriesDao();
    }

    /** Inserta usuario y entrega su id generado. */
    public void insertUser(UserEntity entity, IntCallback cb) {
        io.execute(() -> {
            long id = userDao.insertUser(entity);
            int uid = (int) id;
            main.post(() -> { if (cb != null) cb.onResult(uid); });
        });
    }

    /** Actualiza un usuario existente. */
    public void updateUser(UserEntity entity, VoidCallback cb) {
        io.execute(() -> {
            userDao.updateUser(entity);
            main.post(() -> { if (cb != null) cb.onComplete(); });
        });
    }

    /** Obtiene el último usuario creado (para fallback). */
    public void getLatestUserId(IntCallback cb) {
        io.execute(() -> {
            UserEntity u = userDao.getLatestUser();
            int uid = (u != null) ? u.id : -1;
            main.post(() -> { if (cb != null) cb.onResult(uid); });
        });
    }

    /** Lee User por id. */
    public void getUserById(int userId, UserCallback cb) {
        io.execute(() -> {
            UserEntity u = userDao.getUser(userId);
            main.post(() -> { if (cb != null) cb.onResult(u); });
        });
    }

    /** Upsert del perfil fitness (REPLACE por PK userId). */
    public void upsertFitness(FitnessProfileEntity e, VoidCallback cb) {
        io.execute(() -> {
            fitnessDao.insertFitnessProfile(e);
            main.post(() -> { if (cb != null) cb.onComplete(); });
        });
    }

    /** Lee FitnessProfile por userId. */
    public void getFitnessByUserId(int userId, FitnessCallback cb) {
        io.execute(() -> {
            FitnessProfileEntity e = fitnessDao.getFitnessProfile(userId);
            main.post(() -> { if (cb != null) cb.onResult(e); });
        });
    }

    /** Upsert de equipamiento. */
    public void upsertEquipment(EquipmentEntity e, VoidCallback cb) {
        io.execute(() -> {
            equipmentDao.insertEquipment(e);
            main.post(() -> { if (cb != null) cb.onComplete(); });
        });
    }

    /** Lee Equipment por userId. */
    public void getEquipmentByUserId(int userId, EquipmentCallback cb) {
        io.execute(() -> {
            EquipmentEntity e = equipmentDao.getEquipment(userId);
            main.post(() -> { if (cb != null) cb.onResult(e); });
        });
    }

    /** Upsert de lesiones. */
    public void upsertInjuries(InjuriesEntity e, VoidCallback cb) {
        io.execute(() -> {
            injuriesDao.insertInjuries(e);
            main.post(() -> { if (cb != null) cb.onComplete(); });
        });
    }

    /** Lee Injuries por userId. */
    public void getInjuriesByUserId(int userId, InjuriesCallback cb) {
        io.execute(() -> {
            InjuriesEntity e = injuriesDao.getInjuries(userId);
            main.post(() -> { if (cb != null) cb.onResult(e); });
        });
    }
}
