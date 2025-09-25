package umg.edu.gt.trainupapp.data.api.dto;

import com.google.gson.annotations.SerializedName;

/**
 * CategoryApiDto
 * --------------
 * DTO para elementos devueltos por /exercisecategory/.
 * Nota: Los nombres pueden venir en inglés. Se podría traducir/localizar en fases futuras.
 */
public class CategoryApiDto {
    @SerializedName("id") public int id;
    @SerializedName("name") public String name;
}

