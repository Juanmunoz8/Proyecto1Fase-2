import java.util.*;
public class Evaluador {
    private final Map<String, Object> variables = new HashMap<>();
    
    public Object evaluar(Object expresion) {
        if (expresion instanceof List<?>) {
            List<?> lista = (List<?>) expresion;
            if (lista.isEmpty()) return null;
            String operador = (String) lista.get(0);
            return switch (operador) {
                case "+" -> (int) evaluar(lista.get(1)) + (int) evaluar(lista.get(2));
                case "-" -> (int) evaluar(lista.get(1)) - (int) evaluar(lista.get(2));
                case "*" -> (int) evaluar(lista.get(1)) * (int) evaluar(lista.get(2));
                case "/" -> (int) evaluar(lista.get(1)) / (int) evaluar(lista.get(2));
                case "SETQ" -> {
                    String varName = (String) lista.get(1);
                    Object value = evaluar(lista.get(2));
                    variables.put(varName, value);
                    yield value;
                }
                case "QUOTE" -> lista.get(1);
                default -> null;
            };
        } else if (expresion instanceof String varName && variables.containsKey(varName)) {
            return variables.get(varName);
        } else {
            return expresion;
        }
    }
}
