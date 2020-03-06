package cat.paucasesnoves.telehgram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import cat.paucasesnoves.telehgram.gestor.DBInterface;
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
    private DBInterface bd;

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

        // Recargar mensajes cada 5 segundos y guardarlos en la BBDD
        final Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                new RequestAsync().execute("actualiza_mensajes");
                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(r, 5000);
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
                // Obtener el usuario que ha iniciado sesión
                Usuario usuario = obtenUsuarioShared();
                switch (opcion) {
                    case "enviar":
                        // POST para el login
                        JSONObject parametros = new JSONObject();
                        // Añadimos los parámetros a un JSONObject
                        parametros.put("msg", mensajeEnviar);
                        parametros.put("codiusuari", usuario.getCodigoUsuario());

                        Log.d("CHAT", "Enviando mensajes.");
                        return gestorBBDD.enviarPost("http://52.44.95.114/quepassaeh/server/public/missatge/", parametros, usuario.getToken());
                    case "actualiza_mensajes":
                        return gestorBBDD.enviarGet("http://52.44.95.114/quepassaeh/server/public/missatge/", usuario.getToken());
                    case "actualiza_lista":
                    case "recibir":
                    default:
                        Log.d("CHAT", "Recibiendo mensajes.");
                        return gestorBBDD.enviarGet("http://52.44.95.114/quepassaeh/server/public/missatge/", usuario.getToken());
                }
            } catch (Exception e) {
                return "Excepción: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            // Obtenemos respuesta de la api
            if (s != null) {
                switch (opcion) {
                    case "enviar":
                        Log.d("CHAT", "Mensaje enviado.");
                        new RequestAsync().execute("recibir");
                    case "recibir":
                        cargarMensajesRecibidos(s);
                    case "actualiza_mensajes":
                        actualizarMensajes(s);
                    case "actualiza_lista":
                        actualizaLista(s);
                }
            }
        }
    }

    public void actualizarMensajes(String mensajes) {
        try {
            ArrayList<Mensaje> mensajesNuevos = new ArrayList<>();

            // Cogemos el JSONObject del JSON file
            JSONObject obj = new JSONObject(mensajes);

            // Creamos un objeto Dato para guardar la respuesta principal de la petición.
            JSONArray datos = obj.getJSONArray("dades");

            Dato dato = new Dato(null, obj.getBoolean("correcta"), obj.getString("missatge"), obj.getInt("rowcount"));

            // Usamos el dato básico de correcta que nos envia el servidor,
            // si es true significa que la petición contiene todos los datos que hemos pedido.
            if (dato.isCorrecta()) {
                // Usamos un for para conseguir los datos
                for (int i = 0; i < datos.length(); i++) {
                    // Conseguimos la array principal que es la de dades, esta contiene la información
                    // del mensaje
                    JSONObject mensaje = datos.getJSONObject(i);

                    // Asignamos cada campo a nuestra ArrayList
                    mensajesNuevos.add(new Mensaje(mensaje.getString("codimissatge"), mensaje.getString("msg"),
                            mensaje.getString("datahora"), mensaje.getString("codiusuari"),
                            mensaje.getString("nom"), mensaje.getString("foto")));

                }
                if (guardaMensajesBBDD(mensajesNuevos)) {
                    new RequestAsync().execute("actualiza_lista");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void actualizaLista(String s) {
        try {
            // Cogemos el JSONObject del JSON file
            JSONObject obj = new JSONObject(s);
            // Creamos un objeto Dato para guardar la respuesta principal de la petición.
            JSONArray datos = obj.getJSONArray("dades");

            Dato dato = new Dato(null, obj.getBoolean("correcta"), obj.getString("missatge"), obj.getInt("rowcount"));

            // Usamos el dato básico de correcta que nos envia el servidor,
            // si es true significa que la petición contiene todos los datos que hemos pedido.
            if (dato.isCorrecta()) {
                // Usamos un for para conseguir los datos
                for (int i = 0; i < datos.length(); i++) {
                    // Conseguimos la array principal que es la de dades, esta contiene la información
                    // del mensaje
                    JSONObject mensaje = datos.getJSONObject(i);

                    // Asignamos cada campo a nuestra ArrayList
                    listaMensajes.add(new Mensaje(mensaje.getString("codimissatge"), mensaje.getString("msg"),
                            mensaje.getString("datahora"), mensaje.getString("codiusuari"),
                            mensaje.getString("nom"), mensaje.getString("foto")));

                }

                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void cargarMensajesRecibidos(String mensajes) {
        try {
            // Cogemos el JSONObject del JSON file
            JSONObject obj = new JSONObject(mensajes);

            // Creamos un objeto Dato para guardar la respuesta principal de la petición.
            JSONArray datos = obj.getJSONArray("dades");

            Dato dato = new Dato(null, obj.getBoolean("correcta"), obj.getString("missatge"), obj.getInt("rowcount"));

            // Usamos el dato básico de correcta que nos envia el servidor,
            // si es true significa que la petición contiene todos los datos que hemos pedido.
            if (dato.isCorrecta()) {
                // Usamos un for para conseguir los datos
                for (int i = 0; i < datos.length(); i++) {
                    // Conseguimos la array principal que es la de dades, esta contiene la información
                    // del mensaje
                    JSONObject mensaje = datos.getJSONObject(i);

                    // Asignamos cada campo a nuestra ArrayList
                    listaMensajes.add(new Mensaje(mensaje.getString("codimissatge"), mensaje.getString("msg"),
                            mensaje.getString("datahora"), mensaje.getString("codiusuari"),
                            mensaje.getString("nom"), mensaje.getString("foto")));
                }

                ListView lista = findViewById(R.id.lista);
                adapter = new CustomAdapter(getApplicationContext(), listaMensajes);
                lista.setAdapter(adapter);
            } else {
                Toast.makeText(getApplicationContext(), "No hay mensajes", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
        Type type = new TypeToken<Usuario>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public boolean guardaMensajesBBDD(ArrayList<Mensaje> mensajes) {
        for (Mensaje m : mensajes) {
            bd = new DBInterface(getApplicationContext());
            bd.abre();
            if (bd.insertarMensaje((new Mensaje(m.getCodigoMensaje(), m.getContenido(), m.getFechaHora(),
                    m.getCodigoUsuario(), m.getNombreUsuario(), m.getFoto())))) {
                Log.d("Insertar mensaje", "Mensaje insertado");
            } else {
                Log.d("Insertar mensaje", "El mensaje ya está en la BBDD.");
            }
            bd.cierra();
        }
        return true;
    }
}