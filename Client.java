import java.rmi.Naming;
import java.rmi.RemoteException;

public class Client {
    public static void main(String[] args) {
        try {
            // Conectează-te la MasterServer prin RMI
            MasterServerInterface masterServer = (MasterServerInterface) Naming.lookup("//localhost/MasterServer");
            
            // În acest exemplu, masterServer va coordona scrierea și citirea
            // Însă scrierea și citirea efectivă vor fi gestionate de serverele de replicare.
            
            // Scrierea unui fișier prin MasterServer (va ajunge la primul server de replicare)
            String fileName = "example2.txt";
            String data = "Hello, Distributed File System2!";
            masterServer.writeFile(fileName, data);

            // Citirea unui fișier prin MasterServer (va veni de la ultimul server de replicare)
            String fileContent = masterServer.readFile(fileName);
            System.out.println("File content: " + fileContent);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

