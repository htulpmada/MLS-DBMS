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
 
    public static void printArray(ArrayList<String> arr){
        arr.stream().forEach((a) -> {
            System.out.print(a + "\t");
        });
        System.out.print("\n");
    }
    public static void printTable(ArrayList<ArrayList<String>> arr){
        arr.stream().forEach((ar) -> {
            ar.stream().forEach((a) -> {
                System.out.print(a + "\t");
            });
        System.out.print("\n");
        });
    }
   
    ArrayList<ArrayList<ArrayList<String>>> tables;
    ArrayList<ArrayList<String>> result;
    String token[];
    ArrayList<String> select,from,where,colsIndex,tblIndex,cond;
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
            // no from clause
            if(i==(t.length - 1)){
                for(int j = 1; j <= i; j++){
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

    public boolean execute(ArrayList<ArrayList<ArrayList<String>>> t, int sLvl) {
        tables = t;
        result = new ArrayList<>();

        // get tables from 'FROM' clause
        switch (from.size()) {
            case 1:
                result = getOneTable();
                break;
            case 2:
                result = getTwoTables();
                break;
            case 3:
                result = getAllTables();
                break;
            default:
                break;
        }
        
        //securityFilter();
        
        
        return false;
    }
    
    
    
    private ArrayList<ArrayList<String>> getOneTable() {
        ArrayList<ArrayList<String>> table;
        
        switch(from.get(0).compareTo("T2")){
            case -1:
                table = tables.get(0);
                break;
            case 0:
                table = tables.get(1);
                break;
            default:
                table = tables.get(2);
                break;
        }
        return table;
    }

    private ArrayList<ArrayList<String>> getTwoTables() {
        ArrayList<ArrayList<String>> table1;
        ArrayList<ArrayList<String>> table2;
        
        table1 = getOneTable();
        switch(from.get(1).compareTo("T2")){
            case -1:
                table2 = tables.get(0);
                break;
            case 0:
                table2 = tables.get(1);
                break;
            default:
                table2 = tables.get(2);
                break;
        }
        return CartesianTables(table1,table2);
    }
    
    private ArrayList<ArrayList<String>> getAllTables() {
        ArrayList<ArrayList<String>> table;
        
        switch(from.get(0).compareTo("T2")){
            case -1:
                table = tables.get(0);
                break;
            case 0:
                table = tables.get(1);
                break;
            default:
                table = tables.get(2);
                break;
        }
        return table;
    }

    private ArrayList<ArrayList<String>> shuffleTables
        (ArrayList<ArrayList<String>> table1, ArrayList<ArrayList<String>> table2) {
        printTable(table1);
        // which one is bigger?
        int k = (table1.size()<table2.size())? table1.size() : table2.size() ;
        
        ArrayList<String> temp1, temp2;
        
        for(int i = 0; i < k; i++){
//            for(int j = 0; j < table2.size(); j++){
                temp1 = table1.get(i);
                temp2 = table2.get(i);//
                temp1.addAll(temp2);
                table1.set(i,temp1);
//            }
        }
        //manipulate KC's
        int z = 1;
        ArrayList<String> row = new ArrayList<>();
        for(String s : table1.get(0)){
            if(s.compareTo("KC")==0){
                row.add("KC" + z);
                z++;
            }
            else{
                row.add(s);
            }
        }
        table1.set(0, row);
        printTable(table1);
        
        return table1;
    }

    private ArrayList<ArrayList<String>> CartesianTables
        (ArrayList<ArrayList<String>> table1, ArrayList<ArrayList<String>> table2) {

            
        ArrayList<ArrayList<String>> table = new ArrayList<>();
        
        // which one is bigger?
        int k = (table1.size()<table2.size())? table1.size() : table2.size() ;
        
        ArrayList<String> temp1 = new ArrayList<>();
        ArrayList<String> temp2 = new ArrayList<>();
        
        //first row
        temp1 = table1.get(0);
        temp2 = table2.get(0);//
        temp1.addAll(temp2);
        table.add(temp1);

        for(int i = 1; i < k; i++){
            for(int j = 1; j < table2.size(); j++){
                temp1 = new ArrayList<>(table1.get(i));
                temp2 = new ArrayList<>(table2.get(j));
                temp1.addAll(temp2);
                table.add(temp1);
            }
        }
        //manipulate KC's
        int z = 1;
        ArrayList<String> row = new ArrayList<>();
        for(String s : table.get(0)){
            if(s.compareTo("KC")==0){
                row.add("KC" + z);
                z++;
            }
            else{
                row.add(s);
            }
        }
        table.set(0, row);
        printTable(table);
        
        return table;
    }
 
    
    
    
 
}