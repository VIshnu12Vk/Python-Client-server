import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
public class ClientSimulator {
    public String type;
    public static void main(String[] args) {
        String server_ip ="192.168.1.1";
        int numberOfClientsNormal = 3;
        int numberOfClientMalicious = 5;
        String[] ips ={"192.168.56.101","10.0.12.45","172.16.88.200","192.168.1.150","10.1.75.90"};
        System.out.println("Enter no of Normal client :");
        Scanner s1 = new Scanner(System.in);
        numberOfClientsNormal = s1.nextInt();
        //reading no malicious client
        System.out.println("Enter no of Malicious client :");
        Scanner s2 = new Scanner(System.in);
        numberOfClientMalicious = s2.nextInt();
        //
        String[] normal_mssage = new String[ numberOfClientsNormal];
        String[] normal_message_type = new String[numberOfClientsNormal];
        if (numberOfClientsNormal > 0)
        {

            for( int i =0; i < numberOfClientsNormal; i++)
            {
                System.out.println("enter client "+i+" message");
                Scanner s = new Scanner(System.in);
                normal_mssage[i] = s.nextLine();
                System.out.println("enter its message type :");
                Scanner ss = new Scanner(System.in);
                normal_message_type[i] = ss.nextLine();
            }
            for (int i = 0; i < numberOfClientsNormal; i++) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                String msg = normal_mssage[i];
                String msg_type = normal_message_type[i];
                int randomIndex = (int) (Math.random()*5);
                String ipaddr = ips[randomIndex];
                new Thread(() -> Client.main(new String[] {ipaddr,server_ip,msg_type,msg})).start();
            }

        }
        //malicious request
        //ddos
        for (int j = 0; j < numberOfClientMalicious; j++) {
            int randomIndex = (int) (Math.random()*5);
            String ipaddr = ips[randomIndex];
            new Thread(() -> Client.main(new String[] {ipaddr,server_ip,"test","password dump"})).start();
        }
        
    }
}
