package ProblemParser;

import Problems.JSOCMSR;
import Problems.Job;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSOCMSR_parser {

    public static JSOCMSR parseJSOCMSR_file(String filepath, int numberOfJobs) {
        int numResources = 0;
        List<Job> jobs = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String fline = br.readLine();
            numResources = Integer.parseInt(fline);
            String line;
            while ((line = br.readLine()) != null && numberOfJobs > 0) {
                String[] parts = line.trim().split("\\s+");
                int resUsed = Integer.parseInt(parts[0]);
                int process = Integer.parseInt(parts[1]);
                int preProcess = Integer.parseInt(parts[2]);
                int comNeeded = Integer.parseInt(parts[3]);
                Job toAdd = new Job(resUsed, preProcess, comNeeded+preProcess, process);
                jobs.add(toAdd);
                numberOfJobs--;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSOCMSR(numResources, jobs);
    }

}
