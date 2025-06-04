package servidor;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import controller.UsuarioController;

import java.net.Socket;
import java.net.SocketException;
import java.io.*;

public class ServidorThread extends Thread {
	private Socket cliente;

	public ServidorThread(Socket socket) {
		this.cliente = socket;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
			PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);

			String inputLine;
			JSONParser parser = new JSONParser();

			while ((inputLine = in.readLine()) != null) {
				System.out.println("JSON recebido do cliente: " + inputLine);
				try {
					JSONObject entrada = (JSONObject) parser.parse(inputLine);
					String operacao = (String) entrada.get("operacao");

					if ("cadastro".equalsIgnoreCase(operacao)) {
						JSONObject resposta = UsuarioController.realizarCadastro(entrada);
						out.println(resposta.toJSONString());
						System.out.println("JSON enviado ao cliente: " + resposta.toJSONString());
					}

					else if ("login".equalsIgnoreCase(operacao)) {
						JSONObject resposta = UsuarioController.realizarLogin(entrada);
						out.println(resposta.toJSONString());
						System.out.println("JSON enviado ao cliente: " + resposta.toJSONString());
					}

					else if ("logout".equalsIgnoreCase(operacao)) {
						JSONObject resposta = UsuarioController.realizarLogout(entrada);
						out.println(resposta.toJSONString());
						System.out.println("JSON enviado ao cliente: " + resposta.toJSONString());
					}

					else if ("ler_dados".equalsIgnoreCase(operacao)) {
						JSONObject resposta = UsuarioController.realizarLeituraDeDados(entrada);
						out.println(resposta.toJSONString());
						System.out.println("JSON enviado ao cliente: " + resposta.toJSONString());
					}

					else if ("editar_usuario".equalsIgnoreCase(operacao)) {
						JSONObject resposta = UsuarioController.realizarEdicao(entrada);
						out.println(resposta.toJSONString());
						System.out.println("JSON enviado ao cliente: " + resposta.toJSONString());
					}

					else if ("excluir_usuario".equalsIgnoreCase(operacao)) {
						JSONObject resposta = UsuarioController.realizarExclusao(entrada);
						out.println(resposta.toJSONString());
						System.out.println("JSON enviado ao cliente: " + resposta.toJSONString());
					}
					
					else if ("listar_usuarios".equalsIgnoreCase(operacao)) {
						JSONObject resposta = UsuarioController.realizarListagemDeUsuarios(entrada);
						out.println(resposta.toJSONString());
						System.out.println("JSON enviado ao cliente: " + resposta.toJSONString());
					}


				} catch (ParseException e) {
					String erro = "{\"status\":\"erro\",\"operacao\":\"cadastro\",\"mensagem\":\"JSON malformado\"}";
					out.println(erro);
					System.out.println("JSON enviado ao cliente: " + erro);
				}
			}

			cliente.close();
		} catch (SocketException e) {
			System.out.println("Cliente desconectou abruptamente: " + cliente.getInetAddress().getHostAddress());
		} catch (IOException e) {
			System.err.println("Erro de IO na conexão com o cliente: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro inesperado: " + e.getMessage());
		}
	}
}
