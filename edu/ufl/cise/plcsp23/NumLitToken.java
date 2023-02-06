package edu.ufl.cise.plcsp23;


import static java.lang.Integer.parseInt;

public class NumLitToken implements INumLitToken {
    final Kind kind;
    final int pos;
    final int length;
    final char[] source;

    public NumLitToken(int pos, int length, char[] source){
        this.kind = Kind.NUM_LIT;
        this.pos = pos;
        this.length = length;
        this.source = source;
    }

    @Override
    public int getValue() {
        return parseInt(getTokenString());
    }

    @Override
    public SourceLocation getSourceLocation() {
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
