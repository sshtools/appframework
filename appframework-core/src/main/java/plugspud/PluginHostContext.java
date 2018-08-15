/*-- 

 Copyright (C) 2003 Brett Smith.
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions, and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions, and the disclaimer that follows 
    these conditions in the documentation and/or other materials 
    provided with the distribution.

 3. The name "Plugspud" must not be used to endorse or promote products
    derived from this software without prior written permission.  For
    written permission, please contact t_magicthize@users.sourceforge.net.
 
 4. Products derived from this software may not be called "Plugspud", nor
    may "Plugspud" appear in their name, without prior written permission.
 
 In addition, we request (but do not require) that you include in the 
 end-user documentation provided with the redistribution and/or in the 
 software itself an acknowledgement equivalent to the following:
     "This product includes software developed for the Gruntspud
     "Project (http://gruntspud.sourceforge.net/)."

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.  IN NO EVENT SHALL THE PLUGSPUD AUTHORS OR THE PROJECT
 CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE. 
 */
package plugspud;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.cli.CommandLine;

/**
 *  The  hosting application must provide an implementation of this interface.
 */
public interface PluginHostContext
{
	public final static int LOG_DEBUG = 3;
	public final static int LOG_ERROR = 0;
	public final static int LOG_INFORMATION = 1;
	public final static int LOG_WARNING = 2;
	
	/**
	 * Get the parsed command line
	 * 
	 * @return command line
	 */
	public CommandLine getCommandLine();
	
	/**
	 * Return the directory where plugins will be stored
	 * 
	 * @return plugin directory
	 */
	public File getPluginDirectory();
	
	/**
	 * Return a name for the plugin host. This will be used in the UI
	 * components, errors and logging
	 * 
	 * @return plugin host name
	 */
	public String getPluginHostName();
	
	/**
	 * Return the version for the plugin host. Each plugin may specify the
	 * version of the plugin host that it requires. This version string should
	 * be in the format of major.minor[.micro[-other]].
	 * 
	 * @return plugin host version
	 */
	public PluginVersion getPluginHostVersion();
	
	/**
	 * Return a the location where plugin updates and installs may be obtained.
	 * This is used by the plugin manager UI components(s). If <code>null</code>
	 * is returned, no plugin updates / installs will be possible.
	 * 
	 * @return plugin updates resource
	 */
	public URL getPluginUpdatesResource();
	
	/**
	 * Implement to get a preference. This is not required, but is recommended.
	 * If this host does not want to do this, it should just return the default
	 * value supplied.
	 * 
	 * @param key preference name
	 * @param defaultValue default value
	 * @return value
	 */
	public String getPreference(String key, String defaultValue);
	
	/**
	 * Return a resource that contains a list of "standard" plugins, i.e.
	 * plugins whose classes are provided by the same class loader as is used
	 * by the plugin manager itself. These plugins cannot be removed or updated
	 * and are not showing by the plugin manager UI. If <code>null</code> is
	 * returned, then no standard plugins are loaded.
	 * 
	 * @return standard plugins resource
	 */
	public URL getStandardPluginsResource();
	
	/**
	 * Log. Type may be one of ..<br><br>
	 * 
	 * PluginHostContext.LOG_ERROR<br>
	 * PluginHostContext.LOG_DEBUG<br>
	 * PluginHostContext.LOG_WARNING<br>
	 * PluginHostContext.LOG_INFORMATION
	 * 
	 * @param type error type
	 * @param message message
	 */
	public void log(int type, String message);
	
	/**
	 * Log. Type may be one of ..<br><br>
	 * 
	 * PluginHostContext.LOG_ERROR<br>
	 * PluginHostContext.LOG_DEBUG<br>
	 * PluginHostContext.LOG_WARNING<br>
	 * PluginHostContext.LOG_INFORMATION
	 * 
	 * @param type error type
	 * @param message message
	 * @param exception exception
	 */
	public void log(int type, String message, Throwable exception);
	
	/**
	 * Log. Type may be one of ..<br><br>
	 * 
	 * PluginHostContext.LOG_ERROR<br>
	 * PluginHostContext.LOG_DEBUG<br>
	 * PluginHostContext.LOG_WARNING<br>
	 * PluginHostContext.LOG_INFORMATION
	 * 
	 * @param type error type
	 * @param exception exception
	 */
	public void log(int type, Throwable exception);
	
	/**
	 * Open a URL with a browser. 
	 * 
	 * @param url url to open
	 * @throws IOException if url can't be opened
	 */
	public void openURL(URL url)
		throws IOException;
	
	/**
	 * Implement to save a preference. This is not required, but is recommended
	 * 
	 * @param key preference name
	 * @param val value
	 */
	public void putPreference(String key, String val);
}

