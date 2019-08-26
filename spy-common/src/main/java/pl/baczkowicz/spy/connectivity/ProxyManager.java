package pl.baczkowicz.spy.connectivity;

/**
 * Copyright Olivier Matheret 2019 - add HTTP-Proxy feature
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager of the proxy in the JVM environment
 */
public class ProxyManager {
    /** Diagnostic logger. */
    private final static Logger logger = LoggerFactory.getLogger(ProxyManager.class);

    /**
     * Forces the use of an HTTP proxy for all schemes
     */
    private static class ProxyForceHttp extends ProxySelector {
        ProxySelector defSel = null;

        ProxyForceHttp(ProxySelector def) {
            defSel = def;
        }

        @Override
        public java.util.List<Proxy> select(URI uri) {
            if (uri == null)
                throw new IllegalArgumentException("URI can't be null.");

            URI httpUri;
            try {
                httpUri = new URI("http", uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment());
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("URI is invalid", e);
            }

            if (defSel != null) {
                return defSel.select(httpUri);
            } else {
                ArrayList<Proxy> l = new ArrayList<>();
                l.add(Proxy.NO_PROXY);
                return l;
            }
        }

        @Override
        public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        }
    }

    /**
     * Sets in the JVM environment the proxy defined, for internet hosts only
     */
    public static void loadProxy(String host, int port, String user, char[] password) {

        if (host != null && !(host.isEmpty())) {
            logger.info("Using HTTP proxy {}{}{}", host, (user != null && !user.isEmpty()) ? " with user " : "", user);

            // Define the default proxy as being the one in the properties
            ProxySelector.setDefault(new ProxySelector() {
                @Override
                public List<Proxy> select(URI uri) {
                    List<Proxy> proxies = new ArrayList<>();
                    String h = uri.getHost();
                    if (h.contains(".")) {
                        try {
                            InetAddress hname = InetAddress.getByName(h);
                            if (!hname.isLoopbackAddress() && !hname.isAnyLocalAddress() && !hname.isLinkLocalAddress() && !hname.isSiteLocalAddress()) {
                                InetSocketAddress addr = new InetSocketAddress(host, port);
                                proxies.add(new Proxy(Proxy.Type.HTTP, addr));
                                return proxies;
                            }
                        } catch (UnknownHostException e) {
                            // host is unknown : proxy might be compulsory to even access DNS !
                            logger.warn("Unknown hostname {}", h);
                            InetSocketAddress addr = new InetSocketAddress(host, port);
                            proxies.add(new Proxy(Proxy.Type.HTTP, addr));
                            return proxies;
                        }
                    }
                    proxies.add(Proxy.NO_PROXY);
                    return proxies;
                }

                @Override
                public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                }
            });

            // Define the associated user/password
            if (user != null && !(user.isEmpty())) {
                Authenticator.setDefault(new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        if (getRequestingHost() != null && getRequestingHost().equals(host))
                            return new PasswordAuthentication(user, password);
                        return null;
                    }
                });
            }

            // Finally, force the HTTP proxy for other schemes
            ProxySelector.setDefault(new ProxyForceHttp(ProxySelector.getDefault()));
        }
    }
}