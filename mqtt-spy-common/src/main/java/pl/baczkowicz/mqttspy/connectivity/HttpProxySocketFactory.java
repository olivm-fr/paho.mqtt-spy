package pl.baczkowicz.mqttspy.connectivity;

/**
 * Copyright Olivier Matheret 2019 - add HTTP-Proxy feature
 */

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.ProxyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.SocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.List;

public class HttpProxySocketFactory extends SocketFactory {

    public static class HttpProxySocket extends Socket {
        Socket appSocket;

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
            if (proxy != null && proxy.type() == Proxy.Type.HTTP) {
                // Add Proxy Authorization using Java environment
                InetSocketAddress pa = ((InetSocketAddress) proxy.address());
                PasswordAuthentication pwd = Authenticator.requestPasswordAuthentication(pa.getHostString(), pa.getAddress(), pa.getPort(), "http", null, null);

                // HTTP CONNECT protocol RFC 2616
                RequestConfig.Builder requestConfig = RequestConfig.copy(RequestConfig.custom().build())
                        .setSocketTimeout(timeout)
                        .setConnectTimeout(timeout)
                        .setConnectionRequestTimeout(timeout);
                HttpHost target = new HttpHost(((InetSocketAddress) endpoint).getHostName(), ((InetSocketAddress) endpoint).getPort());
                HttpHost proxyHost = new HttpHost(((InetSocketAddress) proxy.address()).getHostName(), ((InetSocketAddress) proxy.address()).getPort());
                Credentials credentials;
                if (pwd != null) {
                    if (pwd.getUserName().contains("/") || pwd.getUserName().contains("\\")) {
                        requestConfig.setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.SPNEGO));
                        String[] usersplit = pwd.getUserName().split("/|\\\\", 2);
                        credentials = new NTCredentials(usersplit[1], new String(pwd.getPassword()), InetAddress.getLocalHost().getHostName(), usersplit[0]);
                    } else {
                        requestConfig.setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC));
                        credentials = new UsernamePasswordCredentials(pwd.getUserName(), new String(pwd.getPassword()));
                    }
                }
                else {
                    credentials = new UsernamePasswordCredentials("", "");
                }
                try {
                    ProxyClient proxyClient = new ProxyClient(requestConfig.build());
                    appSocket = proxyClient.tunnel(proxyHost, target, credentials);
                } catch (HttpException e) {
                    logger.warn("Proxy connection failed", e);
                    throw new IOException(e);
                }

                logger.debug("Proxy connected");

            } else {
                appSocket = new Socket();
                appSocket.connect(endpoint, timeout);
            }
        }

        @Override
        public InetAddress getInetAddress() {
            return appSocket.getInetAddress();
        }

        @Override
        public InetAddress getLocalAddress() {
            return appSocket.getLocalAddress();
        }

        @Override
        public int getPort() {
            return appSocket.getPort();
        }

        @Override
        public int getLocalPort() {
            return appSocket.getLocalPort();
        }

        @Override
        public SocketAddress getRemoteSocketAddress() {
            return appSocket.getRemoteSocketAddress();
        }

        @Override
        public SocketAddress getLocalSocketAddress() {
            return appSocket.getLocalSocketAddress();
        }

        @Override
        public SocketChannel getChannel() {
            return appSocket.getChannel();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return appSocket.getInputStream();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return appSocket.getOutputStream();
        }

        @Override
        public void setTcpNoDelay(boolean on) throws SocketException {
            appSocket.setTcpNoDelay(on);
        }

        @Override
        public boolean getTcpNoDelay() throws SocketException {
            return appSocket.getTcpNoDelay();
        }

        @Override
        public void setSoLinger(boolean on, int linger) throws SocketException {
            appSocket.setSoLinger(on, linger);
        }

        @Override
        public int getSoLinger() throws SocketException {
            return appSocket.getSoLinger();
        }

        @Override
        public void sendUrgentData(int data) throws IOException {
            appSocket.sendUrgentData(data);
        }

        @Override
        public void setOOBInline(boolean on) throws SocketException {
            appSocket.setOOBInline(on);
        }

        @Override
        public boolean getOOBInline() throws SocketException {
            return appSocket.getOOBInline();
        }

        @Override
        public synchronized void setSoTimeout(int timeout) throws SocketException {
            appSocket.setSoTimeout(timeout);
        }

        @Override
        public synchronized int getSoTimeout() throws SocketException {
            return appSocket.getSoTimeout();
        }

        @Override
        public synchronized void setSendBufferSize(int size) throws SocketException {
            appSocket.setSendBufferSize(size);
        }

        @Override
        public synchronized int getSendBufferSize() throws SocketException {
            return appSocket.getSendBufferSize();
        }

        @Override
        public synchronized void setReceiveBufferSize(int size) throws SocketException {
            appSocket.setReceiveBufferSize(size);
        }

        @Override
        public synchronized int getReceiveBufferSize() throws SocketException {
            return appSocket.getReceiveBufferSize();
        }

        @Override
        public void setKeepAlive(boolean on) throws SocketException {
            appSocket.setKeepAlive(on);
        }

        @Override
        public boolean getKeepAlive() throws SocketException {
            return appSocket.getKeepAlive();
        }

        @Override
        public void setTrafficClass(int tc) throws SocketException {
            appSocket.setTrafficClass(tc);
        }

        @Override
        public int getTrafficClass() throws SocketException {
            return appSocket.getTrafficClass();
        }

        @Override
        public void setReuseAddress(boolean on) throws SocketException {
            appSocket.setReuseAddress(on);
        }

        @Override
        public boolean getReuseAddress() throws SocketException {
            return appSocket.getReuseAddress();
        }

        @Override
        public synchronized void close() throws IOException {
            appSocket.close();
        }

        @Override
        public void shutdownInput() throws IOException {
            appSocket.shutdownInput();
        }

        @Override
        public void shutdownOutput() throws IOException {
            appSocket.shutdownOutput();
        }

        @Override
        public String toString() {
            return appSocket.toString();
        }

        @Override
        public boolean isConnected() {
            return appSocket.isConnected();
        }

        @Override
        public boolean isBound() {
            return appSocket.isBound();
        }

        @Override
        public boolean isClosed() {
            return appSocket.isClosed();
        }

        @Override
        public boolean isInputShutdown() {
            return appSocket.isInputShutdown();
        }

        @Override
        public boolean isOutputShutdown() {
            return appSocket.isOutputShutdown();
        }

        @Override
        public void setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
            appSocket.setPerformancePreferences(connectionTime, latency, bandwidth);
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
        throw new UnsupportedOperationException(new SocketException("Unconnected sockets not implemented"));
    }

    @Override
    public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) throws IOException, UnknownHostException {
        throw new UnsupportedOperationException(new SocketException("Unconnected sockets not implemented"));
    }

    @Override
    public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
        throw new UnsupportedOperationException(new SocketException("Unconnected sockets not implemented"));
    }

    @Override
    public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) throws IOException {
        throw new UnsupportedOperationException(new SocketException("Unconnected sockets not implemented"));
    }
}
