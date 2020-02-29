package cat.paucasesnoves.telehgram.entidades;

public class Usuario {

    private String codigoUsuario;
    private String nombre;
    private String email;
    private String token;

    public Usuario(String codigoUsuario, String nombre, String email, String token) {
        this.codigoUsuario = codigoUsuario;
        this.nombre = nombre;
        this.email = email;
        this.token = token;
    }

    public Usuario() {
    }

    public String getCodigoUsuario() {
        return codigoUsuario;
    }

    public void setCodigoUsuario(String codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "codigoUsuario=" + codigoUsuario +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
