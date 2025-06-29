package servidor;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import controller.UsuarioController;
import controller.OrdemServicoController;

import java.net.Socket;
import java.net.SocketException;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import java.io.*;

import java.util.function.Consumer;

public class ServidorThread extends Thread {
    private Socket cliente;
    private Consumer<String> logger;
    private Runnable atualizarUsuarios;

    public ServidorThread(Socket socket, Consumer<String> logger, Runnable atualizarUsuarios) {
        this.cliente = socket;
        this.logger = logger;
        this.atualizarUsuarios = atualizarUsuarios;
    }

    private void log(String msg) {
        if (logger != null) logger.accept(msg);
    }

    private void atualizarUsuariosLogadosGUI() {
        if (atualizarUsuarios != null) SwingUtilities.invokeLater(atualizarUsuarios);
    }
    
    private volatile boolean encerrado = false;

    public void encerrar() {
        encerrado = true;
        try {
            cliente.close(); // força encerramento
        } catch (IOException e) {
            log("Erro ao encerrar conexão com cliente: " + e.getMessage());
        }
    }



    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);

            String inputLine;
            JSONParser parser = new JSONParser();

            while (!encerrado && (inputLine = in.readLine()) != null) {
                log("JSON recebido do cliente: " + inputLine);
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
                            atualizarUsuariosLogadosGUI();
                            break;

                        case "logout":
                            resposta = UsuarioController.realizarLogout(entrada);
                            atualizarUsuariosLogadosGUI();
                            break;

                        case "ler_dados":
                            resposta = UsuarioController.realizarLeituraDeDados(entrada);
                            break;
                        case "editar_usuario":
                            if (entrada.containsKey("usuario_alvo") && entrada.get("usuario_alvo") != null &&
                                !((String) entrada.get("usuario_alvo")).isEmpty()) {
                                resposta = UsuarioController.realizarEdicaoComoAdm(entrada);
                            } else {
                                resposta = UsuarioController.realizarEdicao(entrada);
                            }
                            break;
                        case "listar_usuarios":
                            resposta = UsuarioController.realizarListagemDeUsuarios(entrada);
                            break;
                        case "excluir_usuario":
                            if (entrada.containsKey("usuario_alvo")) {
                                resposta = UsuarioController.realizarExclusaoComoAdm(entrada);
                            } else {
                                resposta = UsuarioController.realizarExclusao(entrada);
                            }
                            break;
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
                    log("JSON enviado ao cliente: " + resposta.toJSONString());

                } catch (ParseException e) {
                    String erro = "{\"status\":\"erro\",\"operacao\":\"desconhecida\",\"mensagem\":\"JSON malformado\"}";
                    out.println(erro);
                    log("JSON enviado ao cliente: " + erro);
                }
            }

            cliente.close();
        } catch (SocketException e) {
            log("Cliente desconectou abruptamente: " + cliente.getInetAddress().getHostAddress());
        } catch (IOException e) {
            log("Erro de IO na conexão com o cliente: " + e.getMessage());
        } catch (Exception e) {
            log("Erro inesperado: " + e.getMessage());
        }
    }
}
