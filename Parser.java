import java.util.*;
public class Parser {
    private List<Token> tokens;
    private int posicion;
    
    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
        this.posicion = 0;
    }
    //Permite al parser continuar al siguiente elemento de la lista 
    private Token consume() {
        return tokens.get(posicion++);
    }
    
    private Token peek() {
        return tokens.get(posicion);
    }
    
    // Procesa los tokens dentro de la expresion
    public Object parseExpression() {
        Token token = consume();
    
        if (token.getTipo() == TiposTokens.Parentesis_Abierto) {
            List<Object> lista = new ArrayList<>();
            while (peek().getTipo() != TiposTokens.Parentesis_Cerrado) {
                lista.add(parseExpression());
            }
            consume(); 
            return lista;
            // Retorna los tokens con su respectivo valor numerico
        } else if (token.getTipo() == TiposTokens.Numero_entero) {
            return Integer.parseInt(token.getValor());
        } else if (token.getTipo() == TiposTokens.Numero_decimal) {
            return Double.parseDouble(token.getValor());
        } else {
            // Regresan los tokens que no son numericos sin modificar, ya que la logica de estos estara en Evaluador
            return token.getValor();  
        }
    }
}
