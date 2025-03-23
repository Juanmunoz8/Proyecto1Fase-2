import java.util.*;
public class EjecutorLisp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Lexer lexer = new Lexer();
        Parser parser = new Parser();
        Evaluador evaluador = new Evaluador();

        System.out.print("Ingrese una expresión Lisp: ");
        String expression = scanner.nextLine();
        
        List<Token> tokens = lexer.tokenizar(expression);
        parser.setTokens(tokens);
        Object estructuraParseada = parser.parseExpression();
        
        Object resultado = evaluador.evaluar(estructuraParseada);
        
        // Muestra el resultado de analisis de Lexer, Parser y Evaluador
        System.out.println("\nResultado Evaluado:");
        System.out.println(resultado);
        scanner.close();
    }
}
