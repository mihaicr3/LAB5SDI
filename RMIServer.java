import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServer {
    public static void main(String[] args) {
        try {
            // Crearea serverelor de replicare
            FileServerInterface replica3 = new ReplicaServer(null);
            FileServerInterface replica2 = new ReplicaServer(replica3);
            FileServerInterface replica1 = new ReplicaServer(replica2);

            // Crearea serverului master
            MasterServer masterServer = new MasterServer(replica1);

            // Înregistrarea serverelor în registry
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("MasterServer", masterServer);

            System.out.println("Master server is ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

