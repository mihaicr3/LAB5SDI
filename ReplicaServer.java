import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ReplicaServer extends UnicastRemoteObject implements FileServerInterface {
    private FileServerInterface nextServer;  // Serverul următor din lanț
    private String directoryPath;  // Directorul unde fișierele vor fi stocate
    private boolean isActive = true;  // Starea serverului (dacă este activ sau nu)

    public ReplicaServer(String directoryPath, FileServerInterface nextServer) throws RemoteException {
        this.directoryPath = directoryPath;
        this.nextServer = nextServer;

        // Asigură-te că directorul există
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    @Override
    public void writeFile(String fileName, String data) throws RemoteException {
        if (!isActive) {
            System.out.println("Serverul nu este activ. Se trimite cererea la următorul server.");
            if (nextServer != null) {
                nextServer.writeFile(fileName, data);
            }
            return;
        }

        // Scrierea datelor pe disc
        try {
            FileWriter writer = new FileWriter(directoryPath + "/" + fileName);
            writer.write(data);
            writer.close();
            System.out.println("Data written to file on this server: " + fileName);

            // Transmiterea cererii de scriere către următorul server, dacă există
            if (nextServer != null) {
                nextServer.writeFile(fileName, data);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RemoteException("Error writing file: " + fileName, e);
        }
    }

    @Override
    public String readFile(String fileName) throws RemoteException {
        if (!isActive) {
            System.out.println("Serverul nu este activ. Se trimite cererea la următorul server.");
            if (nextServer != null) {
                return nextServer.readFile(fileName);
            } else {
                throw new RemoteException("Niciun server disponibil pentru citire.");
            }
        }

        // Citirea fișierului de pe disc
        try {
            String content = new String(Files.readAllBytes(Paths.get(directoryPath + "/" + fileName)));
            System.out.println("Reading data from file on this server: " + fileName);

            // Dacă acest server este ultimul în lanț, returnează conținutul fișierului
            if (nextServer == null) {
                return content;
            } else {
                // Dacă nu este ultimul server, transmite cererea către următorul server
                return nextServer.readFile(fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RemoteException("Error reading file: " + fileName, e);
        }
    }
}

