package cat.paucasesnoves.telehgram.entidades;

public class Dato {

    private Usuario usuario;
    private boolean correcta;
    private String mensaje;
    private int rowCount;

    public Dato(Usuario usuario, boolean correcta, String mensaje, int rowCount) {
        this.usuario = usuario;
        this.correcta = correcta;
        this.mensaje = mensaje;
        this.rowCount = rowCount;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
                "usuario=" + usuario +
                ", correcta=" + correcta +
                ", mensaje='" + mensaje + '\'' +
                ", rowCount=" + rowCount +
                '}';
    }
}
