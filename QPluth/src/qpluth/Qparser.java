
package qpluth;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author Adam Pluth
 */
class Qparser {
static Qlexeme tree;
static Qlexeme t;

    public Qparser() throws FileNotFoundException, IOException {
        t=Qlexer.t;
    }
    
    //---------------------------------utility functions-------------------------------------//
    public Qlexeme parse() {
        Qlexeme root = pro();
        Qlexeme eof = match("ENDOFINPUT");
        return cons("PARSE",root,eof);
    }
    
    public static void fatal(String problem){
        System.out.println("\nERROR: "+problem);
        System.exit(0);
    }
    
    public static void fatal(String problem, int i){
        System.out.println("\nERROR: "+problem +" line: "+ i);
        System.exit(0);
    }
    
    public Boolean check(String type){
        return t.type.equals(type);
    }
    
    public Qlexeme advance(){
        Qlexeme old = t;
        t=t.left;
        old.left=null;
        //System.out.println(old.type+" "+old.string);
        return old;
    }
    
    public Qlexeme match(String type){
        if(check(type)){return advance();}
        fatal("Syntax Expected "+type+", Received "+t.type+ " line: "+t.line);//t.line
        return null;
    }
    
    public Qlexeme cons(String value,Qlexeme l,Qlexeme r){
        return new Qlexeme(value, value, l, r);
    }

    //----------------------------------grammar functions-------------------------------------//
    public Qlexeme pro(){
        Qlexeme d= def();
            if(proPending()){
                Qlexeme p=pro();
                return cons("PRO",d,cons("JOIN",p,null));
            }
            return cons("PRO",d,null);
    }

    public Qlexeme def(){
        if(vDefPending()){
            Qlexeme v = varDef();
            return cons("DEF", v, null);
        }
        else if(fDefPending()){
            Qlexeme f = fDef();
            return cons("DEF", f, null);
        }
        else if(fDefPending()){
            Qlexeme f = fDef();
            return cons("DEF", f, null);
        }
        else if(exprPending()){
            Qlexeme f = expr();
            return cons("DEF", f, null);//was f;
        }
        return null;
    }
    
    public Qlexeme varDef() {
        Qlexeme v = match("TYPE");
        Qlexeme i = match("ID");
        Qlexeme eq = match("EQUAL");
        Qlexeme e = expr();
        Qlexeme s = match("SEMI");
        return cons("VDEF", v, cons("JOIN", i, cons("JOIN", eq, cons("JOIN", e, cons("JOIN", s, null)))));
    }

    public Qlexeme fDef(){
        Qlexeme f = match("FUNC");
        Qlexeme e = match("ID");
        if(check("EQUAL")){//lambdas here<-------
            Qlexeme eq = match("EQUAL");
            if(check("ID")){
                Qlexeme i = match("ID");
                Qlexeme s = match("SEMI");//lex.r.r.r.r=semi
                return cons("FDEF", f, cons("JOIN", e, cons("JOIN", eq, cons("JOIN", i, cons("JOIN", s, null)))));
            }
            else{
                Qlexeme l = lambda();
                Qlexeme s = match("SEMI");
                Qlexeme o = null;
                Qlexeme op = l.right.right.left;
                Qlexeme c = null;
                Qlexeme b = l.right.right.right.right.left;
                return cons("FDEF", f, cons("JOIN", e, cons("JOIN", o, cons("JOIN", op, cons("JOIN", c, cons("JOIN", b, null))))));
            }
        }
        else{
            Qlexeme o = match("OPAREN");
            Qlexeme op = optPList();
            Qlexeme c = match("CPAREN");
            Qlexeme b = block();
            return cons("FDEF", f, cons("JOIN", e, cons("JOIN", o, cons("JOIN", op, cons("JOIN", c, cons("JOIN", b, null))))));
        }
    }
    
    public Qlexeme idDef(){
        Qlexeme i = match("ID");
        if (check("OPAREN")){
            Qlexeme o = match("OPAREN");
            Qlexeme e = optExprList();
            Qlexeme c = match("CPAREN");
            return cons("FCALL", i, cons("JOIN", o, cons("JOIN", e, cons("JOIN", c, null))));
        }
        else if (check("OBRACKET")){
            Qlexeme o = match("OBRACKET");
            Qlexeme e = expr();
            Qlexeme c = match("CBRACKET");
            return cons("ARRAYACCESS", i, cons("JOIN", o, cons("JOIN", e, cons("JOIN", c, null))));
        }
        else{
            return cons("IDDEF", i, null);
        }
    }
    
    public Qlexeme optPList(){
        if(pListPending()){
            Qlexeme p = pList();
            return cons("OPTPLIST", p, null);
        }
        return cons("OPTPLIST", null, null);
    }
    
