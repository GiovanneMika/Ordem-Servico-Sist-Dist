// controller/UsuarioController.java
package controller;

import banco.UsuarioDB;
import modelo.Usuario;
import org.json.simple.JSONObject;
import utils.Validador;

public class UsuarioController {

    public static JSONObject realizarCadastro(JSONObject entrada) {
        JSONObject resposta = new JSONObject();
        resposta.put("operacao", "cadastro");

        String nome = (String) entrada.get("nome");
        String usuario = (String) entrada.get("usuario");
        String senha = (String) entrada.get("senha");

        if (nome == null || usuario == null || senha == null ||
                !Validador.validarNome(nome) || !Validador.validarUsuario(usuario) || !Validador.validarSenha(senha)) {
            resposta.put("status", "erro");
            resposta.put("mensagem", "Os campos recebidos não são válidos");
        } else if (UsuarioDB.usuarioExiste(usuario)) {
            resposta.put("status", "erro");
            resposta.put("mensagem", "Usuário já cadastrado");
        } else {
            Usuario novoUsuario = new Usuario(nome, usuario, senha);
            UsuarioDB.adicionarUsuario(novoUsuario);
            resposta.put("status", "sucesso");
            resposta.put("mensagem", "Cadastro realizado com sucesso");
        }

        return resposta;
    }

    public static JSONObject realizarLogin(JSONObject entrada) {
        JSONObject resposta = new JSONObject();
        resposta.put("operacao", "login");

        String usuario = (String) entrada.get("usuario");
        String senha = (String) entrada.get("senha");

        if (usuario == null || senha == null || usuario.isBlank() || senha.isBlank()) {
            resposta.put("status", "erro");
            resposta.put("mensagem", "Informações incorretas");
        } else if (!UsuarioDB.usuarioExiste(usuario)) {
            resposta.put("status", "erro");
            resposta.put("mensagem", "Informações incorretas");
        } else if (UsuarioDB.estaLogado(usuario)) {
            resposta.put("status", "erro");
            resposta.put("mensagem", "Usuario já logado");
        } else if (!UsuarioDB.login(usuario, senha)) {
            resposta.put("status", "erro");
            resposta.put("mensagem", "Informações incorretas");
        } else {
            Usuario u = UsuarioDB.getUsuario(usuario);
            resposta.put("status", "sucesso");
            resposta.put("token", usuario);
            resposta.put("perfil", u.getPerfil());
        }

        return resposta;
    }

    public static JSONObject realizarLogout(JSONObject entrada) {
        JSONObject resposta = new JSONObject();
        resposta.put("operacao", "logout");

        String token = (String) entrada.get("token");

        if (token == null || token.isBlank() || !UsuarioDB.estaLogado(token)) {
            resposta.put("status", "erro");
            resposta.put("mensagem", "Token invalido");
        } else {
            UsuarioDB.logout(token);
            resposta.put("status", "sucesso");
            resposta.put("mensagem", "Logout realizado com sucesso");
        }

        return resposta;
    }
    
    public static JSONObject realizarLeituraDeDados(JSONObject entrada) {
        JSONObject resposta = new JSONObject();
        resposta.put("operacao", "ler_dados");

        String token = (String) entrada.get("token");

        if (token == null || token.isBlank() || !UsuarioDB.estaLogado(token)) {
            resposta.put("status", "erro");
            resposta.put("mensagem", "Token invalido");
        } else {
            Usuario usuario = UsuarioDB.getUsuario(token);

            JSONObject dados = new JSONObject();
            dados.put("nome", usuario.getNome());
            dados.put("usuario", usuario.getUsuario());
            dados.put("senha", usuario.getSenha());

            resposta.put("status", "sucesso");
            resposta.put("dados", dados);
        }

        return resposta;
    }

}
