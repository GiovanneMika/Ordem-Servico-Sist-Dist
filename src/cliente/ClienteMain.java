package cliente;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;

public class ClienteMain {

	public static void main(String[] args) {
		try {
			String token = null;
			String perfil = null;
			BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

			System.out.print("IP do servidor: ");
			String ip = teclado.readLine();
			System.out.print("Porta: ");
			int porta = Integer.parseInt(teclado.readLine());

			Socket socket = new Socket(ip, porta);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			System.out.println("Conectado com o servidor.");

			while (true) {
				if (token != null) {
					if (!"adm".equals(perfil)) {
						System.out.println(perfil);
					System.out.println("\n===== MENU =====");
					System.out.println("1 - Logout");
					System.out.println("2 - Ler meus dados");
					System.out.println("3 - Editar meus dados");
					System.out.println("4 - Excluir minha conta");
					System.out.println("0 - Sair");
					System.out.print("Escolha: ");
					String escolha = teclado.readLine();

					if (escolha.equals("1")) {
						JSONObject logout = new JSONObject();
						logout.put("operacao", "logout");
						logout.put("token", token);

						out.println(logout.toJSONString());
						System.out.println("JSON enviado ao servidor: " + logout.toJSONString());

						String resposta = in.readLine();
						System.out.println("JSON recebido do servidor: " + resposta);

						// se logout for sucesso, apaga o token
						if (resposta.contains("\"status\":\"sucesso\"")) {
							token = null;
							perfil = null;
						}
					}

					else if (escolha.equals("2")) {
						JSONObject leitura = new JSONObject();
						leitura.put("operacao", "ler_dados");
						leitura.put("token", token);

						out.println(leitura.toJSONString());
						System.out.println("JSON enviado ao servidor: " + leitura.toJSONString());

						String resposta = in.readLine();
						System.out.println("JSON recebido do servidor: " + resposta);
					}

					else if (escolha.equals("3")) {
						JSONObject editar = new JSONObject();
						editar.put("operacao", "editar_usuario");
						editar.put("token", token);

						System.out.print("Novo nome: ");
						editar.put("novo_nome", teclado.readLine());

						System.out.print("Novo usuário: ");
						editar.put("novo_usuario", teclado.readLine());

						System.out.print("Nova senha: ");
						editar.put("nova_senha", teclado.readLine());

						out.println(editar.toJSONString());
						System.out.println("JSON enviado ao servidor: " + editar.toJSONString());

						String resposta = in.readLine();
						System.out.println("JSON recebido do servidor: " + resposta);

						if (resposta.contains("\"status\":\"sucesso\"")) {
							org.json.simple.parser.JSONParser parser = new org.json.simple.parser.JSONParser();
							JSONObject obj = (JSONObject) parser.parse(resposta);
							token = (String) obj.get("token"); // atualiza token
						}
					}

					else if (escolha.equals("4")) {
						JSONObject excluir = new JSONObject();
						excluir.put("operacao", "excluir_usuario");
						excluir.put("token", token);

						out.println(excluir.toJSONString());
						System.out.println("JSON enviado ao servidor: " + excluir.toJSONString());

						String resposta = in.readLine();
						System.out.println("JSON recebido do servidor: " + resposta);

						if (resposta.contains("\"status\":\"sucesso\"")) {
							token = null; // remove o token do cliente
							perfil = null;

						}
					}

					else if (escolha.equals("0")) {
						System.out.println("Encerrando cliente...");
						break;
					}

					else {
						System.out.println("Opção inválida!");
					}
					}else {
						System.out.println("\n===== MENU ADMINISTRATIVO =====");
						System.out.println("1 - Logout");
						System.out.println("2 - Ler meus dados");
						System.out.println("3 - Cadastrar novo usuário comum");
						System.out.println("4 - Listar todos os usuários");
						System.out.println("5 - Editar usuário comum");
						System.out.println("6 - Excluir usuário comum");
						System.out.println("0 - Sair");
						System.out.print("Escolha: ");
						String escolha = teclado.readLine();
						
						if (escolha.equals("1")) {
							JSONObject logout = new JSONObject();
							logout.put("operacao", "logout");
							logout.put("token", token);

							out.println(logout.toJSONString());
							System.out.println("JSON enviado ao servidor: " + logout.toJSONString());

							String resposta = in.readLine();
							System.out.println("JSON recebido do servidor: " + resposta);

							// se logout for sucesso, apaga o token
							if (resposta.contains("\"status\":\"sucesso\"")) {
								token = null;
							}
						}

						else if (escolha.equals("2")) {
							JSONObject leitura = new JSONObject();
							leitura.put("operacao", "ler_dados");
							leitura.put("token", token);

							out.println(leitura.toJSONString());
							System.out.println("JSON enviado ao servidor: " + leitura.toJSONString());

							String resposta = in.readLine();
							System.out.println("JSON recebido do servidor: " + resposta);
						}
						
						else if (escolha.equals("3")) {
							JSONObject cadastro = new JSONObject();
							cadastro.put("operacao", "cadastro");

							System.out.print("Nome: ");
							cadastro.put("nome", teclado.readLine());

							System.out.print("Usuario: ");
							cadastro.put("usuario", teclado.readLine());

							System.out.print("Senha: ");
							cadastro.put("senha", teclado.readLine());

							cadastro.put("perfil", "comum");

							out.println(cadastro.toJSONString());
							System.out.println("JSON enviado ao servidor: " + cadastro.toJSONString());

							String resposta = in.readLine();
							System.out.println("JSON recebido do servidor: " + resposta);
						}
						
						else if (escolha.equals("4")) {
							JSONObject listar = new JSONObject();
							listar.put("operacao", "listar_usuarios");
							listar.put("token", token);

							out.println(listar.toJSONString());
							System.out.println("JSON enviado ao servidor: " + listar.toJSONString());

							String resposta = in.readLine();
							System.out.println("JSON recebido do servidor: " + resposta);
						}
						
						else if (escolha.equals("5")) {
							JSONObject editarOutro = new JSONObject();
							editarOutro.put("operacao", "editar_usuario");
							editarOutro.put("token", token);

							System.out.print("Usuário a ser editado: ");
							editarOutro.put("usuario_alvo", teclado.readLine());

							System.out.print("Novo nome: ");
							editarOutro.put("novo_nome", teclado.readLine());

							System.out.print("Nova senha: ");
							editarOutro.put("nova_senha", teclado.readLine());

							System.out.print("Novo perfil (comum/adm): ");
							editarOutro.put("novo_perfil", teclado.readLine());

							out.println(editarOutro.toJSONString());
							System.out.println("JSON enviado ao servidor: " + editarOutro.toJSONString());

							String resposta = in.readLine();
							System.out.println("JSON recebido do servidor: " + resposta);
						}
						
						else if (escolha.equals("6")) {
							JSONObject excluir = new JSONObject();
							excluir.put("operacao", "excluir_usuario");
							excluir.put("token", token);

							System.out.print("Usuário a ser excluído: ");
							excluir.put("usuario_alvo", teclado.readLine());

							out.println(excluir.toJSONString());
							System.out.println("JSON enviado ao servidor: " + excluir.toJSONString());

							String resposta = in.readLine();
							System.out.println("JSON recebido do servidor: " + resposta);
						}


						
						else if (escolha.equals("0")) {
							System.out.println("Encerrando cliente...");
							break;
						}

						else {
							System.out.println("Opção inválida!");
						}
					}
				} else {
					System.out.println("\n===== MENU =====");
					System.out.println("1 - Cadastrar usuário");
					System.out.println("2 - Login");
					System.out.println("0 - Sair");

					String escolha = teclado.readLine();

					if (escolha.equals("1")) {
						JSONObject cadastro = new JSONObject();
						cadastro.put("operacao", "cadastro");

						System.out.print("Nome: ");
						cadastro.put("nome", teclado.readLine());

						System.out.print("Usuario: ");
						cadastro.put("usuario", teclado.readLine());

						System.out.print("Senha: ");
						cadastro.put("senha", teclado.readLine());

						cadastro.put("perfil", "comum");

						out.println(cadastro.toJSONString());
						System.out.println("JSON enviado ao servidor: " + cadastro.toJSONString());

						String resposta = in.readLine();
						System.out.println("JSON recebido do servidor: " + resposta);
					}

					else if (escolha.equals("2")) {
						JSONObject login = new JSONObject();
						login.put("operacao", "login");

						System.out.print("Usuario: ");
						login.put("usuario", teclado.readLine());

						System.out.print("Senha: ");
						login.put("senha", teclado.readLine());

						out.println(login.toJSONString());
						System.out.println("JSON enviado ao servidor: " + login.toJSONString());

						String resposta = in.readLine();
						System.out.println("JSON recebido do servidor: " + resposta);

						if (resposta.contains("\"status\":\"sucesso\"")) {
							// extrair o token do JSON (modo simples)
							org.json.simple.parser.JSONParser parser = new org.json.simple.parser.JSONParser();
							JSONObject obj = (JSONObject) parser.parse(resposta);
							token = (String) obj.get("token");
							perfil = (String) obj.get("perfil"); // <-- pegar o perfil
						}

					}

					else if (escolha.equals("0")) {
						System.out.println("Encerrando cliente...");
						break;
					}

					else {
						System.out.println("Opção inválida!");
					}

				}
			}

			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
