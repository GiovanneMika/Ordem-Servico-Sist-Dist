package cliente.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.Socket;

public class TelaConexaoCliente extends JFrame {
    private JTextField ipField;
    private JTextField portaField;
    private JButton conectarButton;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public TelaConexaoCliente() {
        setTitle("Conectar ao Servidor");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel camposPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        camposPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        camposPanel.add(new JLabel("IP do Servidor:"));
        ipField = new JTextField("127.0.0.1");
        camposPanel.add(ipField);

        camposPanel.add(new JLabel("Porta:"));
        portaField = new JTextField("23000");
        camposPanel.add(portaField);

        add(camposPanel, BorderLayout.CENTER);

        conectarButton = new JButton("Conectar");
        conectarButton.addActionListener(this::conectarAoServidor);
        add(conectarButton, BorderLayout.SOUTH);
    }

    private void conectarAoServidor(ActionEvent e) {
        String ip = ipField.getText().trim();
        int porta;

        try {
            porta = Integer.parseInt(portaField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Porta inválida!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            socket = new Socket(ip, porta);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            JOptionPane.showMessageDialog(this, "Conexão bem-sucedida com o servidor!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            // Aqui você pode chamar a próxima tela, passando o socket
            abrirTelaInicial(socket, out, in);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Não foi possível conectar ao servidor.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirTelaInicial(Socket socket, PrintWriter out, BufferedReader in) {
        this.dispose();
        new TelaLoginCliente(socket, out, in).setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaConexaoCliente().setVisible(true));
    }
}
