/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
/*-- 

 $Id: PluginVersion.java,v 1.1.2.1 2010-04-30 22:04:38 brett Exp $

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

import java.util.StringTokenizer;

/**
 * Encapsule a version of either the plugin host or a plugin. A version consists
 * of a major version number and option minor, micro and other elements. Each
 * numeric element of the version cannot have a value of more than 1000.
 * 
 * @author magicthize
 * @created 26 May 2002
 */
public class PluginVersion implements Comparable {

    /**
     * Construct a new PluginVersion.
     * 
     * @param major
     *            major version
     */
    public PluginVersion(int major) throws IllegalArgumentException {
        this(major, -1, -1, null);
    }

    /**
     * Construct a new PluginVersion.
     * 
     * @param major
     *            major version
     * @param minor
     *            minor version
     */
    public PluginVersion(int major, int minor) throws IllegalArgumentException {
        this(major, minor, -1, null);
    }

    /**
     * Construct a new PluginVersion.
     * 
     * @param major
     *            major version
     * @param minor
     *            minor version
     * @param micro
     *            micro version
     */
    public PluginVersion(int major, int minor, int micro) throws IllegalArgumentException {
        this(major, minor, micro, null);
    }

    /**
     * Construct a new PluginVersion.
     * 
     * @param major
     *            major version
     * @param minor
     *            minor version
     * @param micro
     *            micro version
     * @param other
     *            other version
     */
    public PluginVersion(int major, int minor, int micro, String other) throws IllegalArgumentException {
        this.major = major;
        this.minor = minor;
        this.micro = micro;
        this.other = other;
        checkVersionNumbers();
    }

    /**
     * Construct a new PluginVersion given a string. The string must be in the
     * format <code><b>Version string must be in format <major>[.<minor>[.
     * <micro>[-<other>]]]</b></code>.
     * 
     * @param versionString
     * @throws IllegalArgumentException
     *             if version string is in incorrect format
     */
    public PluginVersion(String versionString) throws IllegalArgumentException {
        String s = versionString;
        int idx = s.lastIndexOf('-');
        if (idx == -1) {
            idx = s.lastIndexOf('_');
        }
        if (idx != -1) {
            other = versionString.substring(idx + 1);
            s = s.substring(0, idx);
        }
        StringTokenizer t = new StringTokenizer(s, ".");
        if (t.countTokens() < 1 || t.countTokens() > 3)
            throw new IllegalArgumentException("Version string '" + versionString
                            + "' must be in format <major>[.<minor>[.<micro>[-<other>]]]");
        try {
            major = Integer.parseInt(t.nextToken());
            if (t.hasMoreTokens())
                minor = Integer.parseInt(t.nextToken());
            else
                minor = -1;
            if (t.hasMoreTokens())
                micro = Integer.parseInt(t.nextToken());
            else
                micro = -1;
            checkVersionNumbers();
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Version string '" + versionString
                            + "' must be in format <major>.<minor>[.<micro>[-<other>]]");
        }
    }

    private void checkVersionNumbers() throws IllegalArgumentException {
        if (major > 1000 || major < -1)
            throw new IllegalArgumentException("Major version number is out of range.");
        if (minor > 1000 || minor < -1)
            throw new IllegalArgumentException("Minor version number is out of range.");
        if (micro > 1000 || micro < -1)
            throw new IllegalArgumentException("Micro version number is out of range.");
    }

    /**
     * Return the 'other' part of the version. Typically the phase such as
     * alpha, beta etc
     * 
     * @return other version
     */
    public String getOther() {
        return other;
    }

    /**
     * Return the full version string
     * 
     * @return version string
     */
    public String getVersionString() {
        StringBuffer buf = new StringBuffer(String.valueOf(getMajorVersion()));
        if (getMinorVersion() != -1) {
            buf.append(".");
            buf.append(String.valueOf(getMinorVersion()));
        }
        if (getMicroVersion() != -1) {
            buf.append(".");
            buf.append(String.valueOf(getMicroVersion()));
        }
        if (getOther() != null) {
            buf.append("-");
            buf.append(String.valueOf(getOther()));
        }
        return buf.toString();
    }

    /**
     * Return the full version string
     * 
     * @return version string
     */
    public String toString() {
        return getVersionString();
    }

    /**
     * Return the major version number
     * 
     * @return major version number
     */
    public int getMajorVersion() {
        return major;
    }

    /**
     * Return the minor version number
     * 
     * @return minor version
     */
    public int getMinorVersion() {
        return minor;
    }

    /**
     * Return the micro version number
     * 
     * @return micro version
     */
    public int getMicroVersion() {
        return micro;
    }

    /**
     * Compare two version
     * 
     * @param version
     *            version to compare this version against
     * @return comparison
     * @throws IllegalArgumentException
     *             if the two version cannot be compared
     */
    public int compareTo(Object o) {
        PluginVersion v2 = (PluginVersion) o;
        if (getElementCount() < 4 && v2.getElementCount() < 4 && getElementCount() != v2.getElementCount())
            throw new IllegalArgumentException("Version numbers " + toString() + " and " + o.toString()
                            + " are incompatible and cannot be compared");
        long l1 = getMajorVersion() * 10000000;
        if (getMinorVersion() != -1)
            l1 += getMinorVersion() * 10000;
        if (getMicroVersion() != -1)
            l1 += getMicroVersion();
        long l2 = v2.getMajorVersion() * 10000000;
        if (v2.getMinorVersion() != -1)
            l2 += v2.getMinorVersion() * 10000;
        if (v2.getMicroVersion() != -1)
            l2 += v2.getMicroVersion();

        int v = new Long(l1).compareTo(new Long(l2));
        if (v == 0) {
            boolean pre1 = getOther() != null && ( getOther().startsWith("rc") || getOther().startsWith("beta") || getOther().startsWith("alpha")
                            || getOther().startsWith("fcs") );
            boolean pre2 = v2.getOther() != null && ( v2.getOther().startsWith("rc") || v2.getOther().startsWith("beta") 
                            || v2.getOther().startsWith("alpha") || v2.getOther().startsWith("fcs") );
           
            if(pre1 && !pre2) {
                v = 1;
            }
            else if(!pre1 && pre2) {
                v = -1;
            }
            else if(!pre1 && !pre2) {
                if (getOther() != null && v2.getOther() == null)
                    v = -1;
                else if (getOther() == null && v2.getOther() != null)
                    v = 1;
                else if (getOther() == null && v2.getOther() == null)
                    v = 0;
                else 
                    v = getOther().compareTo(v2.getOther());
            }
            else {
                v =  -(getOther().compareTo(v2.getOther()));
            }
        }
        return v;
    }

    /**
     * Return how many elements of the version string this version has. I.e, if
     * there is a major and minor but no micro or other, then this method will
     * return two. This metho is used to make sure two version strings are
     * compatible and can be compared
     */
    public int getElementCount() {
        int i = 1;
        if (getMinorVersion() != -1)
            i++;
        if (getMicroVersion() != -1)
            i++;
        if (getOther() != null)
            i++;
        return i;
    }

    public static void main(String[] args) {

    }

    //	Private instance variables

    private int major, minor, micro;
    private String other;
}