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

    //constructor
    public Scanner(String input){
        this.input = input;
        inputChars = Arrays.copyOf(input.toCharArray(), input.length() + 1);
        pos = 0;
        ch = inputChars[0];
    }

    private enum State{
        START,
        HAVE_EQ,
        IN_IDENT,
        IN_NUM_LIT
    }

    private void nextChar(){
        pos++;
        ch = inputChars[pos];
    }

    private void error(String message) throws LexicalException{
        throw new LexicalException("Error at pos" + pos + ": " + message);
    }
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

    @Override
    public IToken next() throws LexicalException {
        State state = State.START;
        int tokenStart = -1;
        while(true){   //exits when token is returned
            switch(state){
                case START -> {
                    tokenStart = pos;
                    switch(ch){
                        case 0 -> {    //end of input | empty string
                            return new Token(Kind.EOF, tokenStart, 0, inputChars);
                        }
                        case ' ', '\n', '\t', '\r' ,'\f' -> { nextChar(); }
                        case '+' -> {
                            nextChar();
                            return new Token(Kind.PLUS, tokenStart, 1, inputChars);
                        }
                        case '*' -> {
                            nextChar();
                            if(ch == '*'){
                                nextChar();
                                return new Token(Kind.EXP, tokenStart, 2, inputChars);
                            }else{
                                return new Token(Kind.TIMES, tokenStart, 1, inputChars);
                            }
                        }

                        case '-' -> {
                            nextChar();
                            return new Token(Kind.MINUS, tokenStart, 1, inputChars);
                        }

                        case '0' -> {
                            nextChar();
                            return new Token(Kind.NUM_LIT, tokenStart, 1, inputChars);
                        }
                        case '=' -> {
                            state = State.HAVE_EQ;
                            nextChar();
                        }
                        case '1','2','3','4','5','6','7','8','9' -> { //nonzero digit
                            state = State.IN_NUM_LIT;
                            nextChar();
                        }
                        default -> {
                            if(isLetter(ch)) {
                                state = State.IN_IDENT;
                                nextChar();
                            }else{
                                error("illegal char with ascii value: " + (int)ch);
                            }
                            //throw new LexicalException("Not yet implemented");
                        }
                    }
                }
                case HAVE_EQ -> {
                    if(ch == '='){
                        state = State.START;
                        nextChar();
                        return new Token(Kind.EQ, tokenStart, 2, inputChars);
                    }else{
                        error("expected =");
                    }
                }
                case IN_NUM_LIT -> {
                    if(isDigit(ch)){  //continue in this state
                        nextChar();
                    }else{
                        int length = pos - tokenStart;
                        return new Token(Kind.NUM_LIT, tokenStart, length, inputChars);
                    }
                }
                case IN_IDENT -> {
                    tokenStart = pos;
                    if(isIdentStart(ch) || isDigit(ch)) { nextChar(); }
                    else{
                        //curr char belongs to the next token
                        int length = pos-tokenStart;
                        //check if reserved word
                        String txt = input.substring(tokenStart, tokenStart + length);
                        Kind kind = reservedWrds.get(txt);
                        if(kind == null) { kind = Kind.IDENT;};
                        return new Token(kind, tokenStart, length, inputChars);
                    }
                }
                default ->{
                    throw new LexicalException("buggy scanner");
                }
            }
        }
    }
}

