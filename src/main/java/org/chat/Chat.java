package org.chat;

import org.chat.net.server.ClientHandler;

import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

// java -cp <classpath> <main_class> <arguments>
// 192.168.1.228
public class Chat {

    public static void main(String[] args) throws IOException {
        int port = 5002;
        if(args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        Peer peer = Peer.create(port);
        peer.startServer();

        String opts =
                """
                
                Please select a command:
                1: Help
                2: My IP
                3: My Port
                4: Connect <destination> <port>
                5: List
                6: Terminate a Connection
                7: Send Message
                8: Exit
                
                """;

        String help =
                """
                
                My IP: Displays the IP address of the process.
                My Port: Displays the port on which this process is listening for incoming connections
                Connect to Device: Establishes a new TCP connection to the specified destination at the specified port
                List: Displays a numbered list of all the connections that this process is a part of
                Terminate a Connection: Terminate the connection listed under the specified number when List is used to display all connections
                Send Message: Send a message to the host on the connection that is designated
                Exit: Closes all connections and terminates the process
                
                """;

        Scanner scanner = new Scanner(System.in);

        boolean isRunning = true;

        while(isRunning) {
            System.out.println(opts);
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> System.out.println(help);
                case "2" -> System.out.println("process IP address: " + peer.server().getIP());
                case "3" -> System.out.println("Port: " + peer.server().getPort());
                case "4" -> {
                    System.out.println("Enter <destination> <port> :");
                    String[] parts = scanner.nextLine().split("\\s+");
                    String dest = parts[0];
                    String ip = parts[1];
                    if (Integer.parseInt(ip) == port) {
                        System.out.println("Sorry, you can't do a self-connection.");
                        break;
                    }
                    peer.connect(dest, Integer.parseInt(ip));
                }
                case "5" -> {
                    Set<ClientHandler> connections = peer.getConnections();
                    System.out.println("id: IP address\t\t\tPort No.");
                    int i = 0;
                    for(ClientHandler c : connections) {
                        System.out.println((i+1)+": " + c.getIp() + "\t\t\t" + c.getPort());
                        i++;
                    }
                }
                case "6" -> {
                    System.out.println("Enter in a <connection id> to terminate");
                    int line = scanner.nextInt();
                    peer.terminate(line);
                }
                case "7" -> {
                    System.out.println("Enter <connection.id.> <message>");
                    String line = scanner.nextLine();
                    String[] parts = line.split("\\s", 2);
                    int id = Integer.parseInt(parts[0]);
                    String message = parts[1];
                    if(message.length() > 100) {
                        System.out.println("Unable to send message with length " + message.length());
                    } else {
                        peer.sendMessage(id, message);
                    }
                }
                case "8" -> isRunning = false;
            }
        }
    }
}