package entities;

import com.opencsv.bean.CsvBindByPosition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Participant {
    @CsvBindByPosition(position = 0)
    private String name;

    @CsvBindByPosition(position = 1)
    private String email;

    private final Map<String, Statistics> statistics = new HashMap<>();

    private void clearStatistics() {
        statistics.clear();
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getDuration(Conference conference) {
        Guest guest = conference.getGuests().stream().filter(g -> g.getEmail().equals(this.email)).findFirst().orElse(null);
        if (guest != null) {
            return guest.getDuration();
        } else {
            return 0;
        }
    }

    public void calcStatistics(List<Conference> conferences) {
        this.clearStatistics();
        for (Conference conf : conferences) {
            int duration = this.getDuration(conf);
            Statistics stat = new Statistics(duration, duration * 100.0 / conf.getDuration());
            statistics.put(conf.getId(), stat);
        }
    }

    public double getAvgPercent() {
        return statistics.values().stream().mapToDouble(Statistics::getPercent).average().orElse(0);
    }

    public Statistics getStatistics(String confId) {
        return statistics.get(confId);
    }

    public int getCountVisits() {
        return statistics.size();
    }

    @Override
    public String toString() {
        return "Partricipant{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public static class Statistics {
        private final int duration;
        private final double percent;

        public Statistics(int duration, double percent) {
            this.duration = duration;
            this.percent = percent;
        }

        public int getDuration() {
            return duration;
        }

        public double getPercent() {
            return percent;
        }
    }
}