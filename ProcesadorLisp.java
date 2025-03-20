import java.util.*;

public class ProcesadorLisp {

    enum Estado {
        INICIO, NUMERO, IDENTIFICADOR, CADENA, OPERADOR, TERMINADO
    }

    private static final Set<String> PALABRAS_CLAVE = Set.of("QUOTE", "SETQ", "DEFUN");

    public static String cleanInput(String input) {
        return input.replaceAll("\\s+", " ").trim();
    }

    public static boolean isBalanced(String input) {
        Stack<Character> stack = new Stack<>();
        for (char c : input.toCharArray()) {
            if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                if (stack.isEmpty()) {
                    return false;
                }
                stack.pop();
            }
        }
        return stack.isEmpty();
    }

    public List<Token> tokenizar(String expresion) {
        if (!isBalanced(expresion)) {
            throw new IllegalArgumentException("La expresión tiene un desbalance en los paréntesis");
        }

        expresion = cleanInput(expresion);
        List<Token> tokens = new ArrayList<>();
        Estado estado = Estado.INICIO;
        StringBuilder buffer = new StringBuilder();
        char[] caracteres = expresion.toCharArray();

        for (int i = 0; i < caracteres.length; i++) {
            char c = caracteres[i];
            switch (estado) {
                case INICIO -> {
                    if (Character.isWhitespace(c)) {
                        continue;
                    }
                    if (c == '(') {
                        tokens.add(new Token(TiposTokens.Parentesis_Abierto, "("));
                    } else if (c == ')') {
                        tokens.add(new Token(TiposTokens.Parentesis_Cerrado, ")"));
                    } else if (Character.isDigit(c)) {
                        buffer.append(c);
                        estado = Estado.NUMERO;
                    } else if (Character.isLetter(c)) {
                        buffer.append(c);
                        estado = Estado.IDENTIFICADOR;
                    } else if (c == '"') {
                        buffer.append(c);
                        estado = Estado.CADENA;
                    } else if ("+-*/<>=,.".indexOf(c) != -1) {
                        buffer.append(c);
                        estado = Estado.OPERADOR;
                    } else if (c == '\'') {
                        tokens.add(new Token(TiposTokens.Cita, "'"));
                    } else if (c == '`') {
                        tokens.add(new Token(TiposTokens.Quasiquote, "`"));
                    } else if (c == ',') {
                        tokens.add(new Token(TiposTokens.Unquote, ","));
                    } else if (c == '@') {
                        tokens.add(new Token(TiposTokens.Unquote_Splicing, "@"));
                    } else if (c == '#') {
                        if (i + 1 < caracteres.length && caracteres[i + 1] == '(') {
                            tokens.add(new Token(TiposTokens.Vector, "#("));
                            i++; 
                        } else {
                            tokens.add(new Token(TiposTokens.Hashtag, "#"));
                        }
                    } else {
                        throw new IllegalArgumentException("Carácter inesperado: " + c);
                    }
                }
                case NUMERO -> {
                    if (Character.isDigit(c) || c == '.') {
                        buffer.append(c);
                    } else {
                        tokens.add(new Token(TiposTokens.Numero_entero, buffer.toString()));
                        buffer.setLength(0);
                        estado = Estado.INICIO;
                        i--;
                    }
                }
                case IDENTIFICADOR -> {
                    if (Character.isLetterOrDigit(c) || c == '-') {
                        buffer.append(c);
                    } else {
                        String palabra = buffer.toString().toUpperCase();
                        TiposTokens tipo = PALABRAS_CLAVE.contains(palabra) ? TiposTokens.Palabra_clave : TiposTokens.Identificador;
                        tokens.add(new Token(tipo, buffer.toString()));
                        buffer.setLength(0);
                        estado = Estado.INICIO;
                        i--;
                    }
                }
                case CADENA -> {
                    buffer.append(c);
                    if (c == '"') {
                        tokens.add(new Token(TiposTokens.Cadena, buffer.toString()));
                        buffer.setLength(0);
                        estado = Estado.INICIO;
                    }
                }
                case OPERADOR -> {
                    if ("+-*/<>=,.".indexOf(c) != -1) {
                        buffer.append(c);
                    } else {
                        tokens.add(new Token(TiposTokens.Operador, buffer.toString()));
                        buffer.setLength(0);
                        estado = Estado.INICIO;
                        i--;
                    }
                }
            }
        }

        if (buffer.length() > 0) {
            String palabra = buffer.toString().toUpperCase();
            TiposTokens tipo = PALABRAS_CLAVE.contains(palabra) ? TiposTokens.Palabra_clave : TiposTokens.Identificador;
            tokens.add(new Token(tipo, buffer.toString()));
        }

        return tokens;
    }

    public static void main(String[] args) {
        String expression = "(SETQ x 10 'y `z ,w @v #t #(1 2 3))";

        String cleanedExpression = cleanInput(expression);
        System.out.println("Expresión limpiada: " + cleanedExpression);

        boolean balanced = isBalanced(cleanedExpression);
        System.out.println("Balance de paréntesis: " + (balanced ? "Correcto" : "Incorrecto"));

        ProcesadorLisp procesador = new ProcesadorLisp();
        List<Token> tokens = procesador.tokenizar(expression);

        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
