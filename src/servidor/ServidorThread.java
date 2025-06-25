package servidor;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import controller.UsuarioController;
import controller.OrdemServicoController;

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

                    JSONObject resposta;

                    switch (operacao) {
                        case "cadastro":
                            resposta = UsuarioController.realizarCadastro(entrada);
                            break;

                        case "login":
                            resposta = UsuarioController.realizarLogin(entrada);
                            break;

                        case "logout":
                            resposta = UsuarioController.realizarLogout(entrada);
                            break;

                        case "ler_dados":
                            resposta = UsuarioController.realizarLeituraDeDados(entrada);
                            break;

                        case "editar_usuario":
                            if (entrada.containsKey("usuario_alvo") && entrada.get("usuario_alvo") != null &&
                                !((String) entrada.get("usuario_alvo")).isEmpty()) {
                                resposta = UsuarioController.realizarEdicaoComoAdm(entrada);
                            } else {
                                resposta = UsuarioController.realizarEdicao(entrada); // edição própria
                            }
                            break;

                        case "listar_usuarios":
                            resposta = UsuarioController.realizarListagemDeUsuarios(entrada);
                            break;

                        case "excluir_usuario":
                            if (entrada.containsKey("usuario_alvo")) {
                                resposta = UsuarioController.realizarExclusaoComoAdm(entrada);
                            } else {
                                resposta = UsuarioController.realizarExclusao(entrada); // exclusão própria
                            }
                            break;

                        // Novas funcionalidades de ordem de serviço
                        case "cadastrar_ordem":
                            resposta = OrdemServicoController.realizarCadastroOrdem(entrada);
                            break;

                        case "listar_ordens":
                            resposta = OrdemServicoController.realizarListagemOrdens(entrada);
                            break;

                        case "editar_ordem":
                            resposta = OrdemServicoController.realizarEdicaoOrdem(entrada);
                            break;
                            
                        case "alterar_ordem":
                            resposta = OrdemServicoController.realizarAlteracaoStatusOrdem(entrada);
                            break;


                        default:
                            resposta = new JSONObject();
                            resposta.put("status", "erro");
                            resposta.put("operacao", operacao);
                            resposta.put("mensagem", "Operação desconhecida");
                            break;
                    }

                    out.println(resposta.toJSONString());
                    System.out.println("JSON enviado ao cliente: " + resposta.toJSONString());

                } catch (ParseException e) {
                    String erro = "{\"status\":\"erro\",\"operacao\":\"desconhecida\",\"mensagem\":\"JSON malformado\"}";
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
