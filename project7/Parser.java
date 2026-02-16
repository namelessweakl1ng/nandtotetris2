import java.io.*;
import java.util.*;

public class Parser {

    private List<String> commands = new ArrayList<>();
    private int currentIndex = -1;
    private String currentCommand;

    public Parser(String fileName) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(fileName));

        String line;
        while ((line = br.readLine()) != null) {

            line = line.split("//")[0].trim();

            if (!line.isEmpty()) {
                commands.add(line);
            }
        }

        br.close();
    }

    public boolean hasMoreCommands() {
        return currentIndex + 1 < commands.size();
    }

    public void advance() {
        currentIndex++;
        currentCommand = commands.get(currentIndex);
    }

    public String commandType() {
        if (currentCommand.startsWith("push"))
            return "C_PUSH";
        if (currentCommand.startsWith("pop"))
            return "C_POP";
        return "C_ARITHMETIC";
    }

    public String arg1() {
        String[] parts = currentCommand.split("\\s+");
        if (commandType().equals("C_ARITHMETIC"))
            return parts[0];
        return parts[1];
    }

    public int arg2() {
        String[] parts = currentCommand.split("\\s+");
        return Integer.parseInt(parts[2]);
    }
}
