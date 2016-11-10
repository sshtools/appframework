/* HEADER */
package com.sshtools.virtualsession;

import com.sshtools.profile.ProfileTransport;

/**
 * The transport mechanism used by a
 * {@link com.sshtools.virtualsession.VirtualSession} should implement this
 * interface. This simply extends {@link com.sshtools.profile.ProfileTransport}
 * to provide additional session related methods.
 *
 * @author Brett
 */
public interface VirtualSessionTransport<S> extends ProfileTransport<S> {

}