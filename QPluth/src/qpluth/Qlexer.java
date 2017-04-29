//#!/bin/bash
//java NntndoHRS.class $*

        
package qpluth;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.nio.charset.Charset;

/**
 *
 * @author Adam Pluth
 */
public class Qlexer {
    static PushbackReader src;
    static int chr=0;
    static char c=0;
    static String token="";
    static Qlexeme l;
    static Qlexeme t;
    static Qlexeme n;
    static int lineNum=1;
    /**
     * @param file
     * @param go boolean to print lex list or not
     * @throws IOException 
     */
public Qlexer(String file,boolean go) throws IOException{
        src = new PushbackReader(
            new InputStreamReader(
            new FileInputStream(file),
            Charset.forName("UTF-8")));
        while(chr!=65535){
            l=lex();
            //System.out.println(l);
            if(t==null){t=l;n=t;}
            else{n.left=l;n=n.left;}
        }
    if(go){
        System.out.println(t);
    }
}
public static Qlexeme lex() throws IOException{    
        
       skipWhitSpace();
       chr=src.read();
       c=(char) chr;
       if(c=='\uffff'){return new Qlexeme("ENDOFINPUT","\uffff");}
       switch(c){
            case '(': return new Qlexeme("OPAREN","(");
            case ')': return new Qlexeme("CPAREN",")");
            case '*': return new Qlexeme("STAR","*");
            case ';': return new Qlexeme("SEMI",";");

            default:
                //multi char tokens
                if(Character.isDigit(c)){
                    return lexNummber();
                }
                else if(Character.isLetter(c)){
                    return lexVariableorKeyword();
                }
                else if(c=='-'){
                    return lexMinus();
                }
               else if(c=='<'|c=='>'|c=='='|c=='!'){
                    return lexOperator();
                }
                else if(c=='@'){skipComment(); return lex();}
                else{//needs to be last
                    return new Qlexeme("unknown");
                }
        }
    }
       
    private static Qlexeme lexOperator() throws IOException{
        char b=c;
        c=(char)src.read();
        switch(b){
            case '<':
                if(c=='='){return new Qlexeme("LESSEQUAL","<=");}
                else{src.unread(c);return new Qlexeme("LESS","<");}
            case '>':
                if(c=='='){return new Qlexeme("GREATEREQUAL",">=");}
                else{src.unread(c);return new Qlexeme("GREATER",">");}
            case '=':
                if(c=='='){return new Qlexeme("DOUBLEEQUAL","==");}
                else{src.unread(c);return new Qlexeme("EQUAL","=");}
            case '!':
                if(c=='='){return new Qlexeme("NOTEQUAL","!=");}
                else{src.unread(c);return new Qlexeme("NOT","!");}
        }
        return new Qlexeme("unknown");
    }

    /**
     *  skips all whitespace and pushes back extra char after end of whitespace
     */
    private static void skipWhitSpace() throws IOException {
        chr=src.read();
        c=(char) chr;
        if(c=='\n'){lineNum++;}
        while(Character.isWhitespace(c)){
            chr=src.read();
            c=(char) chr;
            if(c=='\n'){lineNum++;}
        }
       src.unread(c);
    }
    
    /**
     *  skips all chars after '@' on current line
     * '@'=comment
     * @throws IOException 
     */
    private static void skipComment() throws IOException{
        chr=src.read();
        c=(char) chr;
        if(c=='\n'){lineNum++;}
        while(c!='\n'){
            chr=src.read();
            c=(char) chr;
            if(c=='\n'){lineNum++;}
        }
    }
    
    private static Qlexeme lexVariableorKeyword() throws IOException{
        token="";
        while(Character.isLetter(c)||Character.isDigit(c)||c=='.'){
            token+=c;
            chr=src.read();
            c=(char)chr;
        }
        src.unread(chr);
        //read for keywords
        if(token.toLowerCase().equals("select")){return new Qlexeme("SELECT","select");}
        else if(token.toLowerCase().equals("join")){return new Qlexeme("JOIN","join");}
        else if(token.toLowerCase().equals("where")){return new Qlexeme("WHERE","where");}
        else if(token.toLowerCase().equals("cart")){return new Qlexeme("CART","cart");}
        else {return new Qlexeme("ID",token);}
        
    }

   
    private static Qlexeme lexNummber() throws IOException {
        token="";
        boolean real=false;
        while(Character.isDigit(c)){
            token+=c;
            chr=src.read();
            c=(char)chr;
            if(c=='.'){
                real=true;
                token+=c;
                chr=src.read();
                c=(char)chr;
            }
        }
        src.unread(c);
        if(real){return new Qlexeme("REAL",token);}
        return new Qlexeme("INTEGER",token);
    }

    private static Qlexeme lexMinus() throws IOException {
        token="";
        token+=c;
        c=(char)src.read();
        boolean real=false;
        if(!(Character.isDigit(c))){src.unread(c);return new Qlexeme("MINUS","-");}
        else{
            while(Character.isDigit(c)){
                token+=c;
                c=(char) src.read();
                    if(c=='.'){
                    real=true;
                    token+=c;
                    chr=src.read();
                    c=(char)chr;
                }
            }
            src.unread(c);
            if(real){return new Qlexeme("REAL",token);}
            return new Qlexeme("INTEGER",token);
        }
    }

}