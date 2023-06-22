package Checker;

import Problems.JSOCMSR;
import Problems.Job;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSOCMSRKeyParser {

    public static SolutionKeyJSOCMSR parseJSOCMSRKey_file(String filepath) {
        List<JobStartTime> jobs = new ArrayList<>();
        List<MakespanVariable> mVariables = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                String indicator = (parts[0]);
                if (indicator.equals("s")) {
                    int jobNumber = Integer.parseInt(parts[1]);
                    int startTime = Integer.parseInt(parts[2]);
                    int variableID = Integer.parseInt(parts[3]);
                    jobs.add(new JobStartTime(jobNumber, startTime, variableID));
                } else if (indicator.equals("m")) {
                    int time = Integer.parseInt(parts[1]);
                    int variableID = Integer.parseInt(parts[2]);
                    mVariables.add(new MakespanVariable(variableID, time));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new SolutionKeyJSOCMSR(jobs, mVariables);
    }

}
