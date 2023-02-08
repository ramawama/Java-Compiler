package edu.ufl.cise.plcsp23;

public class StringLitToken implements IStringLitToken {
    final Kind kind;
    final int pos;
    final int length;
    final char[] source;
    final int row;
    final int col;
    final String value;

    public StringLitToken(int pos, int length, char[] source, int row, int col) {
        this.kind = Kind.STRING_LIT;
        this.pos = pos;
        this.length =length;
        this.source = source;
        this.row = row;
        this.col = col;
        this.value = String.valueOf(source, pos, length);
    }

    public String escapeSeq(String val) {
        StringBuilder builder = new StringBuilder();
        for(int i = 1; i < val.length()-1; i++) {
            char ch = val.charAt(i);
            if (ch == '\\' ) {
                i++;
                if(i >= val.length()){
                    System.out.println("too big");
                    break;
                }
                switch (val.charAt(i)) {
                    case 'n', 't', 'r', 'f', 'b', '\\', '\'', '\"' -> {
                        builder.append("\\" + val.charAt(i));
                    }
                    default -> {
                        //throw new LexicalException("invalid escape sequence");
                        System.out.println("invalid");
                    }
                }
            }else if (ch != '"'){
                builder.append(ch);
            }
        }
        return builder.toString();
    }

    @Override
    public String getValue() {
        return escapeSeq(value);//.substring(1, escapeSeq(value).length() - 1);
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
