package net;

import Command.ChatCommand;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;

public class TCPConnection {
    private final Socket socket;
    private final Thread rxThread;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final TCPConnectionListener eListener;
    private String userName;
    private RsaCrypt rsaCrypt;
    private ChatCommand activeChat;

    public TCPConnection (Socket socket, TCPConnectionListener eListener) throws IOException, NoSuchAlgorithmException {
        this.eListener=eListener;
        this.socket=socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),Charset.forName("UTF-8")));
        rsaCrypt= new RsaCrypt();
        rxThread= new Thread(new Runnable()
        {
            @Override
            public void run() {
                try
                {
                    eListener.onConnectionReady(TCPConnection.this);
                    while (!rxThread.isInterrupted())
                    {
                        eListener.onInputMsg(TCPConnection.this, in.readLine());
                    }
                }
                 catch (Exception e) {
                    eListener.onException(TCPConnection.this, e);
                } finally {
                    eListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        rxThread.start();
    }

    public TCPConnection (String ip, int port,TCPConnectionListener eListener) throws IOException, NoSuchAlgorithmException {
        this(new Socket(ip,port), eListener);
    }

    public synchronized void  sendMsg(String valueMsg)
    {
        try {
            out.write(valueMsg+ "\n");
            out.flush();
        } catch (IOException e) {
            eListener.onException(TCPConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect ()
    {
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eListener.onException(TCPConnection.this, e);
        }
    }

    public synchronized RsaCrypt  getEncryption() { return rsaCrypt; }
    public synchronized String getUserName() {
        return userName;
    }
    public synchronized void setUserName(String userName) {
        this.userName = userName;
    }
    public synchronized ChatCommand getActiveChat() { return activeChat; }
    public synchronized void setActiveChat(ChatCommand activeChat) { this.activeChat = activeChat; }
    @Override
    public String toString() {
        return socket.getInetAddress()+ ": "+ socket.getPort();
    }
}







