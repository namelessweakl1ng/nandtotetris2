import java.io.*;

public class CodeWriter {

    private BufferedWriter writer;
    private int labelCounter = 0;

    public CodeWriter(String fileName) throws Exception {
        writer = new BufferedWriter(new FileWriter(fileName));
    }

    public void close() throws Exception {
        writer.close();
    }

    private void writeLine(String s) throws Exception {
        writer.write(s);
        writer.newLine();
    }

    public void writeArithmetic(String command) throws Exception {

        switch (command) {

            case "add":
                binaryOp("M=M+D");
                break;

            case "sub":
                binaryOp("M=M-D");
                break;

            case "and":
                binaryOp("M=M&D");
                break;

            case "or":
                binaryOp("M=M|D");
                break;

            case "neg":
                writeLine("@SP");
                writeLine("A=M-1");
                writeLine("M=-M");
                break;

            case "not":
                writeLine("@SP");
                writeLine("A=M-1");
                writeLine("M=!M");
                break;

            case "eq":
            case "gt":
            case "lt":
                comparison(command);
                break;
        }
    }

    private void binaryOp(String op) throws Exception {
        writeLine("@SP");
        writeLine("AM=M-1");
        writeLine("D=M");
        writeLine("A=A-1");
        writeLine(op);
    }

    private void comparison(String cmd) throws Exception {

        String trueLabel = "TRUE" + labelCounter;
        String endLabel = "END" + labelCounter;
        labelCounter++;

        writeLine("@SP");
        writeLine("AM=M-1");
        writeLine("D=M");
        writeLine("A=A-1");
        writeLine("D=M-D");

        writeLine("@" + trueLabel);

        if (cmd.equals("eq")) writeLine("D;JEQ");
        if (cmd.equals("gt")) writeLine("D;JGT");
        if (cmd.equals("lt")) writeLine("D;JLT");

        writeLine("@SP");
        writeLine("A=M-1");
        writeLine("M=0");
        writeLine("@" + endLabel);
        writeLine("0;JMP");

        writeLine("(" + trueLabel + ")");
        writeLine("@SP");
        writeLine("A=M-1");
        writeLine("M=-1");

        writeLine("(" + endLabel + ")");
    }

    public void writePushPop(String type, String segment, int index) throws Exception {

        if (type.equals("C_PUSH")) {

            if (segment.equals("constant")) {
                writeLine("@" + index);
                writeLine("D=A");
                pushD();
            }

            else if (segment.equals("local")) pushFromSegment("LCL", index);
            else if (segment.equals("argument")) pushFromSegment("ARG", index);
            else if (segment.equals("this")) pushFromSegment("THIS", index);
            else if (segment.equals("that")) pushFromSegment("THAT", index);

            else if (segment.equals("temp")) {
                writeLine("@" + (5 + index));
                writeLine("D=M");
                pushD();
            }

            else if (segment.equals("pointer")) {
                writeLine(index == 0 ? "@THIS" : "@THAT");
                writeLine("D=M");
                pushD();
            }

            else if (segment.equals("static")) {
                writeLine("@Static." + index);
                writeLine("D=M");
                pushD();
            }
        }

        else if (type.equals("C_POP")) {

            if (segment.equals("local")) popToSegment("LCL", index);
            else if (segment.equals("argument")) popToSegment("ARG", index);
            else if (segment.equals("this")) popToSegment("THIS", index);
            else if (segment.equals("that")) popToSegment("THAT", index);

            else if (segment.equals("temp")) {
                popToAddress(5 + index);
            }

            else if (segment.equals("pointer")) {
                popToSymbol(index == 0 ? "THIS" : "THAT");
            }

            else if (segment.equals("static")) {
                popToSymbol("Static." + index);
            }
        }
    }

    private void pushFromSegment(String base, int index) throws Exception {
        writeLine("@" + base);
        writeLine("D=M");
        writeLine("@" + index);
        writeLine("A=D+A");
        writeLine("D=M");
        pushD();
    }

    private void popToSegment(String base, int index) throws Exception {
        writeLine("@" + base);
        writeLine("D=M");
        writeLine("@" + index);
        writeLine("D=D+A");
        writeLine("@R13");
        writeLine("M=D");

        popToD();

        writeLine("@R13");
        writeLine("A=M");
        writeLine("M=D");
    }

    private void pushD() throws Exception {
        writeLine("@SP");
        writeLine("A=M");
        writeLine("M=D");
        writeLine("@SP");
        writeLine("M=M+1");
    }

    private void popToD() throws Exception {
        writeLine("@SP");
        writeLine("AM=M-1");
        writeLine("D=M");
    }

    private void popToAddress(int addr) throws Exception {
        popToD();
        writeLine("@" + addr);
        writeLine("M=D");
    }

    private void popToSymbol(String symbol) throws Exception {
        popToD();
        writeLine("@" + symbol);
        writeLine("M=D");
    }
}
