package cat.paucasesnoves.telehgram.gestor;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static cat.paucasesnoves.telehgram.gestor.DBInterface.BD_CREATE_TELEHGRAM;
import static cat.paucasesnoves.telehgram.gestor.DBInterface.BD_NOMBRE;
import static cat.paucasesnoves.telehgram.gestor.DBInterface.BD_TABLA_MENSAJES;
import static cat.paucasesnoves.telehgram.gestor.DBInterface.TAG;
import static cat.paucasesnoves.telehgram.gestor.DBInterface.VERSIO;

public class AyudaBD extends SQLiteOpenHelper {

    AyudaBD(Context con) {
        super(con, BD_NOMBRE, null, VERSIO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(BD_CREATE_TELEHGRAM);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int VersioAntiga, int VersioNova) {
        Log.w(TAG, "Actualitzant Base de dades de la versió" + VersioAntiga + " a " + VersioNova + ". Destruirà totes les dades");
        db.execSQL("DROP TABLE IF EXISTS " + BD_TABLA_MENSAJES);
        onCreate(db); }
}