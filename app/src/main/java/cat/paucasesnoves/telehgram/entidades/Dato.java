package cat.paucasesnoves.telehgram.entidades;

public class Dato {

    private Object objeto;
    private boolean correcta;
    private String mensaje;
    private int rowCount;

    public Dato(Object objeto, boolean correcta, String mensaje, int rowCount) {
        this.objeto = objeto;
        this.correcta = correcta;
        this.mensaje = mensaje;
        this.rowCount = rowCount;
    }

    public Object getObjeto() {
        return objeto;
    }

    public void setObjeto(Object objeto) {
        this.objeto = objeto;
    }

    public boolean isCorrecta() {
        return correcta;
    }

    public void setCorrecta(boolean correcta) {
        this.correcta = correcta;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    @Override
    public String toString() {
        return "Datos{" +
                "objeto=" + objeto +
                ", correcta=" + correcta +
                ", mensaje='" + mensaje + '\'' +
                ", rowCount=" + rowCount +
                '}';
    }
}
