package cliente.gui;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

import java.net.Socket;

public class TelaLoginCliente extends JFrame {
	private JTextField usuarioField;
	private JPasswordField senhaField;
	private JButton entrarButton;
	private JButton cadastrarButton;

	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;

	public TelaLoginCliente(Socket socket, PrintWriter out, BufferedReader in) {
		this.socket = socket;
		this.out = out;
		this.in = in;

		setTitle("Login");
		setSize(400, 250);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

		panel.add(new JLabel("Usuário:"));
		usuarioField = new JTextField();
		panel.add(usuarioField);

		panel.add(new JLabel("Senha:"));
		senhaField = new JPasswordField();
		panel.add(senhaField);

		entrarButton = new JButton("Entrar");
		entrarButton.addActionListener(this::realizarLogin);
		panel.add(entrarButton);

		cadastrarButton = new JButton("Cadastrar novo usuário");
		cadastrarButton.addActionListener(this::abrirTelaCadastro);
		panel.add(cadastrarButton);

		add(panel, BorderLayout.CENTER);
	}

	private void realizarLogin(ActionEvent e) {
		String usuario = usuarioField.getText().trim();
		String senha = new String(senhaField.getPassword());

		if (usuario.isEmpty() || senha.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			JSONObject login = new JSONObject();
			login.put("operacao", "login");
			login.put("usuario", usuario);
			login.put("senha", senha);

			out.println(login.toJSONString());

			String resposta = in.readLine();
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(resposta);

			if ("sucesso".equals(obj.get("status"))) {
				String token = (String) obj.get("token");
				String perfil = (String) obj.get("perfil");

				JOptionPane.showMessageDialog(this, "Login realizado com sucesso!", "Sucesso",
						JOptionPane.INFORMATION_MESSAGE);
				abrirMenuInicial(token, perfil);

			} else {
				JOptionPane.showMessageDialog(this, obj.get("mensagem"), "Erro no login", JOptionPane.ERROR_MESSAGE);
			}

		} catch (IOException | ParseException ex) {
			JOptionPane.showMessageDialog(this, "Erro ao comunicar com o servidor.", "Erro", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	private void abrirMenuInicial(String token, String perfil) {
	    this.dispose();

	    if ("adm".equals(perfil)) {
	        new TelaMenuAdministradorCliente(socket, out, in, token).setVisible(true);
	    } else {
	        new TelaMenuComumCliente(socket, out, in, token).setVisible(true);
	    }
	}


	private void abrirTelaCadastro(ActionEvent e) {
		new TelaCadastroCliente(socket, out, in).setVisible(true);
	}

}
