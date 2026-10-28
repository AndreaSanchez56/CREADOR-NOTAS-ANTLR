package com.musica;

import com.musica.parser.MusicaLexer;
import com.musica.parser.MusicaParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.antlr.v4.runtime.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WebApp {
  static String json(String s) { return "\""+s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n").replace("\r","\\r")+"\""; }
  static void reply(HttpExchange x,int status,String type,byte[] body) throws IOException {
    x.getResponseHeaders().set("Content-Type",type);
    x.getResponseHeaders().set("Access-Control-Allow-Origin","*");
    x.sendResponseHeaders(status,body.length);
    try(OutputStream o=x.getResponseBody()){o.write(body);}
  }
  static void replyText(HttpExchange x,int status,String type,String body) throws IOException {reply(x,status,type,body.getBytes(StandardCharsets.UTF_8));}
  static String validate(String source){
    List<String> errors=new ArrayList<>();
    BaseErrorListener errorListener=new BaseErrorListener(){@Override public void syntaxError(Recognizer<?,?> recognizer,Object offendingSymbol,int line,int pos,String msg,RecognitionException ex){errors.add("Línea "+line+", columna "+(pos+1)+": "+msg);}};
    try{
      MusicaLexer lexer=new MusicaLexer(CharStreams.fromString(source));
      lexer.removeErrorListeners();lexer.addErrorListener(errorListener);
      MusicaParser parser=new MusicaParser(new CommonTokenStream(lexer));
      parser.removeErrorListeners();parser.addErrorListener(errorListener);
      parser.partitura();
    }catch(Exception ex){errors.add(ex.getMessage()==null?"Error de análisis":ex.getMessage());}
    StringBuilder out=new StringBuilder("{\"ok\":").append(errors.isEmpty()?"true":"false").append(",\"errors\":[");
    for(int i=0;i<errors.size();i++){if(i>0)out.append(',');out.append(json(errors.get(i)));}
    return out.append("]}").toString();
  }
  public static void main(String[] args)throws Exception{
    int port=Integer.parseInt(System.getenv().getOrDefault("PORT","8080"));
    HttpServer server=HttpServer.create(new InetSocketAddress("0.0.0.0",port),0);
    server.createContext("/api/validar",x->{
      if("OPTIONS".equals(x.getRequestMethod())){x.getResponseHeaders().set("Access-Control-Allow-Methods","POST, OPTIONS");x.getResponseHeaders().set("Access-Control-Allow-Headers","Content-Type");x.sendResponseHeaders(204,-1);return;}
      if(!"POST".equals(x.getRequestMethod())){replyText(x,405,"text/plain","POST requerido");return;}
      byte[] b=x.getRequestBody().readNBytes(65537);
      if(b.length>65536){replyText(x,413,"text/plain","Melodía demasiado larga");return;}
      replyText(x,200,"application/json; charset=utf-8",validate(new String(b,StandardCharsets.UTF_8)));
    });
    server.createContext("/",x->{
      String path=x.getRequestURI().getPath();
      if(path.equals("/"))path="/index.html";
      if(!path.equals("/index.html")){replyText(x,404,"text/plain","No encontrado");return;}
      try(InputStream in=WebApp.class.getResourceAsStream("/public/index.html")){
        if(in==null){replyText(x,500,"text/plain","Falta index.html");return;}
        reply(x,200,"text/html; charset=utf-8",in.readAllBytes());
      }
    });
    server.start();System.out.println("Creador ANTLR en http://localhost:"+port);
  }
}
