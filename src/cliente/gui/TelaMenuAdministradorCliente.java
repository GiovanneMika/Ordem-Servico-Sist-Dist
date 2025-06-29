package cliente.gui;

import javax.swing.*;
import java.awt.*;
import java.io.*;

import java.net.Socket;

public class TelaMenuAdministradorCliente extends JFrame {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String token;

    private JTabbedPane abas;

    public TelaMenuAdministradorCliente(Socket socket, PrintWriter out, BufferedReader in, String token) {
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.token = token;

        setTitle("Painel Administrativo");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel de abas
        abas = new JTabbedPane();

        abas.addTab("Usuários", new PainelUsuarios(socket, out, in, token));
        abas.addTab("Ordens de Serviço", new PainelOrdensAdm(socket, out, in, token));

        add(abas, BorderLayout.CENTER);
    }
}

