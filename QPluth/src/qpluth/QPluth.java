package qpluth;

import java.io.File;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 *
 * @author Adam Pluth
 */
public class QPluth {

    static String[] data = {"T1.txt","T2.txt","T3.txt"};
    static String[] query;
    static Scanner sc;
    static int sLvl = 1;// security level
    static QObject QObj;
    File t1,t2,t3;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ArrayList<ArrayList<ArrayList<String>>> t; // 3D array for tables of data
        boolean bob = true;// condition of loop
        sc = new Scanner(System.in);            
        QObj = new QObject();
        // read in files 
        // from command line? or default files
        if(checkInput(args))
            {t = readFile(args);}
        else
            {t = readFile(data);}
        // Lopp for Queries
        while(bob){//loop till bob not true or break -_-
            // promt for security level
            sLvl = securityRequest();
            // promt and read Query
            getQuery();
            // parse and execute query
            QObj.parse(query);
            //process queries until exit on bad query or exit word
            bob = QObj.execute(t,sLvl);
        }
    }

    public static void getQuery(){
        String token = "";
        System.out.println("Enter a Query:\n\t");
        while(!token.endsWith("; ")){
            token += sc.next();
            token += " ";
        }
        token = token.split(";")[0];
        query = token.split(" ");
    //    System.out.print(token + "\n");
    //    for(String q : query){System.out.print(q + "\n");}
        if(token.toLowerCase().compareTo("exit")==0){System.exit(0);}
    }
    
    public static int securityRequest(){
        int i;
        System.out.println("Enter a Security Level:\n\t");
        try{
            i = sc.nextInt();
        }
        catch(InputMismatchException m){
            System.out.println("Please Enter A Number\n -_- \n");
            i = securityRequest();
        }
        return i;
    }
    
    public static ArrayList<ArrayList<ArrayList<String>>> readFile(String[] args){
        ArrayList<ArrayList<ArrayList<String>>> tables = new ArrayList<>();
        QDataReader dReader = new QDataReader();
        int i = 1;    
        for(String a : args){
            System.out.print("Reading Table:\n\t" + i + "---" + a + "\n");
            tables.add(dReader.read(a));
            i++;
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
