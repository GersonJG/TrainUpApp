package umg.edu.gt.trainupapp.data.api.dto;

import com.google.gson.annotations.SerializedName;

/**
 * MuscleApiDto
 * ------------
 * DTO para /muscle/.
 * Los nombres pueden venir en inglés; se pueden traducir en una fase futura si se requiere internacionalización.
 */
public class MuscleApiDto {
    @SerializedName("id") public int id;
    @SerializedName("name") public String name;
    @SerializedName("is_front") public boolean isFront; // true si es visible en vista frontal anatómica
}

