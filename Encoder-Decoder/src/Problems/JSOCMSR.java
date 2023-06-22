package Problems;

import java.util.List;

public class JSOCMSR {

    public int resourcesNumber;
    public List<Job> jobs;

    public JSOCMSR(int resourcesNumber, List<Job> jobs) {
        this.resourcesNumber = resourcesNumber;
        this.jobs = jobs;
    }

    public Job getJob(int jobID) {
        return jobs.get(jobID);
    }

    public void scale(int factor) {
        for (Job j: jobs) {
            j.timeToSecEnd = j.timeToSecEnd/factor;
            j.timeToComEnd = j.timeToComEnd/factor;
            j.timeToPreEnd = j.timeToPreEnd/factor;
        }
    }

    @Override
    public String toString() {
        return "JSOCMSR{" +
                "resourcesNumber=" + resourcesNumber +
                ", jobs=" + jobs +
                '}';
    }
}


