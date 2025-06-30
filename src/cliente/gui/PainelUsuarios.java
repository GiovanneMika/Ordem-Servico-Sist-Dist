package cliente.gui;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class PainelUsuarios extends JPanel {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String token;

    private JTable tabelaUsuarios;
    private DefaultTableModel modelo;

    public PainelUsuarios(Socket socket, PrintWriter out, BufferedReader in, String token) {
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.token = token;

        setLayout(new BorderLayout());

        modelo = new DefaultTableModel(new Object[]{"Usuário", "Nome", "Perfil"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaUsuarios = new JTable(modelo);
        add(new JScrollPane(tabelaUsuarios), BorderLayout.CENTER);

        JPanel botoes = new JPanel(new FlowLayout());
        JButton cadastrar = new JButton("Cadastrar Usuário");
        JButton editar = new JButton("Editar Usuário");
        JButton excluir = new JButton("Excluir Usuário");

        cadastrar.addActionListener(e -> cadastrarUsuario());
        editar.addActionListener(e -> editarUsuario());
        excluir.addActionListener(e -> excluirUsuario());

        botoes.add(cadastrar);
        botoes.add(editar);
        botoes.add(excluir);

        add(botoes, BorderLayout.SOUTH);

        carregarUsuarios();
    }

    private void carregarUsuarios() {
        try {
            JSONObject req = new JSONObject();
            req.put("operacao", "listar_usuarios");
            req.put("token", token);

            String jsonEnviado = req.toJSONString();
            System.out.println("JSON enviado ao servidor: " + jsonEnviado);
            out.println(jsonEnviado);

            String respStr = in.readLine();
            System.out.println("JSON recebido do servidor: " + respStr);


            JSONParser parser = new JSONParser();
            JSONObject resposta = (JSONObject) parser.parse(respStr);

            modelo.setRowCount(0);

            if ("sucesso".equals(resposta.get("status"))) {
                JSONArray usuarios = (JSONArray) resposta.get("usuarios");
                for (Object u : usuarios) {
                    JSONObject user = (JSONObject) u;
                    modelo.addRow(new Object[]{
                            user.get("usuario"),
                            user.get("nome"),
                            user.get("perfil")
                    });
                }
            } else {
                JOptionPane.showMessageDialog(this, resposta.get("mensagem"), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar usuários.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cadastrarUsuario() {
        new DialogCadastroUsuario((Frame) SwingUtilities.getWindowAncestor(this), out, in, token, this::carregarUsuarios).setVisible(true);
    }


    private void editarUsuario() {
        int linha = tabelaUsuarios.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para editar.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String usuarioSelecionado = (String) modelo.getValueAt(linha, 0);

        // ADM não pode editar a si mesmo
        if (usuarioSelecionado.equals(obterUsuarioLogado())) {
            JOptionPane.showMessageDialog(this, "Você não pode editar a si mesmo.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nomeAtual = (String) modelo.getValueAt(linha, 1);
        String perfilAtual = (String) modelo.getValueAt(linha, 2);

        new DialogEditarUsuario(
                (Frame) SwingUtilities.getWindowAncestor(this),
                out, in, token,
                usuarioSelecionado,
                nomeAtual,
                perfilAtual,
                this::carregarUsuarios
        ).setVisible(true);
    }

    // Método auxiliar para descobrir quem é o ADM logado (com base nos dados da tabela)
    private String obterUsuarioLogado() {
        try {
            JSONObject req = new JSONObject();
            req.put("operacao", "ler_dados");
            req.put("token", token);

            String jsonEnviado = req.toJSONString();
            System.out.println("JSON enviado ao servidor: " + jsonEnviado);
            out.println(jsonEnviado);

            String respStr = in.readLine();
            System.out.println("JSON recebido do servidor: " + respStr);


            org.json.simple.parser.JSONParser parser = new org.json.simple.parser.JSONParser();
            JSONObject resp = (JSONObject) parser.parse(respStr);
            return (String) resp.get("usuario");

        } catch (Exception e) {
            e.printStackTrace();
            return ""; // fallback
        }
    }


    private void excluirUsuario() {
        int linha = tabelaUsuarios.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para excluir.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String usuarioSelecionado = (String) modelo.getValueAt(linha, 0);

        if (usuarioSelecionado.equals(obterUsuarioLogado())) {
            JOptionPane.showMessageDialog(this, "Você não pode excluir a si mesmo.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o usuário \"" + usuarioSelecionado + "\"?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION);

        if (confirmar == JOptionPane.YES_OPTION) {
            try {
                JSONObject req = new JSONObject();
                req.put("operacao", "excluir_usuario");
                req.put("token", token);
                req.put("usuario_alvo", usuarioSelecionado);

                String jsonEnviado = req.toJSONString();
                System.out.println("JSON enviado ao servidor: " + jsonEnviado);
                out.println(jsonEnviado);

                String resposta = in.readLine();
                System.out.println("JSON recebido do servidor: " + resposta);


                if (resposta.contains("\"status\":\"sucesso\"")) {
                    JOptionPane.showMessageDialog(this, "Usuário excluído com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    carregarUsuarios();
                } else {
                    JOptionPane.showMessageDialog(this, resposta, "Erro", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao excluir usuário.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
