/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2010 Technische Universitaet Berlin
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.mumie.speedcheck;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import net.mumie.util.CmdlineParamHelper;
import java.io.IOException;

public class Netspeed
{
  // --------------------------------------------------------------------------------
  // Tasks
  // --------------------------------------------------------------------------------

  /**
   * Transmits test data to the client and prints the measured times to stdout.
   */

  protected static void server (int port, int blockNum, int blockSize)
    throws Exception
  {
    // Create test data:
    byte[] data = new byte[blockSize];
    byte value = Byte.MIN_VALUE;
    for (int i = 0; i < blockSize; i++)
      {
        data[i] = value;
        if ( value == Byte.MAX_VALUE )
          value = Byte.MIN_VALUE;
        else
          value++;
      }
    // Initialize server:
    ServerSocket serverSocket = new ServerSocket(port);
    try
      {
        // Listen on port, return socket when client connects:
        Socket socket = serverSocket.accept();
        try
          {
            OutputStream out = socket.getOutputStream();
            long startTime = System.currentTimeMillis();
            for (int k = 0; k < blockNum; k++) send(out, data);
            long time = System.currentTimeMillis() - startTime;
            int totalCount = blockNum*blockSize;
            int speed = (int)((double)totalCount*8*1000/time);
            System.out.println("Sent " + totalCount + " bytes in " + time + " ms");
            System.out.println(String.format("Speed: %8d bit/s", speed));
          }
        finally
          {
            socket.close();
          }
      }
    finally
      {
	serverSocket.close();
      }
  }

  /**
   * Recieves test data from the server and prints the measured times to stdout.
   */

  protected static void client (String host, int port, int blockSize)
    throws Exception
  {
    // Connect to server:
    Socket socket = new Socket(host, port);
    try
      {
        InputStream in = socket.getInputStream();
        byte[] data = new byte[blockSize];
        int totalCount = 0;
        int count;
        long startTime = System.currentTimeMillis();
        while ( (count = in.read(data)) != -1 )
          totalCount += count;
        long time = System.currentTimeMillis() - startTime;
        int speed = (int)((double)totalCount*8*1000/time);
        System.out.println("Recieved " + totalCount + " bytes in " + time + " ms");
        System.out.println(String.format("Speed: %8d bit/s", speed));
      }
    finally
      {
        socket.close();
      }
  }

  /**
   * Prints a help text to stdout.
   */

  public static void showHelp ()
    throws Exception
  {
    final String[] HELP_TEXT =
    {
      "Usage:",
      "  mmnetspeed {server|client} [OPTIONS]",
      "  mmnetspeed --help | -h | --version | -v",
      "Description:",
      "  With the 'server' keyword, sends a certain number of bytes. With the",
      "  'client' keyword, receives a certain number of bytes. In both cases,",
      "  prints the number of bytes sent resp. received, the time to sent resp.",
      "  receive them, and the speed. The byts are sent in blocks. The size and",
      "  number of blocks can be adjusted by the options (see below).",
      "Options:",
      "  --host=HOST, -H HOST",
      "      Sets the host where the server is started. Allowed only with the",
      "      'client' keyword. Default is 127.0.0.1",
      "  --port=PORT, -p PORT",
      "      Port where the server listens. Default is 1234.",
      "  --size=SIZE, -s SIZE",
      "      Block size, in bytes. Default is 131072 (128 Kbyte)",
      "  --blocks=BLOCKS, -b BLOCKS",
      "      Number of blocks. Default is 16.",
      "  --help, -h",
      "      Prints this help text and exists",
      "  --version, -v",
      "      Prints version information and exits",
    };
    for (String line : HELP_TEXT)
      System.out.println(line);
  }

  /**
   * Prints version information to the shell output.
   */

  public static void showVersion ()
    throws Exception
  {
    System.out.println("$Revision: 1.2 $");
  }

  // --------------------------------------------------------------------------------
  // Main method
  // --------------------------------------------------------------------------------

  /**
   * 
   */

  public static void main (String[] params)
    throws Exception
  {
    final int UNSET = -1;
    final int SERVER = 0;
    final int CLIENT = 1;
    final int SHOW_HELP = 2;
    final int SHOW_VERSION = 3;
    int task = UNSET;

    String host = "127.0.0.1";
    int port = 1234;
    int blockNum = 16;
    int blockSize = 128 * 1024; // 128 Kbyte = 1 Mbit

    CmdlineParamHelper paramHelper = new CmdlineParamHelper(params);

    // Get first parameter:
    if ( !paramHelper.next() )
      throw new IllegalArgumentException("Missing parameter(s)");

    // Check first parameter, get task:
    if ( paramHelper.checkParam("--help", "-h") )
      task = SHOW_HELP;
    else if ( paramHelper.checkParam("--version", "-v") )
      task = SHOW_VERSION;
    else if ( paramHelper.checkParam("server") )
      task = SERVER;
    else if ( paramHelper.checkParam("client") )
      task = CLIENT;
    else
      throw new IllegalArgumentException
        ("First parameter must be server|client|--help|-h|--version|-v");

    // Process further parameters:
    while ( paramHelper.next() )
      {
        if (paramHelper.checkOptionWithValue("--host", "-H") )
          {
            host = paramHelper.getValue();
            if ( task == SERVER )
              System.err.println("Ignoring host parameter");
          }
        else if ( paramHelper.checkOptionWithValue("--port", "-p") )
          port = Integer.parseInt(paramHelper.getValue());
        else if ( paramHelper.checkOptionWithValue("--blocks", "-b") )
          blockNum = Integer.parseInt(paramHelper.getValue());
        else if ( paramHelper.checkOptionWithValue("--size", "-s") )
          blockSize = Integer.parseInt(paramHelper.getValue());
        else
          throw new IllegalArgumentException
            ("Unknown parameter: " + paramHelper.getParam());
      }

    switch ( task )
      {
      case SERVER:
        server(port, blockNum, blockSize);
        break;
      case CLIENT:
        client(host, port, blockSize);
        break;
      case SHOW_HELP:
        showHelp();
        break;
      case SHOW_VERSION:
        showVersion();
        break;
      }
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * 
   */

  public static void send (OutputStream out, int value)
    throws Exception
  {
    out.write(value);
    out.flush();
  }

  /**
   * 
   */

  public static void send (OutputStream out, byte[] data)
    throws Exception
  {
    out.write(data);
    out.flush();
  }
}
