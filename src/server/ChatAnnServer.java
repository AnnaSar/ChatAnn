package server;

import main.ChatAnnProp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ChatAnnServer {


    public static List<ClientThread> getConnections() {
        return connections;
    }

    private static List<ClientThread> connections = Collections.synchronizedList(new ArrayList<>());
    private ServerSocket serverSocket;

    public ChatAnnServer() {
        Properties prop = ChatAnnProp.setProp();
        try {
            serverSocket = new ServerSocket(Integer.parseInt(prop.getProperty("portnum")));

            while (true) {
                System.out.println("Waiting for a connection ");
                Socket socket = serverSocket.accept();

                ClientThread myConn = new ClientThread(socket);
                connections.add(myConn);

                myConn.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeServer();
        }
    }

    //закрываем сервер
    private void closeServer() {
        try {
            serverSocket.close();

            synchronized (connections) {
                Iterator<ClientThread> iter = connections.iterator();
                while (iter.hasNext()) {
                    iter.next().close();
                }
            }
        } catch (Exception e) {
            System.err.println("Error close Server!");
        }
    }


}