    public Qlexeme pList(){
        Qlexeme i = match("ID");
        if (check("COMMA")){
            Qlexeme c = match("COMMA");
            Qlexeme p = pList();
            return cons("PLIST", i,p);
        }
        return cons("PLIST", i, null);
    }
    
    public Qlexeme optExprList(){
        if(exprListPending()){
            Qlexeme e = exprList();
            return cons("OPTEXPRLIST", e, null);
        }            
        return cons("OPTEXPRLIST", null, null);
    }
    
    public Qlexeme exprList(){
        Qlexeme e = expr();
        if (check("COMMA")){
            Qlexeme c = match("COMMA");
            Qlexeme ex = exprList();
            return cons("EXPRLIST", e, ex);
        }
        return cons("EXPRLIST", e, null);
    }

     public Qlexeme expr(){
        Qlexeme u = unary();
        if(opPending()){
            Qlexeme o = op();
            Qlexeme e = expr();
            return cons("EXPR", new Qlexeme("OP", o.type, u, e),null);
        }     
    return cons("EXPR", u, null);
    }    
    
    public Qlexeme unary(){
        if (idDefPending()){
            Qlexeme p = idDef();
            return cons("UNARY", p, null);
        }
        else if(check("STRING")){
            Qlexeme p = match("STRING");
            return cons("UNARY", p, null);
        }
        else if(check("INTEGER")){
            Qlexeme p = match("INTEGER");
            return cons("UNARY", p, null);
        }
        else if(check("REAL")){
            Qlexeme p = match("REAL");
            return cons("UNARY", p, null);
        }
        else if (check("NOT")){
            Qlexeme n = match("NOT");
            Qlexeme p = unary();
            return cons("UNARY", n, cons("JOIN", p, null));
        }
        else if (check("OPAREN")){
            Qlexeme o = match("OPAREN");
            Qlexeme e = expr();
            Qlexeme c = match("CPAREN");
            return cons("UNARY", o, cons("JOIN", e, cons("JOIN", c, null)));
        }
        else if (lambdaPending()){
            Qlexeme p = lambda();
            return cons("UNARY", p, null);
        }
        else if (fDefPending()){
            Qlexeme p = fDef();
            return cons("UNARY", p, null);
        }
        else if (check("OBRACKET")){//array initializer
            Qlexeme o = match("OBRACKET");
            Qlexeme e = optExprList();
            Qlexeme c = match("CBRACKET");
            return cons("UNARY", o, cons("JOIN", e, cons("JOIN", c, null)));
        }
        else if (check("NIL")){
            Qlexeme n = match("NIL");
            return cons("UNARY", n, null);
        }
        else if (check("BOOLEAN")){
            Qlexeme b = match("BOOLEAN");
            return cons("UNARY", b, null);
        }
        else if (check("PRINT")){
            Qlexeme f = match("PRINT");
            Qlexeme o = match("OPAREN");
            Qlexeme e = exprList();
            Qlexeme c = match("CPAREN");
            return cons("PRINT", f, cons("JOIN", e, null));
        }
        else if (check("sNodeV")){
            Qlexeme f = match("sNodeV");
            Qlexeme o = match("OPAREN");
            Qlexeme e = exprList();
            Qlexeme c = match("CPAREN");
            return cons("sNodeV", f, cons("JOIN", e, null));
        }
        else if (check("sNodeL")){
            Qlexeme f = match("sNodeL");
            Qlexeme o = match("OPAREN");
            Qlexeme e = exprList();
            Qlexeme c = match("CPAREN");
            return cons("sNodeL", f, cons("JOIN", e, null));
        }
        else if (check("sNodeR")){
            Qlexeme f = match("sNodeR");
            Qlexeme o = match("OPAREN");
            Qlexeme e = exprList();
            Qlexeme c = match("CPAREN");
            return cons("sNodeR", f, cons("JOIN", e, null));
        }
        else if (check("gNodeV")){
            Qlexeme f = match("gNodeV");
            Qlexeme o = match("OPAREN");
            Qlexeme e = exprList();
            Qlexeme c = match("CPAREN");
            return cons("gNodeV", f, cons("JOIN", e, null));
        }
        else if (check("gNodeL")){
            Qlexeme f = match("gNodeL");
            Qlexeme o = match("OPAREN");
            Qlexeme e = exprList();
            Qlexeme c = match("CPAREN");
            return cons("gNodeL", f, cons("JOIN", e, null));
        }
        else if (check("gNodeR")){
            Qlexeme f = match("gNodeR");
            Qlexeme o = match("OPAREN");
            Qlexeme e = exprList();
            Qlexeme c = match("CPAREN");
            return cons("gNodeR", f, cons("JOIN", e, null));
        }
        else if (check("BREAK")){
            Qlexeme b = match("BREAK");
            return b;
        }
        else if (check("APPEND")){
            Qlexeme f = match("APPEND");
            Qlexeme o = match("OPAREN");
            Qlexeme e = exprList();
            Qlexeme c = match("CPAREN");
            return cons("APPEND", f,cons("JOIN", e, null));
        }
        else if (check("INSERT")){
            Qlexeme f = match("INSERT");
            Qlexeme o = match("OPAREN");
            Qlexeme e = exprList();
            Qlexeme c = match("CPAREN");
            return cons("INSERT", f, cons("JOIN", e, null));
        }
        else if (check("REMOVE")){
            Qlexeme f = match("REMOVE");
            Qlexeme o = match("OPAREN");
            Qlexeme e = exprList();
            Qlexeme c = match("CPAREN");
            return cons("REMOVE", f, cons("JOIN", e, null));
        }
        else if (check("SET")){
            Qlexeme f = match("SET");
            Qlexeme o = match("OPAREN");
            Qlexeme e = exprList();
            Qlexeme c = match("CPAREN");
            return cons("SET", f, cons("JOIN", e, null));
        }
        else if (check("LENGTH")){
            Qlexeme f = match("LENGTH");
            Qlexeme o = match("OPAREN");
            Qlexeme e = exprList();
            Qlexeme c = match("CPAREN");
            return cons("LENGTH", f, cons("JOIN", e,null));
        }
        else return null;
    }
    
