package cat.paucasesnoves.telehgram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import cat.paucasesnoves.telehgram.entidades.Dato;
import cat.paucasesnoves.telehgram.entidades.Usuario;
import cat.paucasesnoves.telehgram.gestor.GestorBBDD;

public class LoginActivity extends AppCompatActivity {

    private GestorBBDD gestorBBDD = new GestorBBDD();
    private EditText email;
    private EditText pass;
    private Button btnLogin;

    private String emailEnvia;
    private String passEnvia;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        // Obtenemos los datos
        email = findViewById(R.id.login_email);
        pass = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.boton_login);

        // Comprobar conexión
        if (internetIsConnected()) {
            Toast.makeText(getApplicationContext(), "Hay conexión", Toast.LENGTH_SHORT).show();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Comprobamos que los datos sean correctos
                if (compruebaDatos(email.getText().toString(), pass.getText().toString())) {
                    emailEnvia = email.getText().toString();
                    passEnvia = pass.getText().toString();
                    new RequestAsync().execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Login incorrecto", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    /**
     * Ping a google para comprobar nuestra conexión a internet.
     * @return True o False dependiendo de si hay conexión.
     */
    public boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() != 0);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Comprueba los campos del login
     * @param email
     * @param pass
     * @return boolean true si no hay campos vacios
     */
    public boolean compruebaDatos(String email, String pass) {
        if (!("").equals(email.trim()) || !("").equals(pass.trim())) {
            return true;
        } else {
            // ERROR
            Log.d("ERROR LOGIN", "Campos email o password incorrectos.");
        }
        return false;
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
                parametros.put("email", emailEnvia);
                parametros.put("password", passEnvia);

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
                        usuario.setPassword(passEnvia);
                        // Le añadimos a nuestro objeto Dato la información que nos faltaba
                        datos.setObjeto(usuario);

                        System.out.println(usuario);

                        // Guardamos los datos con Shared Preferences
                        guardarDatosLogin(usuario);

                        // Redirigimos a la activity de chat
                        startActivity(new Intent(getApplicationContext(), ChatActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Login incorrecto", Toast.LENGTH_LONG).show();
                        Log.d("MSG SERVIDOR", obj.getString("missatge"));
                        // Borrar texto de los EditText
                        email.setText("");
                        pass.setText("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
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
}

