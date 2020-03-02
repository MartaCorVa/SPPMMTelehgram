package cat.paucasesnoves.telehgram;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cat.paucasesnoves.telehgram.entidades.Dato;
import cat.paucasesnoves.telehgram.entidades.Mensaje;
import cat.paucasesnoves.telehgram.entidades.Usuario;
import cat.paucasesnoves.telehgram.gestor.GestorBBDD;
import cat.paucasesnoves.telehgram.utilidades.CustomAdapter;

public class ChatActivity extends AppCompatActivity {

    private ArrayList<Mensaje> listaMensajes = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);

        new RequestAsync().execute();
    }

    /**
     * Creamos una clase Asincrona y la llamamos RequestAsync.
     * Las clases asincronas se ejecutan independientemente de los otros procesos.
     * Así que ejecutará el proceso en segundo plano.
     */
    public class RequestAsync extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                //GET Request
                return GestorBBDD.enviarGet("http://52.44.95.114/quepassaeh/server/public/provamissatge/");
            } catch (Exception e) {
                return "Excepción: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            // Obtenemos respuesta de la api
            if (s != null) {
                try {
                    // get JSONObject from JSON file
                    JSONObject obj = new JSONObject(s);

                    // fetch JSONArray named users
                    JSONArray datos = obj.getJSONArray("dades");

                    Dato dato = new Dato(null, obj.getBoolean("correcta"), obj.getString("missatge"), obj.getInt("rowcount"));

                    if (dato.isCorrecta()) {
                        // implement for loop for getting users list data
                        for (int i = 0; i < datos.length(); i++) {
                            // create a JSONObject for fetching single user data
                            JSONObject mensaje = datos.getJSONObject(i);

                            // fetch email and name and store it in arraylist
                            listaMensajes.add(new Mensaje(mensaje.getString("codimissatge"), mensaje.getString("msg"),
                                    mensaje.getString("datahora"), mensaje.getString("codiusuari"),
                                    mensaje.getString("nom"), mensaje.getString("foto")));

                        }

                        ListView lista = findViewById(R.id.lista);
                        CustomAdapter adapter = new CustomAdapter(getApplicationContext(), listaMensajes);
                        lista.setAdapter(adapter);

                    } else {
                        Toast.makeText(getApplicationContext(), "No hay mensajes", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        public Usuario recuperarUsuarioSP() {
            SharedPreferences sharedPreferences = getSharedPreferences("datos_login", MODE_PRIVATE);
            return new Usuario(sharedPreferences.getString("codigo", null), sharedPreferences.getString("nombre", null),
                    sharedPreferences.getString("email", null), sharedPreferences.getString("token", null));
        }
    }
}