    public Qlexeme op(){
        if(check("EQUAL")){
            Qlexeme op = match("EQUAL");
            return cons("EQUAL", op, null);
        }
        else if(check("DOUBLEEQUAL")){
            Qlexeme op = match("DOUBLEEQUAL");
            return cons("DOUBLEEQUAL", op, null);
        }
        else if(check("NOTEQUAL")){
            Qlexeme op = match("NOTEQUAL");
            return cons("NOTEQUAL", op, null);
        }
        else if(check("GREATER")){
            Qlexeme op = match("GREATER");
            return cons("GREATER", op, null);
        }
        else if(check("LESS")){
            Qlexeme op = match("LESS");
            return cons("LESS", op, null);
        }
        else if(check("GREATEREQUAL")){
            Qlexeme op = match("GREATEREQUAL");
            return cons("GREATEREQUAL", op, null);
        }
        else if(check("LESSEQUAL")){
            Qlexeme op = match("LESSEQUAL");
            return cons("LESSEQUAL", op, null);
        }
        else if(check("PLUS")){
            Qlexeme op = match("PLUS");
            return cons("PLUS", op, null);
        }
        else if(check("MINUS")){
            Qlexeme op = match("MINUS");
            return cons("MINUS", op, null);
        }
        else if(check("TIMES")){
            Qlexeme op = match("TIMES");
            return cons("TIMES", op, null);
        }
        else if(check("DIVIDE")){
            Qlexeme op = match("DIVIDE");
            return cons("DIVIDE", op, null);
        }
        else if(check("INTDIVIDE")){
            Qlexeme op = match("INTDIVIDE");
            return cons("INTDIVIDE", op, null);
        }
        else if(check("POWER")){
            Qlexeme op = match("POWER");
            return cons("POWER", op, null);
        }
        else if(check("AND")){
            Qlexeme op = match("AND");
            return cons("AND", op, null);
        }
        else if(check("OR")){
            Qlexeme op = match("OR");
            return cons("OR", op, null);
        }
        else return null;
    }
    
     public Qlexeme block(){
        Qlexeme o = match("OCURLY");
        Qlexeme s = optStateList();
        Qlexeme c = match("CCURLY");
        return cons("BLOCK", o, cons("JOIN", s, cons("JOIN", c, null)));
     }
    
    public Qlexeme optStateList(){
        if (stateListPending()){
            Qlexeme s = stateList();
            return cons("OPTSTATELIST", s, null);
        }
    return cons("OPTSTATELIST", null, null);
    }
     
    public Qlexeme stateList(){
        Qlexeme s = state();
        if(stateListPending()){
            Qlexeme sl = stateList();
            return cons("STATELIST", s, cons("JOIN", sl, null));
        }
    return cons("STATELIST", s, null);
    }    
    
    public Qlexeme state(){
        if(vDefPending()){
            Qlexeme v = varDef();
            return cons("STATE", v, null);
        }
        else if(fDefPending()){
            Qlexeme f = fDef();
            return cons("STATE", f, null);
        }
        else if(exprPending()){
            Qlexeme e = expr();
            Qlexeme s = match("SEMI");
            return cons("STATE", e, cons("JOIN", s, null));
        }
        else if(whileLoopPending()){
            Qlexeme w = whileLoop();
            return cons("STATE", w, null);
        }
        else if(ifStatePending()){
            Qlexeme i = ifState();
            return cons("STATE", i, null);
        }
        else if(check("RETURN")){
            Qlexeme r = match("RETURN");
            Qlexeme e = expr();
            Qlexeme s = match("SEMI");
        return cons("STATE", r ,cons("JOIN", e, cons("JOIN", s, null)));
        }
        else return null;
    }
    
