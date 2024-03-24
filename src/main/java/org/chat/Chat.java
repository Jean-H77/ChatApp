package org.chat;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Chat {

    public static void main(String[] args) throws IOException {
        int port = 5000;
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
                    String[] split = scanner.nextLine().split("\\s+");
                    String dest = split[0];
                    String ip = split[1];
                    if (Integer.parseInt(ip) == port) {
                        System.out.println("Sorry, you can't do a self-connection.");
                        break;
                    }
                    peer.connect(dest, Integer.parseInt(ip));
                }
                case "5" -> {
                   List<String> connections = peer.getConnectionsList();
                    System.out.println("Connections ---- start");
                    for(int i = 0; i < connections.size(); i++) {
                       System.out.println(i+")" + connections.get(i));
                   }
                    System.out.println("Connections ---- end");
                }
                case "8" -> isRunning = false;
            }
        }
    }
}