package utils;

public class Validador {

    public static boolean validarNome(String nome) {
        return nome.matches("^[a-zA-Z0-9]{3,30}$");
    }

    public static boolean validarUsuario(String usuario) {
        return usuario.matches("^[a-zA-Z0-9]{3,30}$");
    }

    public static boolean validarSenha(String senha) {
        return senha.matches("^[a-zA-Z0-9]{4,10}$");
    }
}
