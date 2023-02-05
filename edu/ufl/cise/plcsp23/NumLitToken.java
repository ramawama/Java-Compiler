package edu.ufl.cise.plcsp23;

///Maybe there is a way to extend this from token class cuz all dis does the same ish basically

public class NumLitToken implements INumLitToken {
    final Kind kind;
    final int pos;
    final int length;
    final int[] source;

    public NumLitToken(int pos, int length, int[] source){
        this.kind = Kind.NUM_LIT;
        this.pos = pos;
        this.length = length;
        this.source = source;
    }
    @Override
    public int getValue() {
        return 0;
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
        return null;
    }
}
