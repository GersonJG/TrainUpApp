package umg.edu.gt.trainupapp.data.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;
import umg.edu.gt.trainupapp.data.api.dto.ApiResponse;
import umg.edu.gt.trainupapp.data.api.dto.CategoryApiDto;
import umg.edu.gt.trainupapp.data.api.dto.EquipmentApiDto;
import umg.edu.gt.trainupapp.data.api.dto.ExerciseApiDto;
import umg.edu.gt.trainupapp.data.api.dto.ExerciseImageApiDto;
import umg.edu.gt.trainupapp.data.api.dto.MuscleApiDto;

/**
 * WgerApiService
 * --------------
 * Interface Retrofit que define los endpoints necesarios para sincronizar datos
 * desde la API pública de wger (https://wger.de/api/v2/).
 *
 * Notas:
 *  - Se usan objetos ApiResponse<T> para manejar la paginación (campos next / previous).
 *  - Para ejercicios se soporta paginación a través de getExercises(...) y getExercisesByUrl(...).
 *  - imageUrl no se obtiene aún (descarga diferida de imágenes).
 */
public interface WgerApiService {

    // Ejercicios paginados: language=2 (español), page número progresivo
    @GET("exercise/")
    Call<ApiResponse<ExerciseApiDto>> getExercises(@Query("language") int language,
                                                   @Query("page") int page);

    // Siguiente página usando URL completa provista en el campo 'next'
    @GET
    Call<ApiResponse<ExerciseApiDto>> getExercisesByUrl(@Url String nextUrl);

    // Catálogo de categorías
    @GET("exercisecategory/")
    Call<ApiResponse<CategoryApiDto>> getCategories();

    // Catálogo de músculos
    @GET("muscle/")
    Call<ApiResponse<MuscleApiDto>> getMuscles();

    // Catálogo de equipamiento
    @GET("equipment/")
    Call<ApiResponse<EquipmentApiDto>> getEquipment();

    // Imágenes de ejercicios (opcional, futuro)
    @GET("exerciseimage/")
    Call<ApiResponse<ExerciseImageApiDto>> getExerciseImages(@Query("exercise") int exerciseId);
}

