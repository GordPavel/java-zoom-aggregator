import java.nio.file.Path;
import java.nio.file.Paths;

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