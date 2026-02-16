import java.io.*;

public class CompilationEngine {

    private BufferedWriter writer;
    private JackTokenizer tokenizer;

    public CompilationEngine(JackTokenizer tokenizer, String outputFile) throws IOException {
        this.tokenizer = tokenizer;
        writer = new BufferedWriter(new FileWriter(outputFile));
    }

    public void compileClass() throws IOException {

        writer.write("<class>\n");

        while (tokenizer.hasMoreTokens()) {
            String line = tokenizer.currentLine();
            if (line != null && !line.isEmpty()) {
                writer.write("  <line> " + escapeXML(line) + " </line>\n");
            }
            tokenizer.advance();
        }

        writer.write("</class>\n");
        writer.close();
    }

    private String escapeXML(String input) {
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
    }
}
