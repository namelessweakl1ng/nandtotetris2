// Mult.asm
// Computes R0 * R1 and stores result in R2

    @R2
    M=0          // R2 = 0 (result)

    @R1
    D=M
    @END
    D;JEQ        // If R1 == 0, skip loop

(LOOP)
    @R0
    D=M
    @R2
    M=M+D        // R2 += R0

    @R1
    M=M-1        // R1--

    D=M
    @LOOP
    D;JGT        // Repeat while R1 > 0

(END)
    @END
    0;JMP        // Infinite loop
