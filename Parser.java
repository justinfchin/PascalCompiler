/*
 * Parser Class to Understand the Tokens from Scanner Class
 *
 * @author Justin Chin
 * @since 2018.04.05
 */

import java.io.*;
import java.util.*;

public final class Parser {

   

    private static Token currTk;
    private static Iterator<Token> iter;
    private static Byte[] instructions = new Byte[100];
    private static int ip = 0;
    private static int address = 0;
    public static enum OPCODE {
        ADD, SUB, PUSH, POP, EXIT, PUSHS, PRINTS, PUSHI, PRINTI,
        PUSHC, PRINTC, PUSHR, PRINTR, JUMP, LT
    }
    private static final HashMap<String,Symbol> symTable;
    static {
        symTable = new HashMap<>();
    }



    /*
     * Parses Tokens
     */
    public static Byte[] parse(ArrayList<Token> tkList){
        // start with iterator
        iter = tkList.iterator();
        // get beginning token
        getNextTk();

        // tokens should be in following order, if yes then move on
        checkNext("TK_PROGRAM");
        checkNext("TK_ID");
        checkNext("TK_SEMICOLON");
        //
        declarations();
        return instructions;
    
    }

    /*
     * Sets currTk as nextTk
     *
     */
    public static void getNextTk(){
        // added if statement for EOF
        if (iter.hasNext()){
            currTk = iter.next();
         //   System.out.println("GetNextTK: "+currTk.getTkType());
        }
    }

    
    /*
     * Adds instructions to byte at current ip
     * then increase ip 
     */
    public static void addInstruct(OPCODE op){
//        System.out.println("IP: "+ip+" Instruction: "+ (byte) op.ordinal()+" "+ op);
        instructions[ip] = (byte) op.ordinal();
        ip++;
    }



    /*
     * Checks if currentTk matches parameter Tk, if it does then getNextTk
     *
     * @param String tkType
     * @return boolean true if matches else false
     */
    public static boolean checkNext(String tkType){
        if (tkType.equals(currTk.getTkType())){
            getNextTk();
            return true;
        }
       // System.out.println("***SYNTAX ERROR*** "+ tkType +" != "+currTk.getTkType() +" Check Line: "+currTk.getRow()+" Col: "+currTk.getCol()); 
        System.exit(0);
        return false;
    }


    //---------------------------------------------------
    // DECLARATIONS
    // --------------------------------------------------
    
    /*
     * eg. VAR -> BEGIN
     */
    public static void declarations(){
        while(true){
            switch(currTk.getTkType()){
                case "TK_BEGIN":
                    declarationBegin();
                    return;
                case "TK_VAR":
                    declarationVar();
                    break;
                default:
                    System.out.println("***DECLARATION NOT FOUND***");
                    System.exit(0);
                    break;
            }   
        }
    }


    public static void declarationBegin(){
        getNextTk();
        statements();
        checkNext("TK_END");
        checkNext("TK_PERIOD");
        checkNext("TK_EOF");
        // finished create exit
        addInstruct(OPCODE.EXIT);
    }

    public static void declarationVar(){
        getNextTk();
        // Store Variable in a List
        ArrayList<Token> varList = new ArrayList<>();

        while(currTk.getTkType().equals("TK_ID")){
            varList.add(currTk);
            getNextTk();
            if (currTk.getTkType().equals("TK_COMMA")){
                getNextTk();
            }    
        }

        checkNext("TK_COLON");
        String tempTk = currTk.getTkType();
        getNextTk();

        // cycle through all the variable list
        for (Token tk : varList){
            // create a new symbol
            Symbol symbol = new Symbol(tk.getTkValue(), "TK_ID", tempTk.substring(3),address);
            // increase address
            address += 4; 
            
            // insert symbol in symTable
            symTable.put(symbol.getName(),symbol);
        }

        // if we have an array
        if (tempTk.equals("TK_ARRAY")){
            declarationArray(varList);
        }

            
        //System.out.println("CHECKING DECLARATION>>>>>>>:");
        checkNext("TK_SEMICOLON");
    }

    public static void declarationArray(ArrayList<Token> varlist){

    }

    //---------------------------------------------------
    // STATEMENTS
    // --------------------------------------------------

    /*
     * Switch Statement in Order to Properly Determine What to do with Token
     */
    public static void statements(){
        while(!currTk.getTkType().equals("TK_END")){
        //System.out.println("STATEMENTS:::::::::::::"+currTk.getTkType());
            switch(currTk.getTkType()){
                case "TK_WRITELN":
                    stateWriteln();
                    break;
                case "TK_IF":
                    stateIf();
                    break;
                case "TK_ID":
                    stateID();
                    break;
                case "TK_WHILE":
                    stateWhile();
                    break;
                case "TK_ARRAY":
                    stateArray();
                    break;
                case "TK_SEMICOLON":
                    getNextTk();
                    break;
                default:
                    System.out.println("***ERROR*** Missing Statement Case");
                    System.exit(0);
            }
        }
    }

