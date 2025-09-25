package umg.edu.gt.trainupapp.data.api;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * WgerApiClient
 * --------------
 * Responsable de construir una instancia singleton de Retrofit configurada
 * para consumir la API pública de wger: https://wger.de/api/v2/
 *
 * Ajuste: Se eliminó la dependencia directa a BuildConfig debido a error de compilación
 * (BuildConfig no encontrado). Para evitar romper entornos donde BuildConfig no se generó
 * todavía (o se cambió la configuración de generación), se usa reflexión segura para
 * detectar el valor de DEBUG. Si falla la reflexión, simplemente no se añade el interceptor
 * de logging.
 */
public class WgerApiClient {

    private static final String BASE_URL = "https://wger.de/api/v2/";
    private static volatile WgerApiService SERVICE;

    private WgerApiClient() { /* no instantiation */ }

    public static WgerApiService getService() {
        if (SERVICE == null) {
            synchronized (WgerApiClient.class) {
                if (SERVICE == null) {
                    OkHttpClient.Builder okBuilder = new OkHttpClient.Builder()
                            .connectTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS);

                    boolean isDebug = resolveDebugFlag();
                    if (isDebug) {
                        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(
                                message -> Log.d("WgerApi", message)
                        );
                        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
                        okBuilder.addInterceptor(logging);
                    }

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(okBuilder.build())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    SERVICE = retrofit.create(WgerApiService.class);
                }
            }
        }
        return SERVICE;
    }

    /**
     * Intenta leer el campo DEBUG de la clase BuildConfig mediante reflexión para no forzar
     * dependencia de compilación directa (que estaba fallando). Si no existe o ocurre
     * cualquier excepción, retorna false.
     */
    private static boolean resolveDebugFlag() {
        try {
            Class<?> clazz = Class.forName("umg.edu.gt.trainupapp.BuildConfig");
            Object value = clazz.getField("DEBUG").get(null);
            if (value instanceof Boolean) {
                return (Boolean) value;
            }
        } catch (Exception ignored) {}
        return false;
    }
}
