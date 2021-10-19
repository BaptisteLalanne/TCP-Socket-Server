///A Simple Web Server (WebServer.java)

package http.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.List;
import java.util.ArrayList;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {
  /**
   * WebServer constructor.
   */
  protected void start() {
    ServerSocket s;
    List<String> current_request;

    System.out.println("Webserver starting up on port 80");
    System.out.println("(press ctrl-c to exit)");
    try {
      // create the main server socket
      s = new ServerSocket(3000);
    } catch (Exception e) {
      System.out.println("Error: " + e);
      return;
    }

    System.out.println("Waiting for connection");
    for (;;) {
      try {
        // wait for a connection
        Socket remote = s.accept();

        current_request = new ArrayList<String>();

        // remote is now the connected socket
        System.out.println("Connection, sending data.");
        BufferedReader in = new BufferedReader(new InputStreamReader(
            remote.getInputStream()));
        //PrintWriter out = new PrintWriter(remote.getOutputStream());

        OutputStream out = remote.getOutputStream();

        // read the data sent. We basically ignore it,
        // stop reading once a blank line is hit. This
        // blank line signals the end of the client HTTP
        // headers.
        String str = ".";
        while (str != null && !str.equals("")) {
          str = in.readLine();
          if (str != null && !str.equals("")) {
            Logger.debug("WebServer_run", str);
            current_request.add(str);
          }

        }

        // CHECK HEADER
        byte[] response = handleRoutes(current_request);
        //out.println(response);
        out.write(response);

        out.flush();
        remote.close();
      } catch (Exception e) {
        System.out.println("Error: " + e);
      }
    }
  }

  /**
   * Start the application.
   * 
   * @param args
   *            Command line parameters are not used.
   */
  public static void main(String args[]) {
    WebServer ws = new WebServer();
    ws.start();
  }

  public static String getMethod(List<String> req) {

    // TODO: check if request is not empty
    String first_line = req.get(0);
    String header = first_line.split(" ")[0];
    return header;

  }

  public static String getPath(List<String> req) {

    // TODO: check if request is not empty
    String first_line = req.get(0);
    String header = first_line.split(" ")[1];
    return header;

  }

  public static byte[] handleRoutes(List<String> req) {

    String method = getMethod(req);

    byte[] response = null;
    String header_method = "";

    switch (method) {
      case "GET":

        String path = getPath(req);

        if (path.equals("/"))
          path = "/index.html";

        Logger.debug("WebServer_handleRoutes", "path: '" + path + '"');
        
        String content_type = getContentType(path);
        response = readFile("../doc/" + path);
        if(response == null){
          String errorName = "File not found";
          Logger.error("WebServer_handleRoutes", "File not found");
          header_method = generateErrorHeader(errorName, 404);
          response = generateErrorResponse(errorName,404).getBytes();
        }else{
          header_method = generateHeader(content_type, response.length);
        }

        break;
      case "POST":
        // do things
        break;
      case "HEAD":
        // do things
        break;
      case "PUT":
        // do things
        break;
      case "DELETE":
        // do things
        break; 
      default:
        String errorName = "File not found";
        Logger.error("WebServer_handleRoutes", "File not found");
        header_method = generateErrorHeader(errorName, 404);
        response = generateErrorResponse(errorName,404).getBytes();
        break;
    }

    ByteArrayOutputStream my_stream = new ByteArrayOutputStream();
    try {
      my_stream.write(header_method.getBytes());
      if(response != null){
        my_stream.write(response);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    byte[] concatenated_byte_array = my_stream.toByteArray();  
    //return header_method + generateHTMLHead() + response + generateFooter();
    return concatenated_byte_array;
  }

  public static byte[] readFile(String path) {

    String content = "";
    byte[] data = null;
    BufferedReader reader;

    try {
      
    
      File f = new File(path);

      if(f.exists() && !f.isDirectory()){
        FileInputStream fis = new FileInputStream(f);
        data = new byte[fis.available()];
        fis.read(data);
      }
      

    } catch (Exception e) {
      e.printStackTrace();
    }
 
    return data;
  }


  public static String getContentType(String path) {

    Logger.warning("WebServer_getContentType", "path: " + path);

    String[] splitted = path.split("\\.");
    Logger.warning("WebServer_getContentType", "splitted length: " + splitted.length);


    String extension = "";
    try {
      extension = splitted[(splitted.length)-1];
    } catch (Exception e) {
      Logger.error("WebServer_getContentType", e.getMessage());
    }

    String content_type = "";

    switch (extension) {
      case "html":
        content_type = "text/html";
        break;
      case "jpg":
      case "jpeg":
        content_type = "image/jpeg";
        break;
      case "png":
        content_type = "image/png";
        break;
      case "mp4":
        content_type = "video/mp4";
        break;
      case "mp3":
        content_type = "audio/mpeg";
        break;
      default:
        content_type = "text/plain";
        break;
    }

    return content_type;

  }

  public static String generateHeader(String content_type, Integer content_length) {

    List<String> components = new ArrayList<String>();

    components.add("HTTP/1.0 200 OK");
    components.add("Content-Type: " + content_type);
    components.add("Content-Length: " + content_length);
    components.add("Server: Bot");
    // this blank line signals the end of the headers
    components.add("");
    // Send the HTML page

    return String.join("\r\n", components) + "\r\n";
 
  }

  public static String generateErrorHeader(String error_name, Integer code_error) {

    List<String> components = new ArrayList<String>();

    components.add("HTTP/1.0 "+code_error + " " + error_name);
    components.add("Server: Bot");
    // this blank line signals the end of the headers
    components.add("");
    // Send the HTML page

    return String.join("\r\n", components) + "\r\n";
 
  }

  public static String generateErrorResponse(String error_name, Integer code_error) {

    List<String> components = new ArrayList<String>();
    components.add(generateHTMLHead());


    components.add("<H1>ERROR: "+ code_error+" : "+error_name+"</H1>");
    components.add(generateFooter());

    return String.join("\r\n", components) + "\r\n";
 
  }

  



  public static String generateResponse() {

    List<String> components = new ArrayList<String>();

    components.add("<H1>Welcome to the Ultra Mini-WebServer</H1>");

    return String.join("\r\n", components);
  }

  public static String generateHTMLHead() {

    List<String> head_parts = new ArrayList<String>();

    head_parts.add("<!DOCTYPE html>");
    head_parts.add("<html lang='fr'>");
    head_parts.add("<head>");
    head_parts.add("<meta charset='utf-8'>");
    head_parts.add("<title>TP PR</title>");
    head_parts.add("</head>");
    head_parts.add("<body>");

    return String.join("\r\n", head_parts) + "\r\n";

  }

  public static String generateFooter() {

    List<String> footer_parts = new ArrayList<String>();

    footer_parts.add("</body>");
    footer_parts.add("</html>");

    return String.join("\r\n", footer_parts) + "\r\n";

  }


  

}
