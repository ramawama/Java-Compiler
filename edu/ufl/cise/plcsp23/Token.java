package edu.ufl.cise.plcsp23;

public class Token implements IToken {
    final Kind kind;
    final int pos;
    final int length;
    final char[] source;

    public Token(Kind kind, int pos, int length, char[] source){
        super();
        this.kind = kind;
        this.pos = pos;
        this.length = length;
        this.source = source;
    }

    @Override
    public SourceLocation getSourceLocation() {
        //start both line location and character location at 1
        return null;
    }

    @Override
    public Kind getKind() {
        return kind;
    }

    @Override
    public String getTokenString() {
        String str = String.valueOf(source,pos,length);
        return str;
    }
}
