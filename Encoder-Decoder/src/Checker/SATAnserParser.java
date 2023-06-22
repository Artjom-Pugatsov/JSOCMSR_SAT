package Checker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SATAnserParser {

    public static SAT_Answer parseSAT_Answer(String filepath) {
        List<Variable_Assignment> assignments = new ArrayList<>();
        String test = "v -1 2 -3 4 ";
        String[] parts2 = test.trim().split("\\s+");
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                for (int k = 0; k < parts.length; k++) {
                    parts[k] = parts[k].replaceAll("\0+", "");
                }
                String indicator = (parts[0]);
                if (indicator.equals("v")) {
                    for (int i = 1; i < parts.length; i++) {
                        int asInt = Integer.parseInt(parts[i]);
                        int varId = Math.abs(asInt);
                        if (asInt > 0) {
                            assignments.add(new Variable_Assignment(varId, true));
                        } else {
                            assignments.add(new Variable_Assignment(varId, false));
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new SAT_Answer(assignments);
    }

}
