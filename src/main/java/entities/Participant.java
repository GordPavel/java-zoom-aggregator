package entities;

import static java.util.stream.Collectors.toList;

import com.opencsv.bean.CsvBindByPosition;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Participant {

    @CsvBindByPosition(position = 0)
    private String name;

    @CsvBindByPosition(position = 1)
    private String email;

    private final Map<String, Statistics> statistics = new HashMap<>();

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Optional<Integer> getDuration(Conference conference) {
        return conference
            .getGuests()
            .stream()
            .filter(g -> g.getEmail().equals(this.email))
            .findFirst()
            .map(Guest::getDuration);
    }

    public void calcStatistics(List<Conference> conferences) {
        for (Conference conf : conferences) {
            getDuration(conf).ifPresent(duration -> {
                Statistics stat = new Statistics(duration, duration * 100.0 / conf.getDuration());
                Conference.addUsuallyVisits(conf.getGuests().stream().map(Guest::getEmail).collect(toList()));
                statistics.put(conf.getId(), stat);
            });
        }
    }

    public double getAvgPercent() {
        return statistics.values().stream().mapToDouble(Statistics::getPercent).average().orElse(0);
    }

    public Optional<Statistics> getStatistics(String confId) {
        return Optional.ofNullable(statistics.get(confId));
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