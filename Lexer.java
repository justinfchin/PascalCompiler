/*
 * Scanner Class to convert Pascal Source Code to Tokens
 *
 * @author Justin Chin:
 * @since 2018.04.03
 */

import java.io.*;
import java.util.*;


public final class Lexer {
    // final used to make sure cannot be changed
    // static used to ensure it belongs to the class and not instance

    private static int col = 0;
    private static int row = 0;
    private static String tkType = "";
    private static String tkValue = "";
    private static ArrayList<Token> tkList = new ArrayList<Token>();

    /*
     * Method called to scan a file 
     *
     * @param file a file
     * @return an arrayList of Tokens
     */
    
    public static ArrayList<Token> scan(File file) throws IOException {
        
        Scanner input = new Scanner(file).useDelimiter(""); // remove delimiter;
        while(input.hasNext()){
            // convert to character
            char element = input.next().charAt(0);
            System.out.println("line"+" col"+" character");
            System.out.println(row+" "+col+" "+element);
            col += 1;
            if (element == '\n'){
                System.out.println("TRRUREUEU");
                row += 1;
                col = 0;
            }
            assignTK(element);
            }

        // Broke out of loop so EOF
        

        return tkList;
    };


    /*
     * Assigns the Correct Token to the Character
     *
     * @param character char
     * @returns 
     */
    public static void assignTK(char character){
    
        // Ignore Comments & Source Code formatting like blanks/tabs
    }

    /*
     * Creates Tokens and adds them the tkList
     *
     * @param tkString String
     */
    public static void createToken(String tkString){
    
    }

    //---------
    // TABLES
    //
    // Create & Populate a static final HashTable 
    //---------
    
    // OPERATORS
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
    };

    // KEYWORDS from txt file
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


}

