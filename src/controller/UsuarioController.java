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
		String perfil = (String) entrada.get("perfil");

		if (nome == null || usuario == null || senha == null || perfil == null || !Validador.validarPerfil(perfil)
				|| !Validador.validarNome(nome) || !Validador.validarUsuario(usuario)
				|| !Validador.validarSenha(senha)) {
			resposta.put("status", "erro");
			resposta.put("mensagem", "Os campos recebidos não são válidos");
		} else if (UsuarioDB.usuarioExiste(usuario)) {
			resposta.put("status", "erro");
			resposta.put("mensagem", "Usuário já cadastrado");
		} else {
			Usuario novoUsuario = new Usuario(nome, usuario, senha, perfil);
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

	public static JSONObject realizarEdicao(JSONObject entrada) {
		JSONObject resposta = new JSONObject();
		resposta.put("operacao", "editar_usuario");

		String token = (String) entrada.get("token");
		String novoUsuario = (String) entrada.get("novo_usuario");
		String novoNome = (String) entrada.get("novo_nome");
		String novaSenha = (String) entrada.get("nova_senha");

		// Verifica se o token é válido
		if (token == null || !UsuarioDB.estaLogado(token)) {
			resposta.put("status", "erro");
			resposta.put("mensagem", "Token invalido");
			return resposta;
		}

		// Verifica campos obrigatórios
		if (novoUsuario == null || novoNome == null || novaSenha == null || !Validador.validarNome(novoNome)
				|| !Validador.validarUsuario(novoUsuario) || !Validador.validarSenha(novaSenha)) {
			resposta.put("status", "erro");
			resposta.put("mensagem", "Campos inválidos");
			return resposta;
		}

		// Se novo usuário já existir com outro nome
		if (!token.equals(novoUsuario) && UsuarioDB.usuarioExiste(novoUsuario)) {
			resposta.put("status", "erro");
			resposta.put("mensagem", "Usuario já cadastrado");
			return resposta;
		}

		// Atualiza o usuário
		Usuario usuarioAntigo = UsuarioDB.getUsuario(token);
		Usuario novo = new Usuario(novoNome, novoUsuario, novaSenha, usuarioAntigo.getPerfil());

		UsuarioDB.atualizarUsuario(token, novo);
		resposta.put("status", "sucesso");
		resposta.put("mensagem", "Dados atualizados com sucesso");
		resposta.put("token", novoUsuario); // token é atualizado

		return resposta;
	}

	public static JSONObject realizarExclusao(JSONObject entrada) {
		JSONObject resposta = new JSONObject();
		resposta.put("operacao", "excluir_usuario");

		String token = (String) entrada.get("token");

		// Verifica se o token é válido
		if (token == null || !UsuarioDB.estaLogado(token)) {
			resposta.put("status", "erro");
			resposta.put("mensagem", "Token invalido");
			return resposta;
		}

		// Remove usuário
		UsuarioDB.excluirUsuario(token);
		resposta.put("status", "sucesso");
		resposta.put("mensagem", "Conta excluída com sucesso");

		return resposta;
	}

}
