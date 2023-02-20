import static java.lang.String.join;
import static java.util.stream.Collectors.toList;

import com.opencsv.bean.CsvToBeanBuilder;
import entities.Conference;
import entities.Guest;
import entities.Participant;
import entities.Participant.Statistics;
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
import org.apache.commons.lang3.math.NumberUtils;
import utils.CSVUtils;

public class Report {

    private static final String DELIMITER = ";";

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
                    .parse()
                    .stream()
                    .map(g -> {
                        final var nG = new Guest();
                        nG.setName(g.getName());
                        nG.setEmail(g.getEmail().toLowerCase());
                        nG.setDuration(g.getDuration());
                        return nG;
                    })
                    .collect(toList());

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
                .parse()
                .stream()
                .map(p -> {
                    final var nP = new Participant();
                    nP.setName(p.getName());
                    nP.setEmail(p.getEmail().toLowerCase());
                    return nP;
                })
                .collect(toList());

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
        for (Participant part : participants) {
            part.calcStatistics(conferences);
        }
    }

    public void printReport(BufferedWriter writer) throws IOException {
        writer.write(
            join(
                DELIMITER,
                "Имя",
                "Email",
                "Посещал хотя бы 1 раз",
                "Кол-во посещенных конференций",
                "Средний процент посещения"
            )
        );
        for (Conference conference : conferences) {
            writer.write(DELIMITER + conference.getId());
        }

        writer.write("\n");
        writer.write("Дата конференции" + DELIMITER + DELIMITER + DELIMITER + DELIMITER);

        for (Conference conference : conferences) {
            writer.write(DELIMITER + new SimpleDateFormat(CSVUtils.DATE_FRMT).format(conference.getStartDate()));
        }

        writer.write("\n");

        for (Participant part : participants) {
            writer.write(String.format(
                "%s" + DELIMITER + "%s" + DELIMITER + "%d" + DELIMITER + "%d" + DELIMITER + "%.2f",
                part.getName(),
                part.getEmail(),
                Conference.hasAtLEastOneVisit(part.getEmail()) ? 1 : 0,
                part.getCountVisits(),
                part.getAvgPercent()
            ));
            for (Conference conference : conferences) {
                var duration = part.getStatistics(conference.getId())
                    .map(Statistics::getDuration)
                    .orElse(0);
                writer.write(DELIMITER + duration);
            }

            writer.write("\n");
        }

        writer.append("Количество участников" + DELIMITER + DELIMITER + DELIMITER + DELIMITER);
        for (Conference conference : conferences) {
            writer.write(DELIMITER);
            writer.write(String.valueOf(conference.getCountGuests()));
        }

        writer.write("\n");

        writer.append("Процент участников" + DELIMITER + DELIMITER + DELIMITER + DELIMITER);
        for (Conference conference : conferences) {
            writer.write(DELIMITER);
            writer.write(String.format("%.2f", conference.getPercentGuests(participants)));
        }

        writer.write("\n");

        writer.append("Процент постоянных участников" + DELIMITER + DELIMITER + DELIMITER + DELIMITER);
        for (Conference conference : conferences) {
            writer.write(DELIMITER);
            writer.write(String.format("%.2f", conference.getPercentUsualGuests()));
        }
    }
}
