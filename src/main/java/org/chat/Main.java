package org.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;


public class Main {
    private  ServerSocket serverSocket;
    private  Socket connectedSocket;
    private Socket clientSocket;


    public static void main(String[] args) throws IOException {
        String port = args[0];
        Scanner obj = new Scanner(System.in);
        Main x = new Main();
        int newport;
        String newIP;

        int choice = 0;

        while(Integer.parseInt(port) <= 0) {
            System.out.println("Invalid port, please pick another one!");
            port = obj.nextLine();
        }

        System.out.println("Please select a command:");
        System.out.println("1: Help");
        System.out.println("2: My IP");
        System.out.println("3: My Port");
        System.out.println("4: Connect To Device");
        System.out.println("5: List");
        System.out.println("6: Terminate a Connection");
        System.out.println("7: Send Message");
        System.out.println("8: Exit");
        x.startServer(Integer.parseInt(port));
        x.clientConnect(x.getIP(), Integer.parseInt(port));

        while (choice != 8) {
            choice = obj.nextInt();

            if (choice == 1) {
                x.help();
            }

            else if(choice == 2) {
                System.out.println("Your IP address is: " + x.getIP());
            }

            else if(choice == 3) {
                System.out.println("Your port is: " + port);
            }

            else if(choice == 4) {
                System.out.println("Please select a connection to connect to. First enter in a IP address.");
                obj.nextLine();
                newIP = obj.nextLine();
                System.out.println("Next, enter in the port.");
                newport = obj.nextInt();

                x.clientConnect(newIP, newport);
                System.out.println("Successfully connected!");
            }
        }
        System.out.println("Thank you for using the app!");

    }
    public void help() {
        System.out.println("My IP: Displays the IP address of the process.\n" +
                "My Port: Displays the port on which this process is listening for incoming connections\n" +
                "Connect to Device: Establishes a new TCP connection to the specified destination at the specified port\n" +
                "List: Displays a numbered list of all the connections that this process is a part of\n" +
                "Terminate a Connection: Terminate the connection listed under the specified number when List is used to display all connections\n" +
                "Send Message: Send a message to the host on the connection that is designated\n" +
                "Exit: Closes all connections and terminates the process\n");
    }

    public void startServer(int port) throws IOException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(port);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    connectedSocket = serverSocket.accept();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }

    public String getIP() {
        InetAddress myIP = null;
        try {
            myIP = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return myIP.getHostAddress();
    }

    public void clientConnect(String ip, int port) throws IOException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    clientSocket = new Socket(ip, port);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }


}