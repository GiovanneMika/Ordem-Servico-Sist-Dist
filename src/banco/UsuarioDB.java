// banco/UsuarioDB.java
package banco;

import modelo.Usuario;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class UsuarioDB {
	private static HashMap<String, Usuario> usuarios = new HashMap<>();
	private static HashSet<String> usuariosLogados = new HashSet<>();

	public static boolean usuarioExiste(String username) {
		return usuarios.containsKey(username);
	}

	public static void adicionarUsuario(Usuario usuario) {
		usuarios.put(usuario.getUsuario(), usuario);
	}

	public static Usuario getUsuario(String username) {
		return usuarios.get(username);
	}

	public static boolean login(String username, String senha) {
		Usuario usuario = usuarios.get(username);
		if (usuario != null && usuario.getSenha().equals(senha)) {
			if (usuariosLogados.contains(username)) {
				return false; // já está logado
			}
			usuariosLogados.add(username);
			return true;
		}
		return false;
	}

	public static boolean estaLogado(String username) {
		return usuariosLogados.contains(username);
	}

	public static boolean logout(String username) {
		return usuariosLogados.remove(username);
	}

	public static void atualizarUsuario(String tokenAntigo, Usuario novoUsuario) {
		usuarios.remove(tokenAntigo);
		usuarios.put(novoUsuario.getUsuario(), novoUsuario);

		if (usuariosLogados.contains(tokenAntigo)) {
			usuariosLogados.remove(tokenAntigo);
			usuariosLogados.add(novoUsuario.getUsuario());
		}
	}

	public static void excluirUsuario(String usuario) {
		usuarios.remove(usuario);
		usuariosLogados.remove(usuario);
	}

	public static void adicionarAdminTemporario() {
		if (!usuarios.containsKey("admin")) {
			Usuario admin = new Usuario("Administrador", "admin", "123456", "adm");
			usuarios.put("admin", admin);
		}
	}
	public static JSONArray listarTodosUsuarios() {
	    JSONArray lista = new JSONArray();

	    for (Usuario u : usuarios.values()) {
	        JSONObject usuarioJson = new JSONObject();
	        usuarioJson.put("nome", u.getNome());
	        usuarioJson.put("usuario", u.getUsuario());
	        usuarioJson.put("perfil", u.getPerfil());
	        lista.add(usuarioJson);
	    }

	    return lista;
	}
	
	public static Set<String> getUsuariosLogados() {
	    return new HashSet<>(usuariosLogados);
	}


	
	

}
