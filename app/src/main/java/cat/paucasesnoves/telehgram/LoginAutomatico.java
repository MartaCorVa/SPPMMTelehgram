package cat.paucasesnoves.telehgram;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import cat.paucasesnoves.telehgram.entidades.Dato;
import cat.paucasesnoves.telehgram.entidades.Usuario;
import cat.paucasesnoves.telehgram.gestor.GestorBBDD;

public class LoginAutomatico extends AppCompatActivity {

    private GestorBBDD gestorBBDD;
    private Usuario user;
    private ReceptorXarxa receptor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gestorBBDD = new GestorBBDD();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receptor = new ReceptorXarxa();
        this.registerReceiver(receptor, filter);
    }

    public void obtenDatosLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences("datos_login", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("usuario", null);
        Type type = new TypeToken<Usuario>() {}.getType();
        user = gson.fromJson(json, type);

        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            new RequestAsync().execute();
        }
    }

    /**
     * Creamos una clase Asincrona y la llamamos RequestAsync.
     * Las clases asincronas se ejecutan independientemente de los otros procesos.
     * Así que ejecutará el proceso en segundo plano.
     */
    public class RequestAsync extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                // POST para el login
                JSONObject parametros = new JSONObject();
                // Añadimos los parámetros a un JSONObject
                parametros.put("email", user.getEmail());
                parametros.put("password", user.getPassword());

                return gestorBBDD.loguear("http://52.44.95.114/quepassaeh/server/public/login/", parametros);
            }
            catch(Exception e){
                return "Excepción: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            // Obtenemos respuesta de la api
            if(s!=null){
                try {
                    // Cogemos el JSONObject del JSON file
                    JSONObject obj = new JSONObject(s);
                    // Creamos un objeto Dato para guardar la respuesta principal de la petición.
                    Dato datos = new Dato(null, obj.getBoolean("correcta"), obj.getString("missatge"), obj.getInt("rowcount"));

                    // Usamos el dato básico de correcta que nos envia el servidor,
                    // si es true significa que la petición contiene todos los datos que hemos pedido.
                    if (datos.isCorrecta()) {
                        // Conseguimos la array principal que es la de dades, esta contiene la información
                        // del usuario
                        JSONObject descarga = obj.getJSONObject("dades");
                        // Asignamos cada campo a nuestro objeto Usuario
                        Usuario usuario = new Usuario(descarga.getString("codiusuari"), descarga.getString("nom"),
                                descarga.getString("email"), descarga.getString("token"));

                        usuario.setPassword(user.getPassword());

                        // Le añadimos a nuestro objeto Dato la información que nos faltaba
                        datos.setObjeto(usuario);

                        // Borramos el Shared antiguo
                        borrarShared();

                        // Guardar usuario en el Shared
                        guardarDatosLogin(usuario);

                        // Redirigimos a la activity de chat
                        startActivity(new Intent(getApplicationContext(), ChatActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Error al intentar iniciar sesión.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void borrarShared() {
        this.getSharedPreferences("datos_login", 0).edit().clear().apply();
        Log.d("Actualiza Shared", "Shared eliminado.");
    }

    /**
     * Método para guardar todos los datos de un usuario en un xml
     * @param usuario
     */
    public void guardarDatosLogin(Usuario usuario) {
        SharedPreferences sharedPreferences = getSharedPreferences("datos_login", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(usuario);
        editor.putString("usuario", json);
        editor.apply();
    }

    /**
     * Ping a google para comprobar nuestra conexión a internet.
     * @return True o False dependiendo de si hay conexión.
     */
    public void internetIsConnected(Context context) {
        //Obtenim un gestor de les connexions de xarxa
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //Obtenim l’estat de la xarxa
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        //Si està connectat
        if (networkInfo != null && networkInfo.isConnected()) {
            Toast.makeText(getApplicationContext(), "Hay conexión", Toast.LENGTH_SHORT).show();
            obtenDatosLogin();
        } else {
            Toast.makeText(getApplicationContext(), "No hay conexión", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, OfflineActivity.class));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Donam de baixa el receptor de broadcast quan es destrueix l’aplicació
        if (receptor != null) {
            this.unregisterReceiver(receptor);
        }
    }

    public class ReceptorXarxa extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Actualitza l'estat de la xarxa
            internetIsConnected(context);
        }
    }
}
