package cliente.gui;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

import java.net.Socket;

public class TelaCadastroCliente extends JFrame {
    private JTextField nomeField;
    private JTextField usuarioField;
    private JPasswordField senhaField;
    private JButton cadastrarButton;
    private JButton voltarButton;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public TelaCadastroCliente(Socket socket, PrintWriter out, BufferedReader in) {
        this.socket = socket;
        this.out = out;
        this.in = in;

        setTitle("Cadastro de Usuário");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        panel.add(new JLabel("Nome completo:"));
        nomeField = new JTextField();
        panel.add(nomeField);

        panel.add(new JLabel("Usuário:"));
        usuarioField = new JTextField();
        panel.add(usuarioField);

        panel.add(new JLabel("Senha:"));
        senhaField = new JPasswordField();
        panel.add(senhaField);

        cadastrarButton = new JButton("Cadastrar");
        cadastrarButton.addActionListener(this::realizarCadastro);
        panel.add(cadastrarButton);

        voltarButton = new JButton("Voltar");
        voltarButton.addActionListener(this::voltarParaLogin);
        panel.add(voltarButton);

        add(panel, BorderLayout.CENTER);
    }

    private void realizarCadastro(ActionEvent e) {
        String nome = nomeField.getText().trim();
        String usuario = usuarioField.getText().trim();
        String senha = new String(senhaField.getPassword());

        if (nome.isEmpty() || usuario.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            JSONObject cadastro = new JSONObject();
            cadastro.put("operacao", "cadastro");
            cadastro.put("nome", nome);
            cadastro.put("usuario", usuario);
            cadastro.put("senha", senha);
            cadastro.put("perfil", "comum");

            String jsonEnviado = cadastro.toJSONString();
            System.out.println("JSON enviado ao servidor: " + jsonEnviado);
            out.println(jsonEnviado);

            String resposta = in.readLine();
            System.out.println("JSON recebido do servidor: " + resposta);

            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(resposta);

            if ("sucesso".equals(obj.get("status"))) {
                JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                this.dispose(); // Fecha a tela de cadastro
            } else {
                JOptionPane.showMessageDialog(this, obj.get("mensagem"), "Erro no cadastro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException | ParseException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao comunicar com o servidor.", "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void voltarParaLogin(ActionEvent e) {
        this.dispose();
    }
}
