package servidor.gui;

import servidor.ServidorThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TelaServidorGUI extends JFrame {
    private JTextArea logArea;
    private JButton iniciarButton;
    private JButton pararButton;
    private JTextField portaField;
    private ServerSocket serverSocket;
    private Thread serverThread;
    private boolean rodando = false;

    public TelaServidorGUI() {
        setTitle("Servidor - Monitoramento");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Porta:"));
        portaField = new JTextField("12345", 8);
        topPanel.add(portaField);

        iniciarButton = new JButton("Iniciar Servidor");
        iniciarButton.addActionListener(this::iniciarServidor);
        topPanel.add(iniciarButton);

        pararButton = new JButton("Parar Servidor");
        pararButton.setEnabled(false);
        pararButton.addActionListener(this::pararServidor);
        topPanel.add(pararButton);

        add(topPanel, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(logArea);
        add(scroll, BorderLayout.CENTER);
    }

    private void iniciarServidor(ActionEvent e) {
        int porta;
        try {
            porta = Integer.parseInt(portaField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Porta inválida", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            serverSocket = new ServerSocket(porta);
            rodando = true;
            log("Servidor iniciado na porta " + porta);
            iniciarButton.setEnabled(false);
            pararButton.setEnabled(true);
            portaField.setEnabled(false);

            serverThread = new Thread(() -> {
                try {
                    while (rodando) {
                        Socket cliente = serverSocket.accept();
                        log("Cliente conectado: " + cliente.getInetAddress().getHostAddress());
                        new ServidorThread(cliente) {
                            @Override
                            public void run() {
                                super.run();
                                log("Thread finalizada para cliente: " + cliente.getInetAddress().getHostAddress());
                            }
                        }.start();
                    }
                } catch (IOException ex) {
                    if (rodando) log("Erro no servidor: " + ex.getMessage());
                }
            });
            serverThread.start();

        } catch (IOException ex) {
            log("Erro ao iniciar o servidor: " + ex.getMessage());
        }
    }

    private void pararServidor(ActionEvent e) {
        try {
            rodando = false;
            if (serverSocket != null) serverSocket.close();
            log("Servidor parado.");
        } catch (IOException ex) {
            log("Erro ao parar o servidor: " + ex.getMessage());
        } finally {
            iniciarButton.setEnabled(true);
            pararButton.setEnabled(false);
            portaField.setEnabled(true);
        }
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> logArea.append(msg + "\n"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaServidorGUI().setVisible(true));
    }
}
