/*
 * Class of Tokens
 *
 * @author Justin Chin
 * @since 2018.04.03
 */

public final class Token{
    private String tkType = "";
    private String tkValue = ""; 

    // Constructor
    public Token(String tkType, String tkValue){
        this.tkType = tkType;
        this.tkValue = tkValue;
    }

    // Public Get Methods
    public String getTokenType(){
        return tkType;
    }

    public String getTokenValue(){
        return tkValue;
    }

    
}
