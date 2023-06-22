package Problems;

public class Job {

    public int resourceUsed;
    public int timeToPreEnd;
    public int timeToComEnd;
    public int timeToSecEnd;

    public Job(int resourceUsed, int timeToPreEnd, int timeToComEnd, int timeToSecEnd) {
        this.resourceUsed = resourceUsed;
        this.timeToPreEnd = timeToPreEnd;
        this.timeToComEnd = timeToComEnd;
        this.timeToSecEnd = timeToSecEnd;
    }
}
