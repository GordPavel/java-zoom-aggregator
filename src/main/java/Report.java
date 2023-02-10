import com.opencsv.bean.CsvToBeanBuilder;
import entities.Conference;
import entities.Guest;
import entities.Participant;
import org.apache.commons.lang3.math.NumberUtils;
import utils.CSVUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Report {
    private final List<Conference> conferences = new ArrayList<>();
    private final List<Participant> participants = new ArrayList<>();

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
            files.filter(Files::isRegularFile).forEach(this::readConferenceFromFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readConferenceFromFile(Path filePath) {
        try {
            List<Conference> conferences = new CsvToBeanBuilder<Conference>(new FileReader(filePath.toFile()))
                    .withSkipLines(1)
                    .withType(Conference.class)
                    .withFilter(str -> NumberUtils.isParsable(str[0]) && str.length == 8)
                    .build()
                    .parse();

            List<Guest> guests = new CsvToBeanBuilder<Guest>(new FileReader(filePath.toFile()))
                    .withSkipLines(5)
                    .withType(Guest.class)
                    .build()
                    .parse();

            conferences.get(0).addGuests(guests);
            this.conferences.addAll(conferences);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getReport() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(",");
        for (Conference conference : conferences) {
            stringBuilder
                    .append(",")
                    .append(conference.getId())
                    .append(" - ")
                    .append(new SimpleDateFormat(CSVUtils.DATE_FRMT).format(conference.getStartDate()));
        }

        stringBuilder.append("\n");

        for (Participant part: participants) {
            stringBuilder
                    .append(part.getName())
                    .append(",")
                    .append(part.getEmail())
                    .append(",");

            part.calcStatistics(conferences);
            for (Conference conference : conferences) {
                Participant.Statistics stat = part.getStatistics(conference.getId());
                stringBuilder.append(stat.getDuration()).append(",");
            }

            stringBuilder.append(part.getCountVisits()).append(" - ");
            stringBuilder.append(part.getAvgPercent());
            stringBuilder.append("\n");
        }

        stringBuilder.append(",");
        for (Conference conference : conferences) {
            stringBuilder
                    .append(",")
                    .append(conference.getCountGuests())
                    .append(" - ")
                    .append(conference.getPercentGuests());
        }

        return stringBuilder.toString();
    }

    public void printReport() {
        System.out.println(getReport());
    }
}
