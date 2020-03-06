package cat.paucasesnoves.telehgram;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import cat.paucasesnoves.telehgram.entidades.Mensaje;
import cat.paucasesnoves.telehgram.gestor.DBInterface;
import cat.paucasesnoves.telehgram.utilidades.CustomAdapter;

public class OfflineActivity extends AppCompatActivity {

    private ListView listaMensajes;
    private ArrayList<Mensaje> mensajes = new ArrayList<>();
    private CustomAdapter adapter;
    private DBInterface bd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offline_layout);

        listaMensajes = findViewById(R.id.listaOffline);

        if (mensajes.isEmpty()) {
            obtenerMensajes();
        }
        adapter = new CustomAdapter(this, mensajes);
        listaMensajes.setAdapter(adapter);
    }

    public void obtenerMensajes() {
        bd = new DBInterface(this);
        bd.abre();
        Cursor c = bd.obtenerMensajes();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            mensajes.add(new Mensaje(c.getString(0), c.getString(1), c.getString(2), c.getString(3),
                    c.getString(4), c.getString(5)));
            c.moveToNext();
        }
        bd.cierra();
    }

}
