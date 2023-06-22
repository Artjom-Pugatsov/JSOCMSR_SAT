package Checker;

import Problems.JSOCMSR;
import Problems.Job;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class JSOCMSR_Checker {


    public static boolean check(JSOCMSR problemInstance, SolutionKeyJSOCMSR key, SAT_Answer varAss) {

        //Map from key to the instance
        List<FullJobInfo> fullJobInfos = new ArrayList<>();
        for (JobStartTime keyVal: key.startTimes) {
            int varID = keyVal.variableID;
            boolean assignment = varAss.getAssignment(varID);
            if (assignment) {
                Job job = problemInstance.getJob(keyVal.job);
                int sTime = (keyVal.startTime);
                fullJobInfos.add(new FullJobInfo(job.resourceUsed, sTime, sTime+job.timeToSecEnd, sTime+ job.timeToPreEnd, sTime+job.timeToComEnd));
            }
        }
        int makeSpan = 0;
        for (MakespanVariable mVar: key.makespanVars) {
            int varID = mVar.variableID;
            boolean assignment = varAss.getAssignment(varID);
            if (assignment) {
                if (mVar.time > makeSpan) {
                    makeSpan = mVar.time+1;
                }
            }
        }

        for (int i = 1; i<= problemInstance.resourcesNumber; i++) {
            int finalI = i;
            List<FullJobInfo> useSameRes = new ArrayList<>(fullJobInfos.stream().filter(x -> x.resource == finalI).toList());
            useSameRes.sort(Comparator.comparingInt(p -> p.startTime));
            for (int j = 1; j < useSameRes.size(); j++) {
                myAssert(useSameRes.get(j-1).endTime <= useSameRes.get(j).startTime, "Jobs use the secondary resource at the same time");
            }
        }
        fullJobInfos.sort(Comparator.comparingInt(p -> p.startCom));
        for (int j = 1; j < fullJobInfos.size(); j++) {
            myAssert(fullJobInfos.get(j-1).endCom <= fullJobInfos.get(j).startCom, "Jobs use common resource at the same time");
        }
        fullJobInfos.sort(Comparator.comparingInt(p -> p.endTime));
        int lastEndTime = fullJobInfos.get(fullJobInfos.size()-1).endTime;
        myAssert(makeSpan == lastEndTime, "Reported makespan does not match actual makespan");
        System.out.println("All good!");
        return true;
    }

    private static boolean myAssert(boolean to, String message) {
        if (!to) {
            System.out.println(message);
            throw new RuntimeException("The solution is invalid. Reported problem: " + message);
        }
        return false;
    }

    private static class FullJobInfo {
        int resource;
        int startTime;
        int endTime;
        int startCom;
        int endCom;

        public FullJobInfo(int resource, int startTime, int endTime, int startCom, int endCom) {
            this.resource = resource;
            this.startTime = startTime;
            this.endTime = endTime;
            this.startCom = startCom;
            this.endCom = endCom;
        }
    }

}
