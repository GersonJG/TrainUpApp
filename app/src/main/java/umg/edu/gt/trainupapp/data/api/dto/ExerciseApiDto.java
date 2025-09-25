package umg.edu.gt.trainupapp.data.api.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * ExerciseApiDto
 * --------------
 * DTO que representa un ejercicio recibido desde el endpoint /exercise/ de la API wger.
 * Notas:
 *  - Algunos campos vienen en inglés aun cuando se filtra por language=2 (ES) para name/description.
 *  - Las listas de IDs (muscles, muscles_secondary, equipment) se mapearán directamente a List<Integer>
 *    y luego se persistirán en Room como JSON (con un TypeConverter) dentro de ExerciseEntity.
 *  - imageUrl NO se provee directamente; se deja null (imagen diferida).
 */
public class ExerciseApiDto {
    @SerializedName("id") public int id;
    @SerializedName("name") public String name;
    @SerializedName("description") public String description;
    @SerializedName("category") public int categoryId; // ID de categoría
    @SerializedName("muscles") public List<Integer> muscles; // principales
    @SerializedName("muscles_secondary") public List<Integer> musclesSecondary; // secundarios
    @SerializedName("equipment") public List<Integer> equipment; // IDs de equipamiento
    @SerializedName("language") public int languageId; // 2 = español
}

