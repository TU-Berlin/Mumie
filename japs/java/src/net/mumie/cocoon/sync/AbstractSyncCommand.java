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

package net.mumie.cocoon.sync;

import java.util.regex.Pattern;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import net.mumie.cocoon.notions.UserGroupName;
import net.mumie.cocoon.util.DocUtil;

/**
 * Abstract base class for synchronization commands
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: AbstractSyncCommand.java,v 1.12 2009/03/06 14:22:36 rassy Exp $</code>
 */

public abstract class AbstractSyncCommand extends AbstractLogEnabled 
  implements SyncCommand, Serviceable, Poolable
{
  /**
   * Default section path for users.
   */

  protected static final String USERS_SECTION_PATH = "org/users";

  /**
   * Indicates that the command should fail if the entity to create already exists.
   */

  protected static final int IF_EXISTS_FAIL = 0;

  /**
   * Indicates that the command should overwrite the entity if entity to create already exists.
   */

  protected static final int IF_EXISTS_OVERWRITE = 1;

  /**
   * Indicates that the command should be ignored if entity to create already exists.
   */

  protected static final int IF_EXISTS_IGNORE = 2;

  /**
   * Groups that should have read access to created entities by default:
   */

  protected String[] readAllowedGroups = new String[]
    {
      UserGroupName.ADMINS,
      UserGroupName.LECTURERS,
      UserGroupName.TUTORS,
      UserGroupName.AUTHORS,
      UserGroupName.SYNCS,
    };

  /**
   * The service manager of this object.
   */

  protected ServiceManager serviceManager = null;

  /**
   * Pattern object to split comma- or space-separated strings.
   */

  protected Pattern splitPattern = Pattern.compile("\\s+|\\s*,\\s*");

  /**
   * Sets the service manager of this object.
   */

  public void service (ServiceManager serviceManager)
  {
    this.serviceManager = serviceManager;
  }

  /**
   * Returns true if the specified string is not null, not empty, and not a whitespace-only
   * string. Otherwise returns false.
   */

  protected static final boolean isNotVoid (String string)
  {
    return ( string != null && string.trim().length() > 0 );
  }

  /**
   * Creates a suitable pure name from the specified name. The string is converted to
   * lower case, and all characters which are no letters and no digits are converted to
   * underscores (<code>'_'</code>). Multiple underscores are combined to one
   * underscore. German umlauts are replaced by their two-letter ASCII replacements.
   * If the specified prefix is non-null, it is prepended to the name, separated by an
   * underscore.
   *
   * @deprecated use {@link DocUtil#nameToPureName DocUtil.nameToPureName} now.
   */

  protected static final String suggestPureName (String prefix, String name)
  {
    StringBuilder pureName = new StringBuilder(name.length());
    boolean lastWasWhitesapce = false;
    for (char c : name.toLowerCase().toCharArray())
      {
        if ( Character.isLetterOrDigit(c) )
          {
            switch ( c )
              {
              case 228: // a umlaut
                pureName.append('a').append('e');
                break;
              case 246: // o umlaut
                pureName.append('o').append('e');
                break;
              case 252: // u umlaut
                pureName.append('u').append('e');
                break;
              case 223: // sz
                pureName.append('s').append('s');
                break;
              default:
                pureName.append(c);
              }
            lastWasWhitesapce = false;
          }
        else if ( !lastWasWhitesapce )
          {
            pureName.append('_');
            lastWasWhitesapce = true;
          }
      }
    if ( prefix != null )
      pureName.insert(0, "_").insert(0, prefix);
    return pureName.toString();
  }

  /**
   * Obtains the "if exists mode" from the specified keyword.
   */

  protected int getIfExistsMode (String keyword)
  {
    if ( keyword == null || keyword.equals("fail") )
      return IF_EXISTS_FAIL;
    else if ( keyword.equals("overwrite") )
      return IF_EXISTS_OVERWRITE;
    else if ( keyword.equals("ignore") )
      return IF_EXISTS_IGNORE;
    else
      throw new IllegalArgumentException("Illegal value for \"if-exists\": " + keyword);
  }
}
