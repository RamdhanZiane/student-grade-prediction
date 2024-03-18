import java.io.FileWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DataServer extends UnicastRemoteObject implements DataInterface {

    private List<List<String>> allGrades = new ArrayList<>();
    private AtomicInteger clientsCount = new AtomicInteger(0);
    private final int expectedClients = 2;

    public DataServer() throws RemoteException {
        super();
    }

    // Receive grades from client
    @Override
    public synchronized void receiveGrades(List<List<String>> grades) throws RemoteException {
        allGrades.addAll(grades);
        int count = clientsCount.incrementAndGet();
        if (count == expectedClients) {
            // All clients have sent their files, proceed with training
            train();
        }
    }

    // Train model
    @Override
    public void train() throws RemoteException {
        StringBuilder response = new StringBuilder();
        for (List<String> studentGrades : allGrades) {
            for (String grade : studentGrades) {
                response.append(grade).append(",");
            }
            response.append("\n");
        }
        try {
            FileWriter writer = new FileWriter("../train_result.csv");
            writer.write(response.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Error saving train result to CSV file.");
        }
        System.out.println("Grades received and stored in train_result.csv");
        System.out.println("Training model with received grades...");
        // Additional logic for training the model can be added here
    }

    // Predict grade
    @Override
    public String predict(List<String> grades) throws RemoteException {
        double average = calculateAverage(grades);
        return "Predicted grade: " + average;
    }

    private double calculateAverage(List<String> grades) {
        double sum = 0;
        for (String grade : grades) {
            sum += Double.parseDouble(grade);
        }
        return sum / grades.size();
    }

    public static void main(String[] args) {
        try {
            java.rmi.registry.LocateRegistry.createRegistry(50000);
            java.rmi.Naming.rebind("rmi://localhost:50000/DataServer", new DataServer());
            System.out.println("Server started...");

        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
