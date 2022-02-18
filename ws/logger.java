import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class logger {

    public static void addLog(String username, String logEntry) throws IOException {

        String filePathString = "logs" + File.separator + "logs.txt";
        File f = new File(filePathString);

        final String dir = System.getProperty("user.dir");
        Path p = Paths.get(dir + "/logs/logs.txt");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String s = System.lineSeparator() + dtf.format(now) + " : " + username + " : " + logEntry;

        if(f.exists())
        {
            try {
                Files.write(p, s.getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                System.err.println(e);
            }
        }
        else
        {
            f.getParentFile().mkdirs();
            f.createNewFile();

            try {
                Files.write(p, s.getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
}