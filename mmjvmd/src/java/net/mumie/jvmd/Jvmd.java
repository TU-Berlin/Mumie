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

package net.mumie.jvmd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import net.mumie.ipc.unix.UnixServerSocket;
import net.mumie.ipc.unix.UnixSocket;
import net.mumie.jvmd.cmd.CommandFactory;
import net.mumie.jvmd.cmd.CommandThread;
import net.mumie.jvmd.cmd.CommandThreadInfo;
import net.mumie.jvmd.cmdlib.Loadcmd;
import net.mumie.jvmd.cmdlib.Stopcmd;
import net.mumie.util.logging.SimpleLogger;

public class Jvmd
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * Indicates that jvmd is currently running.
   */

  public static final int RUNNING = -1;

  /**
   * Indicates that jvmd recieved the signal for stopping.
   */

  public static final int STOP = 0;

  /**
   * Indicates that jvmd encountered an error.
   */

  public static final int ERROR = 1;

  /**
   * List of running command threads.
   */

  protected List commandThreads = new ArrayList();

  /**
   * Shared instance of this class.
   */

  protected static Jvmd sharedInstance = null;

  // --------------------------------------------------------------------------------
  // Logging
  // --------------------------------------------------------------------------------

  /**
   * Writes a log message. Same as
   * <pre>SimpleLogger.getSharedInstance().log("Jvmd: " + message, throwable);</pre>
   */

  protected final void log (String message, Throwable throwable)
  {
    SimpleLogger.getSharedInstance().log("Jvmd: " + message, throwable);
  }

  /**
   * Writes a log message. Same as
   * <pre>this.log(message, null);</pre>
   */

  protected final void log (String message)
  {
    this.log(message, null);
  }

  // --------------------------------------------------------------------------------
  // Command threads
  // --------------------------------------------------------------------------------

  /**
   * Registers the specified <code>CommandThread</code>.
   */

  public void registerCommandThread (CommandThread thread)
  {
    synchronized(this.commandThreads)
      {
        this.commandThreads.add(thread);
      }
  }

  /**
   * Unregisters the specified <code>CommandThread</code>.
   */

  public void unregisterCommandThread (CommandThread thread)
  {
    synchronized(this.commandThreads)
      {
        this.commandThreads.remove(thread);
      }
  }

  /**
   * Returns a list of <code>CommandThreadInfo</code> objects representing the currently
   * active command threads.
   */

  public List getCommandThreadInfos ()
  {
    synchronized(this.commandThreads)
      {
        List threadInfos = new ArrayList();
        Iterator iterator = this.commandThreads.iterator();
        while ( iterator.hasNext() )
          {
            CommandThread thread = (CommandThread)iterator.next();
            threadInfos.add(new CommandThreadInfo(thread));
          }
        return threadInfos;
      }
  }

  /**
   * Returns the <code>CommandThread</code> with the specified client PID, or null if no
   * such thread exists.
   */

  protected CommandThread findCommandThread (long clientPID)
  {
    CommandThread wantedThread = null;
    Iterator iterator = this.commandThreads.iterator();
    while ( wantedThread == null && iterator.hasNext() )
      {
        CommandThread thread = (CommandThread)iterator.next();
        if ( thread.getClientPID() == clientPID )
          wantedThread = thread;
      }
    return wantedThread;
  }

  /**
   * Sends the <code>CommandThread</code> with the specified client PID the stop
   * signal.
   */

  public void stopCommandThread (long clientPID)
  {
    CommandThread thread = this.findCommandThread(clientPID);
    if ( thread == null )
      throw new IllegalArgumentException
        ("Failed to stop thread: No thread for client PID: " + clientPID);
    thread.stopCommand();
  }

  /**
   * Sends all <code>CommandThread</code>s the stop signal.
   */

  protected void stopCommandThreads ()
  {
    Iterator iterator = this.commandThreads.iterator();
    while ( iterator.hasNext() )
      {
        CommandThread thread = (CommandThread)iterator.next();
        thread.stopCommand();
      }
  }

  // --------------------------------------------------------------------------------
  // Running the server
  // --------------------------------------------------------------------------------

  /**
   * Runs the server.
   */

  public int run ()
  {
    int status = RUNNING;

    try
      {
        this.log("Started");

        // Load basic commands:
        CommandFactory commandFactory = CommandFactory.getSharedInstance();
        commandFactory.loadCommand(Loadcmd.class, 5);
        commandFactory.loadCommand(Stopcmd.class, 5);

        UnixServerSocket serverSocket = new UnixServerSocket();
        try
          {
            // Init server socket:
            serverSocket.bind(System.getProperty(JvmdConfigParam.SOCKET_FILE), "---rwxrwx");
            serverSocket.listen();

            // Handle client requests:
            while ( status == RUNNING )
              {
                this.log("Waiting");
                UnixSocket socket = serverSocket.accept();
                int type = socket.getInputStream().read();
                switch ( type )
                  {
                  case RequestType.CMD:
                    this.log("Recieved CMD request");
                    new CommandThread(socket);
                    break;
                  case RequestType.PING:
                    this.log("Recieved PING request");
                    this.respond(socket, ResponseCode.OK);
                    break;
                  case RequestType.STOP:
                    this.log("Recieved STOP request");
                    if ( this.commandThreads.size() == 0 )
                      {
                        this.respond(socket, ResponseCode.OK);
                        status = STOP;
                      }
                    else
                      {
                        this.respond(socket, ResponseCode.FAILED);
                        this.log("STOP request rejected: Active clients exist");
                      }
                    break;
                  case RequestType.FORCED_STOP:
                    this.log("Recieved FORCED_STOP request");
                    this.respond(socket, ResponseCode.OK);
                    status = STOP;
                    break;
                  }
              }
          }
        finally
          {
            serverSocket.close();
            if ( serverSocket.getFilename() != null )
              serverSocket.remove();
            this.log("Stop");
          }
      }
    catch(Throwable throwable)
      {
        throwable.printStackTrace();
        status = ERROR;
      }

    return status;
  }

  // --------------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------------

  /**
   * Creates a <code>Jvmd</code> instance.
   */

  protected Jvmd ()
    throws IllegalStateException
  {
    if ( sharedInstance != null )
      throw new IllegalStateException("Jvmd instance already exists");
    sharedInstance = this;
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * 
   */

  protected void respond (UnixSocket socket, int code)
    throws IOException 
  {
    socket.getOutputStream().write(code);
    socket.close();
  }

  // --------------------------------------------------------------------------------
  // Shared instance
  // --------------------------------------------------------------------------------

  /**
   * Returns the shared <code>Jvmd</code> instance.
   */

  public static Jvmd getSharedInstance ()
  {
    if ( sharedInstance == null )
      sharedInstance = new Jvmd();
    return sharedInstance;
  }

  // --------------------------------------------------------------------------------
  // Main method
  // --------------------------------------------------------------------------------

  /**
   * Main entry point.
   */

  public static void main (String[] params)
  {
    // Load native library for IPC by Unix sockets:
    System.loadLibrary("mmjipc");

    // Install a security manager that disallows System.exit:
    System.setSecurityManager(new JvmdRunSecurityManager());

    // Run jvmd:
    int status = getSharedInstance().run();

    // Uninstalls the security manager:
    System.setSecurityManager(null);

    // Exit:
    System.exit(status);
  }
}
