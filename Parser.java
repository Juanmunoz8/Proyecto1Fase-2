import java.util.*;
public class Parser {
    private List<Token> tokens;
    private int posicion;
    
    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
        this.posicion = 0;
    }
    
    private Token consume() {
        return tokens.get(posicion++);
    }
    
    private Token peek() {
        return tokens.get(posicion);
    }
    
    public Object parseExpression() {
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
        } else {
            return token.getValor();
        }
    }
}
