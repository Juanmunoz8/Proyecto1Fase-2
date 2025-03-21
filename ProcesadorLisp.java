import java.util.*;

public class ProcesadorLisp {
    private final Map<String, Object> variables = new HashMap<>();
    private final Map<String, List<Object>> funciones = new HashMap<>();

    public Object evaluar(Object expresion, Map<String, Object> contexto) {
        if (expresion instanceof Integer) {
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
    }
}
