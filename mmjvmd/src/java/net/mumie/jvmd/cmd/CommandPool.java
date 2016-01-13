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

import net.mumie.util.ResourcePool;
import net.mumie.util.ResourcePoolException;
import java.lang.reflect.Field;

/**
 * A pool of {@link Command Command} objects. All objects are instances of the same class,
 * which must implement the {@link Command Command} interface.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: CommandPool.java,v 1.3 2007/07/16 10:49:29 grudzin Exp $</code>
 */

public class CommandPool extends ResourcePool
{
  /**
   * The class of the commands in this pool.
   */

  protected Class commandClass;

  /**
   * Returns a newly created command object.
   */

  protected Object newResource ()
    throws ResourcePoolException
  {
    try
      {
	return (Command)this.commandClass.newInstance();
      }
    catch (Exception exception)
      {
	throw new ResourcePoolException(exception);
      }
  }

  /**
   * Called before a command is returned to the pool. Calls the command's
   * {@link Command#reset reset} method.
   */

  protected void beginPutHook (Object resource)
    throws ResourcePoolException
  {
    try
      {
	((Command)resource).reset();
      }
    catch (Exception exception)
      {
	throw new ResourcePoolException(exception);
      }
  }

  /**
   * Returns the description of command pooled by this instance. 
   */

  public String getCommandDescription ()
  {
    try
      {
        return (String)this.commandClass.getField("COMMAND_DESCRIPTION").get(null);
      }
    catch (NoSuchFieldException ignored)
      {
        return "- no description available -";
      }
    catch (IllegalAccessException ignored)
      {
        return "- no description available -";
      }
  }

  /**
   * Creates a new command pool.
   */

  public CommandPool (Class commandClass, int maxSize)
  {
    if ( ! Command.class.isAssignableFrom(commandClass) )
      throw new IllegalArgumentException("Not a Command: " + commandClass);
    this.commandClass = commandClass;
    this.maxSize = maxSize;
  }

  /**
   * Creates a new command pool.
   */

  public CommandPool (Class commandClass)
  {
    this(commandClass, 5);
  }
}
