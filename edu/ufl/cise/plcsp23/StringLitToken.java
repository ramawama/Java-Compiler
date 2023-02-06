package edu.ufl.cise.plcsp23;

public class StringLitToken implements IStringLitToken {
    final Kind kind;
    final int pos;
    final int length;
    final char[] source;

    public StringLitToken(int pos, int length, char[] source){
        this.kind = Kind.STRING_LIT;
        this.pos = pos;
        this.length =length;
        this.source = source;
    }
    @Override
    public String getValue() {
        return getTokenString();
    }

    @Override
    public SourceLocation getSourceLocation() {
        return null;
    }

    @Override
    public Kind getKind() {
        return kind;
    }

    //This & num lit are extended from token class, maybe there is a way to not redefine this function in each one cuz its exactly the same
    //same with getKind()
    //not important issues but sumth to think ab once everything is taken care of
    @Override
    public String getTokenString() {
        String str = String.valueOf(source,pos,length);
        return str;
    }
}
