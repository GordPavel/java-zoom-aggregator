package entities;

import com.opencsv.bean.CsvBindByPosition;

import java.util.ArrayList;
import java.util.List;

public class Participant {
    @CsvBindByPosition(position = 0)
    private String name;

    @CsvBindByPosition(position = 1)
    private String email;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    List<Integer> durations = new ArrayList<>();

    List<Double> percents = new ArrayList<>();

    private void clearStatistics() {
        this.durations.clear();
        this.percents.clear();
    }

    public int getDuration(Conference conference) {
        Guest guest = conference.getGuests().stream().filter(g -> g.getEmail().equals(this.email)).findFirst().orElse(null);
        if (guest != null) {
            return guest.getDuration();
        } else {
            return 0;
        }
    }

    public void checkConferences(List<Conference> conferences) {
        this.clearStatistics();
        for (Conference conf: conferences) {
            int duration = this.getDuration(conf);
            durations.add(duration);
            percents.add(duration * 100.0 / conf.getDuration());
        }
    }

    public double getAvgPercent() {
        return percents.stream().mapToDouble(d -> d).average().orElse(0);
    }

    public List<Integer> getDurations() {
        return durations;
    }

    public int getCountVisits() {
        return durations.size();
    }

    @Override
    public String toString() {
        return "Partricipant{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}