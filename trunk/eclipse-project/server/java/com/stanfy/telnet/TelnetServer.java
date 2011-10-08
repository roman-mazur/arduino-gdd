package com.stanfy.telnet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;

/**
 * Telnet server.
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 */
public class TelnetServer {

  private enum Command {

    SHOW_HARDWARE("show hardware", "hardware.txt");

    final String text;
    final String file;
    private Command(final String text, final String file) {
      this.text = text;
      this.file = file;
    }
  }

  /** UTF-8 charset. */
  public static final Charset UTF_8 = Charset.forName("UTF-8");

  /** Buffer size. */
  private static final int BUF_SIZE = 8192;

  public static void main(final String[] args) throws IOException {
    final ServerSocket serverSocket = new ServerSocket(8080);
    System.out.println("Start");
    while (true) {
      final Socket socket = serverSocket.accept();
      System.out.println("News client, " + socket.getInetAddress());
      new Thread() {
        @Override
        public void run() {
          processClientConnection(socket);
        }
      }.start();
    }
  }

  private static void processClientConnection(final Socket socket) {
    try {
      final InputStream in = new BufferedInputStream(socket.getInputStream());
      final OutputStream out = new BufferedOutputStream(socket.getOutputStream());

      final Writer writer = new OutputStreamWriter(out, UTF_8);
      final Reader reader = new InputStreamReader(in, UTF_8);

      writer.write("Router>");
      writer.flush();

      final StringBuilder command = new StringBuilder();
      final char[] buffer = new char[BUF_SIZE];
      while (true) {
        final int count = reader.read(buffer);
        if (count == -1) { break; }
        if (count == 0) { continue; }
        command.append(buffer, 0, count);
        final int cIndex = command.indexOf("\n");
        if (cIndex == -1) { continue; }
        final char[] cmdChars = new char[cIndex];
        command.getChars(0, cIndex, cmdChars, 0);
        command.delete(0, cIndex + 1);
        final String cmd = new String(cmdChars).trim();
        final String output = processCommand(cmd);
        if (output == null) { break; }
        writer.write(output);
        writer.write("Router>");
        writer.flush();
      }

      System.out.println("Client finished");
      writer.close();
      reader.close();
    } catch (final SocketException e) {
      System.out.println(e.getMessage());
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  private static String processCommand(final String cmd) throws IOException {
    System.out.println(cmd);
    if ("halt".equals(cmd)) { System.exit(0); }
    if ("close".equals(cmd)) {
      return null;
    }
    for (final Command c : Command.values()) {
      if (c.text.equals(cmd)) {
        return streamToString(TelnetServer.class.getResourceAsStream(c.file)) + "\n";
      }
    }
    return "Unknown\n";
  }

  /**
   * Input stream is closed after this method invocation.
   * @param stream input stream
   * @param charset input characters set
   * @return string
   * @throws IOException if an error happens
   */
  public static String streamToString(final InputStream stream, final Charset charset) throws IOException {
    final Reader in = new InputStreamReader(new BufferedInputStream(stream), charset);
    final StringBuilder result = new StringBuilder();
    final char[] buffer = new char[BUF_SIZE];
    int cnt;
    try {
      do {
        cnt = in.read(buffer);
        if (cnt > 0) { result.append(buffer, 0, cnt); }
      } while (cnt >= 0);
      return result.toString();
    } finally {
      in.close();
    }
  }

  /**
   * Input stream is closed after this method invocation.
   * Uses <b>UTF-8</b> charset.
   * @param stream input stream
   * @return string
   * @throws IOException if an error happens
   */
  public static String streamToString(final InputStream stream) throws IOException { return streamToString(stream, UTF_8); }

}
