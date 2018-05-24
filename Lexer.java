/*c
 * Scanner Class to convert Pascal Source Code to Tokens
 *
 * @author Justin Chin
 * @since 2018.04.03
 */

import java.io.*;
import java.util.*;


public final class Lexer {
    // final used to make sure cannot be changed
    // static used to ensure it belongs to the class and not instance

    private static int col = 0;
    private static int row = 0;
    private static String tkValue = "";
    private static ArrayList<Token> tkList = new ArrayList<Token>();
    private static boolean isReal = false;
    private static State currState = State.SOTHER;
    //---------------------------------------------
    // ENUMS
    // -------------------------------------------

    /* 
     * FSM STATES
     *
     * SSTRING = when we see a quote
     * SNUMBER = when we see a number (integer vs real)
     * SCOMMENT = when we see a (*, ignore till *)
     * SOTHER = build tokens here
     */
    private enum State {
       SSTRING, SNUMBER, SCOMMENT, SOTHER
    }

    /*
     * CHARACTER TYPES
     *
     * sfdf
     */
    private enum Type {
        WHITESPACE, OPERATOR, NUMBER, DOT,SEMI,EQUALS,LETTER, EOF, QUOTE, STAR, PAREN
        
    }

    //----------------------------------------------
    // TABLES
    //
    // Create & Populate a static final HashTable 
    //----------------------------------------------
    
    // OPERATORS -----------------------------------
    private static final HashMap<String,String> TK_OPERATORS;
    static {
        TK_OPERATORS = new HashMap<String,String>();
        TK_OPERATORS.put("+","TK_PLUS");
        TK_OPERATORS.put("-","TK_MINUS");
        TK_OPERATORS.put("*","TK_TIMES");
        TK_OPERATORS.put("/","TK_DIVIDE");
        TK_OPERATORS.put("%","TK_MOD");
        TK_OPERATORS.put("=","TK_EQUAL");
        TK_OPERATORS.put("<>","TK_NOT_EQUAL");
        TK_OPERATORS.put(">","TK_GREATER_THAN");
        TK_OPERATORS.put("<","TK_LESS_THAN");
        TK_OPERATORS.put(">=","TK_GREATER_THAN_EQUAL");
        TK_OPERATORS.put("<=","TK_LESS_THAN_EQUAL");
        TK_OPERATORS.put("[","TK_OPEN_BRACKET");
        TK_OPERATORS.put("]","TK_CLOSE_BRACKET");
        TK_OPERATORS.put("(","TK_OPEN_PARENTHESIS");
        TK_OPERATORS.put(")","TK_CLOSE_PARENTHESIS");
        TK_OPERATORS.put(".","TK_PERIOD");
        TK_OPERATORS.put(",","TK_COMMA");
        TK_OPERATORS.put(":","TK_COLON");
        TK_OPERATORS.put(";","TK_SEMICOLON");
        TK_OPERATORS.put(":=","TK_ASSIGNMENT");
    };
    
    // TYPE ----------------------------------------
    //  Note:
    //      Decided to put into hashmap instead of if statements 
    //      because it is easier to add more special cases
    private static final HashMap<String,Type> CHARACTER_TYPE;
    static {
        CHARACTER_TYPE = new HashMap<>();
        
        for (int i = 1; i <= 32; i++){
            // populating WhiteSpaces
            CHARACTER_TYPE.put(Character.toString((char) i), Type.WHITESPACE);
        }
        
        for (int i = 48; i <= 57; i++){
            // populating Numbers
            CHARACTER_TYPE.put(Character.toString((char) i), Type.NUMBER);
        }

        for (int i = 65; i <= 90; i++){
            // populating UpperCase Letters
            CHARACTER_TYPE.put(Character.toString((char) i), Type.LETTER);
        }
        for (int i = 97; i <= 122; i++){
            // populating LowerCase Letters
            CHARACTER_TYPE.put(Character.toString((char) i), Type.LETTER);
        }
        

        // populate Quote for String
        CHARACTER_TYPE.put("'", Type.QUOTE);

        // populate Operates
        for (String key: TK_OPERATORS.keySet()){
            CHARACTER_TYPE.put(key,Type.OPERATOR);
        }

    };
    

    // KEYWORDS -------------------------------------
    private static final HashMap<String,String> TK_KEYWORDS;
    static {
        
        TK_KEYWORDS = new HashMap<String,String>();
        try{
            Scanner input = new Scanner(new File("keywords.txt"));
            while(input.hasNext()){
                String keyword = input.next().toUpperCase();
                TK_KEYWORDS.put(keyword,"TK_"+keyword);
        }}
        catch(FileNotFoundException e){
            System.out.println("Keywords.txt File Not Found...");
            System.out.println("Exiting Compiler. Goodbye.");
            System.exit(0);
        }

    };
    
    //----------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------

    /*
     * Method called to scan a file 
     *
     * @param file a pascal file
     * @return an arrayList of Tokens
     */
    
    public static ArrayList<Token> scan(File file) throws IOException {
        
        Scanner input = new Scanner(file).useDelimiter(""); // remove delimiter;
        // Loop through each char until there is no more
        while(input.hasNext()){
            
            // convert to character
            String element = String.valueOf(input.next().charAt(0));
           // System.out.println("row"+" col"+" character");
           // System.out.println(row+" "+col+" "+element);
            col += 1;
            if (element.equals("\n")){
                row += 1;
                col = 0;
            }
            
            // handle character depending on the state
            switch (currState){
                case SSTRING:
                    handleString(element);
                    break;
                case SNUMBER:
                    handleNumber(element);
                    break;
                case SCOMMENT:
                    handleComment(element);
                    break;
                default:
                    // SOTHER
                    handleOther(element);
                    break;
            }
        }

        // Broke out of loop so EOF
        col += 1;
        tkValue = "EOF";
        createTk("TK_EOF");
        return tkList;
    }


