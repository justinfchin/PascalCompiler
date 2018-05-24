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

        // Make sure the file exists
        try{
            
            // LEXER
           // System.out.println("Getting Your File...");
            // Convert the File to tokens and store in ArrayList
            ArrayList<Token> tkList = Lexer.scan(new File(args[0]));
            // Print tklist
           // System.out.println("--------PRINTING TKLIST----------");
          //  for (Token tk : tkList){
            //    System.out.println(tk.getTkType()+" "+tk.getTkValue());
           // }

            // PARSER
            //System.out.println("Parsing...");
            Byte[] instructions = Parser.parse(tkList);
            // Print instructions
            //System.out.println("----------PRINTING INSTRUCTIONS--------");
           // for (Byte i : instructions){
             //   if (i != null){
              //  System.out.println(i);
               // }

            //}

            // GENERATOR
            //System.out.println("Generating...");
            Generator.generate(instructions);

        }
        catch(IOException e){
            System.out.println("Unable to find source code file...");
            System.out.println("Exiting Compiler. Goodbye.");
            System.exit(0);
        }

    };

}

