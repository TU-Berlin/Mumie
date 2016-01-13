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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import net.mumie.util.ErrorUtil;
import net.mumie.util.ResourcePoolException;
import net.mumie.util.logging.SimpleLogger;

/**
 * Factory for {@link Command Command} instances. This class is also responsible for loading
 * commands. For each loaded command, a pool of corresponding {@link Command Command}
 * instances exist. If an instance is needed, it is first checked if there is one in the
 * pool. If there is one, that is used; otherwise a new instance is created. Instances not
 * needed any longer are returned to the pool for later reuse.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: CommandFactory.java,v 1.5 2007/07/16 10:49:29 grudzin Exp $</code>
 */

public class CommandFactory
{
  // --------------------------------------------------------------------------------
  // Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * Maps command names to the respective command pools.
   */

  protected Map pools = new HashMap ();

  /**
   * A static reference to a fixed instance of this class.
   */

  protected static CommandFactory sharedInstance = null;

  // --------------------------------------------------------------------------------
  // Logging
  // --------------------------------------------------------------------------------

  /**
   * Writes a log message. Same as
   * <pre>SimpleLogger.getSharedInstance().log("CommandFactory: " + message, throwable);</pre>
   */

  protected final void log (String message, Throwable throwable)
  {
    SimpleLogger.getSharedInstance().log("CommandFactory: " + message, throwable);
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
  // Loading commands
  // --------------------------------------------------------------------------------

  /**
   * Loads the command with the specified class.
   */

  public void loadCommand (Class commandClass, int poolMax)
    throws CommandLoadingException
  {
    try
      {
        // Check if commandClass implements the Command interface:
        if ( ! Command.class.isAssignableFrom(commandClass) )
          throw new IllegalArgumentException("Not a command class: " + commandClass);

        // Get the command name:
        String name = null;
        try
          {
            name = (String)commandClass.getField("COMMAND_NAME").get(null);
          }
        catch (NoSuchFieldException exception)
          {
            throw new IllegalArgumentException
              ("Can not determine command name: " +
               "Class has no COMMAND_NAME constant: " + commandClass);
          }

        // Load command:
        this.pools.put(name, new CommandPool(commandClass, poolMax));

        this.log
          ("Loaded command '" + name + "' (class: " + commandClass.getName() + ")");
      }
    catch (Exception exception)
      {
        this.log
          ("ERROR: Failed to load command with class " + commandClass + ": " +
           ErrorUtil.unwrapThrowable(exception));
        throw new CommandLoadingException(exception);
      }
  }

  /**
   * Loads the command with the specified class name.
   */

  public void loadCommand (String commandClassName, int poolMax)
    throws CommandLoadingException
  {
    try
      {
        this.loadCommand(Class.forName(commandClassName), poolMax);
      }
    catch (ClassNotFoundException exception)
      {
        throw new CommandLoadingException(exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Get/put commands
  // --------------------------------------------------------------------------------

  /**
   * Returns the command with the specified name. If the command is unknown, an exeption is
   * thrown.
   */

  public Command get (String name)
    throws ResourcePoolException 
  {
    if ( !this.pools.containsKey(name) )
      throw new IllegalArgumentException("Unknown command: " + name);
    CommandPool pool = (CommandPool)this.pools.get(name);
    return (Command)pool.get();
  }

  /**
   * Puts the specified command back into the corresponding pool.
   */

  public void put (Command command)
    throws ResourcePoolException 
  {
    String name = command.getName();
    if ( !this.pools.containsKey(name) )
      throw new IllegalArgumentException("Unknown command: " + name);
    CommandPool pool = (CommandPool)this.pools.get(name);
    pool.put(command);
  }

  // --------------------------------------------------------------------------------
  // Getting information
  // --------------------------------------------------------------------------------

  /**
   * Returns true if the command with the specified name is known to this factory, otherwise
   * false.
   */

  public boolean knowsCommand (String name)
  {
    return this.pools.containsKey(name);
  }

  /**
   * Returns a map containing a description for each command.
   */

  public Map getCommandDescriptions ()
  {
    Map descriptions = new TreeMap();
    Iterator iterator = this.pools.entrySet().iterator();
    while ( iterator.hasNext() )
      {
        Map.Entry entry = (Map.Entry)iterator.next();
        String name = (String)entry.getKey();
        String description = ((CommandPool)entry.getValue()).getCommandDescription();
        descriptions.put(name, description);
      }
    return descriptions;
  }

  // --------------------------------------------------------------------------------
  // Shared instance
  // --------------------------------------------------------------------------------

  /**
   * Returns a static reference to a fixed instance.
   */

  public static CommandFactory getSharedInstance ()
  {
    if ( sharedInstance == null )
      sharedInstance = new CommandFactory();
    return sharedInstance;
  }
}
