import java.io.*;

public class VMTranslator {

    public static void main(String[] args) throws Exception {

        if (args.length != 1) {
            System.out.println("Usage: VMTranslator <file.vm | directory>");
            return;
        }

        File input = new File(args[0]);
        File output;

        if (input.isDirectory()) {
            output = new File(input, input.getName() + ".asm");
        } else {
            output = new File(input.getParent(),
                    input.getName().replace(".vm", ".asm"));
        }

        CodeWriter writer = new CodeWriter(output);

        if (input.isDirectory()) {
            writer.writeInit(); // bootstrap required
            for (File file : input.listFiles()) {
                if (file.getName().endsWith(".vm")) {
                    processFile(file, writer);
                }
            }
        } else {
            processFile(input, writer);
        }

        writer.close();
    }

    private static void processFile(File file, CodeWriter writer) throws Exception {
        Parser parser = new Parser(file);
        writer.setFileName(file.getName().replace(".vm", ""));

        while (parser.hasMoreCommands()) {
            parser.advance();
            String type = parser.commandType();

            switch (type) {
                case "C_ARITHMETIC":
                    writer.writeArithmetic(parser.arg1());
                    break;
                case "C_PUSH":
                case "C_POP":
                    writer.writePushPop(type, parser.arg1(), parser.arg2());
                    break;
                case "C_LABEL":
                    writer.writeLabel(parser.arg1());
                    break;
                case "C_GOTO":
                    writer.writeGoto(parser.arg1());
                    break;
                case "C_IF":
                    writer.writeIf(parser.arg1());
                    break;
                case "C_FUNCTION":
                    writer.writeFunction(parser.arg1(), parser.arg2());
                    break;
                case "C_CALL":
                    writer.writeCall(parser.arg1(), parser.arg2());
                    break;
                case "C_RETURN":
                    writer.writeReturn();
                    break;
            }
        }
    }
}
