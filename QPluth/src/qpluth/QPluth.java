/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qpluth;

import java.io.File;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 *
 * @author APluth
 */
public class QPluth {

    static String[] data = {"T1.txt","T2.txt","T3.txt"};
    File t1,t2,t3;
    Qlexeme query = null;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ArrayList<ArrayList<ArrayList<String>>> t; // 3D array for tables of data
        boolean bob = true;
        // read in files 
        
        // from command line? or default files
        if(checkInput(args)){
           t = readFile(args);
        }
        else{
           t = readFile(data);
        }
        
        // Lopp for Queries
        while(bob){//loop till bob not true or break -_-
            Scanner sc = new Scanner(System.in);
            // promt for security level
            System.out.println("Enter a Security Level:\n\t");
            try{int i = sc.nextInt();System.out.println(i);}
            catch(InputMismatchException m){System.out.println("Please Enter A Number\n -_- \n");}
            
        }
        
        
        //process queries until exit

    }
    
    
    
    public static ArrayList<ArrayList<ArrayList<String>>> readFile(String[] args){
        ArrayList<ArrayList<ArrayList<String>>> tables = new ArrayList<>();
        QDataReader dReader = new QDataReader();
            
        for(String a : args){
//            System.out.print("NEW Table: \n");
            tables.add(dReader.read(a));
        }
        
        return tables;
    }
    
    public static boolean checkInput(String[] args){
        final boolean status = false;
        if(args.length<3){
            System.out.print("We Need More Than Two Arguments, Proceeding with Defualt Data\n");
            for(String a:args){System.out.print(a + "\n");}
            return status;
        }
        else if(args[0].endsWith(".txt")&&args[1].endsWith(".txt")&&args[2].endsWith(".txt")){
            // all three have to be .txt
            System.out.print("Proceeding with Data Files:\n");
            for(String a:args){System.out.print("\t"+a+"\n");}
            return !status;
        }
        else{
            System.out.print("Sorry We need Three (or more?) .txt Files\n~~~~~~~~`Maybe More in the Future~~~~\n");
            return status;
        }
    }

    
}
