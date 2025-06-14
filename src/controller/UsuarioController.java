// controller/UsuarioController.java
package controller;

import banco.UsuarioDB;
import modelo.Usuario;

import org.json.simple.JSONArray;
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
		String token = (String) entrada.get("token");

		if (token == null) {
			perfil = "comum";
		} else {
			Usuario solicitante = UsuarioDB.getUsuario(token);
			if (!"adm".equals(solicitante.getPerfil()) && "adm".equals(perfil)) {
				resposta.put("status", "erro");
				resposta.put("mensagem", "Somente administradores podem fazer essa ação!");
				return resposta;
			}
		}

		if (nome == null || usuario == null || senha == null || perfil == null || !Validador.validarPerfil(perfil)
				|| !Validador.validarNome(nome) || !Validador.validarUsuario(usuario) || !Validador.validarSenha(senha)
				|| usuario.equalsIgnoreCase("admin")) {
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
			return resposta;
		}

		// Login do usuário ADM fixo
		if (usuario.equals("admin") && senha.equals("123456")) {
			if (UsuarioDB.estaLogado("admin")) {
				resposta.put("status", "erro");
				resposta.put("mensagem", "Usuario já logado");
				return resposta;
			}
			UsuarioDB.adicionarAdminTemporario(); // adiciona admin se ainda não estiver no sistema
			UsuarioDB.login("admin", "123456");
			resposta.put("status", "sucesso");
			resposta.put("token", "admin");
			resposta.put("perfil", "adm");
			return resposta;
		}

		// Login de usuários comuns
		if (!UsuarioDB.usuarioExiste(usuario)) {
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

	public static JSONObject realizarListagemDeUsuarios(JSONObject entrada) {
		JSONObject resposta = new JSONObject();
		resposta.put("operacao", "listar_usuarios");

		String token = (String) entrada.get("token");

		if (token == null || !UsuarioDB.estaLogado(token)) {
			resposta.put("status", "erro");
			resposta.put("mensagem", "Token invalido");
			return resposta;
		}

		Usuario usuario = UsuarioDB.getUsuario(token);
		if (!usuario.getPerfil().equals("adm")) {
			resposta.put("status", "erro");
			resposta.put("mensagem", "Token inválido, precisa ser administrador!");
			return resposta;
		}

		JSONArray usuarios = UsuarioDB.listarTodosUsuarios();
		if (usuarios.isEmpty()) {
			resposta.put("status", "erro");
			resposta.put("mensagem", "Nenhum usuario cadastrado");
			return resposta;
		}

		resposta.put("status", "sucesso");
		resposta.put("usuarios", usuarios);
		return resposta;
	}

	public static JSONObject realizarEdicaoComoAdm(JSONObject entrada) {
		JSONObject resposta = new JSONObject();
		resposta.put("operacao", "editar_usuario");

		String token = (String) entrada.get("token");
		String usuarioAlvo = (String) entrada.get("usuario_alvo");
		String novoNome = (String) entrada.get("novo_nome");
		String novaSenha = (String) entrada.get("nova_senha");
		String novoPerfil = (String) entrada.get("novo_perfil");

		// Verifica se o token é válido e está logado
		if (token == null || !UsuarioDB.estaLogado(token)) {
			resposta.put("status", "erro");
			resposta.put("mensagem", "Token inválido");
			return resposta;
		}

		Usuario solicitante = UsuarioDB.getUsuario(token);
		if (!"adm".equals(solicitante.getPerfil())) {
			resposta.put("status", "erro");
			resposta.put("mensagem", "Somente administradores podem fazer essa ação!");
			return resposta;
		}

		// Verifica se usuário alvo existe
		Usuario alvo = UsuarioDB.getUsuario(usuarioAlvo);
		if (alvo == null) {
			resposta.put("status", "erro");
			resposta.put("mensagem", "Usuario não encontrado");
			return resposta;
		}

		// impede que o administrador padrão seja editado
		if (alvo.getUsuario().equalsIgnoreCase("admin")) {
			resposta.put("status", "erro");
			resposta.put("mensagem", "Não se pode editar o administrador padrão!");
			return resposta;
		}

		// Validação dos novos campos
		if (novoNome == null || novaSenha == null || novoPerfil == null || !Validador.validarNome(novoNome)
				|| !Validador.validarSenha(novaSenha) || !Validador.validarPerfil(novoPerfil)) {
			resposta.put("status", "erro");
			resposta.put("mensagem", "Os campos recebidos não são validos");
			return resposta;
		}

		// Impede que o admin rebaixe a si mesmo para perfil comum
		if (usuarioAlvo.equals(token) && "comum".equals(novoPerfil)) {
			resposta.put("status", "erro");
			resposta.put("mensagem", "Não é permitido mudar o próprio perfil para comum");
			return resposta;
		}

		// Cria novo objeto com os dados atualizados
		Usuario atualizado = new Usuario(novoNome, usuarioAlvo, novaSenha, novoPerfil);
		UsuarioDB.atualizarUsuario(usuarioAlvo, atualizado);

		resposta.put("status", "sucesso");
		resposta.put("mensagem", "Usuário editado com sucesso");
		return resposta;
	}

	public static JSONObject realizarExclusaoComoAdm(JSONObject entrada) {
		JSONObject resposta = new JSONObject();
		resposta.put("operacao", "excluir_usuario");

		String token = (String) entrada.get("token");
		String usuarioAlvo = (String) entrada.get("usuario_alvo");

		// Verifica se token é válido
		if (token == null || !UsuarioDB.estaLogado(token)) {
			resposta.put("status", "erro");
			resposta.put("mensagem", "Token invalido");
			return resposta;
		}

		Usuario solicitante = UsuarioDB.getUsuario(token);
		Usuario alvo = UsuarioDB.getUsuario(usuarioAlvo);

		// Verifica se é administrador
		if (!"adm".equals(solicitante.getPerfil())) {
			resposta.put("status", "erro");
			resposta.put("mensagem", "Somente administradores podem excluir usuários");
			return resposta;
		}
		
		if(alvo == null) {
			resposta.put("status", "erro");
			resposta.put("mensagem", "É preciso de um usuário alvo!");
			return resposta;
		}

		// impede que o administrador padrão seja excluido
		if (alvo.getUsuario().equalsIgnoreCase("admin") ) {
			resposta.put("status", "erro");
			resposta.put("mensagem", "Não se pode excluir o administrador padrão!");
			return resposta;
		}

		// Impede que admin exclua a si mesmo
		if (token.equals(usuarioAlvo)) {
			resposta.put("status", "erro");
			resposta.put("mensagem", "Administrador não pode excluir a si mesmo");
			return resposta;
		}

		// Verifica se o usuário alvo existe
		if (!UsuarioDB.usuarioExiste(usuarioAlvo)) {
			resposta.put("status", "erro");
			resposta.put("mensagem", "Usuário alvo não encontrado");
			return resposta;
		}

		UsuarioDB.excluirUsuario(usuarioAlvo);
		resposta.put("status", "sucesso");
		resposta.put("mensagem", "Usuário excluído com sucesso");
		return resposta;
	}

}
