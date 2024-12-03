import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MasterServerInterface extends Remote {
    void writeFile(String fileName, String data) throws RemoteException;
    String readFile(String fileName) throws RemoteException;
}

