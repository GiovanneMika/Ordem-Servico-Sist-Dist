package servidor.gui;

import servidor.ServidorThread;
import banco.UsuarioDB; // Para acesso aos usuários logados, se disponível

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.HashSet;

public class TelaServidorGUI extends JFrame {
    private JTextArea logArea;
    private JTextArea conexoesArea;
    private JTextArea usuariosArea;
    private JButton iniciarButton;
    private JButton pararButton;
    private JTextField portaField;

    private ServerSocket serverSocket;
    private Thread serverThread;
    private boolean rodando = false;

    private Set<String> ipsConectados = new HashSet<>();

    public TelaServidorGUI() {
        setTitle("Servidor - Monitoramento");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Topo com porta e botões
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

        // Painel principal dividido
        JTabbedPane abas = new JTabbedPane();

        logArea = new JTextArea();
        logArea.setEditable(false);
        abas.addTab("Log", new JScrollPane(logArea));

        conexoesArea = new JTextArea();
        conexoesArea.setEditable(false);
        abas.addTab("IPs conectados", new JScrollPane(conexoesArea));

        usuariosArea = new JTextArea();
        usuariosArea.setEditable(false);
        abas.addTab("Usuários logados", new JScrollPane(usuariosArea));

        add(abas, BorderLayout.CENTER);
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
                        String ip = cliente.getInetAddress().getHostAddress();
                        ipsConectados.add(ip);
                        atualizarConexoes();

                        log("Cliente conectado: " + ip);

                        new ServidorThread(cliente, this::log, this::atualizarUsuariosLogados) {
                            @Override
                            public void run() {
                                super.run();
                                log("Thread finalizada para cliente: " + ip);
                                ipsConectados.remove(ip);
                                atualizarConexoes();
                                atualizarUsuariosLogados();
                            }
                        }.start();


                        atualizarUsuariosLogados();
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
            ipsConectados.clear();
            atualizarConexoes();
            atualizarUsuariosLogados();
        }
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> logArea.append(msg + "\n"));
    }

    private void atualizarConexoes() {
        SwingUtilities.invokeLater(() -> {
            conexoesArea.setText("");
            for (String ip : ipsConectados) {
                conexoesArea.append(ip + "\n");
            }
        });
    }

    private void atualizarUsuariosLogados() {
        SwingUtilities.invokeLater(() -> {
            usuariosArea.setText("");

            // Se tiver controle de sessões em UsuarioDB:
            for (String usuario : UsuarioDB.getUsuariosLogados()) {
                usuariosArea.append(usuario + "\n");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaServidorGUI().setVisible(true));
    }
}
