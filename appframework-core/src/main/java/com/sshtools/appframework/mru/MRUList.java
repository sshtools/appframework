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
package com.sshtools.appframework.mru;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import nanoxml.XMLElement;

import org.xml.sax.SAXException;

@SuppressWarnings("serial")
public class MRUList extends ArrayList<File> {

	public class ElementWrapper {

		String element;
		StringBuffer text;

		ElementWrapper(String element) {
			this.element = element;
			text = new StringBuffer();
		}

	}

	private static final String FILE_ELEMENT = "File";

	private static final String MRU_LIST_ELEMENT = "MRUList";

	public MRUList() {
		super();
	}

	public MRUList(InputStream in) throws SAXException,
			ParserConfigurationException, IOException {
		this();
		reload(in);
	}

	public void reload(InputStream in) throws SAXException,
			ParserConfigurationException, IOException {

		clear();

		// Load the xml data
		XMLElement xml = new XMLElement();
		xml.parseFromReader(new InputStreamReader(in, "UTF-8"));

		for (@SuppressWarnings("unchecked")
		Enumeration<XMLElement> e = xml.enumerateChildren(); e
				.hasMoreElements();) {
			XMLElement el = e.nextElement();
			if (el.getName().equals(FILE_ELEMENT)) {
				File f = new File(el.getContent());
				if (f.exists()) {
					add(f);
				}
			} else {
				throw new IOException("Unexpected element " + el.getName()
						+ ".");
			}

		}

	}

	@Override
	public String toString() {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		xml += ("<!-- Most recently used -->\n<" + MRU_LIST_ELEMENT + ">\n");
		Iterator<File> it = iterator();
		File file = null;
		while (it.hasNext()) {
			file = it.next();
			xml += ("   " + "<" + FILE_ELEMENT + ">" + file.getAbsolutePath()
					+ "</" + FILE_ELEMENT + ">\n");
		}
		xml += ("</" + MRU_LIST_ELEMENT + ">");
		return xml;

	}

}
