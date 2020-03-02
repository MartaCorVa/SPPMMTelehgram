package cat.paucasesnoves.telehgram.entidades;

public class Mensaje {

    private String codigoMensaje;
    private String contenido;
    private String fechaHora;
    private String codigoUsuario;
    private String nombreUsuario;
    private String foto;

    public Mensaje(String codigoMensaje, String contenido, String fechaHora, String codigoUsuario, String nombreUsuario, String foto) {
        this.codigoMensaje = codigoMensaje;
        this.contenido = contenido;
        this.fechaHora = fechaHora;
        this.codigoUsuario = codigoUsuario;
        this.nombreUsuario = nombreUsuario;
        this.foto = foto;
    }

    public String getCodigoMensaje() {
        return codigoMensaje;
    }

    public void setCodigoMensaje(String codigoMensaje) {
        this.codigoMensaje = codigoMensaje;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getCodigoUsuario() {
        return codigoUsuario;
    }

    public void setCodigoUsuario(String codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Override
    public String toString() {
        return "Mensaje{" +
                "codigoMensaje='" + codigoMensaje + '\'' +
                ", contenido='" + contenido + '\'' +
                ", fechaHora='" + fechaHora + '\'' +
                ", codigoUsuario='" + codigoUsuario + '\'' +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", foto='" + foto + '\'' +
                '}';
    }
}
