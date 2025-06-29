package cliente.gui;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;

import java.net.Socket;

public class PainelOrdensAdm extends JPanel {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String token;

    private JTable tabelaOrdens;
    private DefaultTableModel modeloTabela;
    private JComboBox<String> filtroCombo;

    public PainelOrdensAdm(Socket socket, PrintWriter out, BufferedReader in, String token) {
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.token = token;

        setLayout(new BorderLayout());

        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Descrição", "Status", "Usuário"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaOrdens = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaOrdens);
        add(scrollPane, BorderLayout.CENTER);

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtroCombo = new JComboBox<>(new String[]{"todas", "pendente", "finalizada", "cancelada"});
        JButton atualizarBtn = new JButton("Atualizar");
        atualizarBtn.addActionListener(e -> carregarOrdens((String) filtroCombo.getSelectedItem()));

        topo.add(new JLabel("Filtro:"));
        topo.add(filtroCombo);
        topo.add(atualizarBtn);

        add(topo, BorderLayout.NORTH);

        carregarOrdens("todas");
    }

    private void carregarOrdens(String filtro) {
        try {
            JSONObject requisicao = new JSONObject();
            requisicao.put("operacao", "listar_ordens");
            requisicao.put("token", token);
            requisicao.put("filtro", filtro);

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
                            ordem.get("status"),
                            ordem.get("usuario") // Assumindo que o backend envia essa info
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
}
