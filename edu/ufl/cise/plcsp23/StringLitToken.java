package edu.ufl.cise.plcsp23;

public class StringLitToken implements IStringLitToken {
    final Kind kind;
    final int pos;
    final int length;
    final char[] source;
    final int row;
    final int col;
    //final String value;

    public StringLitToken(int pos, int length, char[] source, int row, int col) {
        this.kind = Kind.STRING_LIT;
        this.pos = pos;
        this.length =length;
        this.source = source;
        this.row = row;
        this.col = col;
        //this.value = String.valueOf(source, pos+1, length-2);
    }


    @Override
    public String getValue() {
        String ret = "";
        char[] token = getTokenString().toCharArray();
        for (int i = 1; i < token.length - 1; i++){ // so quotations are not included
            //check for escape values here
            if(token[i] == '\\'){
                i++;
                switch(token[i]){
                    case 'n' -> {ret += String.valueOf('\n');}
                    case 't' -> { ret += String.valueOf('\t');}
                    case '"' -> {ret += String.valueOf('\"');}
                    case 'b' -> {ret += String.valueOf('\b');}
                    case 'r' -> {ret += String.valueOf('\r');}
                    case 'f' -> {ret += String.valueOf('\f');}
                    case '\''-> {ret += String.valueOf('\'');}
                    case '\\' -> {ret += String.valueOf('\\');}
                }
            }else{
                ret+=(String.valueOf(token[i]));
            }
        }
        return ret;
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
