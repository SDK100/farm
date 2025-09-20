package assets;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/** Super-tiny JSON that supports objects, arrays, numbers, booleans, strings. Good enough for scene files. */
public final class Json {
    public sealed interface J permits JObj, JArr, JNum, JStr, JBool, JNull {}
    public static final class JObj implements J { public final Map<String,J> map = new LinkedHashMap<>(); public J get(String k){return map.get(k);} }
    public static final class JArr implements J { public final List<J> list = new ArrayList<>(); }
    public static final class JNum implements J { public final double v; public JNum(double v){this.v=v;} }
    public static final class JStr implements J { public final String v; public JStr(String v){this.v=v;} }
    public static final class JBool implements J { public final boolean v; public JBool(boolean v){this.v=v;} }
    public static final class JNull implements J { public static final JNull I = new JNull(); private JNull(){} }

    public static J parseFile(String path) {
        try { return parse(Files.readString(Path.of(path))); }
        catch (Exception e) { throw new RuntimeException("JSON read failed: " + path, e); }
    }

    public static J parse(String s) {
        return new Parser(s).parse();
    }

    private static final class Parser {
        private final String s; private int i=0, n;
        Parser(String s){ this.s=s; this.n=s.length(); }
        J parse(){ skip(); J v=val(); skip(); return v; }

        J val(){ skip(); if (i>=n) throw err("EOF");
            char c=s.charAt(i);
            if(c=='{') return obj();
            if(c=='[') return arr();
            if(c=='"') return str();
            if(c=='t'||c=='f') return bool();
            if(c=='n') { lit("null"); return JNull.I; }
            return num();
        }
        JObj obj(){ JObj o=new JObj(); i++; skip(); if(peek('}')){i++;return o;}
            while(true){ skip(); String k = ((JStr)str()).v; skip(); expect(':'); skip(); J v=val(); o.map.put(k,v); skip();
                if(peek('}')){i++;return o;} expect(','); }
        }
        JArr arr(){ JArr a=new JArr(); i++; skip(); if(peek(']')){i++;return a;}
            while(true){ a.list.add(val()); skip(); if(peek(']')){i++;return a;} expect(','); }
        }
        JStr str(){ expect('"'); StringBuilder b=new StringBuilder();
            while(i<n){ char c=s.charAt(i++); if(c=='"') break; if(c=='\\'){ char e=s.charAt(i++); switch(e){
                case '"','\\','/' -> b.append(e);
                case 'b'->b.append('\b'); case 'f'->b.append('\f'); case 'n'->b.append('\n'); case 'r'->b.append('\r'); case 't'->b.append('\t');
                case 'u'-> { int cp=Integer.parseInt(s.substring(i,i+4),16); b.append((char)cp); i+=4; }
                default -> throw err("bad escape");
            }} else b.append(c);}
            return new JStr(b.toString());
        }
        JBool bool(){ if(peek('t')){lit("true");return new JBool(true);} lit("false");return new JBool(false); }
        JNum num(){ int st=i; if(peek('-')){} while(i<n && Character.isDigit(s.charAt(i))) i++; if(peek('.')){ while(i<n && Character.isDigit(s.charAt(i))) i++; }
            if(i<n && (s.charAt(i)=='e'||s.charAt(i)=='E')){ i++; if(i<n && (s.charAt(i)=='+'||s.charAt(i)=='-')) i++; while(i<n && Character.isDigit(s.charAt(i))) i++; }
            return new JNum(Double.parseDouble(s.substring(st,i)));
        }
        void skip(){ while(i<n){ char c=s.charAt(i); if(c=='/' && i+1<n && s.charAt(i+1)=='/'){ while(i<n && s.charAt(i)!='\n') i++; } else if(Character.isWhitespace(c)) i++; else break; } }
        void expect(char c){ if(i>=n||s.charAt(i)!=c) throw err("expected '"+c+"'"); i++; }
        boolean peek(char c){ if(i<n && s.charAt(i)==c){return true;} return false; }
        void lit(String w){ if(!s.startsWith(w,i)) throw err("expected "+w); i+=w.length();}
        RuntimeException err(String m){ return new RuntimeException("JSON: "+m+" @"+i); }
    }
}
