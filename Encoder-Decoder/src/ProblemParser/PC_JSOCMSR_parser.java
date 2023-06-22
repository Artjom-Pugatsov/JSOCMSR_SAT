package ProblemParser;

import Problems.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PC_JSOCMSR_parser {


    public static PC_JSOCMSR parsePC_JSOCMSR_file(String filepath) {
        int numResources = 0;
        int numJobs = 0;
        List<JobIntervalPair> jobs = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String[] parts = br.readLine().trim().split("\\s+");
            numJobs = Integer.parseInt(parts[0]);
            numResources = Integer.parseInt(parts[1]);
            String line;
            while ((line = br.readLine()) != null && numJobs > 0) {
                parts = line.trim().split("\\s+");
                int resUsed = Integer.parseInt(parts[0]);
                int process = Integer.parseInt(parts[1]);
                int preProcess = Integer.parseInt(parts[2]);
                int comNeeded = Integer.parseInt(parts[3]);
                int price = Integer.parseInt(parts[4]);
                int numWindows = Integer.parseInt(parts[5]);
                List<TimeInterval> intervals = new ArrayList<>();
                for (int i = 0; i < (numWindows *2); i += 2 ) {
                    int startTime = Integer.parseInt(parts[5 + i]);
                    int endTime = Integer.parseInt(parts[6 + i]);
                    intervals.add(new TimeInterval(startTime, endTime));
                }
                Job toAdd = new Job(resUsed, preProcess, comNeeded+preProcess, process);
                jobs.add(new JobIntervalPair(toAdd, intervals, price));
                numJobs--;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new PC_JSOCMSR(numResources, jobs);
    }

}
