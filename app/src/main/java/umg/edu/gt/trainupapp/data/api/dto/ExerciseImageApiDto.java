package umg.edu.gt.trainupapp.data.api.dto;

import com.google.gson.annotations.SerializedName;

/**
 * ExerciseImageApiDto
 * -------------------
 * DTO opcional del endpoint /exerciseimage/.
 * En esta fase no se descarga para cada ejercicio (costoso). Se deja preparado para
 * una sincronización diferida de imágenes. Si se implementa posteriormente, se buscará
 * la imagen principal (is_main = true) asociada al ID de ejercicio.
 */
public class ExerciseImageApiDto {
    @SerializedName("id") public int id;
    @SerializedName("uuid") public String uuid; // Identificador único de la imagen
    @SerializedName("exercise") public int exerciseId; // ID de ejercicio
    @SerializedName("is_main") public boolean isMain; // true si es imagen principal
    @SerializedName("image") public String imageUrl; // URL absoluta a la imagen
}

