package servidor;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class ServidorMain {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Porta do servidor: ");
        int porta = Integer.parseInt(br.readLine());

        ServerSocket serverSocket = new ServerSocket(porta);
        System.out.println("Servidor rodando na porta " + porta);

        while (true) {
            Socket cliente = serverSocket.accept();
            System.out.println("Cliente conectado: " + cliente.getInetAddress().getHostAddress());
            new ServidorThread(cliente).start();
        }
    }
}
