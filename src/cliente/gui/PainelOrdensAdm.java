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

		modeloTabela = new DefaultTableModel(new Object[] { "ID", "Descrição", "Status", "Usuário" }, 0) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tabelaOrdens = new JTable(modeloTabela);
		JScrollPane scrollPane = new JScrollPane(tabelaOrdens);
		add(scrollPane, BorderLayout.CENTER);

		JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
		filtroCombo = new JComboBox<>(new String[] { "todas", "pendente", "finalizada", "cancelada" });
		JButton atualizarBtn = new JButton("Atualizar");
		atualizarBtn.addActionListener(e -> carregarOrdens((String) filtroCombo.getSelectedItem()));

		topo.add(new JLabel("Filtro:"));
		topo.add(filtroCombo);
		topo.add(atualizarBtn);

		add(topo, BorderLayout.NORTH);

		carregarOrdens("todas");
		JPanel botoes = new JPanel(new FlowLayout());

		JButton novaOrdemBtn = new JButton("Nova Ordem");
		novaOrdemBtn.addActionListener(e -> abrirCadastroOrdem());

		JButton editarOrdemBtn = new JButton("Editar Ordem");
		editarOrdemBtn.addActionListener(e -> editarOrdemSelecionada());

		botoes.add(novaOrdemBtn);
		botoes.add(editarOrdemBtn);

		add(botoes, BorderLayout.SOUTH);
		JButton logoutBtn = new JButton("Logout");
		logoutBtn.addActionListener(e -> realizarLogout());

		botoes.add(logoutBtn); // já existe o JPanel botoes

	}

	private void realizarLogout() {
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

				// Fecha a janela principal (JFrame do admin)
				SwingUtilities.getWindowAncestor(this).dispose();

				// Abre novamente a tela de login
				SwingUtilities.invokeLater(() -> new TelaLoginCliente(socket, out, in).setVisible(true));
			} else {
				JOptionPane.showMessageDialog(this, obj.get("mensagem"), "Erro ao fazer logout",
						JOptionPane.ERROR_MESSAGE);
			}

		} catch (IOException | ParseException ex) {
			JOptionPane.showMessageDialog(this, "Erro ao comunicar com o servidor.", "Erro", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	private void abrirCadastroOrdem() {
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
					carregarOrdens((String) filtroCombo.getSelectedItem());
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

	private void editarOrdemSelecionada() {
		int linhaSelecionada = tabelaOrdens.getSelectedRow();
		if (linhaSelecionada == -1) {
			JOptionPane.showMessageDialog(this, "Selecione uma ordem para editar.", "Aviso",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		Object idObj = modeloTabela.getValueAt(linhaSelecionada, 0);
		String descricaoAtual = (String) modeloTabela.getValueAt(linhaSelecionada, 1);
		String statusAtual = (String) modeloTabela.getValueAt(linhaSelecionada, 2);

		String novaDescricao = JOptionPane.showInputDialog(this, "Nova descrição:", descricaoAtual);
		if (novaDescricao == null || novaDescricao.trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Descrição não pode ser vazia.", "Aviso", JOptionPane.WARNING_MESSAGE);
			return;
		}

		String[] opcoesStatus = { "pendente", "finalizada", "cancelada" };
		String novoStatus = (String) JOptionPane.showInputDialog(this, "Novo status:", "Alterar Status",
				JOptionPane.QUESTION_MESSAGE, null, opcoesStatus, statusAtual);

		if (novoStatus == null)
			return;

		try {
			JSONObject editar = new JSONObject();
			editar.put("operacao", "alterar_ordem");
			editar.put("token", token);
			editar.put("id_ordem", Long.valueOf(idObj.toString()));
			editar.put("nova_descricao", novaDescricao);
			editar.put("novo_status", novoStatus);

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
				carregarOrdens((String) filtroCombo.getSelectedItem());
			} else {
				JOptionPane.showMessageDialog(this, obj.get("mensagem"), "Erro ao editar ordem",
						JOptionPane.ERROR_MESSAGE);
			}

		} catch (IOException | ParseException ex) {
			JOptionPane.showMessageDialog(this, "Erro ao comunicar com o servidor.", "Erro", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	private void carregarOrdens(String filtro) {
		try {
			JSONObject requisicao = new JSONObject();
			requisicao.put("operacao", "listar_ordens");
			requisicao.put("token", token);
			requisicao.put("filtro", filtro);

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
					modeloTabela.addRow(new Object[] { ordem.get("id"), ordem.get("descricao"), ordem.get("status"),
							ordem.get("autor") // Assumindo que o backend envia essa info
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
}
