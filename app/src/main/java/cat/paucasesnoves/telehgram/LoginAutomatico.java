package cat.paucasesnoves.telehgram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
    private Usuario usuario;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gestorBBDD = new GestorBBDD();
        obtenDatosLogin();
    }

    public void obtenDatosLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences("datos_login", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("usuario", null);
        Type type = new TypeToken<Usuario>() {}.getType();
        usuario = gson.fromJson(json, type);

        if (usuario == null) {
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
                parametros.put("email", usuario.getEmail());
                parametros.put("password", usuario.getPassword());

                return gestorBBDD.enviarPost("http://52.44.95.114/quepassaeh/server/public/login/", parametros);
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
                        usuario.setPassword(usuario.getPassword());
                        // Le añadimos a nuestro objeto Dato la información que nos faltaba
                        datos.setObjeto(usuario);

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

}
