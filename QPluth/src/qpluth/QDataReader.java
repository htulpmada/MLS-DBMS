/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qpluth;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 *
 * @author gomez
 */
public class QDataReader {
    
    int lineNum = 0;
    int charNum = 0;
    
    public QDataReader(){}

    public ArrayList<ArrayList<String>> read(String a) {
        lineNum = 0;
        charNum = 0;
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        ArrayList<String> dataLine = new ArrayList<>();
         try {
            for (String line : Files.readAllLines(Paths.get(a))) {
                lineNum++;
                for (String part : line.split("\\s+")) {
                    charNum++;
                    dataLine.add(part);
                }
                if(dataLine.size()>1){data.add(dataLine);}
                dataLine = new ArrayList<>();
            }
            //printData(data);
            return data;
        } catch (IOException i) {
            System.out.print("Error Finding File: " + a + "\n");
            System.exit(1);
        }
        return data;
    }
    
    public void printData(ArrayList<ArrayList<String>> arr){
        for(ArrayList<String> ar : arr){
            for(String a : ar){
                System.out.print( a +"\t");
            }
            System.out.print("\n");
        }
    }
    
}
