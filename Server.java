import java.io.*;
import java.net.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

class DdosCheck {
    public boolean blacklist_check(String ipaddr)
    {
        boolean black_l = false;
        try(BufferedReader br = new BufferedReader(new FileReader("blacklist.txt"))) {
            String line;
            while((line = br.readLine()) != null)
            {
                if (ipaddr.equals(line));
                {
                    black_l = true;
                }
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
         return black_l;
    }
    public String check_ip(String data)
    {   String ic ="";
        List<String> logs = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader("test.txt")))
        {
            String line;
            while((line = br.readLine()) != null)
            {
                String[] parts = line.split("\\|");
                if (parts.length >= 2) {
                    logs.add(parts[0]+"|"+parts[1]+"|");
                } 
                
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        Map<String, Map<Instant, Integer>> ipRequestCounts = new HashMap<>();

        for (String log : logs) {
            String[] parts = log.split("\\|");
            Instant timestamp = Instant.parse(parts[0]);
            String ip = parts[1];

            // Truncate to seconds
            Instant truncatedToSecond = timestamp.truncatedTo(ChronoUnit.SECONDS);

            ipRequestCounts
                .computeIfAbsent(ip, k -> new HashMap<>())
                .merge(truncatedToSecond, 1, Integer::sum);
        }

        // Check if any IP made more than 5 requests in a second
        for (Map.Entry<String, Map<Instant, Integer>> entry : ipRequestCounts.entrySet()) {
            String ip = entry.getKey();
            for (Map.Entry<Instant, Integer> countEntry : entry.getValue().entrySet()) {
                if (countEntry.getValue() > 2) {
                    
                    try(BufferedWriter writer = new BufferedWriter(new FileWriter("blacklist.txt", true)))   
                    {
                        writer.write(ip+"\n");
                    }catch(IOException e)
                    {
                        e.printStackTrace();
                    }
                    ic = ip;
                }
            }
        }
        return ic;
    }
    
}
public class Server {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        System.out.println("Server started, waiting for clients...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                // long startTime = System.nanoTime();
                // System.out.println("client connected time "+startTime);
                // System.out.println("New client connected from " + clientSocket.getInetAddress());
                
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
    }
}

// Handles communication with a single client
class ClientHandler extends Thread {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        String clientMessage="";

        try (
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            clientMessage = in.readLine();
            DdosCheck d  = new DdosCheck();
            String[] parts = clientMessage.split("\\|");
            String detected = d.check_ip(parts[1]);
            //check ip is blacklisted
            if (d.blacklist_check(parts[1]) == true)
            {
                System.out.println("client "+parts[1]+" blocked ");
                socket.close();
            }
            // check Ddos attack
            else if (detected.equals(parts[1]))
            {
                String[] p = clientMessage.split("\\|");
                System.out.println("Client "+p[1]+" aborted");
                socket.close();
                
            }
            //check malicious payload
            else if (parts[4] == "password dump")
            {
                System.out.println("Malicious Client "+parts[1]+"blocked");
                socket.close();
            }
            else
            {
                System.out.println("Client "+parts[1]+"connected");
                try(BufferedWriter writer = new BufferedWriter(new FileWriter("test.txt", true)))   
                {
                    writer.write(clientMessage+"\n");
                }catch(IOException e)
                {
                    e.printStackTrace();
                } 
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                String[] parts = clientMessage.split("\\|");
                System.out.println("Client "+parts[1]+" disconnected.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
