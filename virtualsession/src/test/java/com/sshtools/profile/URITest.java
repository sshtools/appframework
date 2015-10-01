/* HEADER */
package com.sshtools.profile;

import org.junit.Assert;
import org.junit.Test;

import com.sshtools.profile.URI.MalformedURIException;

public class URITest {

    @Test
    public void testIPV6() throws MalformedURIException {
        URI uri = new URI("ssh://[2001:db8:5:1300:212:79ff:fe89:c900]");
        Assert.assertEquals("ssh", uri.getScheme());
        Assert.assertEquals("[2001:db8:5:1300:212:79ff:fe89:c900]", uri.getHost());
        Assert.assertEquals(-1, uri.getPort());
        Assert.assertEquals(null, uri.getUserinfo());
    }

    @Test
    public void testIPV6WithInterface() throws MalformedURIException {
        URI uri = new URI("ssh://[fe80::212:79ff:fe89:c900%5]");
        Assert.assertEquals("ssh", uri.getScheme());
        Assert.assertEquals("[fe80::212:79ff:fe89:c900%5]", uri.getHost());
        Assert.assertEquals(-1, uri.getPort());
        Assert.assertEquals(null, uri.getUserinfo());
    }

    @Test
    public void testIPV6WithPort() throws MalformedURIException {
        URI uri = new URI("ssh://[2001:db8:5:1300:212:79ff:fe89:c900]:22");
        Assert.assertEquals("ssh", uri.getScheme());
        Assert.assertEquals("[2001:db8:5:1300:212:79ff:fe89:c900]", uri.getHost());
        Assert.assertEquals(22, uri.getPort());
        Assert.assertEquals(null, uri.getUserinfo());
    }

    @Test
    public void testIPV4() throws MalformedURIException {
        URI uri = new URI("ssh://192.168.91.1");
        Assert.assertEquals("ssh", uri.getScheme());
        Assert.assertEquals("192.168.91.1", uri.getHost());
        Assert.assertEquals(-1, uri.getPort());
        Assert.assertEquals(null, uri.getUserinfo());
    }

    @Test
    public void testIPV4WithPort() throws MalformedURIException {
        URI uri = new URI("ssh://192.168.91.1:22");
        Assert.assertEquals("ssh", uri.getScheme());
        Assert.assertEquals("192.168.91.1", uri.getHost());
        Assert.assertEquals(22, uri.getPort());
        Assert.assertEquals(null, uri.getUserinfo());
    }

    @Test
    public void testIPV6WithPortAndUserinfo() throws MalformedURIException {
        URI uri = new URI("ssh://brett@[2001:db8:5:1300:212:79ff:fe89:c900]:22");
        Assert.assertEquals("ssh", uri.getScheme());
        Assert.assertEquals("[2001:db8:5:1300:212:79ff:fe89:c900]", uri.getHost());
        Assert.assertEquals(22, uri.getPort());
        Assert.assertEquals("brett", uri.getUserinfo());
    }

    @Test
    public void testIPV4WithPortAndUserInfo() throws MalformedURIException {
        URI uri = new URI("ssh://brett@192.168.91.1:22");
        Assert.assertEquals("ssh", uri.getScheme());
        Assert.assertEquals("192.168.91.1", uri.getHost());
        Assert.assertEquals(22, uri.getPort());
        Assert.assertEquals("brett", uri.getUserinfo());
    }

    @Test
    public void testHostname() throws MalformedURIException {
        URI uri = new URI("ssh://localhost.localdomain");
        Assert.assertEquals("ssh", uri.getScheme());
        Assert.assertEquals("localhost.localdomain", uri.getHost());
        Assert.assertEquals(-1, uri.getPort());
        Assert.assertEquals(null, uri.getUserinfo());
    }

    @Test
    public void testHostnameWithPortAndUserInfo() throws MalformedURIException {
        URI uri = new URI("ssh://brett@localhost.localdomain:22");
        Assert.assertEquals("ssh", uri.getScheme());
        Assert.assertEquals("localhost.localdomain", uri.getHost());
        Assert.assertEquals(22, uri.getPort());
        Assert.assertEquals("brett", uri.getUserinfo());
    }

    @Test
    public void testInvalidURI() {
        try {
            new URI("localhost.localdomain");
            Assert.fail("Should have failed to parse");
        } catch (MalformedURIException muri) {
            // Ok
        }
    }
}
