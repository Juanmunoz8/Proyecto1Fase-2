import java.util.*;
public class Evaluador {
    private final Map<String, Object> variables = new HashMap<>();

    
    public Object evaluar(Object expresion) {
        if (expresion instanceof List<?>) {
            List<?> lista = (List<?>) expresion;
            if (lista.isEmpty()) return null;
            String operador = (String) lista.get(0);
            switch (operador) {
                case "+":
                    return (int) evaluar(lista.get(1)) + (int) evaluar(lista.get(2));
                case "-":
                    return (int) evaluar(lista.get(1)) - (int) evaluar(lista.get(2));
                case "*":
                    return (int) evaluar(lista.get(1)) * (int) evaluar(lista.get(2));
                case "/":
                    return (int) evaluar(lista.get(1)) / (int) evaluar(lista.get(2));
                case "SETQ": {
                    String varName = (String) lista.get(1);
                    Object value = evaluar(lista.get(2));
                    variables.put(varName, value);
                    return value;
                }
                case "QUOTE":
                    return lista.get(1);
                case "EQUAL": {
                    Object value1 = evaluar(lista.get(1));
                    Object value2 = evaluar(lista.get(2));
                    return value1.equals(value2);
                }
                case "ATOM": {
                    Object value = evaluar(lista.get(1));
                    return !(value instanceof List<?>);
                }
                case "List":
                    return lista.subList(1, lista.size());
                case "COND": { 
                    for (int i = 1; i < lista.size(); i++) {
                        List<?> condicion = (List<?>) lista.get(i);
                        if ((boolean) evaluar(condicion.get(0))) {
                            return evaluar(condicion.get(1));
                        }
                    }
                    return null;
                }
                default:
                    return null;
            }
        } else if (expresion instanceof String varName && variables.containsKey(varName)) {
            return variables.get(varName);
        } else {
            return expresion;
        }
    }
}

    