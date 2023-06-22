package Problems;

import java.util.List;

public class JobIntervalPair {

    public Job job;
    public List<TimeInterval> timeInterval;

    int price;

    public JobIntervalPair(Job job, List<TimeInterval> timeInterval, int price) {
        this.job = job;
        this.timeInterval = timeInterval;
        this.price=price;
    }
}
