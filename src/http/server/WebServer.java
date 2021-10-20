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
    List<String> current_header;
    String current_content_type = null;
    List<String> current_body;

    System.out.println("Webserver starting up on port 8000");
    System.out.println("(press ctrl-c to exit)");
    try {
      // create the main server socket
      s = new ServerSocket(8000);
    } catch (Exception e) {
      System.out.println("Error: " + e);
      return;
    }

    System.out.println("Waiting for connection");
    for (;;) {
      try {
        // wait for a connection
        Socket remote = s.accept();

        current_header = new ArrayList<String>();
        current_body = new ArrayList<String>();

        // remote is now the connected socket
        System.out.println("Connection, sending data.");

        BufferedInputStream bis = new BufferedInputStream(remote.getInputStream());

        // read until a single byte is available

        char c = 'a';
        char previous = '\0';
        String current = "";
        boolean in_header = true;
        Logger.debug("WebServer_start", "to read: " + bis.available());

        if (bis.available() != 0) {

          while (bis.available() > 0) {
            c = (char) bis.read();
            if (c != '\0') {
              if (c != '\r' && c != '\n')
                current += c;
              if (c == '\n' && previous == '\r') {
                // append to list
                // Logger.debug("WebServer_start", current);
                if (current != "") {
                  if (in_header) {
                    if (current.contains("Content-Type:")) {
                      current_content_type = current.split(": ")[1];
                    }
                    current_header.add(current);
                  } else {
                    current_body.add(current);
                  }
                }
                // reset string
                current = "";
              }
            } else {
              in_header = false;
            }

            previous = c;
          }

          // bis.reset();
          // bis.close();

          Logger.debug("WebServer_start", "end read");

          if (!current.equals(""))
            current_body.add(current);
          // Logger.debug("WebServer_start", current);

          // testing lists
          for (String ss : current_header) {
            Logger.debug("WebServer_start", "header: " + ss);
          }

          for (String ss : current_body) {
            Logger.debug("WebServer_start", "body: " + ss);
          }

          OutputStream out = remote.getOutputStream();

          // CHECK HEADER
          byte[] response = handleRoutes(current_header, current_body, current_content_type);
          // out.println(response);
          out.write(response);
          out.flush();
        }

        remote.close();
      } catch (Exception e) {
        System.out.println("Error: " + e);
      }
    }
  }

  /**
   * Start the application.
   * 
   * @param args Command line parameters are not used.
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

  public static byte[] handleRoutes(List<String> header, List<String> body, String currentContentType) {

    String method = getMethod(header);

    byte[] response = null;
    String header_method = "";

    switch (method) {
    case "GET":
      response = handleGetRequest(header);
      break;
    case "POST":
      response = handlePostRequest(header, body, currentContentType);
      break;
    case "HEAD":
      response = generateErrorHeader("Not Implemented", 501).getBytes();
      break;
    case "PUT":
      response = generateErrorHeader("Not Implemented", 501).getBytes();
      break;
    case "DELETE":
      response = generateErrorHeader("Not Implemented", 501).getBytes();
      break;
    default:
      String errorName = "File not found";
      Logger.error("WebServer_handleRoutes", "File not found");
      header_method = generateErrorHeader(errorName, 404);
      byte[] headerByte = header_method.getBytes();
      response = generateErrorResponse(errorName, 404).getBytes();
      response = concatByte(headerByte, response);
      break;
    }
    return response;
  }

  public static byte[] handleGetRequest(List<String> header) {
    byte[] response = null;
    String header_method = "";
    String path = getPath(header);

    if (path.equals("/"))
      path = "/index.html";

    Logger.debug("WebServer_handleRoutes", "path: '" + path + '"');

    String content_type = getContentType(path);
    response = readFile("../doc/" + path);
    if (response == null) {
      String errorName = "File not found";
      Logger.error("WebServer_handleRoutes", "File not found");
      header_method = generateErrorHeader(errorName, 404);
      response = generateErrorResponse(errorName, 404).getBytes();
    } else {
      header_method = generateHeader(content_type, response.length);
    }
    byte[] headerByte = header_method.getBytes();
    byte[] output = concatByte(headerByte, response);
    return output;
  }

  public static byte[] handlePostRequest(List<String> header, List<String> body, String currentContentType) {
    byte[] response = null;
    String header_method = "";
    response = readPost(body, currentContentType);
    if (response == null) {
      String errorName = "Server error";
      response = generateErrorResponse(errorName, 500).getBytes();
      header_method = generateErrorHeader(errorName, 500);
      Logger.error("WebServer_handleRoutes", "Body unreadable");
    } else {
      header_method = generateHeader("text/html", response.length);
    }
    byte[] headerByte = header_method.getBytes();
    byte[] output = concatByte(headerByte, response);
    return output;
  }

  public static byte[] concatByte(byte[] headerByte, byte[] response) {
    int lenArray1 = headerByte.length;
    int lenArray2 = response.length;
    byte[] output = new byte[lenArray1 + lenArray2];
    System.arraycopy(headerByte, 0, output, 0, lenArray1);
    System.arraycopy(response, 0, output, lenArray1, lenArray2);
    return output;
  }

  public static byte[] readFile(String path) {

    String content = "";
    byte[] data = null;
    BufferedReader reader;

    try {

      File f = new File(path);

      if (f.exists() && !f.isDirectory()) {
        FileInputStream fis = new FileInputStream(f);
        data = new byte[fis.available()];
        fis.read(data);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return data;
  }

  public static byte[] readPost(List<String> body, String currentContentType) {

    List<String> content = new ArrayList<String>();
    content.add(generateHTMLHead());

    switch (currentContentType) {
    case "text":
      for (String data : body) {
        content.add("<p> " + data + "</p>");
      }
      break;
    case "application/x-www-form-urlencoded":
      for (String data : body) {
        for (String element : data.split("&")) {
          // If argument undefined
          String[] values = element.split("=");
          if (values.length > 1) {
            content.add("<p> " + values[1] + "</p>");
          }
        }
      }

      break;
    case "multipart/form-data":
      // todo
      break;
    default:
      break;
    }

    content.add(generateFooter());
    return String.join("\r\n", content).getBytes();
  }

  public static String getContentType(String path) {

    Logger.warning("WebServer_getContentType", "path: " + path);

    String[] splitted = path.split("\\.");
    Logger.warning("WebServer_getContentType", "splitted length: " + splitted.length);

    String extension = "";
    try {
      extension = splitted[(splitted.length) - 1];
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

    components.add("HTTP/1.0 " + code_error + " " + error_name);
    components.add("Server: Bot");
    // this blank line signals the end of the headers
    components.add("");
    // Send the HTML page

    return String.join("\r\n", components) + "\r\n";

  }

  public static String generateErrorResponse(String error_name, Integer code_error) {

    List<String> components = new ArrayList<String>();
    components.add(generateHTMLHead());

    components.add("<H1>ERROR: " + code_error + " : " + error_name + "</H1>");
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
