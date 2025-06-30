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
        JPanel topoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> filtroCombo = new JComboBox<>(new String[]{"todas", "pendente", "finalizada", "cancelada"});
        JButton atualizarButton = new JButton("Atualizar");

        atualizarButton.addActionListener(e -> carregarOrdens((String) filtroCombo.getSelectedItem()));

        topoPanel.add(new JLabel("Filtro:"));
        topoPanel.add(filtroCombo);
        topoPanel.add(atualizarButton);

        add(topoPanel, BorderLayout.NORTH);

		modeloTabela = new DefaultTableModel(new Object[] { "ID", "Descrição", "Status" }, 0) {
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

		carregarOrdens("todas");
		
		JButton excluirContaButton = new JButton("Excluir Minha Conta");
		excluirContaButton.addActionListener(this::excluirMinhaConta);
		botoesPanel.add(excluirContaButton);

		JButton logoutButton = new JButton("Logout");
		logoutButton.addActionListener(this::realizarLogout);
		botoesPanel.add(logoutButton);

	}

	private void carregarOrdens(String filtro) {
		try {
			JSONObject requisicao = new JSONObject();
			requisicao.put("operacao", "listar_ordens");
			requisicao.put("token", token);
			requisicao.put("filtro", filtro);  // usa filtro vindo da combo

			String jsonEnviado = requisicao.toJSONString();
			System.out.println("JSON enviado ao servidor: " + jsonEnviado);
			out.println(jsonEnviado);

			String respostaStr = in.readLine();
			System.out.println("JSON recebido do servidor: " + respostaStr);
			JSONParser parser = new JSONParser();
			JSONObject resposta = (JSONObject) parser.parse(respostaStr);

			modeloTabela.setRowCount(0); // limpa a tabela

			if ("sucesso".equals(resposta.get("status"))) {
				JSONArray ordens = (JSONArray) resposta.get("ordens");
				for (Object o : ordens) {
					JSONObject ordem = (JSONObject) o;
					modeloTabela.addRow(new Object[] {
						ordem.get("id"),
						ordem.get("descricao"),
						ordem.get("status")
					});
				}
			} else {
				JOptionPane.showMessageDialog(this, resposta.get("mensagem"), "Erro ao carregar ordens",
						JOptionPane.ERROR_MESSAGE);
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

				String jsonEnviado = novaOrdem.toJSONString();
				System.out.println("JSON enviado ao servidor: " + jsonEnviado);
				out.println(jsonEnviado);

				String resposta = in.readLine();
				System.out.println("JSON recebido do servidor: " + resposta);
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(resposta);

				if ("sucesso".equals(obj.get("status"))) {
					JOptionPane.showMessageDialog(this, "Ordem cadastrada com sucesso!", "Sucesso",
							JOptionPane.INFORMATION_MESSAGE);
					carregarOrdens("todas");
				} else {
					JOptionPane.showMessageDialog(this, obj.get("mensagem"), "Erro ao cadastrar",
							JOptionPane.ERROR_MESSAGE);
				}

			} catch (IOException | ParseException ex) {
				JOptionPane.showMessageDialog(this, "Erro ao comunicar com o servidor.", "Erro",
						JOptionPane.ERROR_MESSAGE);
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
			JOptionPane.showMessageDialog(this, "Selecione uma ordem para editar.", "Aviso",
					JOptionPane.WARNING_MESSAGE);
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

				String jsonEnviado = editar.toJSONString();
				System.out.println("JSON enviado ao servidor: " + jsonEnviado);
				out.println(jsonEnviado);

				String resposta = in.readLine();
				System.out.println("JSON recebido do servidor: " + resposta);
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(resposta);

				if ("sucesso".equals(obj.get("status"))) {
					JOptionPane.showMessageDialog(this, "Ordem editada com sucesso!", "Sucesso",
							JOptionPane.INFORMATION_MESSAGE);
					carregarOrdens("todas");
				} else {
					JOptionPane.showMessageDialog(this, obj.get("mensagem"), "Erro ao editar ordem",
							JOptionPane.ERROR_MESSAGE);
				}

			} catch (IOException | ParseException ex) {
				JOptionPane.showMessageDialog(this, "Erro ao comunicar com o servidor.", "Erro",
						JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}

	private void realizarLogout(ActionEvent e) {
		try {
			JSONObject logout = new JSONObject();
			logout.put("operacao", "logout");
			logout.put("token", token);

			String jsonEnviado = logout.toJSONString();
			System.out.println("JSON enviado ao servidor: " + jsonEnviado);
			out.println(jsonEnviado);

			String resposta = in.readLine();
			System.out.println("JSON recebido do servidor: " + resposta);
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(resposta);

			if ("sucesso".equals(obj.get("status"))) {
				JOptionPane.showMessageDialog(this, "Logout realizado com sucesso!", "Logout",
						JOptionPane.INFORMATION_MESSAGE);
				this.dispose(); // Fecha tela atual

				// Reabre a tela de login
				SwingUtilities.invokeLater(() -> {
					new TelaLoginCliente(socket, out, in).setVisible(true);
				});

			} else {
				JOptionPane.showMessageDialog(this, obj.get("mensagem"), "Erro ao fazer logout",
						JOptionPane.ERROR_MESSAGE);
			}

		} catch (IOException | ParseException ex) {
			JOptionPane.showMessageDialog(this, "Erro ao comunicar com o servidor.", "Erro", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}
	
	private void excluirMinhaConta(ActionEvent e) {
		int confirmacao = JOptionPane.showConfirmDialog(this,
				"Tem certeza que deseja excluir sua conta? Esta ação não pode ser desfeita.",
				"Confirmação", JOptionPane.YES_NO_OPTION);

		if (confirmacao == JOptionPane.YES_OPTION) {
			try {
				JSONObject excluir = new JSONObject();
				excluir.put("operacao", "excluir_usuario");
				excluir.put("token", token);

				String jsonEnviado = excluir.toJSONString();
				System.out.println("JSON enviado ao servidor: " + jsonEnviado);
				out.println(jsonEnviado);

				String resposta = in.readLine();
				System.out.println("JSON recebido do servidor: " + resposta);
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(resposta);

				if ("sucesso".equals(obj.get("status"))) {
					JOptionPane.showMessageDialog(this,
							"Conta excluída com sucesso. Você será redirecionado para a tela de login.",
							"Conta excluída", JOptionPane.INFORMATION_MESSAGE);
					this.dispose(); // Fecha a tela atual

					// Redireciona para login
					SwingUtilities.invokeLater(() -> {
						new TelaLoginCliente(socket, out, in).setVisible(true);
					});
				} else {
					JOptionPane.showMessageDialog(this, obj.get("mensagem"),
							"Erro ao excluir conta", JOptionPane.ERROR_MESSAGE);
				}

			} catch (IOException | ParseException ex) {
				JOptionPane.showMessageDialog(this, "Erro ao comunicar com o servidor.",
						"Erro", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}


}
