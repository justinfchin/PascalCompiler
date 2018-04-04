/*
 * Author: Justin Chin
 * Purpose:
 *  Main Program
 */

import java.util.*;
import java.io.*;

public final class Compiler{

    public static void main(String args[]){
        // Make sure something is passed as an arg
        if (args.length == 0){
            System.out.println("Please provide source code...");
            System.out.println("Exiting Compiler. Goodbye.");
            System.exit(0);
        }

        // Create the lexer class
        Lexer test = new Lexer();
        
        // Make sure the file exists
        try{
            
            System.out.println("Getting Your File...");
            // Convert the File to tokens and store in ArrayList
            ArrayList<Token> tkList = test.scan(new File(args[0]));
        }
        catch(IOException e){
            System.out.println("Unable to find source code file...");
            System.out.println("Exiting Compiler. Goodbye.");
            System.exit(0);
        }

        // Pass TokenList to Parser
        System.out.println("Parsing...");
    
    };

}

