import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        Map<String, String> arguments = parseArguments(args);

        Path repsPath = Paths.get(arguments.get("--data"));
        Path partsPath = Paths.get(arguments.get("--participants"));

        Report report = new Report();
        report.readParticipants(partsPath);
        report.readConferences(repsPath);
        report.calculateStatistics();

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
        report.printReport(writer);
        writer.flush();
    }

    public static Map<String, String> parseArguments(String[] args) {
        Map<String, String> arguments = new HashMap<>();
        for (int i = 0; i < args.length;) {
            arguments.put(args[i], args[i + 1]);
            i += 2;
        }
        return arguments;
    }
}