    public Qlexeme whileLoop(){
        Qlexeme w = match("WHILE");
        Qlexeme o = match("OPAREN");
        Qlexeme e = expr();
        Qlexeme c = match("CPAREN");
        Qlexeme b = block();
        return cons("WHILELOOP", w ,cons("JOIN", o, cons("JOIN", e, cons("JOIN", c, cons("JOIN", b, null)))));
    }
    
    public Qlexeme ifState(){
        Qlexeme i = match("IF");
        Qlexeme o = match("OPAREN");
        Qlexeme e = expr();
        Qlexeme c = match("CPAREN");
        Qlexeme b = block();
        Qlexeme oe = optElseState();
        return cons("IFSTATE", i ,cons("JOIN", o, cons("JOIN", e, cons("JOIN", c, cons("JOIN", b, cons("JOIN", oe, null))))));
    }
    
    public Qlexeme optElseState(){
        if (elseStatePending()){
            Qlexeme e = elseState();
            return cons("OPTELSESTATE", e, null);
        }
        return cons("OPTELSESTATE", null, null);
    }
    
    public Qlexeme elseState(){
        Qlexeme e = match("ELSE");
        if(blockPending()){
            Qlexeme b = block();
            return cons("ELSESTATE", e, cons("JOIN", b, null));
        }
        else if(ifStatePending()){
            Qlexeme i = ifState();
            return cons("ELSESTATE", e, cons("JOIN", i, null));
        }
        else return null;
    }
    
    public Qlexeme lambda(){
        Qlexeme l = match("LAMBDA");
        Qlexeme o = match("OPAREN");
        Qlexeme op = optPList();
        Qlexeme c = match("CPAREN");
        Qlexeme b = block();
        return cons("LAMBDA", l ,cons("JOIN", o, cons("JOIN", op, cons("JOIN", c, cons("JOIN", b, null)))));
    }
//--------------------------------------------------------pending functions------------------------------------------------------//
    public boolean proPending(){return defPending();}
    public boolean defPending(){return vDefPending() | fDefPending() | idDefPending();}
    public boolean vDefPending(){return check("TYPE");}
    public boolean fDefPending(){return check("FUNC");}
    public boolean idDefPending(){return check("ID");}
    public boolean pListPending(){return check("ID");}
    public boolean exprListPending(){return exprPending();}
    public boolean exprPending(){return unaryPending();}
    public boolean blockPending(){return check("OCURLY");}
    public boolean stateListPending(){return statePending();}
    public boolean statePending(){return vDefPending() | fDefPending() | exprPending() | whileLoopPending() | ifStatePending() | check("RETURN");}//| check("BREAK");}
    public boolean whileLoopPending(){return check("WHILE");}
    public boolean ifStatePending(){return check("IF");}
    public boolean elseStatePending(){return check("ELSE");}
    public boolean lambdaPending(){return check("LAMBDA");}
    public boolean unaryPending(){
        return idDefPending() 
                | check("STRING") 
                | check("INTEGER") 
                | check("NOT") 
                | check("OPAREN") 
                | lambdaPending() 
                | fDefPending() 
                | check("OBRACKET") 
                | check("NIL") 
                | check("BOOLEAN") 
                | check("PRINT") 
                | check("APPEND") 
                | check("INSERT") 
                | check("REMOVE") 
                | check("SET") 
                | check("BREAK") 
                | check("LENGTH")
                | check("sNodeV")
                | check("gNodeV")
                | check("sNodeL")
                | check("gNodeL")
                | check("sNodeR")
                | check("gNodeR");
        //^^^^^^^^^^^BUILT-INS go here^^^^^^//
    }
    public boolean opPending(){
        return check("EQUAL") 
                | check("NOTEQUAL") 
                | check("GREATER") 
                | check("LESS") 
                | check("GREATEREQUAL") 
                | check("LESSEQUAL") 
                | check("PLUS") 
                | check("MINUS") 
                | check("TIMES") 
                | check("DIVIDE") 
                | check("INTDIVIDE") 
                | check("POWER") 
                | check("AND") 
                | check("OR") 
                | check("ASSIGN") 
                | check("DOUBLEEQUAL");
    }
    public boolean builtInPending() {
        return check("print");
        //| check();
    }  // | check() all builut in functions
}
