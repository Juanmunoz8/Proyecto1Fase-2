import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;

public class Lisptest {
    @Test
    void testLexer() {
        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.tokenizar("(+ 1 2)");
        assertEquals(5, tokens.size());
        assertEquals(TiposTokens.Parentesis_Abierto, tokens.get(0).getTipo());
        assertEquals(TiposTokens.Identificador, tokens.get(1).getTipo());
        assertEquals("+", tokens.get(1).getValor());
        assertEquals(TiposTokens.Numero_entero, tokens.get(2).getTipo());
        assertEquals("1", tokens.get(2).getValor());
    }

    @Test
    void testParser() {
        Lexer lexer = new Lexer();
        Parser parser = new Parser();
        List<Token> tokens = lexer.tokenizar("(+ 1 2)");
        parser.setTokens(tokens);
        Object parsed = parser.parseExpression();
        assertTrue(parsed instanceof List<?>);
        List<?> list = (List<?>) parsed;
        assertEquals("+", list.get(0));
        assertEquals(1, list.get(1));
        assertEquals(2, list.get(2));
    }

    @Test
    void testEvaluador() {
        Evaluador evaluador = new Evaluador();
        List<Object> expr = Arrays.asList("+", 3, 5);
        Object result = evaluador.evaluar(expr);
        assertEquals(8, result);
    }
}
