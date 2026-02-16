// Fill.asm
// If key pressed → screen black
// If no key → screen white

(START)
    @KBD
    D=M
    @BLACK
    D;JNE        // If key pressed → BLACK

    @WHITE
    0;JMP

// -------- CLEAR SCREEN (WHITE) --------
(WHITE)
    @SCREEN
    D=A
    @addr
    M=D          // addr = SCREEN

(WHITE_LOOP)
    @addr
    D=M
    @24576       // end of screen memory
    D=A-D
    @START
    D;JEQ        // finished → restart

    @addr
    A=M
    M=0          // write white

    @addr
    M=M+1
    @WHITE_LOOP
    0;JMP

// -------- FILL SCREEN (BLACK) --------
(BLACK)
    @SCREEN
    D=A
    @addr
    M=D          // addr = SCREEN

(BLACK_LOOP)
    @addr
    D=M
    @24576
    D=A-D
    @START
    D;JEQ        // finished → restart

    @addr
    A=M
    M=-1         // write black

    @addr
    M=M+1
    @BLACK_LOOP
    0;JMP
