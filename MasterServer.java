import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MasterServer implements MasterServerInterface, Serializable {

    private static final long serialVersionUID = 1L;  // Este o bună practică să adaugi un serialVersionUID.
    private FileServerInterface firstReplica;  // Primul server de replicare

    public MasterServer(FileServerInterface firstReplica) {
        this.firstReplica = firstReplica;
    }

    @Override
    public void writeFile(String fileName, String data) throws RemoteException {
        firstReplica.writeFile(fileName, data);
    }

    @Override
    public String readFile(String fileName) throws RemoteException {
        return firstReplica.readFile(fileName);
    }

    public static void main(String[] args) {
        try {
            // Crearea serverelor de replicare
            ReplicaServer server3 = new ReplicaServer("files/server3", null);
            ReplicaServer server2 = new ReplicaServer("files/server2", server3);
            ReplicaServer server1 = new ReplicaServer("files/server1", server2);
	   server1.setActive(false);
		server2.setActive(false);
            // Crearea serverului master
            MasterServer masterServer = new MasterServer(server1);

            // Crearea și înregistrarea registry-ului RMI pe serverul curent
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("MasterServer", masterServer);

            System.out.println("Serverul Master este activ.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

