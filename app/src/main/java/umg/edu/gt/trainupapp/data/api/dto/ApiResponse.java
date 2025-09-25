package umg.edu.gt.trainupapp.data.api.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * ApiResponse<T>
 * --------------
 * Modelo genérico que representa la estructura de paginación estándar de la API wger.
 * Campos:
 *  - count: total de elementos disponibles.
 *  - next: URL completa de la siguiente página (o null si no hay más páginas).
 *  - previous: URL de la página anterior (puede ser null).
 *  - results: lista de elementos de tipo T.
 */
public class ApiResponse<T> {
    @SerializedName("count")
    public int count;
    @SerializedName("next")
    public String next;
    @SerializedName("previous")
    public String previous;
    @SerializedName("results")
    public List<T> results;
}

