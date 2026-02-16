import java.io.File;

public class JackAnalyzer {

    public static void main(String[] args) throws Exception {

        if (args.length != 1) {
            System.out.println("Usage: JackAnalyzer <file or directory>");
            return;
        }

        File input = new File(args[0]);

        if (input.isDirectory()) {
            File[] files = input.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".jack")) {
                        processFile(file);
                    }
                }
            }
        } else if (input.getName().endsWith(".jack")) {
            processFile(input);
        }
    }

    private static void processFile(File inputFile) throws Exception {

        String outputPath = inputFile.getAbsolutePath()
                .replace(".jack", ".xml");

        JackTokenizer tokenizer = new JackTokenizer(inputFile);
        CompilationEngine engine =
                new CompilationEngine(tokenizer, outputPath);

        engine.compileClass();
    }
}
