package edu.ufl.cise.plcsp23;
import java.util.Arrays;

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
        IN_NUMLIT
    }

    private void nextChar(){
        pos++;
        ch = inputChars[pos];
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
                        default ->{
                            throw new LexicalException("Not yet implemented");
                        }
                    }
                }
                case HAVE_EQ -> {
                    if(ch == '='){
                        state = State.START;
                        nextChar();
                        return new Token(Kind.EQ, tokenStart, 2, inputChars);
                    }else{
                        throw new LexicalException("expected =");
                    }
                }
                case IN_IDENT -> {}
                case IN_NUMLIT -> {}
                default ->{
                    throw new LexicalException("buggy scanner");
                }
            }
        }
    }
}

