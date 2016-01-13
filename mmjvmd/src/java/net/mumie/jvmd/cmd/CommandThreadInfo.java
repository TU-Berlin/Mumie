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

public class CommandThreadInfo
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The instance id of the thread.
   */

  protected long instanceId;

  /**
   * The creation time of the thread.
   */

  protected long creationTime = -1;

  /**
   * The process id of the client.
   */

  protected long clientPID = -1;

  /**
   * The name of the command executed in the thread.
   */

  protected String commandName = null;

  /**
   * The command line executed in the thread. This is the command name and the command
   * parameters, separated by blanks.
   */

  protected String commandLine = null;

  // --------------------------------------------------------------------------------
  // Getting information about this thread
  // --------------------------------------------------------------------------------

  /**
   * Returns the instance id of the thread.
   */

  public long getInstanceId ()
  {
    return this.instanceId;
  }

  /**
   * Returns the creation time of the thread.
   */

  public long getCreationTime ()
  {
    return this.creationTime;
  }

  /**
   * Returns the client pid of the thread.
   */

  public long getClientPID ()
  {
    return this.clientPID;
  }

  /**
   * Returns the command name of the thread.
   */

  public String getCommandName ()
  {
    return this.commandName;
  }

  /**
   * Returns the command line of the thread.
   */

  public String getCommandLine ()
  {
    return this.commandLine;
  }

  // --------------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>CommandThreadInfo</code> object corresponding to the specifed
   * thread.
   */

  public CommandThreadInfo (CommandThread thread)
  {
    this.instanceId = thread.getInstanceId();
    this.creationTime = thread.getCreationTime();
    this.clientPID = thread.getClientPID();
    this.commandName = thread.getCommandName();
    this.commandLine = thread.getCommandLine();
  }
}
