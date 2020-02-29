package cat.paucasesnoves.telehgram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import cat.paucasesnoves.telehgram.gestor.GestorBBDD;

public class MainActivity extends AppCompatActivity {

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
                //GET Request
                //return RequestHandler.sendGet("https://prodevsblog.com/android_get.php");

                // POST para el login
                JSONObject parametros = new JSONObject();
                // Añadimos los parámetros a un JSONObject
                parametros.put("email", emailEnvia);
                parametros.put("password", passEnvia);

                return GestorBBDD.enviarPost("http://52.44.95.114/quepassaeh/server/public/login/", parametros);
            }
            catch(Exception e){
                return "Excepción: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            // Obtenemos respuesta de la api
            if(s!=null){
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
        }


    }

}

