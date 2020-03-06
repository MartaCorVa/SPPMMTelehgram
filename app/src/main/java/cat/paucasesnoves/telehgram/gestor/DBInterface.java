package cat.paucasesnoves.telehgram.gestor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import cat.paucasesnoves.telehgram.entidades.Mensaje;

public class DBInterface {

    // Crear BBDD
    public static final String BD_NOMBRE = "BDTelehgram";

    // Crear tabla mensajes
    public static final String BD_TABLA_MENSAJES = "mensajes";

    // Crear los atributos de la tabla mensajes
    public static final String CODIGO = "_id";
    public static final String CONTENIDO = "contenido";
    public static final String FECHA_HORA = "fechaHora";
    public static final String CODIGO_USUARIO = "codigoUsuario";
    public static final String NOMBRE_USUARIO = "nombreUsuario";
    public static final String FOTO = "foto";

    // Crear tabla
    public static final String BD_CREATE_TELEHGRAM = "create table " + BD_TABLA_MENSAJES + "( " +
            CODIGO + " integer primary key , " +
            CONTENIDO + " text not null, " +
            FECHA_HORA + " text not null, " +
            CODIGO_USUARIO + " integer not null, " +
            NOMBRE_USUARIO + " text not null, " +
            FOTO + " text not null);";

    public static final String TAG = "DBInterface";
    public static final int VERSIO = 1;
    private final Context context;
    private AyudaBD ayudaBD;
    private SQLiteDatabase bd;

    public DBInterface(Context con) {
        this.context = con;
        ayudaBD = new AyudaBD(context);
    }

    // Abrir BBDD
    public DBInterface abre() throws SQLException {
        bd = ayudaBD.getWritableDatabase();
        return this;
    }
    // Cerrar BBDD
    public void cierra() {
        ayudaBD.close();
    }

    // Insertar un mensaje
    public boolean insertarMensaje(Mensaje mensaje) {
        if (!mensajeExistente(mensaje.getCodigoMensaje())){
            ContentValues initialValues = new ContentValues();
            initialValues.put(CODIGO, mensaje.getCodigoMensaje());
            initialValues.put(CONTENIDO, mensaje.getContenido());
            initialValues.put(FECHA_HORA, mensaje.getFechaHora());
            initialValues.put(CODIGO_USUARIO, mensaje.getCodigoUsuario());
            initialValues.put(NOMBRE_USUARIO, mensaje.getNombreUsuario());
            initialValues.put(FOTO, mensaje.getFoto());
            bd.insert(BD_TABLA_MENSAJES, null, initialValues);
            return true;
        }
        return false;
    }

    // Obtener mensajes
    public Cursor obtenerMensajes() {
        return bd.query(BD_TABLA_MENSAJES, new String[] {CODIGO, CONTENIDO, FECHA_HORA, CODIGO_USUARIO, NOMBRE_USUARIO, FOTO},
                null,null, null, null, null, null);
    }

    // Mirar si el mensaje existe
    public boolean mensajeExistente(String codigo){
        AyudaBD bd = new AyudaBD(context);
        SQLiteDatabase query = bd.getWritableDatabase();
        String sql = "select * from " + BD_TABLA_MENSAJES + " where " +
                CODIGO + " = " + codigo;

        Cursor cursor = query.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }
}
