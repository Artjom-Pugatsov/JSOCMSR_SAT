package ProblemToContsrints;

import Constraint.*;
import Problems.JSOCMSR;
import Problems.Job;
import Variable.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JSOCMSREncoder {

    public static CNF_And_Key jsocmsrToCnf(JSOCMSR probInstance) {

        List<String> key = new ArrayList<>();
        int sequentialLength = 0;
        int longestJob = 0;
        List<Job> jobs = probInstance.jobs;

        for (int i = 0; i < jobs.size(); i++) {
            sequentialLength += jobs.get(i).timeToSecEnd;
            if (jobs.get(i).timeToSecEnd > longestJob) {
                longestJob = jobs.get(i).timeToSecEnd;
            }
        }
        //System.out.println(longestJob);
        int maxStartTime = sequentialLength - longestJob;
        //Make a variable for each job and startTime pair
        List<List<Variable>> jobStartsAt = new ArrayList<>();
        int howMany = 0;
        int counter = 0;
        for (Job j: jobs) {
            jobStartsAt.add(new ArrayList<Variable>());
            for (int t = 0; t <= maxStartTime; t++) {
                howMany++;
                Variable var = new Variable();
                key.add("s " + counter + " " + t  + " " + var.id);
                jobStartsAt.get(counter).add(var);
            }
            counter++;
        }
        //System.out.println("There are " + howMany + " variables for job and start time");

        //Make a variable for each job j uses common resource at time t
        List<List<Variable>> jobUsesCommonAtT = new ArrayList<>();
        counter = 0;
        howMany = 0;
        for (Job j: jobs) {
            jobUsesCommonAtT.add(new ArrayList<Variable>());
            for (int t = 0; t <= sequentialLength; t++) {
                jobUsesCommonAtT.get(counter).add(new Variable());
                howMany++;
            }
            counter++;
        }
        //System.out.println("There are " + howMany + " variables for job uses common resource at time t");

        //Make a variable for each job j uses secondary at time t
        List<List<Variable>> jobUsesSecondary = new ArrayList<>();
        counter = 0;
        howMany = 0;
        for (Job j: jobs) {
            jobUsesSecondary.add(new ArrayList<Variable>());
            for (int t = 0; t <= sequentialLength; t++) {
                jobUsesSecondary.get(counter).add(new Variable());
                howMany++;
            }
            counter++;
        }
        //System.out.println("There are " + howMany + " variables for job uses secondary resource at time t");

        List<Variable> isWithinMakespan = new ArrayList<>();
        howMany = 0;
        //Make Max-Sat variables that indicate that are true IFF they are within the makespan
        for (int i = 0; i <= sequentialLength; i++) {
            Variable var = new Variable();
            key.add("m " + i +" "+ var.id);
            isWithinMakespan.add(var);
            howMany++;
        }
        //System.out.println("There are " + howMany + " variables for indicating that time t is within the makespan");
        //Constraints

        //If job starts at i, then secondary is used throughout i, i+1, i+2, ... i+encSec-1
        howMany = 0;
        List<CNFForm> secondaryIsUsed = new ArrayList<>();
        CNFForm buildableCNF = new CNFForm();
        for (int j =0; j < jobs.size(); j++) {
            Job curJob = jobs.get(j);
            for (int t = 0; t <= maxStartTime; t++) {
                for (int curIncrease = 0; curIncrease < curJob.timeToSecEnd; curIncrease++) {
                    howMany++;
                    Constraint implication = new Implication(jobStartsAt.get(j).get(t),
                            jobUsesSecondary.get(j).get(t+curIncrease));
                    secondaryIsUsed.add(implication.toCNFForm());
                }
            }
        }
       // System.out.println("There are " + howMany + " constraints that say that if j starts at i, then resources at i, .. i + makespan are used");
        buildableCNF.addAllCNFs(secondaryIsUsed);
        //Same for common
        List<Constraint> commonIsUsed = new ArrayList<Constraint>();
        for (int j =0; j < jobs.size(); j++) {
            Job curJob = jobs.get(j);
            for (int t = 0; t <= maxStartTime; t++) {
                for (int curIncrease = curJob.timeToPreEnd; curIncrease <= curJob.timeToComEnd; curIncrease++) {
                    Constraint implication = new Implication(jobStartsAt.get(j).get(t), jobUsesCommonAtT.get(j).get(t+curIncrease));
                    commonIsUsed.add(implication);
                }
            }
           // System.out.println("Job " + j);
        }
        //System.out.println("Size of similar for common is: " + commonIsUsed.size() );
        buildableCNF.addAllCNFs(commonIsUsed.stream().map(x -> x.toCNFForm()).collect(Collectors.toList()));

        //Each job has exactly 1 startTime
        for (int j = 0; j < jobs.size(); j++) {
            List<Variable> startTimesOfAJob = jobStartsAt.get(j);
            CNFForm exactlyOne = new ExactlyOneTrue(startTimesOfAJob).toCNFForm();
            buildableCNF.addAnotherCNF(exactlyOne);
        }

        //At each time common resource is used by max 1 job
        for (int t = 0; t <= sequentialLength; t++) {
            List<Variable> jobsUseCommonAtSameTime = new ArrayList<>();
            for (int j = 0; j < jobs.size(); j++) {
                jobsUseCommonAtSameTime.add(jobUsesCommonAtT.get(j).get(t));
            }
            buildableCNF.addAnotherCNF(new AtMostOneTrueMatrixEncoding(jobsUseCommonAtSameTime).toCNFForm());
        }

        //At each time the secondary resource is used by max 1 job that requires this exact resource
        for (int r = 1; r <= probInstance.resourcesNumber; r++) {
            for (int t = 0; t <= sequentialLength; t++) {
                List<Variable> jobsThatUseRAtSameTime = new ArrayList<>();
                for (int j = 0; j < jobs.size(); j++) {
                    if (jobs.get(j).resourceUsed == r) {
                        jobsThatUseRAtSameTime.add(jobUsesSecondary.get(j).get(t));
                    }
                }
                buildableCNF.addAnotherCNF(new AtMostOneTrueMatrixEncoding(jobsThatUseRAtSameTime).toCNFForm());
            }
        }

        //Soft constraints
        //Marker variables for soft constraints. These constraints must hold always
        //If job j uses secondary at t, then insideMakespan(t) is true
        for (int j = 0; j < jobs.size(); j++) {
            for (int t = 0; t <= sequentialLength; t++) {
                buildableCNF.addAnotherCNF(new Implication(jobUsesSecondary.get(j).get(t), isWithinMakespan.get(t)).toCNFForm());
            }
        }

        //If within makespan, then the one smaller is within makespan
        for (int i = 1; i < isWithinMakespan.size(); i++) {
            buildableCNF.addAnotherCNF(new Implication(isWithinMakespan.get(i), isWithinMakespan.get(i-1)).toCNFForm());
        }
        //The only actual soft constraint is that as many isWithinMakespan variables are false as possible
        for (Variable variable : isWithinMakespan) {
            CNF_Clause softClause = new CNF_Clause(new ArrayList<>());
            softClause.vars.add(new VariableInConstraint(variable, false));
            softClause.addWeight(1);
            buildableCNF.addAND(softClause);
        }

        return new CNF_And_Key(buildableCNF, key);

    }

}
