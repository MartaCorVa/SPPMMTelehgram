package cat.paucasesnoves.telehgram.utilidades;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import cat.paucasesnoves.telehgram.R;
import cat.paucasesnoves.telehgram.entidades.Mensaje;

public class CustomAdapter extends ArrayAdapter<Mensaje> {

    private Context context;

    // Constructor
    public CustomAdapter(@NonNull Context context, @NonNull List<Mensaje> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        // Cogemos un mensaje
        Mensaje mensaje = getItem(position);

        //agafam "l'inflater" per "inflar" el layout per a cada item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.mensaje_layout, null);

        //instanciam cada element del layout a utilitzar
        TextView usuario = view.findViewById(R.id.mensaje_usuario);
        TextView contenido = view.findViewById(R.id.mensaje_contenido);
        TextView fecha = view.findViewById(R.id.mensaje_fecha);

        //omplim les dades
        usuario.setText(mensaje.getNombreUsuario());
        contenido.setText(mensaje.getContenido());
        fecha.setText(mensaje.getFechaHora());

        return view;
    }

}
