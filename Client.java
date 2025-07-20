import java.io.*;
import java.net.*;
import java.time.Instant;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()))
        ) {
            // // String message = "Hello from client " + Thread.currentThread().getId();
            // System.out.println("Client "+ Thread.currentThread().getId()+" enter message :");
            // Scanner s = new Scanner(System.in);
            // String msg =s.nextLine();
            //[timestamp]|[source_ip]|[destination_ip]|[message_type]|[payload]
            String isoInstant = Instant.now().toString();
            if ((args[0] != null) && (args[1] != null) && (args[2] != null) && (args[2] != null))
            {
                out.println(isoInstant+"|"+args[0]+"|"+args[1]+"|"+args[2]+"|"+args[3]);
            }
            // out.println("this is a "+args[0]+" client message :"+args[1]);
            String response = in.readLine();
            // System.out.println("Server replied: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
