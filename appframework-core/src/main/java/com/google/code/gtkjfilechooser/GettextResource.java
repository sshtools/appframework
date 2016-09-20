/*******************************************************************************
 * Copyright (c) 2010 Costantino Cerbo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Costantino Cerbo - initial API and implementation
 ******************************************************************************/
package com.google.code.gtkjfilechooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * <p>
 * Read and use .mo files for internationalization.
 * </p>
 * The .mo file format is described <a href=
 * "http://www.gnu.org/software/hello/manual/gettext/MO-Files.html#MO-Files"
 * >here </a>. Other useful information are in the <a
 * href="http://www.gnu.org/software/gettext/manual/gettext.html">gettext
 * tutorial</a>
 * 
 * 
 * @author Costantino Cerbo
 * 
 */
public class GettextResource extends ResourceBundle {
	// enable debugging. Set false in production code.
	static final boolean DEBUG = false;

	// magic number hex = 95 04 12 DE
	static final int[] MAGIC = new int[] { -107, 4, 18, -34 };

	// magic number reversed hex = DE 12 04 95
	static final int[] MAGIC_REVERSED = new int[] { -34, 18, 4, -107 };

	static public final String DEFAULT_LOCALES_DIRECTORY = "/usr/share/locale";

	/**
	 * Cache GettextResource instances with .mo file name.
	 */
	static private Map<String, GettextResource> cachedGettextResource = new HashMap<String, GettextResource>();

	/**
	 * There are entries with a context specifier, for example:
	 * 
	 * <pre>
	 * msgid &quot;paper size|Invoice&quot;
	 * msgstr &quot;Fattura&quot;
	 * </pre>
	 * 
	 * The text before the specifier hasn't to be translated.
	 */
	static private byte CONTEXT_SPECIFIER = 0x04;

	/**
	 * This flag becomes true when a byte with value 0x04 is found.
	 */
	private boolean useContextSpecifier = false;

	/**
	 * Are the byte sequence reversed?
	 */
	private transient Boolean reversed;

	/**
	 * file format revision = 0 (position: 4)
	 */
	private transient int revision;

	/**
	 * Number of strings (position: 8)
	 */
	private transient int n;
	/**
	 * Offset of table with original strings (position: 12)
	 */
	private transient int o;

	/**
	 * Index of the currently processed original string (length & offset) [0,
	 * n-1]
	 */
	private transient int oj;

	/**
	 * Array of the lengths of the original strings.
	 */
	private transient int[] oo_length;

	/**
	 * Array of the offsets of the original strings.
	 */
	private transient int[] oo_offset;

	/**
	 * Offset of table with translation strings (position: 16)
	 */
	private transient int t;

	/**
	 * Index of the currently processed translation string (length & offset) [0,
	 * n-1]
	 */
	private transient int tj;

	/**
	 * Array of the lengths of the translation strings.
	 */
	private transient int[] tt_length;

	/**
	 * Array of the offsets of the translation strings.
	 */
	private transient int[] tt_offset;

	/**
	 * Size of hashing table (position: 20)
	 */
	private transient int s;
	/**
	 * Offset of hashing table (position: 24)
	 */
	private transient int h;

	/**
	 * Array of the byte buffers for each msgid. We use byte buffers, because we
	 * map regions of the file channel into memory.
	 */
	private ByteBuffer[] msgidByteBuffers;

	/**
	 * Array of the byte buffers for each msgstr. We use byte buffers, because
	 * we map regions of the file channel into memory.
	 */
	private ByteBuffer[] msgstrByteBuffers;

	private boolean showMissingTranslation = false;

	private Charset charset = Charset.defaultCharset();

	/**
	 * Load a spefic .mo file
	 * 
	 * @param moFile
	 * @throws IOException
	 */
	public GettextResource(File moFile) {
		init(moFile);
	}

	public GettextResource(Locale loc, String localedir, String textdomain) {
		String moFilename = findMoFile(loc, localedir, textdomain);
		if (moFilename == null) {
			throw new IOError(new FileNotFoundException("Cannot find resource "
					+ moFilename));
		}

		init(new File(moFilename));
	}