    /*
     * Clears Info After Assigning Token
     *
     */
    public static void clear(){
        tkValue = "";    
    }

    /*
     * Creates Tokens and adds them the tkList
     *
     * @param tkString String
     */
    public static void createTk(String tkString){
        Token tk = new Token(tkString, tkValue, col, row);
        tkList.add(tk);
        //System.out.println(tkString);
        clear();
    }

    //----------------------------------------------
    // STATE HANDLING METHODS
    //----------------------------------------------

    /*
     * Handles String State
     * checks the kind of element in the table
     *
     * @param char element
     * 
     */
    public static void handleString(String element){
        switch (CHARACTER_TYPE.get(element)){
            case QUOTE:
                // end string building
                tkValue += element;
                createTk("TK_STRING");
                currState = State.SOTHER;
                break;
            default:
                // continue string building
                tkValue += element;
                break;
        }
    }
    
    
    /*
     * Handles Number State
     *
     * @param char element
     *
     */
    public static void handleNumber(String element){
        switch(CHARACTER_TYPE.get(element)){
            case NUMBER:
                // continue number building
                tkValue += element;
                break;
            case OPERATOR:
                if (element.equals(".")){
                // real number building
                tkValue += element;
                isReal = true;
                break;
                }
                
                if (element.equals(";")){
                    currState = State.SOTHER;
                    if (isReal)
                        createTk("TK_REAL");
                    else
                        createTk("TK_INTEGER");

                    tkValue += element;
                    if (TK_OPERATORS.containsKey(element)){
                        createTk(TK_OPERATORS.get(element));
                
                    }
                }
                break;
            default:
                // end number building
                currState = State.SOTHER;
                if (isReal)
                    createTk("TK_REAL");
                else
                    createTk("TK_INTEGER");
                break;

        }
    }



    /*
     * Handles Comment State
     *
     * @param char element
     */
    public static void handleComment(String element){
        switch(CHARACTER_TYPE.get(element)){
            case OPERATOR:
                if (element.equals("*")){
                // signals possible comment end
                tkValue = "TK_TIMES";
                break;
                }
            case PAREN:
                // end comment building
                if (!tkValue.equals("")){ 
                    currState = State.SOTHER;
                    createTk("TK_COMMENT");
                }    
                break;
            default:
                // continue comment building
                tkValue += element;
                break;
        }
    }

    /*
     * Checks if tkValue is an operator or keywords and creates tk if so
     */
    public static void checkOPKEY(){
        //System.out.println("checkOPKEY....tkValue....."+ tkValue);
        //OPERATOR
        if (TK_OPERATORS.containsKey(tkValue)){
            createTk(TK_OPERATORS.get(tkValue));
        } // KEYWORDS
        else if (TK_KEYWORDS.containsKey(tkValue.toUpperCase())){
            createTk(TK_KEYWORDS.get(tkValue.toUpperCase()));
        } // ID
        else {
            if (!tkValue.equals("")){
                createTk("TK_ID");
            }
        }
        
    }

    /*
     * Handles Other State
     *
     * @param char element
     */
    public static void handleOther(String element){
        //System.out.println("CharacterType: "+CHARACTER_TYPE.get(element));
        switch(CHARACTER_TYPE.get(element)){
            case NUMBER:
                // start number state
                currState = State.SNUMBER;
                tkValue += element;
                break;
            case QUOTE:
                // start string state
                checkOPKEY();
                currState = State.SSTRING;
                tkValue += element;
                break;
            case WHITESPACE:
                if (!tkValue.equals("")){
                  checkOPKEY(); 

                }
                break;
            case OPERATOR:
                // Equals
                if (element.equals("=")){
                    if (tkValue.equals(":")){
                        tkValue += element;
                        createTk(TK_OPERATORS.get(tkValue));
                    }
                    else {
                        createTk(TK_OPERATORS.get(element));
                    }
                    break;
                }

                // Star
                if (element.equals("*")){
                    if (tkValue.equals("(")){ 
                        // start comment state
                        currState = State.SCOMMENT;
                        tkValue += element;
                    }
                    else {
                        createTk(TK_OPERATORS.get(element));
                    }
                    break;
                }

                // Semicolon
                if (element.equals(";")){
                    
                    if (TK_OPERATORS.containsKey(tkValue.toUpperCase())){
                        // OPERATORS
                        createTk(TK_OPERATORS.get(tkValue.toUpperCase()));
                    }
                    else if (TK_KEYWORDS.containsKey(tkValue.toUpperCase())){
                        // KEYWORDS
                        createTk(TK_KEYWORDS.get(tkValue.toUpperCase()));
                    }
                    else if (tkValue.equals("")){
                    }
                    else {
                        // IDENTIFIER TK
                        createTk("TK_ID");
                    }

                    // Create SEMI TK
                    tkValue = element;
                    createTk(TK_OPERATORS.get(element)); 
                    break;
                }


                
                // Any other operate, create token

                checkOPKEY(); 
                tkValue += element;
                break;
            default:
                // if OPERATOR
                if (TK_OPERATORS.containsKey(tkValue.toUpperCase())){
                    createTk(TK_OPERATORS.get(tkValue.toUpperCase()));
                }

                // we have a letter 
                tkValue += element;
                break;
        }

    }







}

