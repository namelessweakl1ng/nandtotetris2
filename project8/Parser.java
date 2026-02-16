import java.io.*;
import java.util.*;

public class Parser {

    private List<String> commands = new ArrayList<>();
    private int index = -1;
    private String current;

    public Parser(File file) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.split("//")[0].trim();
            if (!line.isEmpty()) commands.add(line);
        }
        reader.close();
    }

    public boolean hasMoreCommands() {
        return index + 1 < commands.size();
    }

    public void advance() {
        index++;
        current = commands.get(index);
    }

    public String commandType() {
        if (current.startsWith("push")) return "C_PUSH";
        if (current.startsWith("pop")) return "C_POP";
        if (current.startsWith("label")) return "C_LABEL";
        if (current.startsWith("goto")) return "C_GOTO";
        if (current.startsWith("if-goto")) return "C_IF";
        if (current.startsWith("function")) return "C_FUNCTION";
        if (current.startsWith("call")) return "C_CALL";
        if (current.startsWith("return")) return "C_RETURN";
        return "C_ARITHMETIC";
    }

    public String arg1() {
        if (commandType().equals("C_ARITHMETIC"))
            return current;
        return current.split("\\s+")[1];
    }

    public int arg2() {
        return Integer.parseInt(current.split("\\s+")[2]);
    }
}
