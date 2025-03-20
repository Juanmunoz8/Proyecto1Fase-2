import java.util.*;

public class ProcesadorLisp {

    // Posibles estados 
    enum Estado {
        INICIO, NUMERO, IDENTIFICADOR, CADENA, OPERADOR, TERMINADO
    }

    // Metodo para eliminar espacios en blanco
    public static String cleanInput(String input) {
        return input.replaceAll("\\s+", " ").trim();
    }

    // Metodo para verificar el numero de parentesis de la expresion
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

    // Metodo para organizar los tokens 
    public List<Token> tokenizar(String expresion) {
        if (!isBalanced(expresion)) {
            throw new IllegalArgumentException("La expresión tiene un desbalance en los paréntesis");
        }

        expresion = cleanInput(expresion);
        List<Token> tokens = new ArrayList<>();
        Estado estado = Estado.INICIO;  
        StringBuilder buffer = new StringBuilder();
        char[] caracteres = expresion.toCharArray();
        // Clasificacion del tipo de tokens 
        for (char c : caracteres) {
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
                    }
                }
                case IDENTIFICADOR -> {
                    if (Character.isLetterOrDigit(c) || c == '-') {
                        buffer.append(c);
                    } else {
                        tokens.add(new Token(TiposTokens.Identificador, buffer.toString()));
                        buffer.setLength(0); 
                        estado = Estado.INICIO; 
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
                    }
                }
            }
        }
        
        if (buffer.length() > 0) {
            tokens.add(new Token(TiposTokens.Identificador, buffer.toString()));
        }

        return tokens;
    }

    public static void main(String[] args) {
        String expression = "(+ 2 (* V 8))";

        // Limpiar espacios innecesarios
        String cleanedExpression = cleanInput(expression);
        System.out.println("Expresión limpiada: " + cleanedExpression);

        // Verificar balance de paréntesis
        boolean balanced = isBalanced(cleanedExpression);
        System.out.println("Balance de paréntesis: " + (balanced ? "Correcto" : "Incorrecto"));

        // Crear un procesador y tokenizar la expresión
        ProcesadorLisp procesador = new ProcesadorLisp();
        List<Token> tokens = procesador.tokenizar(expression);

        // Imprimir los tokens obtenidos
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}


