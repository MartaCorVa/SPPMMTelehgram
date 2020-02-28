package cat.paucasesnoves.telehgram.gestor;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GestorBBDD extends AppCompatActivity {

    //private String link = "http://52.44.95.114/quepassaeh/server/public/";
    private URL url;
    private StringBuilder stringBuilder = new StringBuilder();

    public String login(String email, String pass) {
        try {
            url = new URL("http://52.44.95.114/quepassaeh/server/public/login/");

            // Abrir conexión
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(15000);
            con.setChunkedStreamingMode(25000);
            con.setRequestMethod("POST");
            //con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setDoInput(true);
            con.setDoOutput(true);
            //con.connect();

            // Asignamos nuestro output
            OutputStream out = con.getOutputStream();

            // Creamos un buffered writer
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
            // Le pasamos los argumentos
            bufferedWriter.write("email=" + email + "&password=" + pass);
            bufferedWriter.flush();
            bufferedWriter.close();
            out.close();

            // Obtenemos el código de la respuesta
            int codigoRespuesta = con.getResponseCode();
            Log.d("RUN", "Descarrega "+ codigoRespuesta);
            if (codigoRespuesta == HttpURLConnection.HTTP_OK) {
                // Recogemos el texto
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String texto;
                while ((texto = bufferedReader.readLine()) != null) {
                    stringBuilder.append(texto);
                }
                bufferedReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

}
