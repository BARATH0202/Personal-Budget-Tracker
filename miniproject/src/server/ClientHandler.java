package server;

import shared.*;
import model.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Handles a single client connection.
 */
public class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private DatabaseHelper db = new DatabaseHelper();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            System.out.println("Client connected: " + socket.getRemoteSocketAddress());

            while (true) {
                Request request = (Request) ois.readObject();
                System.out.println("Received action: " + request.getAction());

                switch (request.getAction()) {
                    case LOGIN:
                        User u = (User) request.getPayload();
                        User logged = db.login(u.getUsername(), u.getPassword());
                        if (logged != null)
                            oos.writeObject(new Response(Action.RESPONSE_OK, logged));
                        else
                            oos.writeObject(new Response(Action.RESPONSE_ERROR, "Invalid credentials"));
                        break;

                    case REGISTER:
                        User newUser = (User) request.getPayload();
                        boolean registered = db.registerUser(newUser);
                        if (registered)
                            oos.writeObject(new Response(Action.RESPONSE_OK, "User registered"));
                        else
                            oos.writeObject(new Response(Action.RESPONSE_ERROR, "Registration failed"));
                        break;

                    case ADD_TRANSACTION:
                        Transaction t = (Transaction) request.getPayload();
                        boolean added = db.addTransaction(t);
                        if (added)
                            oos.writeObject(new Response(Action.RESPONSE_OK, "Transaction added"));
                        else
                            oos.writeObject(new Response(Action.RESPONSE_ERROR, "Failed to add transaction"));
                        break;

                    case GET_TRANSACTIONS:
                        List<Transaction> txns = db.getTransactions();
                        oos.writeObject(new Response(Action.RESPONSE_OK, txns));
                        break;

                    case DELETE_TRANSACTION:
                        int id = (int) request.getPayload();
                        boolean deleted = db.deleteTransaction(id);
                        if (deleted)
                            oos.writeObject(new Response(Action.RESPONSE_OK, "Transaction deleted"));
                        else
                            oos.writeObject(new Response(Action.RESPONSE_ERROR, "Failed to delete"));
                        break;

                    case GET_CATEGORIES:
                        List<Category> cats = db.getCategories();
                        oos.writeObject(new Response(Action.RESPONSE_OK, cats));
                        break;

                    default:
                        oos.writeObject(new Response(Action.RESPONSE_ERROR, "Unknown action"));
                }
            }

        } catch (Exception e) {
            System.out.println("Client disconnected: " + socket.getRemoteSocketAddress());
        }
    }
}
