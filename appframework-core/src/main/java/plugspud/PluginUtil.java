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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utilities used by the plugin manager API
 */
public class PluginUtil
{
	/**
	 * Prevent instantiation
	 */
	private PluginUtil() {
	}

	/**
	 * Close an input stream and don't worry about any exceptions, but return
	 * true or false instead. If <code>null</code> is supplied as they stream
	 * then it is just ignored
	 *
	 * @param in stream to close
	 * @return closed ok
	 */
	public static boolean closeStream(InputStream in)
	{
		try
		{
			if(in != null)
				in.close();
			return true;
		}
		catch(IOException ioe)
		{
			return false;
		}
	}
	
	/**
	 * Close an output stream and don't worry about any exceptions, but return
	 * true or false instead. If <code>null</code> is supplied as they stream
	 * then it is just ignored
	 *
	 * @param out stream to close
	 * @return closed ok
	 */
	public static boolean closeStream(OutputStream out)
	{
		try
		{
			if(out != null)
				out.close();
			return true;
		}
		catch(IOException ioe)
		{
			return false;
		}
	}
	/**
	 * Copy the input from one stream to the output of another until EOF. It
	 * is up to the invoker to close the streams.
	 *
	 * @param in input stream
	 * @param out output stream
	 * @param buf buffer size (-1 means don't buffer)
	 * @throws IOException on I/O error
	 */
	public static void copyStreams(InputStream in, OutputStream out, int buf)
		throws IOException
	{
		copyStreams(in, out, buf, -1);
	}

	/**
	 * Copy the input from one stream to the output of another for a number of
	 * bytes (-1 means until EOF)
	 *
	 * @param in input stream
	 * @param out output stream
	 * @param buf buffer size (-1 means don't buffer)
	 * @param bytes bytes to copy
	 */
	public static void copyStreams(InputStream in, OutputStream out, int buf, long bytes)
		throws IOException
	{
		InputStream bin = buf == -1 ? in : new BufferedInputStream(in, buf);
		OutputStream bout = buf == -1 ? out : new BufferedOutputStream(out, buf);
		byte[] b = null;
		int r = 0;
		while(true && ( bytes == -1 || ( r >= bytes ) ) )
		{
			int a = bin.available();
			if(a == -1)
				break;
			else if(a == 0)
				a = 1;
			if(bytes != -1 && ( r + a ) > bytes)
				a -= ( r + a - bytes );
			b = new byte[a];
			a = bin.read(b);
			if(a == -1)
				break;
			r += a;
			bout.write(b, 0, a);
		}
		bout.flush();
	}
}