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
        reservedWrds = new HashMap<String, Kind>();
        reservedWrds.put("if", Kind.RES_if);
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
                            return new Token(Kind.TIMES, tokenStart, 1, inputChars);
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

