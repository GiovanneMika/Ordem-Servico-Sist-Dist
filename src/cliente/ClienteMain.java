package cliente;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;

public class ClienteMain {

    public static void main(String[] args) {
        try {
        	String token = null;
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
                System.out.println("\n===== MENU =====");
                System.out.println("1 - Cadastrar usuário");
                System.out.println("2 - Login");
                System.out.println("3 - Logout");
                System.out.println("4 - Ler meus dados");
                System.out.println("0 - Sair");
                System.out.print("Escolha: ");
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
                    }

                }
                
                else if (escolha.equals("3")) {
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
                
                else if (escolha.equals("4")) {
                    JSONObject leitura = new JSONObject();
                    leitura.put("operacao", "ler_dados");
                    leitura.put("token", token);

                    out.println(leitura.toJSONString());
                    System.out.println("JSON enviado ao servidor: " + leitura.toJSONString());

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

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
