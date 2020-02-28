package cat.paucasesnoves.telehgram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
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

import java.util.HashMap;
import java.util.Map;

import cat.paucasesnoves.telehgram.gestor.GestorBBDD;

public class MainActivity extends AppCompatActivity {

    private GestorBBDD gestorBBDD = new GestorBBDD();
    private EditText email;
    private EditText pass;
    private Button btnLogin;

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

                    // Prueba usando Volley
                    /*loginUser(email.getText().toString(),pass.getText().toString());*/

                    // Prueba usando métodos del pdf
                    String mensaje = gestorBBDD.login(email.getText().toString(),pass.getText().toString());
                    Toast.makeText(getApplicationContext(),mensaje + "", Toast.LENGTH_SHORT).show();
                    final byte[] result = mensaje.getBytes();

                    Toast.makeText(getApplicationContext(),result.length + "", Toast.LENGTH_SHORT).show();
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

    public boolean compruebaDatos(String email, String pass) {
        if (!("").equals(email.trim()) || !("").equals(pass.trim())) {
            return true;
        } else {
            // ERROR
        }
        return false;
    }

    // Usando Volley
    private void loginUser(final String email, final String password){
        String url = "https://52.44.95.114/quepassaeh/server/public/login/";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(),"Hola", Toast.LENGTH_SHORT).show();
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_SHORT).show();
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };
        queue.add(postRequest);
    }

}
