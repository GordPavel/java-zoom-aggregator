import com.opencsv.bean.CsvToBeanBuilder;
import entities.Conference;
import entities.Guest;
import entities.Participant;
import org.apache.commons.lang3.math.NumberUtils;
import utils.CSVUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Report {
    private final List<Conference> conferences = new ArrayList<>();
    private final List<Participant> participants = new ArrayList<>();

    private Optional<Conference> readConferenceFromFile(Path filePath) {
        try {
            Optional<Conference> conference = new CsvToBeanBuilder<Conference>(new FileReader(filePath.toFile()))
                    .withSkipLines(1)
                    .withType(Conference.class)
                    .withFilter(str -> NumberUtils.isParsable(str[0]) && str.length == 8)
                    .build()
                    .parse()
                    .stream()
                    .findFirst();

            if (conference.isPresent()) {
                List<Guest> guests = new CsvToBeanBuilder<Guest>(new FileReader(filePath.toFile()))
                        .withSkipLines(5)
                        .withType(Guest.class)
                        .build()
                        .parse();

                conference.get().addGuests(guests);
            }

            return conference;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public void readParticipants(Path filePath) {
        try {
            List<Participant> participants = new CsvToBeanBuilder<Participant>(new FileReader(filePath.toFile()))
                    .withType(Participant.class)
                    .build()
                    .parse();

            this.participants.addAll(participants);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void readConferences(Path dirPath) {
        try (Stream<Path> files = Files.walk(dirPath)) {
            files.filter(Files::isRegularFile)
                    .forEach(f -> readConferenceFromFile(f).ifPresent(this.conferences::add));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void calculateStatistics() {
        for (Participant part: participants) {
            part.calcStatistics(conferences);
        }
    }

    public void printReport(BufferedWriter writer) throws IOException {
        writer.write(",");
        for (Conference conference : conferences) {
            writer.write(",");
            writer.write(conference.getId());
            writer.write(" - ");
            writer.write(new SimpleDateFormat(CSVUtils.DATE_FRMT).format(conference.getStartDate()));
        }

        writer.write("\n");

        for (Participant part: participants) {
            writer.write(part.getName());
            writer.write(",");
            writer.write(part.getEmail());
            writer.write(",");

            for (Conference conference : conferences) {
                Participant.Statistics stat = part.getStatistics(conference.getId());
                writer.write(String.valueOf(stat.getDuration()));
                writer.write(",");
            }

            writer.write(String.valueOf(part.getCountVisits()));
            writer.write(" - ");
            writer.write(String.valueOf(part.getAvgPercent()));
            writer.write("\n");
        }

        writer.append(",");
        for (Conference conference : conferences) {
            writer.write(",");
            writer.write(String.valueOf(conference.getCountGuests()));
            writer.write(" - ");
            writer.write(String.valueOf(conference.getPercentGuests()));
        }
    }
}
