package org.chat.net.client;

import org.chat.net.server.Server;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Client implements Runnable {

    private int port;
    private Socket socket;
    private ArrayList<String> ip2 = new ArrayList<>();
    private ArrayList<String> port2 = new ArrayList<>();
    private ArrayList<String> split = new ArrayList<>();
    public Client() {

    }

    @Override
    public void run() {

    }

    public ArrayList<String> value() {
        return split;
    }

    public void connect(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        System.out.println("Connection Successful!");
        split.add(socket.getRemoteSocketAddress().toString().replaceAll("/","").replaceAll(":", " ").split("\\s+")[0]);
        split.add(socket.getRemoteSocketAddress().toString().replaceAll("/","").replaceAll(":", " ").split("\\s+")[1]);

        /*else {
            split.set(0, socket.getRemoteSocketAddress().toString().replaceAll("/","").replaceAll(":", " ").split("\\s+")[0]);
            split.set(1, socket.getRemoteSocketAddress().toString().replaceAll("/","").replaceAll(":", " ").split("\\s+")[1]);
        }*/
        System.out.println(split);
    }

   public void list(ArrayList<String> value, ArrayList<String> value2) {
        int count = 1;
        if (value2 != null) {
            value.addAll(value2);
        }
        System.out.println("ID: \t Address: \t Port:");
        for(int i = 0; i <= value.size() - 1; i += 2) {
            System.out.println(count + ":\t" + value.get(i) + " \t" + value.get(i+1));
            count++;
        }
   }

   public ArrayList<String> connect2(String ip, String port) {
        split.add(ip);
        split.add(port);
        return split;
   }

}
