/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qpluth;

import java.util.ArrayList;

/**
 *
 * @author gomez
 */
class QObject {
    
    ArrayList<ArrayList<ArrayList<String>>> tables;
    String token[];
    ArrayList<String> select,from,where;
    boolean w,valid;
    
    public QObject(){
        tables = new ArrayList<>();

    }

    public void parse(String[] t) {
        
        select = new ArrayList<>();
        from = new ArrayList<>();
        where = new ArrayList<>();
        valid = false;
        // search for select keyword
        for(int i = 0; i < t.length; i++){
            if(t[i].toLowerCase().compareTo("from")==0){
                for(int j = 1; j < i; j++){
                    select.add(t[j]);
                }
                break;
            }
        }
        //clean up chars
        for(int i = 0; i < select.size();i++){if(select.get(i).endsWith(",")){select.set(i,select.get(i).split(",")[0]);}}
        printArray(select);
        // search for from clause 
        for(int i = select.size(); i < t.length; i++){
            if(t[i].toLowerCase().compareTo("where")==0){
                for(int j = select.size() + 2; j < i; j++){
                    from.add(t[j]);
                }
                valid = true;
                break;
            }
        }
        if(!valid){// no where clause
            for(int j = select.size() + 2; j < t.length; j++){
                    from.add(t[j]);
            }
        for(int i = 0; i < from.size();i++){if(from.get(i).endsWith(",")){from.set(i,from.get(i).split(",")[0]);}}
            printArray(from);
            return;
        }
        for(int i = 0; i < from.size();i++){if(from.get(i).endsWith(",")){from.set(i,from.get(i).split(",")[0]);}}
        printArray(from);
        // search for optional where clause
        for(int j = select.size() + from.size() + 3; j < t.length; j++){
            if(t[j].contains("=")){
                String[] toke = t[j].split("=");
                where.add(toke[0]);
                where.add("=");
                where.add(toke[1]);
            }
            else{
                where.add(t[j]);
            }
        }
        printArray(where);
        
        

    }

    boolean execute(ArrayList<ArrayList<ArrayList<String>>> t, int sLvl) {
        return false;
    }

    public static void printArray(ArrayList<String> arr){
        for(String a : arr){
            System.out.print(a + "\t");
        }
        System.out.print("\n");
    }
    
}
