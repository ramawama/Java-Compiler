package edu.ufl.cise.plcsp23;

public class StringLitToken implements IStringLitToken {
    final Kind kind;
    final int pos;
    final int length;
    final char[] source;
    final int row;
    final int col;
    final String value;

    public StringLitToken(int pos, int length, char[] source, int row, int col){
        this.kind = Kind.STRING_LIT;
        this.pos = pos;
        this.length =length;
        this.source = source;
        this.row = row;
        this.col = col;
        this.value = String.valueOf(source, pos+ 1, length-2);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public SourceLocation getSourceLocation() {
        return new SourceLocation(row,col);
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