	/**
	 * Create a new instance a reuse one already existent in the cache.
	 * 
	 * @param localedir
	 *            The directory where are the locale files. The should have this
	 *            stucture: [localedir]/[iso language] or [localedir]/[iso
	 *            language]_[iso_country]
	 * @param textdomain
	 */
	public GettextResource(String localedir, String textdomain) {
		this(Locale.getDefault(), localedir, textdomain);
	}

	/**
	 * Create a new instance a reuse one already existent in the cache.
	 * 
	 * @param textdomain
	 * @throws IOException
	 */
	public GettextResource(Locale loc, String textdomain) {
		this(loc, DEFAULT_LOCALES_DIRECTORY, textdomain);
	}

	private static String findMoFile(Locale loc, String localedir, String textdomain) {
		String moFilename = localedir + File.separator + loc.toString() + File.separator
				+ "LC_MESSAGES" + File.separator + textdomain + ".mo";
		if (!new File(moFilename).exists()) {
			moFilename = localedir + File.separator + loc.getLanguage() + File.separator
					+ "LC_MESSAGES" + File.separator + textdomain + ".mo";
			if (!new File(moFilename).exists()) {
				return null;
			}
		}

		return moFilename;
	}

	/**
	 * Copy an instance into the current one.
	 * 
	 * @param instance
	 */
	private void copyInstance(GettextResource instance) {
		// The only instance variables needed are msgidByteBuffers,
		// msgstrByteBuffers and showMissingTranslation.
		this.msgidByteBuffers = instance.msgidByteBuffers;
		this.msgstrByteBuffers = instance.msgstrByteBuffers;
		this.showMissingTranslation = instance.showMissingTranslation;
		this.charset = instance.charset;
	}

