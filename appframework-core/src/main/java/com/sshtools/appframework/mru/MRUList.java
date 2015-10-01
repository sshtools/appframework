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

	private static final String MRU_LIST_ELEMENT = "MRUList";

	private static final String FILE_ELEMENT = "File";

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

	public class ElementWrapper {

		String element;
		StringBuffer text;

		ElementWrapper(String element) {
			this.element = element;
			text = new StringBuffer();
		}

	}

}
