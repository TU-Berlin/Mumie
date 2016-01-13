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

package net.mumie.jvmd.cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import net.mumie.ipc.unix.UnixSocket;
import net.mumie.jvmd.Jvmd;
import net.mumie.jvmd.JvmdConfigParam;
import net.mumie.util.ErrorUtil;
import net.mumie.util.logging.SimpleLogger;

public class CommandThread extends Thread
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The instance counter.
   */

  protected static long instanceCounter = 0;

  /**
   * The instance id.
   */

  protected long instanceId;

  /**
   * The creation time of this thread.
   */

  protected long creationTime = -1;

  /**
   * The socket by whitch the server and client communicate.
   */

  protected UnixSocket socket = null;

  /**
   * The process id of the client.
   */

  protected long clientPID = -1;

  /**
   * The command executed in this thread.
   */

  protected Command command = null;

  /**
   * The name of the command executed in this thread.
   */

  protected String commandName = null;

  /**
   * The command line executed in this thread. This is the command name and the command
   * parameters, separated by blanks.
   */

  protected String commandLine = null;

  // --------------------------------------------------------------------------------
  // Instance counter auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Increases the instance counter by 1 and returns the new value. If the instance
   * counter would exceed {@link Long#MAX_VALUE Long.MAX_VALUE}, it is set to 0 before.
   */

  protected static synchronized long increaseInstanceCounter ()
  {
    if ( instanceCounter == Long.MAX_VALUE )
      instanceCounter = 0;
    return ++instanceCounter;
  }

  // --------------------------------------------------------------------------------
  // Getting information about this thread
  // --------------------------------------------------------------------------------

  /**
   * Returns the instance id.
   */

  public long getInstanceId ()
  {
    return this.instanceId;
  }

  /**
   * Returns the creation time.
   */

  public long getCreationTime ()
  {
    return this.creationTime;
  }

  /**
   * Returns the client pid.
   */

  public long getClientPID ()
  {
    return this.clientPID;
  }

  /**
   * Returns the command name.
   */

  public String getCommandName ()
  {
    return this.commandName;
  }

  /**
   * Returns the command line.
   */

  public String getCommandLine ()
  {
    return this.commandLine;
  }

  // --------------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>CommandThread</code> and starts it.
   */

  public CommandThread (UnixSocket socket)
  {
    this.socket = socket;
    this.start();
    this.instanceId = increaseInstanceCounter();
    this.creationTime = System.currentTimeMillis();
  }

  // --------------------------------------------------------------------------------
  // Logging
  // --------------------------------------------------------------------------------

  /**
   * Writes a log message. Same as
   * <pre>SimpleLogger.getSharedInstance().log
   *   ("CommandThread#" + this.instanceId + ": " + message, throwable);</pre>
   */

  protected final void log (String message, Throwable throwable)
  {
    SimpleLogger.getSharedInstance().log
      ("CommandThread#" + this.instanceId + ": " + message, throwable);
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
  // Run method
  // --------------------------------------------------------------------------------

  /**
   * The primary entry point of the thread.
   */

  public void run ()
  {
    try
      {
        Jvmd jvmd = Jvmd.getSharedInstance();

        jvmd.registerCommandThread(this);

        CommandFactory commandFactory = CommandFactory.getSharedInstance();
        this.command = null;

        // Auxiliary to compose the command line:
        StringBuffer commandLineBuffer = new StringBuffer();

        // Working directory:
        File dir = null;

        // Init i/o streams:
        InputStream socketIn = this.socket.getInputStream();
        OutputStream socketOut = this.socket.getOutputStream();
        InputStream in = new CommandInputStream(socketIn);
        PrintStream out = new PrintStream(new CommandOutputStream(socketOut, Channel.STDOUT));
        PrintStream err = new PrintStream(new CommandOutputStream(socketOut, Channel.STDERR));

        try
          {
            // Parse request:
            BufferedReader reader = new BufferedReader(new InputStreamReader(socketIn));
            boolean finished = false;
            while ( !finished )
              {
                String key = reader.readLine().trim();
                if ( key.equals("pid") )
                  {
                    if ( this.clientPID != -1 )
                      throw new HeaderFormatException("Client PID set twice");
                    this.clientPID = Long.parseLong(reader.readLine().trim());
                  }
                else if ( key.equals("cmd") )
                  {
                    if ( this.command != null )
                      throw new HeaderFormatException("Command set twice");
                    this.commandName = reader.readLine().trim();
                    this.command = commandFactory.get(this.commandName);
                    commandLineBuffer.append(this.commandName);
                  }
                else if ( key.equals("argc" ) )
                  {
                    // Currently ignored
                    reader.readLine();
                  }
                else if ( key.equals("arg") )
                  {
                    if ( this.command == null )
                      throw new HeaderFormatException("Command must be set before arguments");
                    int lineNum = Integer.parseInt(reader.readLine().trim());
                    StringBuffer argBuffer = new StringBuffer();
                    for (int i = 0; i < lineNum; i++)
                      {
                        argBuffer.append(reader.readLine());
                        if ( i < lineNum-1 )
                          argBuffer.append("\n");
                      }
                    String arg = argBuffer.toString();
                    this.command.addParameter(arg);
                    commandLineBuffer.append(" ").append(arg);
                  }
                else if ( key.equals("dir") )
                  {
                    if ( this.command == null )
                      throw new HeaderFormatException("Command must be set before arguments");
                    dir = new File(reader.readLine().trim());
                  }
                else if ( key.equals("classpath" ) )
                  {
                    // Currently ignored
                    reader.readLine();
                  }
                else if ( key.equals("start") )
                  finished = true;
                else
                  throw new HeaderFormatException("Unknown keyword: " + key);

                // Read separator (empty line):
                String sep = reader.readLine().trim();
                if ( !sep.equals("") )
                  throw new HeaderFormatException("Expected empty line, found: " + sep);
              }

            // Check if client pid, command, and dir are set:
            if ( this.clientPID == -1 )
              throw new HeaderFormatException("Missing client pid");
            if ( this.command == null )
              throw new HeaderFormatException("Missing command");
            if ( dir == null )
              throw new HeaderFormatException("Missing working directory");

            // Set command directory:
            this.command.setDirectory(dir);

            // Set command i/o streams:
            this.command.setInputStream(in);
            this.command.setOutputStream(out);
            this.command.setErrorStream(err);

            // Set command line:
            this.commandLine = commandLineBuffer.toString();

            // Execute command:
            this.log("Starting execution: " + this.commandLine);
            int exitValue = this.command.execute();
            this.log("Finished execution. exitValue = " + exitValue);

            // Signal end of execution to client:
            signalStop(socketOut, out, err, exitValue);
          }
        catch (Throwable throwable)
          {
            if ( throwable instanceof CommandStoppedException)
              {
                this.log("Execution stopped");
                err.println("Stopped");
                signalStop(socketOut, out, err, ExitValue.EXEC_STOPPED);
              }
            else
              {
                Throwable rootThrowable = ErrorUtil.unwrapThrowable(throwable);
                this.log("ERROR while executing: " + rootThrowable);
                if ( verboseCmdErrs() )
                  throwable.printStackTrace(err);
                else
                  err.println(rootThrowable.toString());
                signalStop(socketOut, out, err, ExitValue.EXEC_ERROR);
              }
          }
        finally
          {
            if ( this.command != null )
              {
                commandFactory.put(this.command);
                this.command = null;
              }
            jvmd.unregisterCommandThread(this);
            this.socket.close();
          }
      }
    catch (Throwable throwable)
      {
        this.log("INTERNAL ERROR", throwable);
      }
  }

  // --------------------------------------------------------------------------------
  // Stopping command execution
  // --------------------------------------------------------------------------------

  /**
   * Stops (or tries to stop) the execution of the command.
   */

  public void stopCommand ()
  {
    try
      {
        synchronized(this.command)
          {
            this.command.stop();
          }
      }
    catch (NullPointerException exception)
      {
        this.log("IGNORED ERROR: Stopping command failed", exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * 
   */

  protected static void signalStop (OutputStream socketOut,
                                    PrintStream out,
                                    PrintStream err,
                                    int exitValue)
    throws IOException
  {
    out.flush();
    err.flush();
    socketOut.write(Channel.TERM);
    socketOut.write(exitValue);
    socketOut.flush();
  }

  /**
   * Auxiliary method; returns true if the system property
   * {@link JvmdConfigParam#VERBOSE_CMD_ERRS JvmdConfigParam.VERBOSE_CMD_ERRS}
   * is <code>"true"</code> or <code>"yes"</code>, otherwise false.
   */

  protected static boolean verboseCmdErrs ()
  {
    String value = System.getProperty(JvmdConfigParam.VERBOSE_CMD_ERRS);
    return
      ( value != null && ( value.equals("true") || value.equals("yes") ) );
  }
}
