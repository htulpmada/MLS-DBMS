//////////////////////////
//      adampluth       //
//      CS 403          //
//      Designer        //
//      Language        //
//      9/18/2016       //
//      (Lexeme)        //
//////////////////////////

package qpluth;

import java.lang.reflect.Type;
import java.util.ArrayList;

//need to add: evalVariable() get/set
//          : line numbers

/**
 *
 * @author adam pluth
 */
 class Qlexeme implements Type{
     
        String type=null;
        String string=null;
        boolean bool;
        int line=0;
        double real=0;
        Qlexeme right=null;
        Qlexeme left=null;
        ArrayList<Qlexeme> strings;

    Qlexeme(String t) {
        type=t;
        getline();
        makeArr();
    }

    Qlexeme(String t, String value, Qlexeme l, Qlexeme r) {
        type=t;
        string=value;
        left=l;
        right=r;
        getline();
        makeArr();
    }
    Qlexeme(String t, String value) {
        type=t;
        string=value;
        getline();
        makeArr();
    }

    Qlexeme(String t, String value, Qlexeme l, Qlexeme r, ArrayList<Qlexeme> str) {
        type=t;
        string=value;
        left=l;
        right=r;
        getline();
        strings=str;
    }
    private void getline(){line=Qlexer.lineNum;}

    public void makeArr(){if(type.equals("ARRAY")||type.equals("NODE")){strings=new ArrayList<>();}}

        @Override
    public String toString(){
        String s="";
        if(type!=null){s+=type+" ";}
        if(string!=null){s+=string+" "+"line #: "+line;}
        if(left!=null){s+="\nLeft: "+left.toString();}
        if(right!=null){s+="\nRight: "+right.toString();}
        return s;
    }
    public int size(){
        int i=1;
        return size(i);
    }
    public int size(int j){
        if(right==null){return j;}
        return right.size(j+1);
    }
}

