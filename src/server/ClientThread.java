package server;

import main.ChatAnnProp;
import server.dao.UserDAO;
import server.dao.MsgDAO;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class ClientThread extends Thread {
    private BufferedReader br;
    private PrintWriter pw;
    private Socket socket;
    private String connectionName = "";


    public String getConnectionName() {
        return connectionName;
    }

    //private String name = "";


    public ClientThread(Socket socket) {
        this.socket = socket;

        try {
            br = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream(), true);

        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }


    @Override
    public void run() {
        String str;
        String command;
        Boolean isFind;
        Properties prop = ChatAnnProp.setProp();

        try {

            String usrMsg = null;
            String usrLogin = null;
            String usrPass = null;
            String usrWho = null;
            String usrWhom = null;
            int cntPassErr = 0;
            boolean isDisconn = false;
            int ret;
            while (true) {
                System.out.println("Begin read");
                str = br.readLine();

                if (!str.contains("|")) {
                    command = str;
                } else {
                    command = str.substring(0, str.indexOf("|"));
                }
                System.out.println("str=" + str);
                String strTmp;
                switch (command) {
                    case "DISCONNECT":
                        isDisconn = true;
                        break;
                    case "ACTIVE":
                        pw.println(getActiveList(connectionName));
                        break;
                    case "LOGIN":
                        strTmp = str.substring(str.indexOf("|") + 1);
                        usrLogin = strTmp.substring(0, strTmp.indexOf("|"));
                        connectionName = usrLogin;
                        usrPass = strTmp.substring(strTmp.indexOf("|") + 1);
//                        isFind = UserDAO.findUser(usrLogin);
//                        if (isFind) {
//                            ret = UserDAO.checkUser(usrLogin, usrPass, false);
//                        } else {
//                            ret = UserDAO.regUser(usrLogin, usrPass);
//                        }
                        ret = UserDAO.checkUser(usrLogin, usrPass, true);

                        if (ret == 1 || ret == 2) {
                            List<ClientThread> connections = ChatAnnServer.getConnections();
                            synchronized (connections) {
                                Iterator<ClientThread> iter = connections.iterator();
                                while (iter.hasNext()) {
                                    ClientThread next = iter.next();
                                    if (!next.connectionName.equals(connectionName)) {
                                        next.pw.println(connectionName + " is active now");
                                    }
                                }
                            }

                            pw.println("Hello, " + connectionName);
                            pw.println(getActiveList(connectionName));
                        } else {
                            cntPassErr = ++cntPassErr;
                            if (cntPassErr < Integer.parseInt(prop.getProperty("cnt_err"))){
                                pw.println("Error login");
                            } else {
                                pw.println("DISCONNECT");
                                isDisconn = true;
                            }
                        }
                        break;

                    case "MSG":
                        strTmp = str.substring(str.indexOf("|") + 1);
                        usrWho = strTmp.substring(0, strTmp.indexOf("|"));
                        strTmp = strTmp.substring(strTmp.indexOf("|") + 1);
                        usrWhom = strTmp.substring(0, strTmp.indexOf("|"));
                        usrMsg = strTmp.substring(strTmp.indexOf("|") + 1);

                        System.out.println("usrWho=" + usrWho);
                        System.out.println("usrWhom=" + usrWhom);
                        System.out.println("usrMsg=" + usrMsg);

                        if (usrWhom.toUpperCase().equals("ALL")) {
                            List<ClientThread> connections = ChatAnnServer.getConnections();
                            synchronized (connections) {
                                Iterator<ClientThread> iter = connections.iterator();
                                while (iter.hasNext()) {
                                    ClientThread next = iter.next();
                                    if (!next.getConnectionName().equals(connectionName)) {
                                        next.pw.println(connectionName + ": " + usrMsg);
                                        MsgDAO.saveMsg(usrWho, next.getConnectionName(), usrMsg);
                                    }
                                }
                            }
                        } else {
                            isFind = UserDAO.findUser(usrWhom);
                            if (isFind) {
                                MsgDAO.saveMsg(usrWho, usrWhom, usrMsg);
                                List<ClientThread> connections = ChatAnnServer.getConnections();
                                synchronized (connections) {
                                    Iterator<ClientThread> iter = connections.iterator();
                                    while (iter.hasNext()) {
                                        ClientThread next = iter.next();
                                        if (next.getConnectionName().equals(usrWhom)) {
                                            next.pw.println(connectionName + ": " + usrMsg);
                                        }
                                    }
                                }
                            } else {
                                pw.println("Incorrect user login :" + usrWhom);
                            }
                        }
                        break;

                    default:
                        break;
                }

                if (isDisconn) {
                    if (!connectionName.isEmpty()) {
                        List<ClientThread> connections = ChatAnnServer.getConnections();
                        Iterator<ClientThread> iter = connections.iterator();
                        while (iter.hasNext()) {
                            ClientThread next = iter.next();
                            if (!next.getConnectionName().equals(connectionName)) {
                                next.pw.println(connectionName + " left chat");
                            }
                        }
                    }
                    close();
                    break;
                }
            }
        } catch (SQLException|IOException e1) {
            e1.printStackTrace();
            close();
        }
    }

    private String getActiveList (String pConnName){
        StringBuilder ret;
        List<ClientThread> connections = ChatAnnServer.getConnections();
        if (!connections.isEmpty()) {
            ret = new StringBuilder();
            Iterator<ClientThread> iter = connections.iterator();
            while (iter.hasNext()) {
                ClientThread next = iter.next();
                if (!next.getConnectionName().equals(pConnName)) {
                    ret.append(next.getConnectionName()).append("#");
                }
            }
            ret.append(" ");
            return ret.toString();
        }
        return "";
    }


    public void close() {
        try {
            br.close();
            pw.close();
            socket.close();
            List<ClientThread> connections = ChatAnnServer.getConnections();
            connections.remove(this);
        } catch (Exception e) {
            System.err.println("Error when client tread disconnect!");
        }
    }
}
