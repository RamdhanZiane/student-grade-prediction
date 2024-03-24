package com.gradeprediction.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gradeprediction.common.DataInterface;

public class DataClient {

    // read csv from client - send it to the server - train and get association
    // rules and store them
    // send grades and predict

    public static List<List<String>> readCSVFile(String filename) {
        List<List<String>> studentGradesList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(","); // Split by whitespace
                if (parts.length == 4) { // Assuming each line has exactly four parts: E0, F1, A2, E
                    List<String> grades = Arrays.asList(parts);
                    studentGradesList.add(grades);
                } else {
                    System.err.println("Invalid format in CSV file. Skipping line: " + line);
                }
            }
            reader.close();
        } catch (Exception e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            e.printStackTrace();
        }
        return studentGradesList;
    }

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Usage: java DataClient <filename>");
            System.exit(1);
        }
        String filename = args[0];
        try {
            DataInterface stub = (DataInterface) Naming.lookup("rmi://localhost:50000/DataServer");

            // Read grades from CSV file
            List<List<String>> studentGrades = readCSVFile("../" + filename);

            // Call the train function on the server
            stub.receiveGrades(studentGrades, "etabk");
            // System.out.println("Grades received: \n" + studentGrades);

            // Sample grades data
            // List<String> grades = new ArrayList<>();
            // grades.add("15.5");
            // grades.add("15.5");
            // grades.add("15.5");

            // Send the grades to the server
            // String prediction = stub.predict(grades);

            // System.out.println(prediction);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

}
