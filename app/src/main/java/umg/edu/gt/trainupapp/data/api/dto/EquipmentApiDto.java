package umg.edu.gt.trainupapp.data.api.dto;

import com.google.gson.annotations.SerializedName;

/**
 * EquipmentApiDto
 * ---------------
 * DTO del endpoint /equipment/. Los nombres de los equipos normalmente vienen en inglés.
 * Futuro: traducir/localizar si es requisito de UX.
 */
public class EquipmentApiDto {
    @SerializedName("id") public int id;
    @SerializedName("name") public String name;
}

