import java.io.*;

public class CodeWriter {

    private BufferedWriter writer;
    private String fileName = "";
    private int labelCounter = 0;
    private String currentFunction = "";

    public CodeWriter(File outputFile) throws Exception {
        writer = new BufferedWriter(new FileWriter(outputFile));
    }

    public void setFileName(String name) {
        fileName = name;
    }

    private void w(String s) throws IOException {
        writer.write(s);
        writer.newLine();
    }

    // Bootstrap
    public void writeInit() throws IOException {
        w("@256");
        w("D=A");
        w("@SP");
        w("M=D");
        writeCall("Sys.init", 0);
    }

    // Arithmetic
    public void writeArithmetic(String cmd) throws IOException {

        if (cmd.equals("add")) binary("M=D+M");
        else if (cmd.equals("sub")) binary("M=M-D");
        else if (cmd.equals("and")) binary("M=D&M");
        else if (cmd.equals("or")) binary("M=D|M");
        else if (cmd.equals("neg")) unary("M=-M");
        else if (cmd.equals("not")) unary("M=!M");
        else if (cmd.equals("eq")) compare("JEQ");
        else if (cmd.equals("gt")) compare("JGT");
        else if (cmd.equals("lt")) compare("JLT");
    }

    private void unary(String op) throws IOException {
        w("@SP");
        w("A=M-1");
        w(op);
    }

    private void binary(String op) throws IOException {
        w("@SP");
        w("AM=M-1");
        w("D=M");
        w("A=A-1");
        w(op);
    }

    private void compare(String jump) throws IOException {

        String trueLabel = "TRUE" + labelCounter;
        String endLabel = "END" + labelCounter;
        labelCounter++;

        w("@SP");
        w("AM=M-1");
        w("D=M");
        w("A=A-1");
        w("D=M-D");

        w("@" + trueLabel);
        w("D;" + jump);

        w("@SP");
        w("A=M-1");
        w("M=0");
        w("@" + endLabel);
        w("0;JMP");

        w("(" + trueLabel + ")");
        w("@SP");
        w("A=M-1");
        w("M=-1");

        w("(" + endLabel + ")");
    }

    // Push/Pop
    public void writePushPop(String type, String segment, int index) throws IOException {

        if (type.equals("C_PUSH")) {

            if (segment.equals("constant")) {
                w("@" + index);
                w("D=A");
                pushD();
            }
            else if (segment.equals("local")) pushFromSegment("LCL", index);
            else if (segment.equals("argument")) pushFromSegment("ARG", index);
            else if (segment.equals("this")) pushFromSegment("THIS", index);
            else if (segment.equals("that")) pushFromSegment("THAT", index);

            else if (segment.equals("temp")) {
                w("@" + (5 + index));
                w("D=M");
                pushD();
            }

            else if (segment.equals("pointer")) {
                w(index == 0 ? "@THIS" : "@THAT");
                w("D=M");
                pushD();
            }

            else if (segment.equals("static")) {
                w("@" + fileName + "." + index);
                w("D=M");
                pushD();
            }
        }

        else if (type.equals("C_POP")) {

            if (segment.equals("local")) popToSegment("LCL", index);
            else if (segment.equals("argument")) popToSegment("ARG", index);
            else if (segment.equals("this")) popToSegment("THIS", index);
            else if (segment.equals("that")) popToSegment("THAT", index);

            else if (segment.equals("temp")) {
                popToD();
                w("@" + (5 + index));
                w("M=D");
            }

            else if (segment.equals("pointer")) {
                popToD();
                w(index == 0 ? "@THIS" : "@THAT");
                w("M=D");
            }

            else if (segment.equals("static")) {
                popToD();
                w("@" + fileName + "." + index);
                w("M=D");
            }
        }
    }

    private void pushFromSegment(String base, int index) throws IOException {
        w("@" + base);
        w("D=M");
        w("@" + index);
        w("A=D+A");
        w("D=M");
        pushD();
    }

    private void popToSegment(String base, int index) throws IOException {
        w("@" + base);
        w("D=M");
        w("@" + index);
        w("D=D+A");
        w("@R13");
        w("M=D");
        popToD();
        w("@R13");
        w("A=M");
        w("M=D");
    }

    private void pushD() throws IOException {
        w("@SP");
        w("A=M");
        w("M=D");
        w("@SP");
        w("M=M+1");
    }

    private void popToD() throws IOException {
        w("@SP");
        w("AM=M-1");
        w("D=M");
    }

    // Program flow
    public void writeLabel(String label) throws IOException {
        w("(" + currentFunction + "$" + label + ")");
    }

    public void writeGoto(String label) throws IOException {
        w("@" + currentFunction + "$" + label);
        w("0;JMP");
    }

    public void writeIf(String label) throws IOException {
        popToD();
        w("@" + currentFunction + "$" + label);
        w("D;JNE");
    }

    // Functions
    public void writeFunction(String name, int nLocals) throws IOException {
        currentFunction = name;
        w("(" + name + ")");
        for (int i = 0; i < nLocals; i++) {
            w("@0");
            w("D=A");
            pushD();
        }
    }

    public void writeCall(String name, int nArgs) throws IOException {

        String ret = "RET_" + labelCounter++;

        w("@" + ret);
        w("D=A");
        pushD();

        pushSegment("LCL");
        pushSegment("ARG");
        pushSegment("THIS");
        pushSegment("THAT");

        w("@SP");
        w("D=M");
        w("@" + (nArgs + 5));
        w("D=D-A");
        w("@ARG");
        w("M=D");

        w("@SP");
        w("D=M");
        w("@LCL");
        w("M=D");

        w("@" + name);
        w("0;JMP");
        w("(" + ret + ")");
    }

    private void pushSegment(String seg) throws IOException {
        w("@" + seg);
        w("D=M");
        pushD();
    }

    public void writeReturn() throws IOException {

        w("@LCL");
        w("D=M");
        w("@R13");
        w("M=D");

        w("@5");
        w("A=D-A");
        w("D=M");
        w("@R14");
        w("M=D");

        popToD();
        w("@ARG");
        w("A=M");
        w("M=D");

        w("@ARG");
        w("D=M+1");
        w("@SP");
        w("M=D");

        restore("THAT",1);
        restore("THIS",2);
        restore("ARG",3);
        restore("LCL",4);

        w("@R14");
        w("A=M");
        w("0;JMP");
    }

    private void restore(String seg,int offset) throws IOException {
        w("@R13");
        w("D=M");
        w("@" + offset);
        w("A=D-A");
        w("D=M");
        w("@" + seg);
        w("M=D");
    }

    public void close() throws IOException {
        writer.close();
    }
}
