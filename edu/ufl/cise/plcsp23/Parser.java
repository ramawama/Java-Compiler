package edu.ufl.cise.plcsp23;

import edu.ufl.cise.plcsp23.ast.AST;



public class Parser implements IParser{
    private IScanner scanner;
    final String input;
    private IToken current;

    public Parser(String input) throws LexicalException {
        this.input = input;
        scanner = new Scanner(input);
        current = scanner.next();
    }

    @Override
    public AST parse() throws PLCException {
        while (scanner.next().getKind() != IToken.Kind.EOF){

        }
        return null;
    }
}
