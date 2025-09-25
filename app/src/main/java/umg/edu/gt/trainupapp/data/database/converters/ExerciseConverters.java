package umg.edu.gt.trainupapp.data.database.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * ExerciseConverters
 * ------------------
 * Convierte listas de Integer a JSON String y viceversa para permitir el almacenamiento
 * de IDs (músculos, músculos secundarios, equipamiento) dentro de una sola columna.
 * Se usa Gson para flexibilidad futura. Esto evita crear tablas relacionales puente
 * en esta primera iteración (optimización futura: tablas many-to-many).
 */
public class ExerciseConverters {
    private static final Gson gson = new Gson();
    private static final Type LIST_INT_TYPE = new TypeToken<List<Integer>>() {}.getType();

    @TypeConverter
    public static String fromList(List<Integer> list) {
        if (list == null) return null;
        return gson.toJson(list, LIST_INT_TYPE);
    }

    @TypeConverter
    public static List<Integer> toList(String json) {
        if (json == null || json.trim().isEmpty()) return new ArrayList<>();
        try {
            return gson.fromJson(json, LIST_INT_TYPE);
        } catch (Exception e) {
            // Si falla la conversión retornamos lista vacía para evitar crashes.
            return new ArrayList<>();
        }
    }
}

