import java.util.*;

public class EjecutorLisp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Lexer lexer = new Lexer();
        Parser parser = new Parser();
        Evaluador evaluador = new Evaluador();

        System.out.println("Ingrese una o más expresiones Lisp (separadas por línea):");
        StringBuilder entrada = new StringBuilder();
        while (true) {
            String linea = scanner.nextLine();
            if (linea.isBlank())
                break; // Enter en blanco para terminar entrada
            entrada.append(linea).append(" ");
        }

        // Divide en expresiones individuales basadas en balanceo de paréntesis
        List<String> expresiones = dividirExpresionesBalanceadas(entrada.toString().trim());

        for (String expresion : expresiones) {
            try {
                List<Token> tokens = lexer.tokenizar(expresion);
                parser.setTokens(tokens);
                Object estructuraParseada = parser.parseExpression();
                Object resultado = evaluador.evaluar(estructuraParseada);
                System.out.println("\nExpresión: " + expresion);
                System.out.println("Resultado Evaluado: " + resultado);
            } catch (Exception e) {
                System.out.println("Error al procesar la expresión: " + expresion);
                e.printStackTrace();
            }
        }

        scanner.close();
    }

    // Método auxiliar para separar expresiones completas (basado en paréntesis
    // balanceados)
    private static List<String> dividirExpresionesBalanceadas(String input) {
        List<String> expresiones = new ArrayList<>();
        int balance = 0;
        StringBuilder buffer = new StringBuilder();

        for (char c : input.toCharArray()) {
            buffer.append(c);
            if (c == '(')
                balance++;
            if (c == ')')
                balance--;

            if (balance == 0 && buffer.length() > 0) {
                expresiones.add(buffer.toString().trim());
                buffer.setLength(0);
            }
        }

        return expresiones;
    }
}
