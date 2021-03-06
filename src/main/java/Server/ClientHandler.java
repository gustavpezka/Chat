package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    Socket socket;
    Server server;
    DataInputStream in;
    DataOutputStream out;

    private boolean authenticated;
    private String nickname;
    private String login;

    public ClientHandler(Socket socket, Server server) {
        try {
            this.socket = socket;
            this.server = server;

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    //socket.setSoTimeout(0);
                    // цикл аутентификации
                    while (true) {
                        String str = in.readUTF();

                        if (str.equals("/end")) {
                            sendMsg("/end");
                            System.out.println("Client disconnected");
                            break;
                        }
                        if (str.startsWith("/auth ")) {
                            String[] token = str.split("\\s+");
                            nickname = server.getAuthService()
                                    .getNicknameByLoginAndPassword(token[1], token[2]);
                            login = token[1];
                            if (nickname != null) {
                                if (!server.isLoginAuthenticated(login)) {
                                    sendMsg("/authok " + nickname);
                                    server.subscribe(this);
                                    authenticated = true;
                                    break;
                                } else {
                                    sendMsg("С этим логином уже вошли");
                                }
                            } else {
                                sendMsg("Неверный логин / пароль");
                            }
                        }

                        if (str.startsWith("/reg ")) {
                            String[] token = str.split("\\s+");
                            if (token.length < 4) {
                                continue;
                            }

                            boolean regOk = server.getAuthService().
                                    addNewUser(token[1], token[2], token[3]);
                            for (int i = 0; i < token.length; i++) {
                                System.out.println(token[i]);
                            }

                            if (regOk) {
                                sendMsg("/regok");
                            } else {
                                sendMsg("/regno");
                            }
                        }
                    }
                    // цикл работы
                    while (authenticated) {
                        String str = in.readUTF();

                        if (str.startsWith("/")) {
                            if (str.equals("/end")) {
                                sendMsg("/end");
                                System.out.println("Client disconnected");
                                break;
                            }

                            if (str.startsWith("/w")) {
                                String[] token = str.split("\\s+", 3);
                                if (token.length < 3) {
                                    continue;
                                }
                                server.privateMsg(this, token[1], token[2]);
                            }
                            if (str.startsWith("/nick")) {
                                String[] token = str.split("\\s+", 2);
                                server.getAuthService().changeNickname(token[1],login);
                                //sendMsg("/nick" + " " + token[1]);
                                //authenticated = false;
                                nickname = server.getAuthService()
                                        .getNicknameByLoginAndPassword(token[1], token[2]);
                                login = token[1];
                                if (nickname != null) {
                                    if (!server.isLoginAuthenticated(login)) {
                                        sendMsg("/authok " + nickname);
                                        server.subscribe(this);
                                        authenticated = true;
                                        break;
                                    } else {
                                        sendMsg("С этим логином уже вошли");
                                    }
                                } else {
                                    sendMsg("Неверный логин / пароль");
                                }
                            }
                        } else {
                            server.broadcastMsg(this, str);
                        }
                    }
                // SocketTimeoutException
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    server.unsubscribe(this);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
    }

    public String getLogin() {
        return login;
    }
}
