/*
 * Class of Tokens
 *
 * @author Justin Chin
 * @since 2018.04.03
 */

public final class Token{
    private String tkType = "";
    private String tkValue = ""; 
    private int col = -1;
    private int row = -1;

    // Constructor
    public Token(String tkType, String tkValue, int col, int row){
        this.tkType = tkType;
        this.tkValue = tkValue;
        this.col = col;
        this.row = row;
    }

    // Public Get Methods
    public String getTkType(){
        return tkType;
    }

    public String getTkValue(){
        return tkValue;
    }

    public int getCol(){
        return col;
    }

    public int getRow(){
        return row;
    }

    
}
