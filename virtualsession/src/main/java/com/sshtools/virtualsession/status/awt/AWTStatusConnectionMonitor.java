/* HEADER */
package com.sshtools.virtualsession.status.awt;

import java.awt.Image;

import com.sshtools.ui.awt.UIUtil;
import com.sshtools.virtualsession.VirtualSession;
import com.sshtools.virtualsession.VirtualSessionAdapter;
import com.sshtools.virtualsession.VirtualSessionManagerListener;

/**
 * Extension of {@link AWTStatusLabel} that monitors the connections to and disconnections
 * from hots.
 *
 * @author $Author: brett $
 */
public class AWTStatusConnectionMonitor
    extends AWTStatusLabel {

  public final static Image CONNECTED = UIUtil.loadImage(AWTStatusConnectionMonitor.class,
      "/images/connected-16x16.png");
  public final static Image DISCONNECTED = UIUtil.loadImage(AWTStatusConnectionMonitor.class,
      "/images/disconnected-16x16.png");

  // private instance variables
  private ConnectionStatusListener connectionStatusListener;

  /**
   * Construct a new AWTStatusConnectionMonitor component.
   *
   * @param terminal
   */
  public AWTStatusConnectionMonitor(int borderType, VirtualSession session) {
    super(0);
    connectionStatusListener = new ConnectionStatusListener();
    session.addVirtualSessionListener(connectionStatusListener);
    session.getVirtualSessionManager().addVirtualSessionManagerListener(new VirtualSessionManagerListener() {

      public void virtualSessionAdded(VirtualSession session) {
          session.addVirtualSessionListener(connectionStatusListener);
      }

      public void virtualSessionRemoved(VirtualSession session) {
          session.removeVirtualSessionListener(connectionStatusListener);
      }

      public void virtualSessionSelected(VirtualSession session) {
        setText(session.isConnected() ? "Connected" : "Disconnected");
        setImage(session.isConnected() ? CONNECTED : DISCONNECTED);
      }

      public void virtualSessionDeselected(VirtualSession session) {
      }

      public void virtualSessionChanged(VirtualSession session) {

      }
    });
    setImage(DISCONNECTED);
    setText("Disconnected");
    setBorderType(borderType);
  }

  //  Supporting classes

  class ConnectionStatusListener
      extends VirtualSessionAdapter {
    public void connected(VirtualSession session) {
      setText("Connected");
      setImage(CONNECTED);
    }

    public void disconnected(VirtualSession session) {
      setText("Disconnected");
      setImage(DISCONNECTED);
    }
  }
}