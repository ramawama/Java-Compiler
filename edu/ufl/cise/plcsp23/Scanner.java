package edu.ufl.cise.plcsp23;

import java.util.Arrays;
import java.util.HashMap;

import static edu.ufl.cise.plcsp23.IToken.Kind;

public class Scanner implements IScanner{
    //variables
    final String input;
    final char[] inputChars;
    int pos;
    char ch;
    int row;
    int col;

    //constructor
    public Scanner(String input){
        this.input = input;
        inputChars = Arrays.copyOf(input.toCharArray(), input.length() + 1);
        pos = 0;
        ch = inputChars[0];
        row = 1;
        col = 0;
    }

    private enum State{
        START,
        IN_IDENT,
        IN_NUM_LIT,
        IN_COMMENT,
        IN_ESCAPE,
        IN_STRING_LIT
    }

    //gets next char
    private void nextChar(){
        pos++;
        ch = inputChars[pos];
    }


    private void error(String message) throws LexicalException{throw new LexicalException("Error at pos" + pos + ": " + message);}

    //Verifying if character is a digit, letter, or other ident characters
    private boolean isDigit(int ch){
        return '0' <= ch && ch <= '9';
    }
    private boolean isLetter(int ch){
        return ('A' <= ch && ch <= 'Z') || ('a' <= ch && ch <= 'z');
    }
    private boolean isIdentStart(int ch){
        return isLetter(ch) || (ch == '$') || (ch == '_');
    }

    private static HashMap<String, Kind> reservedWrds;
    static {
        reservedWrds = new HashMap<>();
        reservedWrds.put("if", Kind.RES_if);
        reservedWrds.put("x", Kind.RES_x);
        reservedWrds.put("X", Kind.RES_X);
        reservedWrds.put("x_cart", Kind.RES_x_cart);
        reservedWrds.put("y", Kind.RES_y);
        reservedWrds.put("Y", Kind.RES_Y);
        reservedWrds.put("y_cart", Kind.RES_y_cart);
        reservedWrds.put("Z", Kind.RES_Z);
        reservedWrds.put("a", Kind.RES_a);
        reservedWrds.put("a_polar", Kind.RES_a_polar);
        reservedWrds.put("r", Kind.RES_r);
        reservedWrds.put("r_polar", Kind.RES_r_polar);
        reservedWrds.put("while", Kind.RES_while);
        reservedWrds.put("sin", Kind.RES_sin);
        reservedWrds.put("cos", Kind.RES_cos);
        reservedWrds.put("rand", Kind.RES_rand);
        reservedWrds.put("atan", Kind.RES_atan);
        reservedWrds.put("image", Kind.RES_image);
        reservedWrds.put("pixel", Kind.RES_pixel);
        reservedWrds.put("int", Kind.RES_int);
        reservedWrds.put("string", Kind.RES_string);
        reservedWrds.put("void", Kind.RES_void);
        reservedWrds.put("nil", Kind.RES_nil);
        reservedWrds.put("load", Kind.RES_load);
        reservedWrds.put("display", Kind.RES_display);
        reservedWrds.put("write", Kind.RES_write);
    }

