import com.opencsv.bean.CsvToBeanBuilder;
import entities.Conference;
import entities.Guest;
import entities.Participant;
import org.apache.commons.lang3.math.NumberUtils;
import utils.CSVUtils;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
        writer.write("Имя,Email,Кол-во посещенных конференций,Средний процент посещения");
        for (Conference conference : conferences) {
            writer.write("," + conference.getId());
        }

        writer.write("\n");
        writer.write("Дата конференции,,,");

        for (Conference conference : conferences) {
            writer.write("," + new SimpleDateFormat(CSVUtils.DATE_FRMT).format(conference.getStartDate()));
        }

        writer.write("\n");

        for (Participant part : participants) {
            writer.write(part.getName() + "," +
                    part.getEmail() + "," +
                    part.getCountVisits() + "," +
                    part.getAvgPercent());

            for (Conference conference : conferences) {
                Participant.Statistics stat = part.getStatistics(conference.getId());
                writer.write("," + stat.getDuration());
            }

            writer.write("\n");
        }

        writer.append("Количество участников,,,,");
        for (Conference conference : conferences) {
            writer.write(String.valueOf(conference.getCountGuests()));
        }

        writer.write("\n");

        writer.append("Процент участников,,,,");
        for (Conference conference : conferences) {
            writer.write(String.valueOf(conference.getPercentGuests()));
        }
    }
}
