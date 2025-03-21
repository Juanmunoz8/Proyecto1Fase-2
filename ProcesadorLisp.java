import java.util.*;

public class ProcesadorLisp {

    enum Estado {
        INICIO, NUMERO, IDENTIFICADOR, CADENA, OPERADOR, TERMINADO
    }

    private static final Set<String> PALABRAS_CLAVE = Set.of("QUOTE", "SETQ", "DEFUN");
    private final Map<String, Object> variables = new HashMap<>();
    private final Map<String, List<Object>> funciones = new HashMap<>();
    private List<Token> tokens;
    private int posicion;

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

    private Token consume() {
        if (posicion >= tokens.size()) {
            throw new IllegalStateException("No hay más tokens para consumir");
        }
        return tokens.get(posicion++);
    }

    private Token peek() {
        if (posicion >= tokens.size()) {
            throw new IllegalStateException("No hay más tokens disponibles");
        }
        return tokens.get(posicion);
    }

    private Object parseExpression() {
        Token token = consume();
        if (token.getTipo() == TiposTokens.Parentesis_Abierto) {
            List<Object> lista = new ArrayList<>();
            while (peek().getTipo() != TiposTokens.Parentesis_Cerrado) {
                lista.add(parseExpression());
            }
            consume(); 
            return lista;
        } else if (token.getTipo() == TiposTokens.Numero_entero) {
            return Integer.parseInt(token.getValor());
        } else if (token.getTipo() == TiposTokens.Identificador || token.getTipo() == TiposTokens.Operador) {
            return token.getValor();
        } else {
            return null;
        }
    }

    public Object evaluar(Object expresion) {
        if (expresion instanceof List<?>) {
            List<?> lista = (List<?>) expresion;
            if (lista.isEmpty()) return null;
            Object operador = lista.get(0);
    
            if (operador instanceof String op) {
                switch (op) {
                    case "+" -> {
                        return (int) evaluar(lista.get(1)) + (int) evaluar(lista.get(2));
                    }
                    case "-" -> {
                        return (int) evaluar(lista.get(1)) - (int) evaluar(lista.get(2));
                    }
                    case "*" -> {
                        return (int) evaluar(lista.get(1)) * (int) evaluar(lista.get(2));
                    }
                    case "/" -> {
                        return (int) evaluar(lista.get(1)) / (int) evaluar(lista.get(2));
                    }
                    case "SETQ" -> {
                        String varName = (String) lista.get(1);
                        Object value = evaluar(lista.get(2));
                        variables.put(varName, value);
                        return value;
                    }
                    case "QUOTE" -> {
                        return lista.get(1);
                    }
                }
            }
        } else if (expresion instanceof String varName && variables.containsKey(varName)) {
            return variables.get(varName);
        } else {
            return expresion;
        }
        return null;
    }

    public static void main(String[] args) {
        ProcesadorLisp procesador = new ProcesadorLisp();
        Scanner scanner = new Scanner(System.in);
    
        
        System.out.print("Ingrese una expresión Lisp: ");
        String expression = scanner.nextLine();  
    
        
        String cleanedExpression = cleanInput(expression);
        if (!isBalanced(cleanedExpression)) {
            System.out.println("Error: La expresión tiene paréntesis desbalanceados.");
            return;
        }
    
        
        List<Token> tokens = procesador.tokenizar(cleanedExpression);
        System.out.println("\nTokens generados:");
        tokens.forEach(System.out::println);
    
        
        procesador.tokens = tokens;  
        procesador.posicion = 0;  
        Object estructuraParseada = procesador.parseExpression();
        System.out.println("\nEstructura parseada:");
        System.out.println(estructuraParseada);
    
        Object resultado = procesador.evaluar(estructuraParseada);
        System.out.println("\nResultado Evaluado:");
        System.out.println(resultado);
    
        scanner.close(); 
    }
}