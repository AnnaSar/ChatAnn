package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

import main.ChatAnnProp;

public class ChatAnnClient {
    private BufferedReader br;
    private PrintWriter pw;
    private Socket socket;

    private String clientName;
    private Scanner scan;

    public Scanner getScan() {
        return scan;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public PrintWriter getPw() {
        return pw;
    }

    public ChatAnnClient() {
        scan = new Scanner(System.in);
        String str;
        Boolean isConnect = false;
        Boolean isResConn = false;
        Properties prop = ChatAnnProp.setProp();

        try {
            socket = new Socket(prop.getProperty("host"), Integer.parseInt(prop.getProperty("portnum")));
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream(), true);

            SetLogin();
            try {
                while (!isResConn) {
                    String strServ = br.readLine();
                    if (strServ.toLowerCase().equals("error login")) {
                           SetLogin();
                    } else {
                        if (strServ.equals("DISCONNECT")) {
                            System.out.println("Count attempts have been exhausted");
                            isConnect = false;
                            isResConn = true;
                        } else {
                            System.out.println(strServ);
                            isConnect = true;
                            isResConn = true;
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error read msg from Server");
                e.printStackTrace();
            }

            if (isConnect) {
                //в отдельном потоке смотрим ответы от сервера
                ServerConn myReader = new ServerConn();
                myReader.start();

                //читаем текст с консоли - то что вводит пользователь
                str = "";
                while (!str.toLowerCase().equals("exit")) {
                    str = scan.nextLine();
                    if (str != null && str.trim().length() != 0) {
                        if (str.toLowerCase().equals("exit")) {
                            pw.println("DISCONNECT");
                        } else {
                            if (str.toLowerCase().equals("active")) {
                                pw.println("ACTIVE");
                            } else {
                                StringBuilder sb = new StringBuilder();
                                if (str.indexOf(":") == -1) {
                                    sb.append("MSG|").append(clientName).append("|ALL|").append(str);
                                } else {
                                    sb.append("MSG|").append(clientName).append("|").append(str.substring(0, str.indexOf(":"))).append("|").append(str.substring(str.indexOf(":") + 1));
                                }
                                pw.println(sb.toString());
                            }
                        }
                    }
                }
                myReader.sayBye();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            chatClose();
        }
    }

    //закрываем все наши ридеры и сокет
    private void chatClose() {
        try {
            br.close();
            pw.close();
            socket.close();
        } catch (Exception e) {
            System.err.println("Error close chat");
        }
    }

    private void SetLogin(){
        String loginStr;
        String passStr;
        PrintWriter vPw = this.getPw();
        Scanner vScan = this.getScan();

        //логинимся
        System.out.println("Put your login: \n");
        loginStr = vScan.nextLine();
        System.out.println("Put your password: \n");
        passStr = vScan.nextLine();

        vPw.println("LOGIN|" + loginStr + "|" + passStr);
        this.setClientName(loginStr);
    }


    private class ServerConn extends Thread {
        private boolean isStop;
//        private int countLogin = 1;

        public void sayBye() {
            isStop = true;
        }

        @Override
        public void run() {
            try {
                while (!isStop) {
                    String strServ = br.readLine();
//                    if (strServ.toLowerCase().equals("error login")) {
//                        if (countLogin < 4) {
//                            System.out.println("Incorrect password, try again");
//                            countLogin = ++countLogin;
//                            SetLogin();
//                        } else {
//                            System.out.println("You put 3 incorrect password :( Good bye");
//                            sayBye();
//                        }
//                    } else {
                        if (strServ.contains("#")) {
                            for (String retval : strServ.split("#")) {
                                System.out.println(retval);
                            }
                        } else {
                            System.out.println(strServ);
                        }
                    }

            } catch (IOException e) {
                System.err.println("Error read msg from Server");
                e.printStackTrace();
            }
        }
    }
}

