package cliente.gui;

import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.PrintWriter;

public class DialogCadastroUsuario extends JDialog {
    private JTextField nomeField;
    private JTextField usuarioField;
    private JPasswordField senhaField;
    private JComboBox<String> perfilCombo;

    private PrintWriter out;
    private BufferedReader in;
    private String token;

    public DialogCadastroUsuario(Frame owner, PrintWriter out, BufferedReader in, String token, Runnable onSuccess) {
        super(owner, "Cadastrar Novo Usuário", true);
        this.out = out;
        this.in = in;
        this.token = token;

        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new GridLayout(5, 2, 10, 10));

        add(new JLabel("Nome:"));
        nomeField = new JTextField();
        add(nomeField);

        add(new JLabel("Usuário:"));
        usuarioField = new JTextField();
        add(usuarioField);

        add(new JLabel("Senha:"));
        senhaField = new JPasswordField();
        add(senhaField);

        add(new JLabel("Perfil:"));
        perfilCombo = new JComboBox<>(new String[]{"comum", "adm"});
        add(perfilCombo);

        JButton cadastrarBtn = new JButton("Cadastrar");
        cadastrarBtn.addActionListener(e -> cadastrar(onSuccess));
        add(cadastrarBtn);

        JButton cancelarBtn = new JButton("Cancelar");
        cancelarBtn.addActionListener(e -> dispose());
        add(cancelarBtn);
    }

    private void cadastrar(Runnable onSuccess) {
        String nome = nomeField.getText().trim();
        String usuario = usuarioField.getText().trim();
        String senha = new String(senhaField.getPassword()).trim();
        String perfil = (String) perfilCombo.getSelectedItem();

        if (nome.isEmpty() || usuario.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            JSONObject req = new JSONObject();
            req.put("operacao", "cadastro");
            req.put("nome", nome);
            req.put("usuario", usuario);
            req.put("senha", senha);
            req.put("perfil", perfil);
            req.put("token", token);

            out.println(req.toJSONString());
            String resposta = in.readLine();

            if (resposta.contains("\"status\":\"sucesso\"")) {
                JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                onSuccess.run();
            } else {
                JOptionPane.showMessageDialog(this, resposta, "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar usuário.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
