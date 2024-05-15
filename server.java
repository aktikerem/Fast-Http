import java.net.InetSocketAddress;
import java.io.FileInputStream;
import java.io.*;
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
// Load the properties file


   
static class NHttpHandler implements HttpHandler {
      
   public void handle(HttpExchange httpExchange) throws IOException{
      	servePage(httpExchange,httpExchange.getRequestURI().toString());
      }
   

   public void servePage(HttpExchange httpExchange,String page) throws IOException{
	try{   
	String pageContents = "";
   	try {
    	  File pageOBJ = new File("contents"+page);
   	  Scanner pageReader = new Scanner(pageOBJ);
   	  while (pageReader.hasNextLine()) {
       	    pageContents = pageReader.nextLine();
            
     	  }
          pageReader.close();
         }catch(Exception e) {
	  System.out.println(httpExchange.getRequestMethod()+" request from "+httpExchange.getRemoteAddress().getHostName()+" for '"+httpExchange.getRequestURI()+"'"+" FAIL");
     	  //System.out.println("An error occurred.");
	  return;
     	 }
	byte[] bytes = pageContents.getBytes();
	httpExchange.sendResponseHeaders(200, bytes.length);
	OutputStream os = httpExchange.getResponseBody();

	os.write(bytes);
	os.close();
	}
	catch(Exception e){
	System.out.println(httpExchange.getRequestMethod()+" request from "+httpExchange.getRemoteAddress().getHostName()+" for '"+httpExchange.getRequestURI()+"'"+" FAIL");
	return;}
	System.out.println(httpExchange.getRequestMethod()+" request from "+httpExchange.getRemoteAddress().getHostName()+" for '"+httpExchange.getRequestURI()+"'"+" SUCSESS");
   	
   }


   }
   private void runServer() throws IOException {
      Properties properties = new Properties();
      properties.load(new FileInputStream("config.properties"));

     final boolean useHttps = Boolean.parseBoolean(properties.getProperty("useHttps"));
     final int portNum = Integer.parseInt(properties.getProperty("serverPort"));
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

   
   
   public static void main(String[] args) throws IOException{ 

      try {
         server Server = new server(); // creates a instance of itself
	 System.out.println("Starting server..");
         Server.runServer(); //uses that instance to call run server so if it fails it can be logged
      } catch (IOException e) {
	 System.out.println("good luck with that ._.");
         e.printStackTrace();
      }

   }
}
