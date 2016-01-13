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

package net.mumie.cocoon.util;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides static methods run subprocesses.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ProcessUtil.java,v 1.2 2008/08/20 15:44:20 rassy Exp $</code>
 */

public class ProcessUtil
{
  // --------------------------------------------------------------------------------
  // h1: Inner class: ProcessThread
  // --------------------------------------------------------------------------------

  /**
   * Thread in which a process runs.
   */

  public static final class ProcessThread extends Thread
  {
    /**
     * The process executed in this thread.
     */

    protected final Process process;

    /**
     * Whether the process has finished.
     */

    protected boolean finished = false;

    /**
     * The object waiting for this thread to finish.
     */

    protected final Object waitingObject;

    /**
     * Runs the thread.
     */

    public void run ()
    {
      try
        {
          this.process.waitFor();
          synchronized (this)
            {
              this.finished = true;
            }
          synchronized (this.waitingObject)
            {
              this.waitingObject.notify();
            }
        }
      catch (Throwable throwable)
        {
          throw new RuntimeException(throwable);
        }
    }

    /**
     * Returns whether the process has finished.
     */

    public synchronized boolean hasFinished ()
    {
      return this.finished;
    }

    /**
     * Creates a new object with the specified process and the specified waiting object.
     */

    public ProcessThread (Process process, Object waitingObject)
    {
      this.process = process;
      this.waitingObject = waitingObject;
      this.start();
    }

  }

  // --------------------------------------------------------------------------------
  // h1: Inner class: ReadThread
  // --------------------------------------------------------------------------------

  /**
   * A thread for asynchronious reading from an input stream. The data read can be obtained
   * as a string.
   */

  public static final class ReadThread extends Thread
  {
    /**
     * The input stream to read from
     */

    protected InputStream in;

    /**
     * Collects the contents of the input stream.
     */

    protected StringBuilder data = new StringBuilder();

    /**
     * Runs thre thread.
     */

    public void run ()
    {
      try
        {
          char[] buffer = new char[1024];
          InputStreamReader reader = new InputStreamReader(this.in);
          int count;
          while ( (count = reader.read(buffer, 0, buffer.length)) != -1 )
            this.data.append(buffer, 0, count);
        }
      catch (Throwable throwable)
        {
          throw new RuntimeException(throwable);
        }
    }

    /**
     * Returns the data read from the stream.
     */

    public String getData ()
    {
      if ( this.isAlive() )
        throw new IllegalStateException("getData: thread is still running");
      return this.data.toString();
    }

    /**
     * Creates a new <code>ReadThread</code> and starts it.
     */

    public ReadThread (InputStream in)
    {
      this.in = in;
      this.start();
    }
  }

  // --------------------------------------------------------------------------------
  // h1: Inner class: WriteThread
  // --------------------------------------------------------------------------------

  /**
   * A thread for asynchronious writing to an output stream. The data to write to the stream
   * can be specified as a string.
   */

  public static final class WriteThread extends Thread
  {
    /**
     * The data to write to the output stream.
     */

    protected String data;

    /**
     * The output stream to write to.
     */

    protected OutputStream out;

    /**
     * Runs thre thread.
     */

    public void run ()
    {
      try
        {
          if ( this.data == null ) return;
          OutputStreamWriter writer = new OutputStreamWriter(this.out);
          writer.write(this.data, 0, this.data.length());
          writer.flush();
        }
      catch (Throwable throwable)
        {
          throw new RuntimeException(throwable);
        }
    }

    /**
     * Creates a new <code>WriteThread</code> and starts it.
     */
    
    public WriteThread (String data, OutputStream out)
    {
      this.data = data;
      this.out = out;
      this.start();
    }
  }

  // --------------------------------------------------------------------------------
  // h1: Executing external programs
  // --------------------------------------------------------------------------------

  /**
   * Executes an external program in a subprocess. The current thread waits until the
   * program has finished or the time limit is reached.
   *
   * @param dir the working directory for the subprocess
   * @param env the environment for the process. If null, the subprocess inherits the
   *   environment of the current process.
   * @param stdinData data to pass to the standard input stream of the subprocess. If null,
   *   no data are passed to the standard input stream.
   * @param timeLimit the time limit for the subprocess. The subprocess is aborted if this
   *   limit is reached.
   * @param command The command line of the program (program name and parameters).
   *
   * @return a {@link ProcessResult ProcessResult} object which comprises information about
   *   the subprocess.
   */

  public static ProcessResult exec (File dir,
                                    Map<String,String> env,
                                    String stdinData,
                                    long timeLimit,
                                    String... command)
    throws Exception
  {
    ProcessBuilder builder = new ProcessBuilder(command);

    // Set working directory if necessary:
    if ( dir != null ) builder.directory(dir);

    // Set environment if necessary:
    if ( env != null )
      {
        for (Map.Entry<String,String> entry : env.entrySet())
          builder.environment().put(entry.getKey(), entry.getValue());
      }

    // Save start time:
    long startTime = System.currentTimeMillis();

    // Start process and respective threads:
    Process process = builder.start();
    ReadThread stdoutThread = new ReadThread(process.getInputStream());
    ReadThread stderrThread = new ReadThread(process.getErrorStream());
    WriteThread stdinThread = new WriteThread(stdinData, process.getOutputStream());
    Thread currentThread = Thread.currentThread();
    ProcessThread processThread = new ProcessThread(process, currentThread);

    boolean timeLimitExceeded = false;

    // Wait until the process finished or the time limit is reached:
    if ( !processThread.hasFinished() )
      {
        synchronized (currentThread)
          {
            currentThread.wait(timeLimit);
          }
      }

    // Check if process is still running:
    if ( !processThread.hasFinished() )
      {
        process.destroy();
        process.waitFor();
        timeLimitExceeded = true;
      }

    // Wait for i/o threads:
    stdinThread.join();
    stdoutThread.join();
    stderrThread.join();

    // Compute elapsed time:
    long time = System.currentTimeMillis() - startTime;

    return new ProcessResult
      (process.exitValue(), stdoutThread.getData(), stderrThread.getData(),
       time, timeLimit, timeLimitExceeded);
  }

  /**
   * Executes an external program in a subprocess. The current thread waits until the
   * program has finished or the time limit is reached. The subprocess inherits its
   * environment from the current process. No data is passed to the standard input of the
   * subprocess.
   *
   * @param dir the working directory for the subprocess
   * @param timeLimit the time limit for the subprocess. The subprocess is aborted if this
   *   limit is reached.
   * @param command The command line of the program (program name and parameters).
   *
   * @return a {@link ProcessResult ProcessResult} object which comprises information about
   *   the subprocess.
   */

  public static ProcessResult exec (File dir, long timeLimit, String... command)
    throws Exception
  {
    return exec(dir, null, null, timeLimit, command);
  }

}
