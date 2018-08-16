/**
 * Maverick Application Framework - Application framework
 * Copyright © ${project.inceptionYear} SSHTOOLS Limited (support@sshtools.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
/*-- 

 $Id: Plugin.java,v 1.1.2.2 2011-10-14 17:26:45 brett Exp $

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

import org.apache.commons.cli.Options;

/**
 * All plugin's must implement this
 * 
 * @param <T> type of host
 * 
 */
public interface Plugin<T extends PluginHostContext> {
	/**
	 * Invoked by Plugspud when it activates the plugin
	 * 
	 * @param context context
	 * @throws PluginException on any initialisation error
	 */
	void activatePlugin(T context) throws PluginException;

	/**
	 * Configure the {@link Options} with any command line arguments this plugin
	 * contributes.
	 * 
	 * @param options options
	 */
	void buildCLIOptions(Options options);

	/**
	 * Invoked by Plugspud when it wants to stop the plugin (e.g. when it is
	 * closing). Returning a value of <code>false</code> prevents the plugin
	 * from being closed.
	 * 
	 * @return can close
	 */
	boolean canStopPlugin();

	/**
	 * Invoked by Plugspud when it starts the plugin
	 * 
	 * @param context context
	 * @throws PluginException on any initialisation error
	 */
	void startPlugin(T context) throws PluginException;

	/**
	 * Stop the plugin.
	 * 
	 * @throws PluginException on any plugin error
	 */
	void stopPlugin() throws PluginException;
}
