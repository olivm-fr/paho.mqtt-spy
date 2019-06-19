package pl.baczkowicz.mqttspy.connectivity;

/**
 * Copyright Olivier Matheret 2019 - add HTTP-Proxy feature
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.SocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Base64;
import java.util.List;

public class HttpProxySocketFactory extends SocketFactory {
    public static class HttpProxySocket extends Socket {
        @Override
        public void connect(SocketAddress endpoint) throws IOException {
            connect(endpoint, 0);
        }

        @Override
        public void connect(SocketAddress endpoint, int timeout) throws IOException {
            // Get the proxies configured in the Java environment, for the target host/port/protocol
            URI uri;
            try {
                uri = new URI("mqtt", null, ((InetSocketAddress) endpoint).getHostString(), ((InetSocketAddress) endpoint).getPort(), null, null, null);
            } catch (URISyntaxException e) {
                throw new IOException(e.getMessage());
            }
            Proxy proxy = null;
            List<Proxy> proxies = ProxySelector.getDefault().select(uri);
            if (proxies != null && !proxies.isEmpty()) {
                proxy = proxies.get(0);
            }

            // for HTTP proxy, we connect to the proxy instead of the target
            SocketAddress addrUse;
            if (proxy != null && proxy.type() == Proxy.Type.HTTP) {
                addrUse = proxy.address();
                super.connect(addrUse, timeout);

                // HTTP CONNECT protocol RFC 2616
                String proxyConnect = "CONNECT " + ((InetSocketAddress) endpoint).getHostString() + ":" + ((InetSocketAddress) endpoint).getPort() + " HTTP/1.0\r\n";
                proxyConnect += "Proxy-Connection: keep-alive\r\n";

                // Add Proxy Authorization using Java environment
                InetSocketAddress pa = ((InetSocketAddress) proxy.address());
                PasswordAuthentication pwd = Authenticator.requestPasswordAuthentication(pa.getHostString(), pa.getAddress(), pa.getPort(), "http", null, null);
                if (pwd != null && !(pwd.getUserName().isEmpty())) {
                    String proxyUserPass = pwd.getUserName() + ":" + new String(pwd.getPassword());
                    proxyConnect += "Proxy-Authorization:Basic " + Base64.getEncoder().encodeToString(proxyUserPass.getBytes()) + "\r\n";
                }
                proxyConnect += "\r\n";
                getOutputStream().write(proxyConnect.getBytes());
                getOutputStream().flush();
                logger.debug("Proxy request : " + proxyConnect.split("\n")[0]);

                // validate HTTP response
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(getInputStream()));
                String proxyResponse = socketInput.readLine();
                logger.debug("Proxy response : {}", proxyResponse);
                if (proxyResponse == null)
                    throw new IOException("Fail to create Socket : proxy closed the connection");
                if (!proxyResponse.trim().matches("HTTP[/0-9.]+ 200 Connection established.*"))
                    throw new IOException("Fail to create Socket : " + proxyResponse);
                do {
                    proxyResponse = socketInput.readLine();
                    if (proxyResponse.length() > 0)
                        logger.debug("Proxy additional response : {}", proxyResponse);
                    if (proxyResponse == null)
                        throw new IOException("Fail to create Socket : proxy closed the connection");
                } while (proxyResponse.length() != 0);

                logger.debug("Proxy connected");

                if (socketInput.ready())
                    logger.debug("Skipping proxy discussion");
                while (socketInput.ready())
                    socketInput.skip(1);
            } else {
                super.connect(endpoint, timeout);
            }
        }
    }

    /**
     * Diagnostic logger.
     */
    private final static Logger logger = LoggerFactory.getLogger(HttpProxySocketFactory.class);

    @Override
    public Socket createSocket() throws IOException {
        // Get the proxies configured in the Java environment, for the an internet-based host/port
        URI uri;
        try {
            uri = new URI("http", null, "google.com", 443, null, null, null);
        } catch (URISyntaxException e) {
            throw new IOException(e.getMessage());
        }
        Proxy proxy = null;
        List<Proxy> proxies = ProxySelector.getDefault().select(uri);
        if (proxies != null && !proxies.isEmpty()) {
            proxy = proxies.get(0);
            // DIRECT proxy is not a proxy
            if (proxy.type() == Proxy.Type.DIRECT)
                proxy = null;
            if (proxy != null)
                logger.info("Using {}-proxy {} to connect", proxy.type(), proxy.address());
        }

        // SOCKS proxy is handled directly by the JVM
        if (proxy == null)
            return new Socket();
        else if (proxy.type() == Proxy.Type.SOCKS)
            return new Socket(proxy);
        else // HTTP proxy : the JVM can't handle that ; we'll do it
            return new HttpProxySocket(); // force the system to ignore its default proxy detection

    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        UnsupportedOperationException var1 = new UnsupportedOperationException();
        SocketException var2 = new SocketException("Unconnected sockets not implemented");
        var2.initCause(var1);
        throw var2;
    }

    @Override
    public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) throws IOException, UnknownHostException {
        UnsupportedOperationException var1 = new UnsupportedOperationException();
        SocketException var2 = new SocketException("Unconnected sockets not implemented");
        var2.initCause(var1);
        throw var2;
    }

    @Override
    public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
        UnsupportedOperationException var1 = new UnsupportedOperationException();
        SocketException var2 = new SocketException("Unconnected sockets not implemented");
        var2.initCause(var1);
        throw var2;
    }

    @Override
    public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) throws IOException {
        UnsupportedOperationException var1 = new UnsupportedOperationException();
        SocketException var2 = new SocketException("Unconnected sockets not implemented");
        var2.initCause(var1);
        throw var2;
    }
}