    //Where all logic for parsing is
    @Override
    public IToken next() throws LexicalException {
        State state = State.START;
        int tokenStart = -1;
        while(true){                   //exits when token is returned
            switch(state){
                case START -> {
                    tokenStart = pos;
                    col++;
                    switch(ch){
                        case 0 -> {    //end of input | empty string
                            return new Token(Kind.EOF, tokenStart, 0, inputChars, row, col);
                        }
                        case '"' -> {
                            nextChar();
                            state = State.IN_STRING_LIT;
                        }
                        case '~' -> {
                            nextChar();
                            state = State.IN_COMMENT;
                        }
                        case ' ', '\n', '\t', '\r' ,'\f' -> {
                            if (ch == '\n'){
                                col = 0;
                                row++;
                            }
                            nextChar();
                        }
                        case '.' -> {
                            nextChar();
                            return new Token(Kind.DOT, tokenStart, 1, inputChars, row, col);
                        }
                        case ',' -> {
                            nextChar();
                            return new Token(Kind.COMMA, tokenStart, 1, inputChars, row, col);
                        }
                        case '?' ->{
                            nextChar();
                            return new Token(Kind.QUESTION, tokenStart, 1, inputChars, row, col);
                        }
                        case ':' -> {
                            nextChar();
                            return new Token(Kind.COLON, tokenStart, 1, inputChars, row, col);
                        }
                        case '(' -> {
                            nextChar();
                            return new Token(Kind.LPAREN, tokenStart, 1, inputChars, row, col);
                        }
                        case ')' -> {
                            nextChar();
                            return new Token(Kind.RPAREN, tokenStart, 1, inputChars, row, col);
                        }
                        case '<' ->{ //less than multiple permutations so send to state
                            nextChar();
                            if (ch == '='){ // <=
                                nextChar();
                                return new Token(Kind.LE, tokenStart, 2, inputChars, row, col);
                            }
                            else if (ch == '-'){
                                nextChar();
                                if (ch == '>'){
                                    nextChar();
                                    return new Token(Kind.EXCHANGE, tokenStart, 3, inputChars, row, col);
                                }
                                else error ("expected >");
                            }
                            else return new Token(Kind.LT, tokenStart, 1, inputChars, row, col);
                        }
                        case '>' ->{
                            nextChar();
                            if (ch == '='){ // <=
                                nextChar();
                                return new Token(Kind.GE, tokenStart, 2, inputChars, row, col);
                            }
                            else return new Token(Kind.GT, tokenStart, 1, inputChars, row, col);

                        }
                        case '[' -> {
                            nextChar();
                            return new Token(Kind.LSQUARE, tokenStart, 1, inputChars, row, col);
                        }
                        case ']' -> {
                            nextChar();
                            return new Token(Kind.RSQUARE, tokenStart, 1, inputChars, row, col);
                        }
                        case '{' -> {
                            nextChar();
                            return new Token(Kind.LCURLY, tokenStart, 1, inputChars, row, col);
                        }
                        case '}' -> {
                            nextChar();
                            return new Token(Kind.RCURLY, tokenStart, 1, inputChars, row, col);
                        }

                        case '+' -> {
                            nextChar();
                            return new Token(Kind.PLUS, tokenStart, 1, inputChars, row, col);
                        }
                        case '*' -> { //change to state version
                            nextChar();
                            if(ch == '*'){
                                nextChar();
                                return new Token(Kind.EXP, tokenStart, 2, inputChars, row, col);
                            }else{
                                return new Token(Kind.TIMES, tokenStart, 1, inputChars, row, col);
                            }
                        }
                        case '-' -> {
                            nextChar();
                            return new Token(Kind.MINUS, tokenStart, 1, inputChars, row, col);
                        }
                        case '/' -> {
                            nextChar();
                            return new Token(Kind.DIV, tokenStart, 1, inputChars, row, col);
                        }

                        case '!' ->{
                            nextChar();
                            return new Token(Kind.BANG, tokenStart, 1, inputChars, row, col);
                        }

                        case '&' ->{
                            nextChar();
                            if(ch == '&'){
                                nextChar();
                                return new Token(Kind.AND, tokenStart, 2, inputChars, row, col);
                            }
                            else return new Token(Kind.BITAND, tokenStart, 1, inputChars, row, col);

                        }

                        case '|' ->{
                            nextChar();
                            if(ch == '|'){
                                nextChar();
                                return new Token(Kind.OR, tokenStart, 2, inputChars, row, col);
                            }
                            else return new Token(Kind.BITOR, tokenStart, 1, inputChars, row, col);
                        }

                        case '%' ->{
                            nextChar();
                            return new Token(Kind.MOD, tokenStart, 1, inputChars, row, col);
                        }
                        case '=' -> {
                            nextChar();
                            if(ch == '='){
                                nextChar();
                                return new Token(Kind.EQ, tokenStart, 2, inputChars, row, col);
                            }
                            else return new Token(Kind.ASSIGN, tokenStart, 1, inputChars, row, col);

                        }
                        case '0' -> {
                            nextChar();
                            return new NumLitToken(tokenStart, 1, inputChars, row, col);
                        }
                        case '1','2','3','4','5','6','7','8','9' -> { //nonzero digit
                            nextChar();
                            state = State.IN_NUM_LIT;
                        }
                        default -> {
                            if(isLetter(ch)) {
                                state = State.IN_IDENT;
                                nextChar();
                            }else{
                                error("illegal char with ascii value: " + (int)ch);
                            }
                        }
                    }
                }

                case IN_STRING_LIT -> {
                    if (ch != '"') {
                        if (ch == '\n' || ch == '\r') {
                            error("Invalid input character LF or CR");
                        }
                        if ((int)ch > 127){ // not in asci
                            error("Not valid ascii value");
                        }
                        if (ch == '\\'){
                            state = State.IN_ESCAPE;
                        }
                        nextChar();
                    }
                    else {
                        nextChar();
                        int length = pos - tokenStart;
                        int tempCol = col;
                        col = col + length;
                        return new StringLitToken(tokenStart, length, inputChars, row, tempCol);
                    }

                }

                case IN_ESCAPE -> {
                    if (ch == '\\'){
                        nextChar();
                    }
                    else if (ch == 'r' || ch == 'n' || ch == 't' || ch == 'f' || ch == 'b' || ch == '"' || ch == '\'') {
                        nextChar();
                        state = State.IN_STRING_LIT;
                    }
                    else error("Invalid escape sequence");
                }

                case IN_COMMENT -> {
                    if (ch == '\n'){
                        state = State.START;
                    }
                    else nextChar();
                }

                case IN_NUM_LIT -> {
                    if(pos-tokenStart > 10){
                       error("number too large");
                    }
                    else if (ch == '\n' || ch == ' ' || !isDigit(ch)){ //recognize whitespace and new line
                        int length = pos - tokenStart;
                        int tempCol = col;
                        col = length;
                        return new NumLitToken(tokenStart , length, inputChars, row, tempCol); //token start is updated in initial switch statement concerning state change
                    }
                    else  {  //continue in this state
                        nextChar();
                    }
                }
                case IN_IDENT -> {
                    if(isIdentStart(ch) || isDigit(ch)) { nextChar(); }
                    else{
                        //curr char belongs to the next token
                        int length = pos-tokenStart;
                        //check if reserved word
                        int tempCol = col;
                        col = col + length - 1;
                        String txt = input.substring(tokenStart, tokenStart + length);
                        Kind kind = reservedWrds.get(txt);
                        if(kind == null) { kind = Kind.IDENT; }
                        return new Token(kind, tokenStart, length, inputChars, row, tempCol);
                    }
                }
                default ->{
                    throw new LexicalException("buggy scanner");
                }
            }
        }
    }
}

