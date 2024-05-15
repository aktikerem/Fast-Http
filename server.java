import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.nio.file.StandardOpenOption;
import java.net.URLDecoder;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.net.InetSocketAddress;
import java.io.FileInputStream;
import com.sun.net.httpserver.*;
import java.util.Scanner;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Runtime;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import java.util.Properties;
import javax.net.ssl.TrustManagerFactory;
public class server {
//configs
public static final String PWD = new String("memedi"); //you still need to change it inside the "footer.html" file too!
public static final int portNum = 443;
public static final String timezone = new String("GMT+3");
public static final Boolean useHttps = true;
//configs


private void runServer() throws IOException {

      System.out.print("\033[H\033[2J");
      System.out.println("Listening on port " + portNum+"...");





//unreadble bulshit go
   if(useHttps){
   try{
      HttpsServer server = HttpsServer.create(new InetSocketAddress(portNum),0);
      SSLContext sslContext = SSLContext.getInstance("TLS");
      char[] password = "password".toCharArray();
      KeyStore ks = KeyStore.getInstance("PKCS12");
      FileInputStream fis = new FileInputStream("ssl/keystore.jks");
      ks.load(fis, password);

      // setup the key manager factory
      
      KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
      kmf.init(ks, password);

      // setup the trust manager factory
      TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
      tmf.init(ks);

      // setup the HTTPS context and parameters
      sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
      server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
         
      public void configure(HttpsParameters params) {
      try {
       // initialise the SSL context
       SSLContext c = getSSLContext();
       SSLEngine engine = c.createSSLEngine();
       params.setNeedClientAuth(false);
       params.setCipherSuites(engine.getEnabledCipherSuites());
       params.setProtocols(engine.getEnabledProtocols());

       // Set the SSL parameters
       SSLParameters sslParameters = c.getSupportedSSLParameters();
       params.setSSLParameters(sslParameters);

      } catch (Exception ex) {
       System.out.println("Failed to create HTTPS port");
       System.out.println(ex.getMessage());
      }
     }
  });


      server.createContext("/", new NHttpHandler());
      server.start(); // starts the server
      
   }
   
   catch(Exception ex){
	   System.out.println("ops..");
	   System.out.println(ex.getMessage());
   
   }
   }
   else{

   HttpServer server = HttpServer.create(new InetSocketAddress(portNum),0);
   server.createContext("/", new NHttpHandler());
   server.start(); // starts the server


   }
   }


public static void main(String[] args) throws IOException {

//InetSocketAddress portOBJ = new InetSocketAddress(port);
//HttpServer httpserver = HttpServer.create(portOBJ, 0);
//httpserver.createContext("/", new NHttpHandler());
//httpserver.start();
      try {
         server Server = new server(); // creates a instance of itself
	 System.out.println("Starting server..");
         Server.runServer(); //uses that instance to call run server so if it fails it can be logged
      } catch (IOException e) {
	 System.out.println("good luck with that ._.");
         e.printStackTrace();
      }
}



