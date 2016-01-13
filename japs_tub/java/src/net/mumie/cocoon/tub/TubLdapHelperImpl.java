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

package net.mumie.cocoon.tub;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import net.mumie.cocoon.service.AbstractJapsService;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.LogUtil;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.thread.ThreadSafe;


public class TubLdapHelperImpl extends AbstractJapsService
  implements ThreadSafe, Configurable, TubLdapHelper
{
  // --------------------------------------------------------------------------------
  // h1: Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(TubLdapHelperImpl.class);

  /**
   * Distinguished name to bind to LDAP.
   */

  protected String bindDN = null;

  /**
   * LDAP password.
   */

  protected String password = null;

  /**
   * Starting point of the LDAP search.
   */

  protected String searchBase = null;

  /**
   * URL(s) of the LDAP server(s).
   */

  protected String hosts = null;

  /**
   * Authentication level for communicating to the LDAP server.
   */

  protected String authLevel = null;

  /**
   * Security protocol for communicating to the LDAP server.
   */

  protected String securityProtocol = null;

  // --------------------------------------------------------------------------------
  // h1: Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new instance.
   */

  public TubLdapHelperImpl ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Configures this instance.
   */

  public void configure (Configuration configuration)
    throws ConfigurationException
  {
    final String METHOD_NAME = "configure";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    if ( configuration == null )
      throw new ConfigurationException("Missing configuration");
    this.bindDN = getConfItem(configuration, "bind-dn");
    this.password = getConfItem(configuration, "password");
    this.searchBase = getConfItem(configuration, "search-base");
    this.hosts = getConfItem(configuration, "hosts");
    this.authLevel = getConfItem(configuration, "auth-level", "simple");
    this.securityProtocol = getConfItem(configuration, "security-protocol", "ssl");
    this.getLogger().debug
      (METHOD_NAME + " 2/2: Done." +
       " bindDN = " + this.bindDN +
       ", password = " + this.password +
       ", searchBase = " + this.searchBase +
       ", hosts = " + this.hosts +
       ", securityProtocol = " + this.securityProtocol);
  }

  // --------------------------------------------------------------------------------
  // h1: LDAP methods
  // --------------------------------------------------------------------------------

  /**
   * Connects to the LDAP server with the specified bind dn and password and
   * returns the corresponding {@link DirContext DirContext} object.
   */

  public DirContext connect (String bindDN, String password)
    throws TubLdapException
  {
    try
      {
	final String METHOD_NAME = "connect";
	this.logDebug(METHOD_NAME + " 1/2: Started");
	Hashtable env = new Hashtable();
	env.put(Context.SECURITY_AUTHENTICATION, this.authLevel);
	env.put(Context.SECURITY_PRINCIPAL, bindDN);
	env.put(Context.SECURITY_CREDENTIALS, password);
	env.put("java.naming.ldap.version", "3");
	env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	env.put(Context.PROVIDER_URL, this.hosts);
	env.put(Context.SECURITY_PROTOCOL, this.securityProtocol);
	env.put(Context.BATCHSIZE, "1");
	DirContext context = new InitialDirContext(env);
	this.logDebug(METHOD_NAME + " 2/2: Done");
	return context;
      }
    catch (Exception exception)
      {
	throw new TubLdapException("connect failed", exception);
      }
  }

  /**
   * Connects to the LDAP server with the default bind dn and default password
   * and returns the corresponding {@link DirContext DirContext} object.
   */

  public DirContext connect ()
    throws TubLdapException
  {
    return this.connect(this.bindDN, this.password);
  }

  /**
   * Searches a user in the LDAP server using the specified context.
   */

  public TubLdapUser searchUser (DirContext context, String filter)
    throws TubLdapException
  {
    try
      {
	final String METHOD_NAME = "searchUser";
	this.logDebug(METHOD_NAME + " 1/2: Started. filter = " + filter);
	TubLdapUser user = null;
	SearchControls controls = new SearchControls();
	controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	NamingEnumeration<SearchResult> results =
	  context.search(this.searchBase, filter, controls);
	if ( results.hasMore() )
	  {
	    SearchResult result = results.next();
	    // Get userDN:
	    String userDN = result.getNameInNamespace().toLowerCase();
	    // Get tubPersonOM:
	    String tubPersonOM = null;
	    Attribute attrib = result.getAttributes().get("tubPersonOM");
	    if ( attrib != null )
	      {
		NamingEnumeration values = attrib.getAll();
		while ( tubPersonOM == null && values.hasMore() )
		  {
		    Object value = values.next();
		    if ( ! (value instanceof String) ) continue;
		    tubPersonOM = (String)value;
		  }
	      }
	    user = new TubLdapUser(userDN, tubPersonOM);
	  }
	this.logDebug(METHOD_NAME + " 2/2: Done. user = " + user);
	return user;
      }
    catch (Exception exception)
      {
	throw new TubLdapException("user search failed", exception);
      }
  }

  /**
   * Searches a user in the LDAP server. A context is created with the default
   * bind dn and password and closed after the search.
   */

  public TubLdapUser searchUser (String filter)
    throws TubLdapException
  {
    DirContext context = this.connect();
    TubLdapUser user = this.searchUser(context, filter);
    this.closeDirContextQuietly(context);
    return user;
  }

  /**
   * Returns true if the user with the specified bind dn can connect to the
   * LDAP server with the specifed password, otherwise false.
   */

  public boolean checkPassword (String bindDN, String password)
    throws TubLdapException
  {
    final String METHOD_NAME = "checkPassword";
    this.logDebug(METHOD_NAME + " 1/2: Started. bindDN = " + bindDN);
    boolean success = false;
    DirContext context = null;
    try
      {
	context = this.connect(bindDN, password);
	success = true;
      }
    catch (Exception exception)
      {
	this.logDebug("verify user failed", exception);
	success = false;
      }
    finally
      {
	this.closeDirContextQuietly(context);
      }
    this.logDebug(METHOD_NAME + " 1/2: Done. success = " + success);
    return success;
  }

  // --------------------------------------------------------------------------------
  // h1: Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Closes the specifed context. If an exception occurs, it is catched and sent to
   * the logs in a debug log message. If the context is null, nothing happens.
   */

  protected void closeDirContextQuietly (DirContext context)
  {
    if ( context != null )
      {
	try
	  {
	    context.close();
	  }
	catch (Exception exception)
	  {
	    this.logDebug("close dir context failed", exception);
	  }
      }
  }

  /**
   * Looks up a child node of a configuration and returns its value as a string, or
   * a default value of the child does not exist.
   *
   * @param configuration the configuration object to look up for the item
   * @param name name of the child node
   * @param defaultValue the default value
   */

  protected static String getConfItem (Configuration configuration,
                                       String name,
                                       String defaultValue)
  {
    return configuration.getChild(name).getValue(defaultValue).trim();
  }

  /**
   * Looks up a child node of a configuration and returns its value as a string.
   *
   * @param configuration the configuration object to look up for the item
   * @param name name of the child node
   * @param defaultValue the default value
   */

  protected static String getConfItem (Configuration configuration,
                                       String name)
    throws ConfigurationException
  {
    return configuration.getChild(name).getValue().trim();
  }
}