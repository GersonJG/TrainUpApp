package umg.edu.gt.trainupapp.data.database.converters;

import androidx.room.TypeConverter;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Converters: utilidades Room para convertir entre String(JSON) y List<String>.
 * Aunque actualmente almacenamos JSON como String directamente en entidades,
 * mantenemos estos conversores para compatibilidad y futuras migraciones.
 */
public class Converters {

    @TypeConverter
    public static List<String> fromString(String value) {
        List<String> list = new ArrayList<>();
        if (value == null || value.isEmpty()) return list;
        try {
            JSONArray arr = new JSONArray(value);
            for (int i = 0; i < arr.length(); i++) {
                list.add(arr.optString(i));
            }
        } catch (Exception ignored) { }
        return list;
    }

    @TypeConverter
    public static String fromList(List<String> list) {
        if (list == null) return "[]";
        return new JSONArray(list).toString();
    }
}

