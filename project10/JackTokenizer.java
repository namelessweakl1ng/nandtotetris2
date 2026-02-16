import java.io.*;
import java.util.*;

public class JackTokenizer {

    private List<String> lines = new ArrayList<>();
    private int index = 0;

    public JackTokenizer(File inputFile) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String line;

        while ((line = reader.readLine()) != null) {
            lines.add(line.trim());
        }
        reader.close();
    }

    public boolean hasMoreTokens() {
        return index < lines.size();
    }

    public void advance() {
        index++;
    }

    public String currentLine() {
        if (index < lines.size())
            return lines.get(index);
        return null;
    }
}
