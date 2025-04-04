import java.util.*;
public class Lexer {
    // Listado de palabras reservadas en lisp.
    private static final Set<String> PALABRAS_CLAVE = Set.of("QUOTE", "SETQ", "DEFUN", "ATOM", "LIST","EQUAL","COND");
    
    // Funcion para eliminar los espacios en blanco
    public static String cleanInput(String input) {
        return input.replaceAll("\\s+", " ").trim();
    }
    
    // Funcion para comprobar que el numero de parentesis apertura igualen los patentesis de cierre
    public static boolean isBalanced(String input) {
        Stack<Character> stack = new Stack<>();
        for (char c : input.toCharArray()) {
            if (c == '(') stack.push(c);
            else if (c == ')') {
                if (stack.isEmpty()) return false;
                stack.pop();
            }
        }
        return stack.isEmpty();
    }
    
    // Metodo para resparar los elementos de la lista en diferentes tokens
    public List<Token> tokenizar(String expresion) {
        // Excepcion cuando registra un numero desigual entre parentesis de entrada y de salida
        if (!isBalanced(expresion)) {
            throw new IllegalArgumentException("La expresión tiene un desbalance en los paréntesis");
        }

        expresion = cleanInput(expresion);
        List<Token> tokens = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        char[] caracteres = expresion.toCharArray();
        
        for (int i = 0; i < caracteres.length; i++) {
            char c = caracteres[i];
            if (Character.isWhitespace(c)) continue;
            if (c == '(') tokens.add(new Token(TiposTokens.Parentesis_Abierto, "("));
            else if (c == ')') tokens.add(new Token(TiposTokens.Parentesis_Cerrado, ")"));
            else if (Character.isDigit(c)) {
                buffer.append(c);
                while (i + 1 < caracteres.length && (Character.isDigit(caracteres[i + 1]) || caracteres[i + 1] == '.')) {
                    buffer.append(caracteres[++i]);
                }
                if (buffer.toString().contains(".")) {
                    tokens.add(new Token(TiposTokens.Numero_decimal, buffer.toString()));
                } else {
                    tokens.add(new Token(TiposTokens.Numero_entero, buffer.toString()));
                }
                buffer.setLength(0);
            } else if (Character.isLetter(c)) {
                buffer.append(c);
                //Recorre toda los caraccteres considerados letras hasta que dicho conjunto de caracteres forme una palabra reservada,
                //sino forma una palabra clave lo interpreta como identificador.
                while (i + 1 < caracteres.length && Character.isLetterOrDigit(caracteres[i + 1])) {
                    buffer.append(caracteres[++i]);
                }
                String palabra = buffer.toString().toUpperCase();
                TiposTokens tipo = PALABRAS_CLAVE.contains(palabra) ? TiposTokens.Palabra_clave : TiposTokens.Identificador;
                tokens.add(new Token(tipo, buffer.toString()));
                buffer.setLength(0);
            }
        }
        return tokens;
    }
}
