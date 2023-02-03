package edu.ufl.cise.plcsp23;

public class NumLitToken implements INumLitToken {

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
        return NUM_LIT;
    }

    @Override
    public String getTokenString() {
        return null;
    }
}
