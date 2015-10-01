/*
 */
package com.sshtools.profile;

import nanoxml.XMLElement;

/**
     * <p>To allow each connection scheme to have its own protocol specific options,
     * a {@link ResourceProfile} can have a single SchemeOptions implementation.</p>
 *
 * <p>When the ResourceProfile XML file is being parsed and a SchemeOptions element
 * is encountered, the <b>className</b> attribute is used to instantiate
     * a class that implements this interface. All child elements are then passed to
 * the instance so the it can build up its properties.</p>
 *
 * @author $Author: brett $
 */
public interface SchemeOptions {

  /** Standard connection - just connect directly to the host  */
  public static final int USE_STANDARD_SOCKET = 1;

  /** Connect to the host through a HTTP proxy */
  public static final int USE_HTTP_PROXY = 2;

  /** Connect to the host using a SOCKS 4 proxy */
  public static final int USE_SOCKS4_PROXY = 3;

  /** Connect to the host using a SOCKS 5 proxy */
  public static final int USE_SOCKS5_PROXY = 4;

  /** Connect to the host using an SSL-Explorer proxy **/
  public static final int USE_SSL_EXPLORER_PROXY = 5;

  /**
   * Invoked when instantiated by the ResourceProfile parser or when creating a
       * new profile. If <code>null</code> is provided as the element, default options
   * will be set.
   *
   * @param element SchemeOptions element
   * @throws ProfileException if any data provided by the element is invalid
   */
  public void init(XMLElement element) throws ProfileException;

  /**
   * Get these options as an XML element that can be persisted along with
   * the ResourceProfile.
   *
   * @return XML element for persisting along with the ResourceProfile
   */
  public XMLElement getElement();

  /**
   * Return if these options are appropriate for a scheme
   *
   * @param schemeName
   * @return <code>true</code> if these options are appropriate for a scheme
   */
  public boolean isAppropriateForScheme(String schemeName);

  /**
   * The type of transport to communicate over. This will be one of the
   * proxy settings defined on this interface
   * @return
   */
  public int getTransportProvider();
}