static class NHttpHandler implements HttpHandler {

public void refresh(HttpExchange exchange, String addr) throws IOException{ 
exchange.getResponseHeaders().set("Location", addr);
exchange.sendResponseHeaders(302, -1); 
exchange.close();

}


public void sendPage(HttpExchange exchange) throws IOException {
if(exchange.getRequestURI().toString().equals("/")){
String headerContent = new String(Files.readAllBytes(Paths.get("/home/Java-Blog-Server/contents/Nheader.html"))); //you will need to change all these witht the abslute paths to the file if u dont git clone into home dir
String middleContent = new String(Files.readAllBytes(Paths.get("/home/Java-Blog-Server/contents/middle.html"))); 
String footerContent = new String(Files.readAllBytes(Paths.get("/home/Java-Blog-Server/contents/Nfooter.html")));

String fullHtml = headerContent + middleContent + footerContent;
byte[] bytes = fullHtml.getBytes();

//DEBUG
System.out.println(exchange.getRequestURI().toString());
System.out.println(exchange.getRequestMethod());
//DEBUG

exchange.sendResponseHeaders(200, bytes.length);
OutputStream os = exchange.getResponseBody();

os.write(bytes);
os.close();
}
else if(exchange.getRequestURI().toString().equals("/Nindex.css")){
System.out.println(exchange.getRequestURI().toString());
System.out.println(exchange.getRequestMethod());
String css = new String(Files.readAllBytes(Paths.get("/home/Java-Blog-Server/contents/Nindex.css")));
byte[] cssBytes = css.getBytes();

exchange.sendResponseHeaders(200, cssBytes.length);
OutputStream cssOS = exchange.getResponseBody();
cssOS.write(cssBytes);
cssOS.close();

}
else if(exchange.getRequestURI().toString().equals("/index.css")){
System.out.println(exchange.getRequestURI().toString());
System.out.println(exchange.getRequestMethod());
String css = new String(Files.readAllBytes(Paths.get("/home/Java-Blog-Server/contents/index.css")));
byte[] cssBytes = css.getBytes();

exchange.sendResponseHeaders(200, cssBytes.length);
OutputStream cssOS = exchange.getResponseBody();
cssOS.write(cssBytes);
cssOS.close();

}

else{
System.err.println("404");
String css = new String(Files.readAllBytes(Paths.get("/home/Java-Blog-Server/contents/404.html")));
byte[] cssBytes = css.getBytes();

exchange.sendResponseHeaders(404, cssBytes.length);
OutputStream cssOS = exchange.getResponseBody();
cssOS.write(cssBytes);
cssOS.close();
        
}


}

public void sendPrivatePage(HttpExchange exchange) throws IOException{

String headerContent = new String(Files.readAllBytes(Paths.get("/home/Java-Blog-Server/contents/header.html")));
String middleContent = new String(Files.readAllBytes(Paths.get("/home/Java-Blog-Server/contents/middle.html")));
String footerContent = new String(Files.readAllBytes(Paths.get("/home/Java-Blog-Server/contents/footer.html")));

String fullHtml = headerContent + middleContent + footerContent;

byte[] bytes = fullHtml.getBytes();

//DEBUG
System.out.println(exchange.getRequestURI().toString());
System.out.println(exchange.getRequestMethod());
//DEBUG


exchange.sendResponseHeaders(200, bytes.length);
OutputStream os = exchange.getResponseBody();


os.write(bytes);
os.close();
}






public void handle(HttpExchange exchange) throws IOException{

if(exchange.getRequestMethod().equals("GET")){

try{
 if(exchange.getRequestURI().toString().substring(0,7).equals("/login=")){
  System.out.println(exchange.getRequestURI().toString().substring(7));
  if(exchange.getRequestURI().toString().substring(7).equals(PWD)){
  	sendPrivatePage(exchange);
  }
  else{
  sendPage(exchange);
 
  }
}
else{sendPage(exchange);}
}
catch (Exception e) {

sendPage(exchange);
}

}
else if(exchange.getRequestMethod().equals("POST")){
if(exchange.getRequestURI().toString().equals("/post="+PWD)){
InputStream inputS = exchange.getRequestBody();
byte[] bufferSized = new byte[2500];
int len = inputS.read(bufferSized);
System.out.println(len);


Date currentDate = new Date();
SimpleDateFormat dateFormat = new SimpleDateFormat("[EEE HH:mm:ss");
dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
String formattedDate = dateFormat.format(currentDate);
formattedDate = formattedDate +" " + timezone+"]";

byte[] bytos = new byte[len];
inputS.read(bytos);
inputS.close();




for(int i =0;i<len;i++){
bytos[i] = bufferSized[i+8];
}



for(int i =0;i<bytos.length;i++){
System.out.print(bytos[i] + " ");
}

String usrInput = new String (bytos, "UTF-8");
usrInput = URLDecoder.decode(usrInput, "UTF-8");
usrInput = "<div class='message'>"  + formattedDate + " " + usrInput+"</div>"; 

BufferedWriter writer = new BufferedWriter(new FileWriter("/home/Java-Blog-Server/contents/middle.html", true));
writer.append(usrInput);
writer.close();


refresh(exchange,"/login="+PWD);
}
else{
refresh(exchange,"/");
}

}


}
}
}
