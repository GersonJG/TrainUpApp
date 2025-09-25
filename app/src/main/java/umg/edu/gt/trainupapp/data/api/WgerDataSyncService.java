package umg.edu.gt.trainupapp.data.api;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;
import umg.edu.gt.trainupapp.data.api.dto.ApiResponse;
import umg.edu.gt.trainupapp.data.api.dto.CategoryApiDto;
import umg.edu.gt.trainupapp.data.api.dto.EquipmentApiDto;
import umg.edu.gt.trainupapp.data.api.dto.ExerciseApiDto;
import umg.edu.gt.trainupapp.data.api.dto.ExerciseImageApiDto;
import umg.edu.gt.trainupapp.data.api.dto.MuscleApiDto;
import umg.edu.gt.trainupapp.data.database.TrainUpDatabase;
import umg.edu.gt.trainupapp.data.database.entity.ExerciseCategoryEntity;
import umg.edu.gt.trainupapp.data.database.entity.ExerciseEntity;
import umg.edu.gt.trainupapp.data.database.entity.ExerciseEquipmentEntity;
import umg.edu.gt.trainupapp.data.database.entity.MuscleEntity;

/**
 * WgerDataSyncService
 * -------------------
 * Orquesta la sincronización completa de catálogos (categorías, músculos, equipamiento)
 * y ejercicios desde la API pública de wger, almacenando los datos en la base local Room.
 *
 * Características principales:
 *  - Descarga paginada de ejercicios usando los enlaces 'next'.
 *  - Limpieza (TRUNCATE lógico) de tablas relacionadas antes de insertar.
 *  - Inserción dentro de una única transacción para garantizar consistencia.
 *  - Rollback automático: si algo falla dentro de la transacción no se aplica ningún cambio.
 *  - Callback al hilo principal para informar éxito o error.
 *  - Un solo intento (reintentos futuros podrían añadirse fácilmente).
 *  - imageUrl se mantiene null (fase futura: sincronización diferida de imágenes).
 *
 * Limitaciones (documentadas para futuras mejoras):
 *  - Filtrado por músculos/equipamiento depende de consultas LIKE sobre JSON (ver ExerciseDao).
 *  - No se hace delta update; siempre se reemplaza la totalidad del dataset.
 *  - Catálogos almacenados tal cual (en inglés) cuando la API no provee traducción.
 */
public class WgerDataSyncService {

    public interface SyncCallback {
        void onSuccess(int totalExercises);
        void onError(String message, Throwable cause);
    }

    private static final String TAG = "WgerSync";
    private static final int LANGUAGE_SPANISH = 2;

    private final WgerApiService apiService;
    private final TrainUpDatabase database;
    private final ExecutorService executor;
    private final Handler mainHandler;

    public WgerDataSyncService(TrainUpDatabase database) {
        this.apiService = WgerApiClient.getService();
        this.database = database;
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Inicia la sincronización completa. Ejecuta todo en un hilo de background.
     */
    public void syncAll(SyncCallback callback) {
        executor.execute(() -> {
            try {
                Log.d(TAG, "Iniciando sincronización wger...");

                // 1. Descargar catálogos
                List<ExerciseCategoryEntity> categoryEntities = fetchCategories();
                List<MuscleEntity> muscleEntities = fetchMuscles();
                List<ExerciseEquipmentEntity> equipmentEntities = fetchEquipment();

                // 2. Descargar ejercicios paginados
                List<ExerciseEntity> exerciseEntities = fetchAllExercisesPaged();

                // 3. Persistir TODO dentro de una transacción para garantizar atomicidad
                database.runInTransaction(() -> {
                    // Limpiar tablas (orden: ejercicios primero por seguridad de FK futuras)
                    database.exerciseDao().deleteAllExercises();
                    database.exerciseCategoryDao().deleteAllCategories();
                    database.muscleDao().deleteAllMuscles();
                    database.exerciseEquipmentDao().deleteAllEquipmentItems();

                    // Insertar catálogos
                    database.exerciseCategoryDao().insertCategories(categoryEntities);
                    database.muscleDao().insertMuscles(muscleEntities);
                    database.exerciseEquipmentDao().insertEquipmentList(equipmentEntities);

                    // Insertar ejercicios
                    database.exerciseDao().insertExercises(exerciseEntities);
                });

                int total = exerciseEntities.size();
                Log.d(TAG, "Sincronización completada. Ejercicios: " + total);
                postSuccess(callback, total);

            } catch (Exception e) {
                Log.e(TAG, "Error en sincronización", e);
                postError(callback, "Error durante la sincronización", e);
            }
        });
    }

    // ------------------ DESCARGA DE CATÁLOGOS ------------------

    private List<ExerciseCategoryEntity> fetchCategories() throws IOException {
        Call<ApiResponse<CategoryApiDto>> call = apiService.getCategories();
        Response<ApiResponse<CategoryApiDto>> response = call.execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new IOException("Fallo al obtener categorías: HTTP " + response.code());
        }
        List<ExerciseCategoryEntity> list = new ArrayList<>();
        if (response.body().results != null) {
            for (CategoryApiDto dto : response.body().results) {
                list.add(new ExerciseCategoryEntity(dto.id, dto.name == null ? "" : dto.name));
            }
        }
        return list;
    }

