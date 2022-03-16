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
package com.sshtools.appframework.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;

/**
 * Useful utilities for general I/O operations.
 *
 * @author $author$
 */
public class IOUtil {
	public static Path mountJarResource(URL resource) throws IOException, URISyntaxException {
		Objects.requireNonNull(resource, "Resource URL cannot be null");
		String scheme = resource.getProtocol();
		if (scheme.equals("file")) {
			return Paths.get(resource.toURI());
		}
		if (!scheme.equals("jar")) {
			throw new IllegalArgumentException("Cannot convert to Path: " + resource);
		}
		String s = resource.toString();
		int separator = s.indexOf("!/");
		String entryName = s.substring(separator + 2);
		URI fileURI = URI.create(s.substring(0, separator));
		FileSystem fs = null;
		try {
			fs = FileSystems.getFileSystem(fileURI);
		}
		catch(FileSystemNotFoundException fnfe) {
			fs = FileSystems.newFileSystem(fileURI, Collections.<String, Object> emptyMap());
		}
		return fs.getPath(entryName);
	}

	/**
	 * Close an <code>InputStream</code>, ignoring if the reference is null or
	 * an exception is thrown.
	 *
	 * @param in stream to close
	 * @return stream closed ok
	 */
	public static boolean closeStream(InputStream in) {
		try {
			if (in != null) {
				in.close();
			}
			return true;
		} catch (IOException ioe) {
			return false;
		}
	}

	/**
	 * Close an <code>OutputStream</code>, ignoring if the reference is null or
	 * an exception is thrown.
	 *
	 * @param out stream to close
	 * @return stream closed ok
	 */
	public static boolean closeStream(OutputStream out) {
		try {
			if (out != null) {
				out.close();
			}
			return true;
		} catch (IOException ioe) {
			return false;
		}
	}

	/**
	 * Copy either a single file to either a directory or another file, or copy
	 * an entire directory and all of its contents to another directory.
	 * 
	 * @param from source
	 * @param to destination
	 * @throws IOException if copy failed for any reason
	 */
	public static void copyFile(File from, File to) throws IOException {
		if (from.isDirectory()) {
			if (!to.exists()) {
				to.mkdir();
			}
			File[] children = from.listFiles();
			for (int i = 0; i < children.length; i++) {
				if (children[i].getName().equals(".") || children[i].getName().equals("..")) {
					continue;
				}
				if (children[i].isDirectory()) {
					File f = new File(to, children[i].getName());
					copyFile(children[i], f);
				} else {
					copyFile(children[i], to);
				}
			}
		} else if (from.isFile() && (to.isDirectory() || to.isFile())) {
			if (to.isDirectory()) {
				to = new File(to, from.getName());
			}
			FileInputStream in = new FileInputStream(from);
			FileOutputStream out = new FileOutputStream(to);
			byte[] buf = new byte[32678];
			int read;
			while ((read = in.read(buf)) > -1) {
				out.write(buf, 0, read);
			}
			closeStream(in);
			closeStream(out);
		}
	}

	/**
	 * Delete either the supplied file or all <strong>files</strong> within the
	 * directory supplied or any sub-directories. Directories will
	 * <strong>not</strong> be deleted.
	 * 
	 * @param file file or directory to delete
	 * @return ok
	 */
	public static boolean delTree(File file) {
		if (file.isFile()) {
			return file.delete();
		}
		File[] list = file.listFiles();
		for (int i = 0; i < list.length; i++) {
			if (!delTree(list[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Delete the specified the directory and <strong>any</strong> of its
	 * children recursively.
	 * 
	 * @param dir directory to delete
	 */
	public static void recurseDeleteDirectory(File dir) {
		File[] files = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});
		if (files == null) {
			return; // Directory could not be read
		}
		for (int i = 0; i < files.length; i++) {
			recurseDeleteDirectory(files[i]);
			files[i].delete();
		}
		files = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return !file.isDirectory();
			}
		});
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
		dir.delete();
	}

	/**
	 * Transfer any bytes read from the inputstream to the output stream until
	 * the input stream reaches end of file. It is up to the caller to flush and
	 * close the streams.
	 * 
	 * @param in source stream
	 * @param out destination stream
	 * @throws IOException on any error
	 */
	public static void transfer(InputStream in, OutputStream out) throws IOException {
		try {
			long bytesSoFar = 0;
			byte[] buffer = new byte[65535];
			int read;
			while ((read = in.read(buffer)) > -1) {
				if (read > 0) {
					out.write(buffer, 0, read);
					// out.flush();
					bytesSoFar += read;
				}
			}
		} finally {
			closeStream(in);
			closeStream(out);
		}
	}
}
