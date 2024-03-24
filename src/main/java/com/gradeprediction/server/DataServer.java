package com.gradeprediction.server;

import java.io.FileWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.gradeprediction.common.DataExchangeCode;
import com.gradeprediction.common.DataInterface;

public class DataServer extends UnicastRemoteObject implements DataInterface {

    private List<List<String>> allGrades = new ArrayList<>();
    private AtomicInteger clientsCount = new AtomicInteger(0);
    private final int expectedClients = 2;
    private HashSet<String> schoolSet = new HashSet<String>();
    private boolean readyToPredict = false;
    private ArrayList<ServerObserver> observers = new ArrayList<ServerObserver>();

    public static int port = 50000;

    public DataServer() throws RemoteException {
        super();
    }

    // Receive grades from client
    @Override
    public synchronized DataExchangeCode receiveGrades(List<List<String>> grades, String school)
            throws RemoteException {
        if (this.schoolSet.contains(school))
            return DataExchangeCode.DATA_ALREADY_RECEIVED;

        for (List<String> studentGrades : grades) {
            if (studentGrades.size() != 4) {
                return DataExchangeCode.NOT_VALID_DATA;
            }
        }
        this.schoolSet.add(school);
        allGrades.addAll(grades);
        int count = clientsCount.incrementAndGet();
        System.out.println("received partial dataset from: " + school);

        for (ServerObserver observer : observers) {
            observer.updateProgressBar((double) count / expectedClients, school);
        }
        return DataExchangeCode.GRADES_RECEIVED;
    }

    // Train model
    public String train() throws RemoteException {
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
        this.readyToPredict = true;
        return "model trained";
    }

    // Predict grade
    @Override
    public String predict(List<String> grades) throws RemoteException {
        if (!readyToPredict) {
            return null;
        }
        if (grades.size() != 4) {
            // return "Invalid data";
        }
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

    public void addObserver(ServerApp serverApp) {
        observers.add(serverApp);
    }

}