    private List<MuscleEntity> fetchMuscles() throws IOException {
        Call<ApiResponse<MuscleApiDto>> call = apiService.getMuscles();
        Response<ApiResponse<MuscleApiDto>> response = call.execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new IOException("Fallo al obtener músculos: HTTP " + response.code());
        }
        List<MuscleEntity> list = new ArrayList<>();
        if (response.body().results != null) {
            for (MuscleApiDto dto : response.body().results) {
                list.add(new MuscleEntity(dto.id, dto.name == null ? "" : dto.name, dto.isFront));
            }
        }
        return list;
    }

    private List<ExerciseEquipmentEntity> fetchEquipment() throws IOException {
        Call<ApiResponse<EquipmentApiDto>> call = apiService.getEquipment();
        Response<ApiResponse<EquipmentApiDto>> response = call.execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new IOException("Fallo al obtener equipamiento: HTTP " + response.code());
        }
        List<ExerciseEquipmentEntity> list = new ArrayList<>();
        if (response.body().results != null) {
            for (EquipmentApiDto dto : response.body().results) {
                list.add(new ExerciseEquipmentEntity(dto.id, dto.name == null ? "" : dto.name));
            }
        }
        return list;
    }

    // ------------------ DESCARGA PAGINADA DE EJERCICIOS ------------------

    private List<ExerciseEntity> fetchAllExercisesPaged() throws IOException {
        List<ExerciseEntity> all = new ArrayList<>();

        int page = 1;
        String nextUrl = null;
        boolean first = true;

        while (first || nextUrl != null) {
            ApiResponse<ExerciseApiDto> body;
            if (first) {
                first = false;
                Response<ApiResponse<ExerciseApiDto>> resp = apiService.getExercises(LANGUAGE_SPANISH, page).execute();
                if (!resp.isSuccessful() || resp.body() == null) {
                    throw new IOException("Fallo al obtener ejercicios página 1: HTTP " + resp.code());
                }
                body = resp.body();
            } else {
                Response<ApiResponse<ExerciseApiDto>> resp = apiService.getExercisesByUrl(nextUrl).execute();
                if (!resp.isSuccessful() || resp.body() == null) {
                    throw new IOException("Fallo al obtener ejercicios página siguiente: HTTP " + resp.code());
                }
                body = resp.body();
            }

            if (body.results != null) {
                for (ExerciseApiDto dto : body.results) {
                    String imageUrl = null;
                    try {
                        // Llamada adicional para intentar obtener imagen principal
                        Response<ApiResponse<ExerciseImageApiDto>> imgResp = apiService.getExerciseImages(dto.id).execute();
                        if (imgResp.isSuccessful() && imgResp.body() != null && imgResp.body().results != null) {
                            for (ExerciseImageApiDto imgDto : imgResp.body().results) {
                                if (imgDto.isMain) { imageUrl = imgDto.imageUrl; break; }
                            }
                            if (imageUrl == null && !imgResp.body().results.isEmpty()) {
                                // Fallback: primera imagen aunque no sea main
                                imageUrl = imgResp.body().results.get(0).imageUrl;
                            }
                        }
                    } catch (Exception ex) {
                        // No interrumpir toda la sincronización si falla una imagen
                        Log.w(TAG, "Fallo obteniendo imagen para ejercicio " + dto.id, ex);
                    }
                    ExerciseEntity entity = new ExerciseEntity(
                            dto.id,
                            dto.name == null ? "" : dto.name,
                            dto.description,
                            imageUrl, // ahora potencialmente con URL
                            dto.categoryId,
                            dto.muscles,
                            dto.musclesSecondary,
                            dto.equipment,
                            dto.languageId
                    );
                    all.add(entity);
                }
            }
            nextUrl = body.next; // puede ser null -> termina loop
            page++;
        }

        return all;
    }

    // ------------------ UTILIDADES DE CALLBACK ------------------

    private void postSuccess(SyncCallback callback, int total) {
        if (callback == null) return;
        mainHandler.post(() -> callback.onSuccess(total));
    }

    private void postError(SyncCallback callback, String message, Throwable cause) {
        if (callback == null) return;
        mainHandler.post(() -> callback.onError(message, cause));
    }

    /**
     * Ejemplo de uso (futuro ViewModel / Activity):
     *
     * // TrainUpDatabase db = TrainUpDatabase.getDatabase(context);
     * // WgerDataSyncService service = new WgerDataSyncService(db);
     * // service.syncAll(new WgerDataSyncService.SyncCallback() {
     * //     @Override public void onSuccess(int totalExercises) {
     * //         Log.d("Sync", "Descargados: " + totalExercises);
     * //     }
     * //     @Override public void onError(String message, Throwable cause) {
     * //         Log.e("Sync", "Fallo: " + message, cause);
     * //     }
     * // });
     */
}
