import java.util.*;

public class ProcesadorLisp {
    private final Map<String, Object> variables = new HashMap<>();
    private final Map<String, List<Object>> funciones = new HashMap<>();
    private List<Token> tokens;
    private int posicion;

<<<<<<< HEAD
    public Object evaluar(Object expresion, Map<String, Object> contexto) {
        if (expresion instanceof Integer) {
=======
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
>>>>>>> 13c6f247de0c0a46189c83ba52d98838d0647968
            return expresion;
        } else if (expresion instanceof String) {
            return contexto.getOrDefault(expresion, expresion);
        } else if (expresion instanceof List) {
            List<Object> lista = (List<Object>) expresion;
            if (lista.isEmpty()) return null;

            String operador = lista.get(0).toString();
            List<Object> argumentos = lista.subList(1, lista.size());

            switch (operador) {
                case "+" -> {
                    return (int) evaluar(argumentos.get(0), contexto) + (int) evaluar(argumentos.get(1), contexto);
                }
                case "-" -> {
                    return (int) evaluar(argumentos.get(0), contexto) - (int) evaluar(argumentos.get(1), contexto);
                }
                case "*" -> {
                    return (int) evaluar(argumentos.get(0), contexto) * (int) evaluar(argumentos.get(1), contexto);
                }
                case "/" -> {
                    return (int) evaluar(argumentos.get(0), contexto) / (int) evaluar(argumentos.get(1), contexto);
                }
                case "=" -> {
                    return Objects.equals(evaluar(argumentos.get(0), contexto), evaluar(argumentos.get(1), contexto));
                }
                case "IF" -> {
                    return (boolean) evaluar(argumentos.get(0), contexto) ? evaluar(argumentos.get(1), contexto) : evaluar(argumentos.get(2), contexto);
                }
                case "COND" -> {
                    return evaluarCond(argumentos, contexto);
                }
                case "DEFUN" -> {
                    return definirFuncion(argumentos, contexto);
                }
                default -> {
                    if (funciones.containsKey(operador)) {
                        return evaluarFuncion(operador, argumentos, contexto);
                    } else {
                        throw new IllegalArgumentException("Función desconocida: " + operador);
                    }
                }
            }
        }
        return null;
    }

<<<<<<< HEAD
    private Object evaluarFuncion(String nombre, List<Object> argumentos, Map<String, Object> contexto) {
        if (!funciones.containsKey(nombre)) {
            throw new IllegalArgumentException("Función no definida: " + nombre);
        }

        List<Object> definicion = funciones.get(nombre);
        List<Object> parametros = (List<Object>) definicion.get(0);
        List<Object> cuerpo = (List<Object>) definicion.get(1);

        if (parametros.size() != argumentos.size()) {
            throw new IllegalArgumentException("Cantidad incorrecta de argumentos para " + nombre + ". Esperados: " + parametros.size() + ", Recibidos: " + argumentos.size());
        }

        Map<String, Object> nuevoContexto = new HashMap<>(contexto);
        for (int i = 0; i < parametros.size(); i++) {
            nuevoContexto.put((String) parametros.get(i), evaluar(argumentos.get(i), contexto));
        }

        Object resultado = null;
        for (Object expresion : cuerpo) {
            resultado = evaluar(expresion, nuevoContexto);
        }
        return resultado;
    }

    private Object evaluarCond(List<Object> condiciones, Map<String, Object> contexto) {
        for (Object condicion : condiciones) {
            List<Object> par = (List<Object>) condicion;
            if (par.size() != 2) {
                throw new IllegalArgumentException("Cada condición en COND debe tener exactamente dos elementos.");
            }
            Object prueba = evaluar(par.get(0), contexto);
            if (Boolean.TRUE.equals(prueba) || "T".equals(prueba) || !(prueba instanceof Boolean)) {
                return evaluar(par.get(1), contexto);
            }
        }
        return null;  // Si no hay condiciones verdaderas
    }

    private Object definirFuncion(List<Object> argumentos, Map<String, Object> contexto) {
        System.out.println("Argumentos DEFUN: " + argumentos);
        if (argumentos.size() < 3) {
            throw new IllegalArgumentException("DEFUN requiere al menos 3 elementos: nombre, parámetros y cuerpo.");
        }
        String nombre = (String) argumentos.get(0);
        List<Object> parametros = (List<Object>) argumentos.get(1);
        List<Object> cuerpo = new ArrayList<>(argumentos.subList(2, argumentos.size()));

        funciones.put(nombre, List.of(parametros, cuerpo));
        System.out.println("Función definida: " + nombre + " con cuerpo " + cuerpo);
        return "Función " + nombre + " definida.";
    }

    private List<Object> parseTokens(List<String> tokens) {
        if (tokens.isEmpty()) throw new IllegalArgumentException("Expresión vacía");

        String token = tokens.remove(0);
        if (token.equals("(")) {
            List<Object> list = new ArrayList<>();
            while (!tokens.get(0).equals(")")) {
                list.add(parseTokens(tokens));
            }
            tokens.remove(0);
            return list;
        } else if (token.equals(")")) {
            throw new IllegalArgumentException("Paréntesis desbalanceados");
        } else {
            try {
                return List.of(Integer.parseInt(token));
            } catch (NumberFormatException e) {
                return List.of(token);
            }
        }
    }

    public static void main(String[] args) {
        ProcesadorLisp procesador = new ProcesadorLisp();
        Map<String, Object> contexto = new HashMap<>();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Procesador Lisp iniciado. Escribe una expresión Lisp:");
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("SALIR")) break;

            try {
                List<Object> expresion = procesador.parseTokens(new ArrayList<>(Arrays.asList(input.replace("(", " ( ").replace(")", " ) ").trim().split("\\s+"))));
                Object resultado = procesador.evaluar(expresion.get(0), contexto);
                System.out.println("Resultado: " + resultado);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        scanner.close();
        System.out.println("Procesador Lisp finalizado.");
=======
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
>>>>>>> 13c6f247de0c0a46189c83ba52d98838d0647968
    }
}