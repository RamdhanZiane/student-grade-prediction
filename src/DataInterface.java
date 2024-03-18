import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface DataInterface extends Remote {
    void train() throws RemoteException;

    String predict(List<String> grades) throws RemoteException;

    void receiveGrades(List<List<String>> grades) throws RemoteException;
}
