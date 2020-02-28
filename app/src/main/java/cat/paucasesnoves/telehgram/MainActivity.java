package cat.paucasesnoves.telehgram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        // Obtenemos los datos
        email = findViewById(R.id.login_email);
        pass = findViewById(R.id.login_password);

        // Comprobamos que los datos sean correctos
        compruebaDatos(email.getText().toString(), pass.getText().toString());

        // Comprobar conexión
        if (internetIsConnected()) {
            Toast.makeText(getApplicationContext(), "Hay conexión", Toast.LENGTH_SHORT).show();
        }
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

        } else {
            // ERROR
        }
        return false;
    }

}
