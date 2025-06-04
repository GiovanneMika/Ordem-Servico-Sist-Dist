package modelo;

public class Usuario {
	private String nome;
	private String usuario;
	private String senha;
	private String perfil;

	public Usuario(String nome, String usuario, String senha, String perfil) {
		this.nome = nome;
		this.usuario = usuario;
		this.senha = senha;
		this.perfil = perfil;
	}

	public String getNome() {
		return nome;
	}

	public String getUsuario() {
		return usuario;
	}

	public String getSenha() {
		return senha;
	}

	public String getPerfil() {
		return perfil;
	}
}
