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
    public static void printIArray(ArrayList<Integer> arr){
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
    public static void printMap(ArrayList<ArrayList<ArrayList<String>>> arra){
        arra.stream().forEach((arr) -> {
            arr.stream().forEach((ar) -> {
                ar.stream().forEach((a) -> {
                    System.out.print(a + "\t");
                });
            System.out.print("\n");
            });
            System.out.print("\n");
            System.out.print("\n");
            System.out.print("\n");
        });
    }
   
    ArrayList<ArrayList<ArrayList<String>>> tables;
    ArrayList<ArrayList<ArrayList<String>>> result;
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
        result = getTables();
        //condition filter
        result = filter(result);
        //securityFilter();
        
        
        printMap(result);
        return false;
    }
    
    public ArrayList<ArrayList<ArrayList<String>>> filter(ArrayList<ArrayList<ArrayList<String>>> table){
        ArrayList<ArrayList<ArrayList<String>>> resTable = new ArrayList<>();
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        ArrayList<String> row = new ArrayList<>();
        
        if(select.get(0).compareTo("*")==0){return table;}

        // find indices to keep
        ArrayList<Integer> indx = new ArrayList<>();
        ArrayList<Integer> cond = new ArrayList<>();
        if(!select.contains("TC")){select.add("TC");}
        for(int t = 0; t < table.size(); t++){
            for(int i = 0; i < table.get(t).get(0).size(); i++){
                if(select.contains(table.get(t).get(0).get(i))){
                    indx.add(i);
                    if(i==0){indx.add(1);}
                }
                if(where.contains(table.get(t).get(0).get(i))){
                    cond.add(i);
                }
            }
        }
        if(indx.isEmpty()){return resTable;}
        for(int t = 0; t < table.size(); t++){
            for(int r = 0; r < table.get(t).get(0).size(); r++){
                for(Integer index : indx){
                    row.add(table.get(t).get(r).get(index));
                }
                for(Integer index : cond){
                    row.add(table.get(t).get(r).get(index));
                    //test condition here!!

                    //!!
                }
                if(!row.isEmpty()){
                    result.add(row);
                    row = new ArrayList<>();
                }
            }
            resTable.add(result);
            result = new ArrayList<>();
        }
//        printTable(result);
        return resTable;
    }
    
    private ArrayList<ArrayList<ArrayList<String>>> getOneTable() {
        ArrayList<ArrayList<ArrayList<String>>> table = new ArrayList<>() ;
        
        switch(from.get(0).compareTo("T2")){
            case -1:
                table.add(tables.get(0));
                break;
            case 0:
                table.add(tables.get(1));
                break;
            default:
               table.add(tables.get(2));
                 break;
        }
        return table;
    }

    private ArrayList<ArrayList<ArrayList<String>>> getTwoTables() {
        ArrayList<ArrayList<ArrayList<String>>> table = null ;
        
        table = getOneTable();
        switch(from.get(1).compareTo("T2")){
            case -1:
               table.add(tables.get(0));
                break;
            case 0:
               table.add(tables.get(1));
                break;
            default:
               table.add(tables.get(2));
                break;
        }
        return table;
    }
    
    private ArrayList<ArrayList<ArrayList<String>>> getAllTables() {
        ArrayList<ArrayList<ArrayList<String>>> table = null ;
        
        table = getOneTable();
        switch(from.get(1).compareTo("T2")){
            case -1:
               table.add(tables.get(0));
                break;
            case 0:
               table.add(tables.get(1));
                break;
            default:
               table.add(tables.get(2));
                break;
        }
        return table;
    }

    private ArrayList<ArrayList<String>> joinTables
        (ArrayList<ArrayList<String>> table1, ArrayList<ArrayList<String>> table2) {
        printTable(table1);
        // which one is bigger?
        int k = (table1.size()<table2.size())? table1.size() : table2.size() ;
        
        ArrayList<String> temp1, temp2;
        
        for(int i = 0; i < k; i++){
//            for(int j = 0; j < table2.size(); j++){
                temp1 = new ArrayList<>(table1.get(i));
                temp2 = new ArrayList<>(table2.get(i));
                temp1 = table1.get(i);
                temp2 = table2.get(i);
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
//        printTable(table1);
        
        return table1;
    }

    private ArrayList<ArrayList<String>> cartesianTables
        (ArrayList<ArrayList<String>> table1, ArrayList<ArrayList<String>> table2) {
            
        if(table1.isEmpty()){return table2;}
        if(table2.isEmpty()){return table1;}
        ArrayList<ArrayList<String>> table = new ArrayList<>();
        ArrayList<String> temp1;
        ArrayList<String> temp2;
        //first row
        temp1 = table1.get(0);
        temp2 = table2.get(0);//
        temp1.addAll(temp2);
        table.add(temp1);

        for(int i = 1; i < table1.size(); i++){
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

    private ArrayList<ArrayList<ArrayList<String>>> getTables() {
        switch (from.size()) {
            case 1:
                if(from.get(0).compareTo("*")==0){
                    result = getAllTables();
                    break;
                }
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
        return result;
    } 
}