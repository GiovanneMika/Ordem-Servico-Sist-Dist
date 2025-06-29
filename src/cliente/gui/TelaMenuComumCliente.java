package cliente.gui;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

import java.net.Socket;

public class TelaMenuComumCliente extends JFrame {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String token;

    private JTable tabelaOrdens;
    private DefaultTableModel modeloTabela;

    public TelaMenuComumCliente(Socket socket, PrintWriter out, BufferedReader in, String token) {
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.token = token;

        setTitle("Menu do Usuário Comum");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Descrição", "Status"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaOrdens = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaOrdens);
        add(scrollPane, BorderLayout.CENTER);

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton novaOrdemButton = new JButton("Nova Ordem de Serviço");
        novaOrdemButton.addActionListener(this::abrirCadastroOrdem);
        botoesPanel.add(novaOrdemButton);

        JButton editarOrdemButton = new JButton("Editar Ordem Selecionada");
        editarOrdemButton.addActionListener(this::editarOrdemSelecionada);
        botoesPanel.add(editarOrdemButton);

        JButton editarUsuarioButton = new JButton("Editar Informações do Usuário");
        editarUsuarioButton.addActionListener(e -> {
            new TelaEditarUsuarioCliente(socket, out, in, token, this).setVisible(true);
        });
        botoesPanel.add(editarUsuarioButton);

        add(botoesPanel, BorderLayout.SOUTH);

        carregarOrdens();
    }

    private void carregarOrdens() {
        try {
            JSONObject requisicao = new JSONObject();
            requisicao.put("operacao", "listar_ordens");
            requisicao.put("token", token);
            requisicao.put("filtro", "todas");

            out.println(requisicao.toJSONString());

            String respostaStr = in.readLine();
            JSONParser parser = new JSONParser();
            JSONObject resposta = (JSONObject) parser.parse(respostaStr);

            modeloTabela.setRowCount(0); // limpa a tabela

            if ("sucesso".equals(resposta.get("status"))) {
                JSONArray ordens = (JSONArray) resposta.get("ordens");
                for (Object o : ordens) {
                    JSONObject ordem = (JSONObject) o;
                    modeloTabela.addRow(new Object[]{
                            ordem.get("id"),
                            ordem.get("descricao"),
                            ordem.get("status")
                    });
                }
            } else {
                JOptionPane.showMessageDialog(this, resposta.get("mensagem"), "Erro ao carregar ordens", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException | ParseException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao comunicar com o servidor.", "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void abrirCadastroOrdem(ActionEvent e) {
        String descricao = JOptionPane.showInputDialog(this, "Digite a descrição da nova ordem:");

        if (descricao != null && !descricao.trim().isEmpty()) {
            try {
                JSONObject novaOrdem = new JSONObject();
                novaOrdem.put("operacao", "cadastrar_ordem");
                novaOrdem.put("token", token);
                novaOrdem.put("descricao", descricao);

                out.println(novaOrdem.toJSONString());

                String resposta = in.readLine();
                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject) parser.parse(resposta);

                if ("sucesso".equals(obj.get("status"))) {
                    JOptionPane.showMessageDialog(this, "Ordem cadastrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    carregarOrdens();
                } else {
                    JOptionPane.showMessageDialog(this, obj.get("mensagem"), "Erro ao cadastrar", JOptionPane.ERROR_MESSAGE);
                }

            } catch (IOException | ParseException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao comunicar com o servidor.", "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    public void atualizarToken(String novoToken) {
        this.token = novoToken;
    }

    private void editarOrdemSelecionada(ActionEvent e) {
        int linhaSelecionada = tabelaOrdens.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma ordem para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object idObj = modeloTabela.getValueAt(linhaSelecionada, 0);
        String novaDescricao = JOptionPane.showInputDialog(this, "Digite a nova descrição:");

        if (novaDescricao != null && !novaDescricao.trim().isEmpty()) {
            try {
                JSONObject editar = new JSONObject();
                editar.put("operacao", "editar_ordem");
                editar.put("token", token);
                editar.put("id_ordem", Long.valueOf(idObj.toString())); // ou Integer.parseInt
                editar.put("nova_descricao", novaDescricao);

                out.println(editar.toJSONString());

                String resposta = in.readLine();
                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject) parser.parse(resposta);

                if ("sucesso".equals(obj.get("status"))) {
                    JOptionPane.showMessageDialog(this, "Ordem editada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    carregarOrdens();
                } else {
                    JOptionPane.showMessageDialog(this, obj.get("mensagem"), "Erro ao editar ordem", JOptionPane.ERROR_MESSAGE);
                }

            } catch (IOException | ParseException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao comunicar com o servidor.", "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