    /*
     * WRITELN STATEMENT
     */
    public static void stateWriteln(){
        getNextTk();
        checkNext("TK_OPEN_PARENTHESIS");
        
        // check if we have a variable / symbol in SymbolTable
        
        //
        if (currTk.getTkType().equals("TK_ID")){
            addInstruct(OPCODE.PUSH);
            addInstruct(OPCODE.PRINTI);
        }
        else{
            addInstruct(OPCODE.PUSHS);
            byte[] temp= currTk.getTkValue().getBytes();
            
            for (byte t: temp){
                instructions[ip]= t;
                ip++;
            } 
            addInstruct(OPCODE.PRINTS);
        }

        getNextTk();
        checkNext("TK_CLOSE_PARENTHESIS");
        checkNext("TK_SEMICOLON");
    }

    /*
     * IF STATEMENT
     */
    public static void stateIf(){
        getNextTk();
        condition();
        checkNext("TK_THEN");
        // false
        statements();

        if (currTk.equals("TK_ELSE")){
    
            addInstruct(OPCODE.JUMP);
            getNextTk();
            statements();
        }

    }

    /*
     * VAR STATEMENT
     */
    public static void stateID(){
        // lookup current token in Symtable, which is our variable
        Symbol sym = symTable.get(currTk.getTkValue());
        //System.out.println("SYM:::" + sym+" "+sym.getName()+" "+sym.getTkType()+" "+sym.getTkValue()+" "+ sym.getAddress());
        if (sym != null) {
            // 
            String symType = sym.getTkValue();

            getNextTk(); // assignment
            getNextTk(); 

            String temp = expression();
            if (symType.equals("INTEGER")){
               addInstruct(OPCODE.POP);
            }
            else{
               System.out.println("***ERROR*** VAR ID TYPE NOT MATCH"); 
            }

        }

    }


    /*
     * WHILE STATEMENT
     */
    public static void stateWhile(){
        getNextTk();

        // handle conditions
        condition();
        //System.out.println("INWHILE>>>>>>>" + currTk.getTkType());
        
        checkNext("TK_DO");
        checkNext("TK_BEGIN");
        //System.out.println("AFTER BEGIN>>>>>> "+currTk.getTkType());
        statements();
        checkNext("TK_END");
        checkNext("TK_SEMICOLON");
    }

    /*
     * ARRAY STATEMENTS
     */
    public static void stateArray(){

    }

    //------------------------------------------------------------
    // LOGIC & GRAMMAR - condition, expression, term, factor
    // -----------------------------------------------------------

    /*
     * CONDITION
     */
    public static void condition(){
        String e1 = expression();
    //    System.out.println("CONDITTTION>>>>"+e1+" and now curtkType " + currTk.getTkType());
        if (currTk.getTkType().matches("TK_GREATER_THAN|TK_LESS_THAN|TK_GREATER_THAN_EQUAL|TK_LESS_THAN_EQUAL|TK_EQUAL| TK_NOT_EQUAL")){
            String operation = currTk.getTkType();
      //      System.out.println("CONDITION BODY " + operation); 
            getNextTk();
          //  System.out.println(currTk.getTkType());
            String e2 = term();
            
            e1 = handle(operation,e1,e2);    
        }

    //return e1;
    }

    /*
     * EXPRESSION
     *
     * e + t | e - t | t
     */
    public static String expression(){
        String t1 = term();
       // System.out.println("EXPRESSSSIONNNNN...."+t1);

        if (currTk.getTkType().matches("TK_PLUS|TK_MINUS")){
            String operation = currTk.getTkType();
            getNextTk();
            String t2 = term();

            t1 = handle(operation, t1, t2);
        }

        return t1;
    }

    /*
     * TERM
     *
     * t * f | t / f | f
     */
    public static String term(){
        String f1 = factor();
       // System.out.println("TERRRMMMMMMM......"+f1);

        if (currTk.getTkType().matches("TK_MULTIPLY|TK_DIVIDE")){
            String operation = currTk.getTkType();
            getNextTk();
            String f2 = factor();

            f1 = handle(operation,f1,f2);
        }

        return f1;
    }

    /*
     * FACTOR
     *
     * id | num | e
     */
    public static String factor(){
       // System.out.println("FACTOR::::: "+currTk.getTkType()+" VALUE>>>"+currTk.getTkValue());
        switch(currTk.getTkType()){
            case "TK_ID":
                Symbol sym = symTable.get(currTk.getTkValue());
                String symTkType = sym.getTkType();
                if (symTkType.equals("TK_ID")){
                    // we have a variable
                    addInstruct(OPCODE.PUSH);
                    getNextTk();
                    return sym.getTkValue();
                }

                break;
            case "TK_INTEGER":
                addInstruct(OPCODE.PUSHI);
                instructions[ip] = Byte.valueOf(currTk.getTkValue());
                ip++;
                getNextTk();
                return "INTEGER";
            case "TK_STRING":
                break;
            case "TK_CHAR":
                addInstruct(OPCODE.PUSHC);
                getNextTk();
                break;
            case "TK_REAL":
                addInstruct(OPCODE.PUSHR);
                getNextTk();
                break;
            default:
                System.out.println("***ERROR*** Unknown Factor Type");
                System.exit(0);
        }

        return "HI";
    }

    /*
     * Handle
     */
    public static String handle(String operation, String a, String b){
        //System.out.println("HANDLEEEEEE........."+operation+ " "+a+ " "+ b);
        switch (operation){
            case "TK_LESS_THAN":
                addInstruct(OPCODE.LT);
                return "BOOL";
        }

        return a;
    }


}

