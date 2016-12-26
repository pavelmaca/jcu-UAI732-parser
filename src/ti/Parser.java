package ti;

/**
 * @author Pavel Máca <maca.pavel@gmail.com>
 * <p>
 * Zadání:
 * Sestrojte gramatiku pro regulární výraz (dle přednášek)
 * <p>
 * Gramatiku upravte tak, aby byla LL1
 * (tj. odstraníte konflikty first-first a first-follow,
 * aby bylo možné jednoznačně určit, které pravidlo se vybere.
 * <p>
 * Metodou rekurzivního sestupu naprogramujte parser, který ověří,
 * zda daný výraz je syntakticky správně a který ne.
 * Program bude akceptovat vstupní řetěz jako parametr příkazu a výsledek A/N vytiskne na standardní výstup.
 * Program kromě A/N nebude nic tisknout a nebude mít žádné uživatelské rozhraní.
 * Bude to program pro příkazovou řádku.
 * <p>
 * Gramatika:
 * (1) S->X#
 * (2) X->Z|K
 * (3) Y->Z|K|P|lambda
 * (4) Z->(X)HY
 * (5) K->TY
 * (6) P->+X
 * (7) T->aH|bH|cH|...zH
 * (8) H->*|lambda
 */

import java.util.Stack;

public class Parser {

    public static void main(String[] args) {
        if (args.length < 1) {
            return;
        }

        Parser parser = new Parser();
        if (parser.validate(args[0])) {
            System.out.println('A');
        } else {
            System.out.println('N');
        }
    }

    private Stack<Character> inputStack;

    private int position;

    public boolean printException = false;

    private int read() {
        position++;
        return inputStack.pop();
    }

    private int peek() {
        return inputStack.peek();
    }

    public boolean validate(String input) {
        position = 0;
        inputStack = new Stack<>();
        inputStack.push('#');
        for (int x = input.length() - 1; x >= 0; x--) {
            // skip whitspace
            if (Character.isSpaceChar(input.charAt(x))) {
                continue;
            }
            inputStack.push(input.charAt(x));
        }


        try {
            S();
            return true;
        } catch (SyntaxException ex) {
            if (printException) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    public boolean isAlphabet(int symbol) {
        return symbol >= 'a' && symbol <= 'z';
    }

    // Syntactic analitics
    // (1) S->X#
    private void S() throws SyntaxException {
        X();
        if (read() != '#') {
            throw new SyntaxException("Unexpected end of string", position);
        }
    }

    // (2) X->Z|K
    private void X() throws SyntaxException {
        int sym = peek();
        if (isAlphabet(sym)) {
            K();
            return;
        } else if (sym == '(') {
            Z();
            return;
        }
        throw new UnexpectedCharException(sym, position);
    }

    //  (3) Y->Z|K|P|lambda
    private void Y() throws SyntaxException {
        int sym = peek();
        if (isAlphabet(sym)) {
            K();
            return;
        } else if (sym == '(') {
            Z();
            return;
        } else if (sym == '+') {
            P();
            return;
        } else if (sym == '#' || sym == ')') {  // follow
            return;
        }
        throw new UnexpectedCharException(sym, position);
    }

    // * (4) Z->(X)HY
    private void Z() throws SyntaxException {
        int sym = peek();
        if (sym == '(') {
            read();
            X();

            sym = peek();
            if (sym == ')') {
                read();
                H();
                Y();
                return;
            } else {
                throw new SyntaxException("Missing ')'", position);
            }
        }

        throw new UnexpectedCharException(sym, position);
    }

    //  (5) K->TY
    private void K() throws SyntaxException {
        T();
        Y();
    }

    //  /* (6) P->+X
    private void P() throws SyntaxException {
        int sym = peek();
        if (sym == '+') {
            read();
            X();
            return;
        }
        throw new SyntaxException("Missing +", position);
    }

    // (7) T->aH|bH|cH|...zH
    private void T() throws SyntaxException {
        int sym = peek();
        if (isAlphabet(sym)) {
            read();
            H();
            return;
        }
        throw new UnexpectedCharException(sym, position);
    }

    // (8) H->*|lambda
    private void H() throws SyntaxException {
        int sym = peek();
        if (sym == '*') {
            read();
            return;
        } else if (isAlphabet(sym) || sym == '#' || sym == ')' || sym == '+' || sym == '(') {
            // follow Y
            return;
        }
        throw new UnexpectedCharException(sym, position);
    }
}

class SyntaxException extends Exception {
    public SyntaxException(String msg, int position) {
        super(msg + " at position '" + (position + 1) + "'");
    }
}

class UnexpectedCharException extends SyntaxException {
    public UnexpectedCharException(int symbol, int position) {
        super("Unexpected char '" + ((char) symbol) + "'", position);
    }
}