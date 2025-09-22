package umg.edu.gt.trainupapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

/**
 * PrefsUtils: utilitario simple para guardar/cargar JSON en SharedPreferences.
 */
public class PrefsUtils {
    private static final String PREFS = "TrainUP";

    public static void saveJson(Context ctx, String key, JSONObject json) {
        if (ctx == null || key == null) return;
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sp.edit().putString(key, json != null ? json.toString() : null).apply();
    }

    public static JSONObject readJson(Context ctx, String key) {
        try {
            SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            String raw = sp.getString(key, null);
            return raw != null ? new JSONObject(raw) : null;
        } catch (Exception e) {
            return null;
        }
    }
}

