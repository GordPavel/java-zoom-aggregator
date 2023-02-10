import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.CsvToBeanFilter;
import com.opencsv.exceptions.CsvException;
import entities.Conference;
import entities.Guest;
import entities.Participant;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Path repsPath = null;
        if (args[0].equals("--data")) {
            repsPath = Paths.get(args[1]);
        } else {
            System.exit(0);
        }

        Path partsPath = null;
        if (args[2].equals("--participants")) {
            partsPath = Paths.get(args[3]);
        } else {
            System.exit(0);
        }

        Report report = new Report();
        report.readParticipants(partsPath);
        report.readConferences(repsPath);

        report.printReport();
    }
}