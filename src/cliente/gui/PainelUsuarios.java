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

            out.println(req.toJSONString());
            String respStr = in.readLine();

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

    // Implementar essas depois:
    private void cadastrarUsuario() {
        // A implementar
    }

    private void editarUsuario() {
        // A implementar
    }

    private void excluirUsuario() {
        // A implementar
    }
}
