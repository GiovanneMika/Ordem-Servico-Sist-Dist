package cliente.gui;

import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.PrintWriter;

public class DialogEditarUsuario extends JDialog {
    private JTextField nomeField;
    private JPasswordField senhaField;
    private JComboBox<String> perfilCombo;

    private PrintWriter out;
    private BufferedReader in;
    private String token;
    private String usuarioAlvo;

    public DialogEditarUsuario(Frame owner, PrintWriter out, BufferedReader in, String token, String usuarioAlvo,
                               String nomeAtual, String perfilAtual, Runnable onSuccess) {
        super(owner, "Editar Usuário: " + usuarioAlvo, true);
        this.out = out;
        this.in = in;
        this.token = token;
        this.usuarioAlvo = usuarioAlvo;

        setSize(400, 250);
        setLocationRelativeTo(owner);
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("Nome:"));
        nomeField = new JTextField(nomeAtual);
        add(nomeField);

        add(new JLabel("Senha (nova):"));
        senhaField = new JPasswordField();
        add(senhaField);

        add(new JLabel("Perfil:"));
        perfilCombo = new JComboBox<>(new String[]{"comum", "adm"});
        perfilCombo.setSelectedItem(perfilAtual);
        add(perfilCombo);

        JButton editarBtn = new JButton("Salvar");
        editarBtn.addActionListener(e -> editar(onSuccess));
        add(editarBtn);

        JButton cancelarBtn = new JButton("Cancelar");
        cancelarBtn.addActionListener(e -> dispose());
        add(cancelarBtn);
    }

    private void editar(Runnable onSuccess) {
        String novoNome = nomeField.getText().trim();
        String novaSenha = new String(senhaField.getPassword()).trim();
        String novoPerfil = (String) perfilCombo.getSelectedItem();

        if (novoNome.isEmpty() || novaSenha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e senha não podem estar vazios!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            JSONObject req = new JSONObject();
            req.put("operacao", "editar_usuario");
            req.put("token", token);
            req.put("usuario_alvo", usuarioAlvo);
            req.put("novo_nome", novoNome);
            req.put("nova_senha", novaSenha);
            req.put("novo_perfil", novoPerfil);

            String jsonEnviado = req.toJSONString();
            System.out.println("JSON enviado ao servidor: " + jsonEnviado);
            out.println(jsonEnviado);

            String resposta = in.readLine();
            System.out.println("JSON recebido do servidor: " + resposta);


            if (resposta.contains("\"status\":\"sucesso\"")) {
                JOptionPane.showMessageDialog(this, "Usuário editado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                onSuccess.run();
            } else {
                JOptionPane.showMessageDialog(this, resposta, "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao editar usuário.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
