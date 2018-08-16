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
package com.google.code.gtkjfilechooser.xbel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.code.gtkjfilechooser.UrlUtil;

/**
 * Manager for the recently used files. See <a href=
 * "http://www.freedesktop.org/wiki/Specifications/desktop-bookmark-spec">desktop-bookmark-spec</a>.
 * 
 * @author c.cerbo
 */
public class RecentlyUsedManager {
	private class RecentFilesHandler extends DefaultHandler {
		private final List<File> allRecentFiles;
		private final ISO8601DateFormat fmt;

		public RecentFilesHandler() {
			this.allRecentFiles = new ArrayList<File>();
			this.fmt = new ISO8601DateFormat();
		}

		@Override
		public void fatalError(SAXParseException spe) throws SAXException {
			throw spe;
		}

		public List<File> getAllRecentFiles() {
			return allRecentFiles;
		}

		@Override
		public void startElement(String uri, String localName, String name, Attributes attr) {
			if (!"bookmark".equals(name)) {
				return;
			}
			int attrCount = attr.getLength();
			if (attrCount == 0) {
				// no attribute found
				return;
			}
			String href = null;
			Date modified = null;
			for (int i = 0; i < attrCount; i++) {
				// Attribute "href"
				if ("href".equals(attr.getQName(i))) {
					href = attr.getValue(i);
					if (!href.startsWith(FILE_PROTOCOL)) {
						// exclude entries that aren't files.
						return;
					}
					if (href.startsWith(FILE_PROTOCOL + System.getProperty("java.io.tmpdir"))) {
						// exclude temporary files.
						return;
					}
					// decode url
					href = UrlUtil.decode(href.substring(FILE_PROTOCOL.length()));
					if (!new File(href).exists()) {
						// exclude files that don't exist anymore
						return;
					}
				}
				// Attribute "modified"
				if ("modified".equals(attr.getQName(i))) {
					try {
						modified = fmt.parse(attr.getValue(i));
					} catch (ParseException e) {
						// modified date attribute is corrupted, set a very old
						// date
						// as workaround, so that this file is postponed in the
						// recent
						// used files list.
						modified = new Date(0);
					}
					// stop the loop, we have that we need (href and modified)!
					break;
				}
			}
			final long modifiedTime = modified.getTime();
			File recentFile = new File(href) {
				private static final long serialVersionUID = 1L;

				@Override
				public long lastModified() {
					return modifiedTime;
				}
			};
			allRecentFiles.add(recentFile);
		}

		@Override
		public void warning(SAXParseException spe) {
			LOG.warning(spe.getMessage());
		}
	}

	private static final String FILE_PROTOCOL = "file://";
	static final private Logger LOG = Logger.getLogger(RecentlyUsedManager.class.getName());
	private List<File> recentFiles;

	/**
	 * Creates a new recent manager object. Recent manager objects are used to
	 * handle the list of recently used resources. {@link RecentlyUsedManager}
	 * objects are expensive: be sure to create them only when needed.
	 * 
	 * @param n The desired number of bookmarks.
	 * @throws IOError on error
	 */
	public RecentlyUsedManager(int n) {
		try {
			init(n);
		} catch (Exception e) {
			throw new IOError(e);
		}
	}

	/**
	 * Returns the desired number of bookmarks sorted by modified date.
	 * 
	 * 
	 * @return The desired number of recent file entries sorted by modified
	 *         date.
	 */
	public List<File> getRecentFiles() {
		return recentFiles;
	}

	protected File getRecentlyUsedFile() {
		return new File(System.getProperty("user.home") + File.separator + ".recently-used.xbel");
	}

	private void init(int n) throws ParserConfigurationException, SAXException, FileNotFoundException, IOException {
		BufferedInputStream stream = null;
		try {
			// Performance note: SAX is here about 2x faster that JAXB.
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			RecentFilesHandler handler = new RecentFilesHandler();
			stream = new BufferedInputStream(new FileInputStream(getRecentlyUsedFile()));
			InputSource is = new InputSource(stream);
			saxParser.parse(is, handler);
			recentFiles = handler.getAllRecentFiles();
			Collections.sort(recentFiles, new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					long date1 = o1.lastModified();
					long date2 = o2.lastModified();
					return (date2 < date1 ? -1 : (date2 == date1 ? 0 : 1));
				}
			});
			if (n < recentFiles.size()) {
				recentFiles = recentFiles.subList(0, n);
			}
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}
}