	private void init(File moFile) {
		try {
			// look in the cache first
			String key = moFile.getAbsolutePath();
			GettextResource instance = cachedGettextResource.get(key);
			if (instance != null) {
				copyInstance(instance);
				return;
			}

			readOffsetsAndLenghts(moFile);

			logOffsetAndLenghtInfo();

			FileChannel channel = null;
			try {
				channel = new RandomAccessFile(moFile, "r").getChannel();
				for (int i = 0; i < n; i++) {
					msgidByteBuffers[i] = channel.map(MapMode.READ_ONLY, oo_offset[i],
							oo_length[i]);
					msgstrByteBuffers[i] = channel.map(MapMode.READ_ONLY, tt_offset[i],
							tt_length[i]);
				}
			} finally {
				if (channel != null) {
					// Despite the fact that the channel has been closed, the
					// data
					// in the file continues to be available via the memory map
					channel.close();
				}
			}

			// We've mapped the file into memory, we can now release the
			// resources not anymore needed.
			releaseOffsetAndLenghtArrays();

			setCharset();

			// update cache
			cachedGettextResource.put(key, this);

			logMessages();
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

	/**
	 * Usually the info message in a .mo file contais also the charset info, for
	 * example:
	 * 
	 * <pre>
	 * Project-Id-Version: gtk+ 2.14
	 * Report-Msgid-Bugs-To: 
	 * POT-Creation-Date: 2009-08-29 00:06-0400
	 * PO-Revision-Date: 2009-04-17 22:57+0200
	 * Last-Translator: Luca Ferretti &lt;elle.uca@libero.it&gt;
	 * Language-Team: Italian &lt;tp@lists.linux.it&gt;
	 * MIME-Version: 1.0
	 * Content-Type: text/plain; charset=UTF-8
	 * Content-Transfer-Encoding: 8bit
	 * Plural-Forms: nplurals=2; plural=(n != 1);
	 * </pre>
	 */
	private void setCharset() {
		String info = getInfoMessage();
		int chIndexOf = info.indexOf("charset");
		if (chIndexOf >= 0) {
			int start = chIndexOf + "charset".length() + 1;
			int end = -1;
			for (int i = start; i < info.length(); i++) {
				if (info.charAt(i) == '\n') {
					end = i;
					break;
				}
			}
			if (end > start) {
				String charsetName = info.substring(start, end);
				if (Charset.isSupported(charsetName)) {
					charset = Charset.forName(charsetName);
				}
			}
		}
	}

	public Charset getCharset() {
		return charset;
	}

	/**
	 * Read the offset and length for each original and translated string. This
	 * information are stored in the arrays: oo_offset, oo_length, tt_offset and
	 * tt_length.
	 * 
	 * This method read sequentially the first h bytes in the .mo file. Then we
	 * use access the file random using a {@link FileChannel} and mapping the
	 * just found positions.
	 */
	private void readOffsetsAndLenghts(File file) {
		try {
			InputStream is = null;
			try {
				is = new FileInputStream(file);
				byte[] buffer = new byte[4096];
				int index = 0;
				for (int n; (n = is.read(buffer)) != -1;) {
					for (int i = 0; i < n; i++) {
						try {
							handleByte(buffer[i], index);
						} catch (ArrayIndexOutOfBoundsException e) {
							e.printStackTrace();
							throw new ArrayIndexOutOfBoundsException("index: " + index);
						}
						index++;
						if (index > 12 && index == h) {
							break;
						}
					}
				}
			} finally {
				if (is != null) {
					is.close();
				}
			}
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

	private void handleByte(byte b, int index) {
		if (index == 0) {
			reversed = b == -34;
		}

		if (!useContextSpecifier && b == CONTEXT_SPECIFIER) {
			useContextSpecifier = true;
		}

		// Initialise the arrays
		if (index == 12) {
			oo_length = new int[n];
			oo_offset = new int[n];
			tt_length = new int[n];
			tt_offset = new int[n];
			msgidByteBuffers = new ByteBuffer[n];
			msgstrByteBuffers = new ByteBuffer[n];
		}

		if (index == 28) {
			logInitOffset();
		}

		if (index < 4) {
			// check if the file is valid
			if (b != MAGIC[index]) {
				if (b != MAGIC_REVERSED[index]) {
					throw new IOError(new IOException(String.format(
							"Invalid .mo file: byte[%d]=%h", index, b & 0xff)));
				}
			}
		} else if (index >= 4 && index < 8) {
			int k = 4;
			revision = swap(b, index, revision, k);
		} else if (index >= 8 && index < 12) {
			int k = 8;
			n = swap(b, index, n, k);
		} else if (index >= 12 && index < 16) {
			int k = 12;
			o = swap(b, index, o, k);
		} else if (index >= 16 && index < 20) {
			int k = 16;
			t = swap(b, index, t, k);
		} else if (index >= 20 && index < 24) {
			int k = 20;
			s = swap(b, index, s, k);
		} else if (index >= 24 && index < 28) {
			int k = 24;
			h = swap(b, index, h, k);
		} else if (index >= o + oj * 8 && index < o + oj * 8 + 4
				&& index < o + (n - 1) * 8 + 4) {
			oo_length[oj] = swap(b, index, oo_length[oj], o + oj * 8);

			// increment the current entry index
			if (index == o + oj * 8 + 3) {
				oj++;
			}
		} else if (oj > 0 && index >= o + (oj - 1) * 8 + 4
				&& index < o + (oj - 1) * 8 + 4 + 4 && index < o + (n - 1) * 8 + 4 + 4) {
			oo_offset[oj - 1] = swap(b, index, oo_offset[oj - 1], o + (oj - 1) * 8 + 4);
		} else if (index >= t + tj * 8 && index < t + tj * 8 + 4
				&& index < t + (n - 1) * 8 + 4) {
			tt_length[tj] = swap(b, index, tt_length[tj], t + tj * 8);

			// increment the current entry index
			if (index == t + tj * 8 + 3) {
				tj++;
			}
		} else if (tj > 0 && index >= t + (tj - 1) * 8 + 4
				&& index < t + (tj - 1) * 8 + 4 + 4 && index < t + (n - 1) * 8 + 4 + 4) {
			tt_offset[tj - 1] = swap(b, index, tt_offset[tj - 1], t + (tj - 1) * 8 + 4);
		}
	}

	/**
	 * Incremental swap
	 * 
	 * @return signed 32 bit integer
	 */
	private int swap(byte b, int index, int n, int k) {
		int x = reversed ? (index - k) * 8 : (3 - index - k) * 8;
		if (x != 0) {
			n = n | (b & 0xff) << x;
		} else {
			n = n | b & 0xff;
		}
		return n;
	}

	/**
	 * Release the offset and length arrays when they aren't needed anymore.
	 */
	private void releaseOffsetAndLenghtArrays() {
		oo_length = null;
		oo_offset = null;
		tt_length = null;
		tt_offset = null;
	}

	@Override
	public Enumeration<String> getKeys() {
		Enumeration<String> en = new Enumeration<String>() {
			private int elementIndex = 0;

			@Override
			public boolean hasMoreElements() {
				return elementIndex < msgidByteBuffers.length;
			}

			@Override
			public String nextElement() {
				String nextElement = GettextResource.this
						.toString(msgidByteBuffers[elementIndex]);
				elementIndex++;
				return nextElement;
			}
		};

		return en;
	}

	/**
	 * Note that we don't throw a MissingResourceException when no translation
	 * is found. In the GNU gettext approach, the gettext function returns the
	 * (English) message key in that case.
	 */
	@Override
	protected Object handleGetObject(String key) {
		return _(key);
	}

	public String _(String msgid) {
		byte[] array = msgid.getBytes();
		// replace the pipe char with the separator char (0x04).
		if (useContextSpecifier) {
			for (int i = 0; i < array.length; i++) {
				if (array[i] == '|') {
					array[i] = CONTEXT_SPECIFIER;
				}
			}
		}

		ByteBuffer msgidByteBuffer = ByteBuffer.wrap(array);
		int idx = Arrays.binarySearch(msgidByteBuffers, msgidByteBuffer);

		if (idx < 0) {
			return handleMissingTranslation(msgid);
		}

		String msgstr = toString(msgstrByteBuffers[idx]);
		if (msgstr.isEmpty()) {
			return handleMissingTranslation(msgid);
		}
		return msgstr;
	}

	private String handleMissingTranslation(String msgid) {
		String tmpMsgid = msgid;
		String msgstr = tmpMsgid;
		int indexOf = tmpMsgid.indexOf('|');
		if (indexOf > 0) {
			tmpMsgid = tmpMsgid.substring(indexOf + 1);

			// try to search the key without context
			msgstr = _(tmpMsgid);
		}

		return showMissingTranslation ? "*" + msgstr + "*" : msgstr;
	}

	public String getInfoMessage() {
		return _("");
	}

	/**
	 * Mark missing translation with one star at the beginning and at the end or
	 * two if also the context lacks (for example: _("Stock label|Missing" -->
	 * "**Missing**" or _("Missing" --> "*Missing*").
	 * 
	 * @param showMissingTranslation
	 */
	public void markMissingTranslation(boolean showMissingTranslation) {
		this.showMissingTranslation = showMissingTranslation;
	}

	private String toString(ByteBuffer buf) {
		byte[] array = new byte[buf.limit()];

		// Reset the position before and after reading
		buf.position(0);
		buf.get(array);
		buf.position(0);

		// replace separator char (0x04) with a pipe char.
		for (int i = 0; i < array.length; i++) {
			if (array[i] == CONTEXT_SPECIFIER) {
				array[i] = '|';
			}
		}

		return new String(array, Charset.forName("UTF-8"));
	}

	static public boolean hasTranslation(Locale loc, String localedir, String textdomain) {
		return findMoFile(loc, localedir, textdomain) != null;
	}

	static public boolean hasTranslation(Locale loc, String textdomain) {
		return hasTranslation(loc, DEFAULT_LOCALES_DIRECTORY, textdomain);
	}

	static public boolean hasTranslation(String textdomain) {
		return hasTranslation(Locale.getDefault(), DEFAULT_LOCALES_DIRECTORY, textdomain);
	}

	/**
	 * Some debugging methods follow...
	 */
	private void logInitOffset() {
		if (DEBUG) {
			debug("reversed: ", reversed);
			debug("n = ", n);
			debug("o = ", o);
			debug("t = ", t);
			debug("s = ", s);
			debug("h = ", h);
		}
	}

	private void logOffsetAndLenghtInfo() {
		if (DEBUG) {
			debug("oo_offset = ", oo_offset);
			debug("oo_length = ", oo_length);
			debug("tt_offset = ", tt_offset);
			debug("tt_length = ", tt_length);
		}
	}

	/**
	 * Log msgids and msgstrs.
	 */
	private void logMessages() {
		if (DEBUG) {
			for (int i = 0; i < msgidByteBuffers.length; i++) {
				debug("msgid: \"" + toString(msgidByteBuffers[i]) + "\"");
				debug("msgstr: \"" + toString(msgstrByteBuffers[i]) + "\"");
				debug("");
			}
		}
	}

	private void debug(Object... msgs) {
		if (DEBUG) {
			for (Object msg : msgs) {
				if (msg == null) {
					System.out.print("null");
				} else if (msg.getClass().isArray()) {
					System.out.print("[");
					int len = Array.getLength(msg);
					for (int i = 0; i < len; i++) {
						System.out.print(Array.get(msg, i));
						if (i != (len - 1)) {
							System.out.print(", ");
						}
					}
					System.out.print("]");
				} else {
					System.out.print(String.valueOf(msg));
				}
			}
			System.out.println();
		}
	}

	/**
	 * Static method for execution as cli tool
	 */

	private static void printUsage() {
		System.out.println("Usage: gettextResource [-k MSGID] | [-i] FILENAME");
		System.out.println();
		System.out.println("Get the translation for the given MSGID or list all");
		System.out.println("MSGID/MGSSTR pairs when no option is given.");
		System.out.println();
		System.out.println("Options:");
		System.out.println("  -h, --help            Show this help message and exit");
		System.out.println("  -i, --info            Show the .mo file info");
		System.out.println("  -k MSGID              Get the associated msgstr");
		System.out.println();
		System.out.println();
		System.out.println("Examples:");
		System.out
				.println("  gettextResource -k Search /usr/share/locale/it/LC_MESSAGES/gtk20.mo");
		System.out.println("  gettextResource /usr/share/locale/it/LC_MESSAGES/gtk20.mo");
	}

	/**
	 * Command line user interface.
	 * 
	 * <pre>
	 * Usage: gettextResource [-k MSGID] FILENAME
	 * 
	 * Get the translation for the given MSGID or list all
	 * MSGID/MGSSTR pairs when no option is given.
	 * 
	 * Options:
	 *   -h, --help            Show this help message and exit
	 *   -k MSGID              Get the associated msgstr
	 * 
	 * 
	 * Examples:
	 *   gettextResource -k Search /usr/share/locale/it/LC_MESSAGES/gtk20.mo
	 *   gettextResource /usr/share/locale/it/LC_MESSAGES/gtk20.mo
	 * </pre>
	 */
	public static void main(String... args) {
		if (args.length == 0) {
			printUsage();
			System.exit(0);
		}

		try {
			String msgid = null;
			String filename = null;
			boolean showInfo = false;
			if ("-h".equals(args[0]) || "--help".equals(args[0])) {
				printUsage();
				System.exit(0);
			}
			if ("-i".equals(args[0]) || "--info".equals(args[0])) {
				showInfo = true;
				filename = args[1];
			} else if ("-k".equals(args[0]) || "-k".equals(args[0])) {
				msgid = args[1];
				filename = args[2];
			} else {
				filename = args[0];
			}

			if (filename != null) {
				if (!new File(filename).isAbsolute()) {
					filename = System.getProperty("user.dir") + File.separator + filename;
				}
			}

			GettextResource r = new GettextResource(new File(filename));
			if (showInfo) {
				System.out.println(r.getInfoMessage());
			} else if (msgid != null) {
				System.out.println(r._(msgid));
			} else {
				Enumeration<String> en = r.getKeys();
				while (en.hasMoreElements()) {
					String id = en.nextElement();
					String msgstr = r._(id);
					System.out.println("msgid \"" + id + "\"");
					System.out.println("msgstr \"" + msgstr + "\"");
					System.out.println();
				}
			}
		} catch (Throwable e) {
			// System.err.println("Invalid options: " + e.getMessage());
			e.printStackTrace();
			printUsage();
		}
	}
}
