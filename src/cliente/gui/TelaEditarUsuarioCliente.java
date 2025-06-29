package cliente.gui;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.Socket;

public class TelaEditarUsuarioCliente extends JFrame {
    private JTextField nomeField;
    private JTextField usuarioField;
    private JPasswordField senhaField;
    private JButton salvarButton;
    private JButton cancelarButton;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String token;
    private TelaMenuComumCliente telaAnterior;

    public TelaEditarUsuarioCliente(Socket socket, PrintWriter out, BufferedReader in, String token, TelaMenuComumCliente telaAnterior) {
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.token = token;
        this.telaAnterior = telaAnterior;

        setTitle("Editar Informações do Usuário");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        panel.add(new JLabel("Nome:"));
        nomeField = new JTextField();
        panel.add(nomeField);

        panel.add(new JLabel("Usuário:"));
        usuarioField = new JTextField();
        panel.add(usuarioField);

        panel.add(new JLabel("Senha:"));
        senhaField = new JPasswordField();
        panel.add(senhaField);

        salvarButton = new JButton("Salvar");
        salvarButton.addActionListener(this::salvarAlteracoes);
        panel.add(salvarButton);

        cancelarButton = new JButton("Cancelar");
        cancelarButton.addActionListener(e -> this.dispose());
        panel.add(cancelarButton);

        add(panel, BorderLayout.CENTER);

        carregarDadosUsuario();
    }

    private void carregarDadosUsuario() {
        try {
            JSONObject requisicao = new JSONObject();
            requisicao.put("operacao", "ler_dados");
            requisicao.put("token", token);

            out.println(requisicao.toJSONString());

            String resposta = in.readLine();
            System.out.println("Resposta ler_dados: " + resposta);

            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(resposta);

            if ("sucesso".equals(obj.get("status"))) {
                JSONObject dados = (JSONObject) obj.get("dados"); // ✅ pega o objeto interno

                nomeField.setText((String) dados.get("nome"));
                usuarioField.setText((String) dados.get("usuario"));
                senhaField.setText(""); // senha não deve ser exibida por segurança

            } else {
                JOptionPane.showMessageDialog(this, obj.get("mensagem"), "Erro", JOptionPane.ERROR_MESSAGE);
                this.dispose();
            }

        } catch (IOException | ParseException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados do usuário.", "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            this.dispose();
        }
    }


    private void salvarAlteracoes(ActionEvent e) {
        String nome = nomeField.getText().trim();
        String usuario = usuarioField.getText().trim();
        String senha = new String(senhaField.getPassword());

        if (nome.isEmpty() || usuario.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            JSONObject editar = new JSONObject();
            editar.put("operacao", "editar_usuario");
            editar.put("token", token);
            editar.put("novo_nome", nome);
            editar.put("novo_usuario", usuario);
            editar.put("nova_senha", senha);

            out.println(editar.toJSONString());

            String resposta = in.readLine();
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(resposta);

            if ("sucesso".equals(obj.get("status"))) {
                JOptionPane.showMessageDialog(this, "Informações atualizadas com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                String novoToken = (String) obj.get("token");
                System.out.println("Novo token recebido: " + novoToken);

                // Fecha a tela de menu atual
                telaAnterior.dispose();

                // Abre uma nova instância com o novo token
                SwingUtilities.invokeLater(() -> {
                    new TelaMenuComumCliente(socket, out, in, novoToken).setVisible(true);
                });

                this.dispose(); // Fecha a tela de edição

            } else {
                JOptionPane.showMessageDialog(this, obj.get("mensagem"), "Erro ao atualizar", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException | ParseException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao comunicar com o servidor.", "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
