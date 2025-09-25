package umg.edu.gt.trainupapp.data.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;

import java.util.List;

/**
 * ExerciseEntity
 * -----------------
 * Representa un ejercicio descargado desde la API pública de wger en idioma español.
 * Para simplificar y acelerar la implementación inicial se almacenan listas de IDs
 * (músculos principales, secundarios y equipamiento) directamente como List<Integer>
 * empleando un TypeConverter (ver ExerciseConverters). Internamente Room persistirá
 * estas listas como un JSON string. Consultas avanzadas de filtrado por IDs dentro
 * de estas listas se harán inicialmente mediante LIKE sobre el JSON; en una fase
 * posterior se puede normalizar a tablas de cruce (many-to-many) para consultas
 * más eficientes y con índices.
 *
 * Índices añadidos:
 * - name: acelera búsquedas por nombre (LIKE case-insensitive)
 * - categoryId: acelera filtrado por categoría
 */
@Entity(tableName = "exercises", indices = { @Index(value = {"name"}), @Index(value = {"categoryId"}) })
public class ExerciseEntity {
    @PrimaryKey
    public int id; // ID único provisto por la API wger

    @NonNull
    public String name; // Nombre del ejercicio

    public String description; // Descripción en HTML (se puede sanitizar al mostrar)

    public String imageUrl; // URL de la imagen principal (puede ser null si no existe)

    public int categoryId; // Referencia a ExerciseCategoryEntity.id

    // Listas convertidas mediante ExerciseConverters
    public List<Integer> muscles; // IDs músculos principales
    public List<Integer> musclesSecondary; // IDs músculos secundarios
    public List<Integer> equipment; // IDs equipamiento requerido

    public int languageId; // Idioma (2 = Español en wger)

    public ExerciseEntity(int id,
                          @NonNull String name,
                          String description,
                          String imageUrl,
                          int categoryId,
                          List<Integer> muscles,
                          List<Integer> musclesSecondary,
                          List<Integer> equipment,
                          int languageId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.muscles = muscles;
        this.musclesSecondary = musclesSecondary;
        this.equipment = equipment;
        this.languageId = languageId;
    }
}
