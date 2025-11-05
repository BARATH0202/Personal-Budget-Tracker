package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Multi-threaded server that accepts client connections.
 */
public class BudgetServer {
    private static final int PORT = 5000;
    private ServerSocket serverSocket;
    private ExecutorService pool = Executors.newCachedThreadPool();

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("BudgetServer started on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection: " + clientSocket.getRemoteSocketAddress());
                pool.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            stop();
        }
    }

    public void stop() {
        try {
            if (serverSocket != null) serverSocket.close();
            pool.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new BudgetServer().start();
    }
}
