import java.util.*;

class Funcion {
    String nombre;
    List<String> parametros;
    List<?> cuerpo;

    public Funcion(String nombre, List<?> parametros, List<?> cuerpo) {
        this.nombre = nombre;
        this.parametros = new ArrayList<>();
        for (Object param : parametros) {
            this.parametros.add((String) param);
        }
        this.cuerpo = cuerpo;
    }

    public Object ejecutar(List<?> argumentos, Evaluador evaluador) {
        if (argumentos.size() != parametros.size()) {
            throw new RuntimeException("Número incorrecto de argumentos en la llamada a " + nombre);
        }

        Map<String, Object> contextoLocal = new HashMap<>();

        for (int i = 0; i < parametros.size(); i++) {
            contextoLocal.put(parametros.get(i), evaluador.evaluar(argumentos.get(i)));
        }

        return evaluador.evaluarEnContexto(cuerpo, contextoLocal);
    }
}

public class Evaluador {
    private final Map<String, Object> variables = new HashMap<>();
    private final Map<String, Funcion> funciones = new HashMap<>();

    public Object evaluar(Object expresion) {
        if (expresion instanceof List<?>) {
            List<?> lista = (List<?>) expresion;
            if (lista.isEmpty())
                return null;
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
                case "EQUAL":
                    return evaluar(lista.get(1)).equals(evaluar(lista.get(2)));
                case "ATOM":
                    return !(evaluar(lista.get(1)) instanceof List<?>);
                case "DEFUN": {
                    String nombreFuncion = (String) lista.get(1);
                    List<?> parametros = (List<?>) lista.get(2);
                    List<?> cuerpo = lista.subList(3, lista.size());
                    funciones.put(nombreFuncion, new Funcion(nombreFuncion, parametros, cuerpo));
                    return "Función '" + nombreFuncion + "' definida correctamente";
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
                case "PROGN": {
                    Object resultado = null;
                    for (int i = 1; i < lista.size(); i++) {
                        resultado = evaluar(lista.get(i));
                    }
                    return resultado;
                }
                default:
                    if (funciones.containsKey(operador)) {
                        Funcion funcion = funciones.get(operador);
                        List<?> argumentos = lista.subList(1, lista.size());
                        return funcion.ejecutar(argumentos, this);
                    }
                    return null;
            }
        } else if (expresion instanceof String varName && variables.containsKey(varName)) {
            return variables.get(varName);
        } else {
            try {
                return Double.parseDouble(expresion.toString()) % 1 == 0
                        ? Integer.parseInt(expresion.toString()) // convierte enteros como "5.0" a 5
                        : Double.parseDouble(expresion.toString());
            } catch (NumberFormatException e) {
                return expresion;
            }
        }
    }

    public Object evaluarEnContexto(List<?> expresiones, Map<String, Object> contexto) {
        Map<String, Object> copiaVariables = new HashMap<>(variables);
        variables.putAll(contexto);

        Object resultado = null;
        for (Object expresion : expresiones) {
            resultado = evaluar(expresion);
        }

        variables.clear();
        variables.putAll(copiaVariables);

        return resultado;
    }
}
    