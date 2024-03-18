import java.io.BufferedReader;
import java.io.FileReader;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DataClient {

    private static final String[][] GRADE_CATEGORIES = {
        // G -> Avertissement
        // F -> Réprimande
        // E -> Acceptable
        // D -> Assez bien
        // C -> Bien
        // B -> Très bien
        // A -> Excellent
        {"G1", "F1", "E1", "D1", "C1", "B1", "A1"},
        {"G2", "F2", "E2", "D2", "C2", "B2", "A2"},
        {"G3", "F3", "E3", "D3", "C3", "B3", "A3"},
        {"G4", "F4", "E4", "D4", "C4", "B4", "A4"}
        
    };

    // Handling rows that have missing values
    private static boolean hasMissingValues(String[] parts) {
        for (String part : parts) {
            if (part.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    // Convert grade to category
    private static String convertToCategory(double grade, int column) {
        int index;
        if (grade >= 18) {
            index = 6;
        } else if (grade >= 16) {
            index = 5;
        } else if (grade >= 14) {
            index = 4;
        } else if (grade >= 12) {
            index = 3;
        } else if (grade >= 10) {
            index = 2;
        } else if (grade >= 8) {
            index = 1;
        } else {
            index = 0;
        }

        if (index < GRADE_CATEGORIES[column].length) {
            return GRADE_CATEGORIES[column][index];
        } else {
            return "Invalid";
        }
    }

    // Categorisation
    public static List<List<String>> readCSVFile(String filename) {
        List<List<String>> studentGradesList = new ArrayList<>();
        boolean firstLine = true;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip the first line
                }
                String[] parts = line.split(","); // Split by whitespace
                if (parts.length == 4 && !hasMissingValues(parts)) {
                    List<String> grades = new ArrayList<>();
                    for (int i = 0; i < parts.length; i++) {
                        String category = convertToCategory(Double.parseDouble(parts[i]), i);
                        grades.add(category);
                    }
                    studentGradesList.add(grades);
                } else {
                    System.err.println("Invalid format or missing values in CSV file. Skipping line: " + line);
                }
            }
            reader.close();
        } catch (Exception e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            e.printStackTrace();
        }
        return studentGradesList;
    }

    // One hot encoding
    public static List<List<Integer>> OneHotEncode(List<List<String>> studentGradesList) {
        List<List<Integer>> oneHotEncodedGrades = new ArrayList<>();
    
        for (List<String> studentGrades : studentGradesList) {
            List<Integer> oneHotEncodedGrade = new ArrayList<>();
            for (String grade : studentGrades) {
                for (String[] category : GRADE_CATEGORIES) {
                    if (Arrays.asList(category).contains(grade)) {
                        for (String value : category) {
                            oneHotEncodedGrade.add(grade.equals(value) ? 1 : 0);
                        }
                        break; // Move to the next column
                    }
                }
            }
            oneHotEncodedGrades.add(oneHotEncodedGrade);
        }
        return oneHotEncodedGrades;
    }

    public static void main(String[] args) {
        
        if (args.length != 1) {
            System.out.println("Usage: java DataClient <filename>");
            System.exit(1);
        }
        String filename = args[0];
        try {
            // Look up for the server
            // DataInterface stub = (DataInterface) Naming.lookup("rmi://localhost:50000/DataServer");

            // ==================================== Train ====================================
            // Categorisation
            List<List<String>> studentGrades = readCSVFile("../"+filename);
            System.out.println("Grades received: \n" + studentGrades);

            // One hot encoding
            // List<List<Integer>> oneHotEncodedGrades = OneHotEncode(studentGrades);
            // System.out.println("Grades received: \n" + oneHotEncodedGrades);

            // Send data to the server
            // stub.receiveGrades(studentGrades);

            // ==================================== Predict ====================================
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
