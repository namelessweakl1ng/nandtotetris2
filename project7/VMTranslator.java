import java.io.File;

public class VMTranslator {

    public static void main(String[] args) throws Exception {

        if (args.length != 1) {
            System.out.println("Usage: VMTranslator <file.vm>");
            return;
        }

        String inputFile = args[0];
        String outputFile = inputFile.replace(".vm", ".asm");

        Parser parser = new Parser(inputFile);
        CodeWriter writer = new CodeWriter(outputFile);

        while (parser.hasMoreCommands()) {
            parser.advance();

            String type = parser.commandType();

            if (type.equals("C_ARITHMETIC")) {
                writer.writeArithmetic(parser.arg1());
            } 
            else if (type.equals("C_PUSH") || type.equals("C_POP")) {
                writer.writePushPop(type, parser.arg1(), parser.arg2());
            }
        }

        writer.close();
    }
}
