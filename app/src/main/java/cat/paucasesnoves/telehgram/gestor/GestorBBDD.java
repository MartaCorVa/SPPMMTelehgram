package cat.paucasesnoves.telehgram.gestor;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class GestorBBDD extends AppCompatActivity {

    /**
     * Método para codificar los parámetros que necesitaremos para las peticiones
     *
     * @param parametros para montar la cadena que necesitará la petición POST
     * @return la cadena concatenada correctamente
     * @throws Exception
     */
    private String codificarParametros(JSONObject parametros) throws Exception {
        StringBuilder resultado = new StringBuilder();

        //Boolean para controlar si es el primer parametro
        boolean primero = true;

        // Recorremos todos los parámetros
        Iterator<String> itr = parametros.keys();
        while (itr.hasNext()) {
            String key = itr.next();
            Object valor = parametros.get(key);

            // Si es el primero no le añadimos nada,
            // sino le añadimos un & para concatenar correctamente
            if (primero) {
                primero = false;
            } else {
                resultado.append("&");
            }

            // Montamos la cadena correctamente
            resultado.append(URLEncoder.encode(key, "UTF-8"));
            resultado.append("=");
            resultado.append(URLEncoder.encode(valor.toString(), "UTF-8"));
        }
        return resultado.toString();
    }

    /**
     * Este método sirve para hacer todas las peticiones POST
     *
     * @param link       url de la api para hacer la petición
     * @param parametros parámetros necesarios para la petición
     * @return respuesta de la api
     * @throws Exception
     */
    public String enviarPost(String link, JSONObject parametros, String token) throws Exception {
        URL url = new URL(link);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(20000);
        conn.setConnectTimeout(20000);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", token);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
        writer.write(codificarParametros(parametros));
        writer.flush();
        writer.close();
        os.close();

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String texto;

            while ((texto = in.readLine()) != null) {
                sb.append(texto);
                break;
            }

            in.close();
            return sb.toString();
        }
        return null;
    }

    public String enviarGet(String url, String token) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", token);
        int responseCode = con.getResponseCode();
        System.out.println("Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
            return "";
        }

    }

    /**
     * Este método sirve para hacer todas las peticiones POST
     *
     * @param link       url de la api para hacer la petición
     * @param parametros parámetros necesarios para la petición
     * @return respuesta de la api
     * @throws Exception
     */
    public String loguear(String link, JSONObject parametros) throws Exception {
        URL url = new URL(link);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(20000);
        conn.setConnectTimeout(20000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
        writer.write(codificarParametros(parametros));
        writer.flush();
        writer.close();
        os.close();

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String texto;

            while ((texto = in.readLine()) != null) {
                sb.append(texto);
                break;
            }

            in.close();
            return sb.toString();
        }
        return null;
    }
}
