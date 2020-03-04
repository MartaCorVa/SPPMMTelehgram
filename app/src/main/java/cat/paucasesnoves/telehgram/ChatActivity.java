package cat.paucasesnoves.telehgram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import cat.paucasesnoves.telehgram.entidades.Dato;
import cat.paucasesnoves.telehgram.entidades.Mensaje;
import cat.paucasesnoves.telehgram.entidades.Usuario;
import cat.paucasesnoves.telehgram.gestor.GestorBBDD;
import cat.paucasesnoves.telehgram.utilidades.CustomAdapter;

public class ChatActivity extends AppCompatActivity {

    private GestorBBDD gestorBBDD;
    private ArrayList<Mensaje> listaMensajes = new ArrayList<>();
    private EditText textoMensaje;
    private ImageButton btnEnviar;
    private String mensajeEnviar;
    private String opcion = "";
    private CustomAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);

        gestorBBDD = new GestorBBDD();

        textoMensaje = findViewById(R.id.chat_mensaje);
        btnEnviar = findViewById(R.id.boton_mensaje);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mensajeEnviar = textoMensaje.getText().toString();
                textoMensaje.setText("");
                new RequestAsync().execute("enviar");
            }
        });

        new RequestAsync().execute("recibir");
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
                for (String s : strings) {
                    opcion = s;
                }
                Log.d("CHAT Opcion", opcion);
                switch (opcion) {
                    case "enviar":
                        // Obtener el usuario que ha iniciado sesión
                        Usuario usuario = obtenUsuarioShared();
                        // POST para el login
                        JSONObject parametros = new JSONObject();
                        // Añadimos los parámetros a un JSONObject
                        parametros.put("msg", mensajeEnviar);
                        parametros.put("codiusuari", usuario.getCodigoUsuario());

                        Log.d("CHAT", "Enviando mensajes.");
                        return gestorBBDD.enviarPost("http://52.44.95.114/quepassaeh/server/public/provamissatge/", parametros);
                    case "recibir":
                    default:
                        Log.d("CHAT", "Recibiendo mensajes.");
                        return gestorBBDD.enviarGet("http://52.44.95.114/quepassaeh/server/public/provamissatge/");
                }
            } catch (Exception e) {
                return "Excepción: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            // Obtenemos respuesta de la api
            if (s != null && !("enviar").equals(opcion)) {
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
                        adapter = new CustomAdapter(getApplicationContext(), listaMensajes);
                        lista.setAdapter(adapter);

                    } else {
                        if (("recibir").equals(opcion)){
                            Toast.makeText(getApplicationContext(), "No hay mensajes", Toast.LENGTH_LONG).show();
                        } else {
                            listaMensajes.clear();
                            new RequestAsync().execute("recibir");
                            adapter.notifyDataSetChanged();
                            /*actualizacionesNuevas.addAll(actualizacionesNuevas);
                            actualizacionAdapter.notifyDataSetChanged();*/
                        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.opciones, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cerrar_sesion:
                Log.d("Cerrar sesion", "Holasss");
                borrarShared();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void borrarShared() {
        this.getSharedPreferences("datos_login", 0).edit().clear().apply();
        startActivity(new Intent(this, LoginActivity.class));
        Log.d("Cerrar sesion", "Shared eliminado.");
    }

    public Usuario obtenUsuarioShared() {
        SharedPreferences sharedPreferences = getSharedPreferences("datos_login", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("usuario", null);
        Type type = new TypeToken<Usuario>() {}.getType();
        return gson.fromJson(json, type);
    }
}

