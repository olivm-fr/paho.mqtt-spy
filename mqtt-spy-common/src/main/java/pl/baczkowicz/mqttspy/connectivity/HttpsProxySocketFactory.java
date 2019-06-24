package pl.baczkowicz.mqttspy.connectivity;

/**
 * Copyright Olivier Matheret 2019 - add HTTP-Proxy feature
 */

import com.sun.istack.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.baczkowicz.mqttspy.connectivity.HttpProxySocketFactory.HttpProxySocket;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.channels.SocketChannel;

public class HttpsProxySocketFactory extends SSLSocketFactory {
    @Nullable
    private final SSLSocketFactory applicationSocketFactory;
    private final HttpProxySocketFactory plainFactory = new HttpProxySocketFactory();

    public HttpsProxySocketFactory(@Nullable SSLSocketFactory socketFactory) {
        this.applicationSocketFactory = socketFactory;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return applicationSocketFactory.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return applicationSocketFactory.getSupportedCipherSuites();
    }

    public class HttpsProxySocket extends SSLSocket {
        Socket plainSocket;
        SSLSocket sslSocket;

        @Override
        public void connect(SocketAddress endpoint) throws IOException {
            connect(endpoint, 0);
        }

        @Override
        public void connect(SocketAddress endpoint, int timeout) throws IOException {
            plainSocket = new HttpProxySocket();
            plainSocket.connect(endpoint, timeout);
            sslSocket = (SSLSocket) applicationSocketFactory.createSocket(plainSocket, ((InetSocketAddress) endpoint).getHostString(), ((InetSocketAddress) endpoint).getPort(), true);
        }

        @Override
        public InetAddress getInetAddress() {
            return sslSocket.getInetAddress();
        }

        @Override
        public InetAddress getLocalAddress() {
            return sslSocket.getLocalAddress();
        }

        @Override
        public int getPort() {
            return sslSocket.getPort();
        }

        @Override
        public int getLocalPort() {
            return sslSocket.getLocalPort();
        }

        @Override
        public SocketAddress getRemoteSocketAddress() {
            return sslSocket.getRemoteSocketAddress();
        }

        @Override
        public SocketAddress getLocalSocketAddress() {
            return sslSocket.getLocalSocketAddress();
        }

        @Override
        public SocketChannel getChannel() {
            return sslSocket.getChannel();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return sslSocket.getInputStream();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return sslSocket.getOutputStream();
        }

        @Override
        public void setTcpNoDelay(boolean on) throws SocketException {
            sslSocket.setTcpNoDelay(on);
        }

        @Override
        public boolean getTcpNoDelay() throws SocketException {
            return sslSocket.getTcpNoDelay();
        }

        @Override
        public void setSoLinger(boolean on, int linger) throws SocketException {
            sslSocket.setSoLinger(on, linger);
        }

        @Override
        public int getSoLinger() throws SocketException {
            return sslSocket.getSoLinger();
        }

        @Override
        public void sendUrgentData(int data) throws IOException {
            sslSocket.sendUrgentData(data);
        }

        @Override
        public void setOOBInline(boolean on) throws SocketException {
            sslSocket.setOOBInline(on);
        }

        @Override
        public boolean getOOBInline() throws SocketException {
            return sslSocket.getOOBInline();
        }

        @Override
        public synchronized void setSoTimeout(int timeout) throws SocketException {
            sslSocket.setSoTimeout(timeout);
        }

        @Override
        public synchronized int getSoTimeout() throws SocketException {
            return sslSocket.getSoTimeout();
        }

        @Override
        public synchronized void setSendBufferSize(int size) throws SocketException {
            sslSocket.setSendBufferSize(size);
        }

        @Override
        public synchronized int getSendBufferSize() throws SocketException {
            return sslSocket.getSendBufferSize();
        }

        @Override
        public synchronized void setReceiveBufferSize(int size) throws SocketException {
            sslSocket.setReceiveBufferSize(size);
        }

        @Override
        public synchronized int getReceiveBufferSize() throws SocketException {
            return sslSocket.getReceiveBufferSize();
        }

        @Override
        public void setKeepAlive(boolean on) throws SocketException {
            sslSocket.setKeepAlive(on);
        }

        @Override
        public boolean getKeepAlive() throws SocketException {
            return sslSocket.getKeepAlive();
        }

        @Override
        public void setTrafficClass(int tc) throws SocketException {
            sslSocket.setTrafficClass(tc);
        }

        @Override
        public int getTrafficClass() throws SocketException {
            return sslSocket.getTrafficClass();
        }

        @Override
        public void setReuseAddress(boolean on) throws SocketException {
            sslSocket.setReuseAddress(on);
        }

        @Override
        public boolean getReuseAddress() throws SocketException {
            return sslSocket.getReuseAddress();
        }

        @Override
        public synchronized void close() throws IOException {
            sslSocket.close();
        }

        @Override
        public void shutdownInput() throws IOException {
            sslSocket.shutdownInput();
        }

        @Override
        public void shutdownOutput() throws IOException {
            sslSocket.shutdownOutput();
        }

        @Override
        public String toString() {
            return sslSocket.toString();
        }

        @Override
        public boolean isConnected() {
            return sslSocket.isConnected();
        }

        @Override
        public boolean isBound() {
            return sslSocket.isBound();
        }

        @Override
        public boolean isClosed() {
            return sslSocket.isClosed();
        }

        @Override
        public boolean isInputShutdown() {
            return sslSocket.isInputShutdown();
        }

        @Override
        public boolean isOutputShutdown() {
            return sslSocket.isOutputShutdown();
        }

        @Override
        public void setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
            sslSocket.setPerformancePreferences(connectionTime, latency, bandwidth);
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return sslSocket.getSupportedCipherSuites();
        }

        @Override
        public String[] getEnabledCipherSuites() {
            return sslSocket.getEnabledCipherSuites();
        }

        @Override
        public void setEnabledCipherSuites(String[] strings) {
            sslSocket.setEnabledCipherSuites(strings);
        }

        @Override
        public String[] getSupportedProtocols() {
            return sslSocket.getSupportedProtocols();
        }

        @Override
        public String[] getEnabledProtocols() {
            return sslSocket.getEnabledProtocols();
        }

        @Override
        public void setEnabledProtocols(String[] strings) {
            sslSocket.setEnabledProtocols(strings);
        }

        @Override
        public SSLSession getSession() {
            return sslSocket.getSession();
        }

        @Override
        public SSLSession getHandshakeSession() {
            return sslSocket.getHandshakeSession();
        }

        @Override
        public void addHandshakeCompletedListener(HandshakeCompletedListener handshakeCompletedListener) {
            sslSocket.addHandshakeCompletedListener(handshakeCompletedListener);
        }

        @Override
        public void removeHandshakeCompletedListener(HandshakeCompletedListener handshakeCompletedListener) {
            sslSocket.removeHandshakeCompletedListener(handshakeCompletedListener);
        }

        @Override
        public void startHandshake() throws IOException {
            sslSocket.startHandshake();
        }

        @Override
        public void setUseClientMode(boolean b) {
            sslSocket.setUseClientMode(b);
        }

        @Override
        public boolean getUseClientMode() {
            return sslSocket.getUseClientMode();
        }

        @Override
        public void setNeedClientAuth(boolean b) {
            sslSocket.setNeedClientAuth(b);
        }

        @Override
        public boolean getNeedClientAuth() {
            return sslSocket.getNeedClientAuth();
        }

        @Override
        public void setWantClientAuth(boolean b) {
            sslSocket.setWantClientAuth(b);
        }

        @Override
        public boolean getWantClientAuth() {
            return sslSocket.getWantClientAuth();
        }

        @Override
        public void setEnableSessionCreation(boolean b) {
            sslSocket.setEnableSessionCreation(b);
        }

        @Override
        public boolean getEnableSessionCreation() {
            return sslSocket.getEnableSessionCreation();
        }

        @Override
        public SSLParameters getSSLParameters() {
            return sslSocket.getSSLParameters();
        }

        @Override
        public void setSSLParameters(SSLParameters sslParameters) {
            sslSocket.setSSLParameters(sslParameters);
        }
    }

    /**
     * Diagnostic logger.
     */
    private final static Logger logger = LoggerFactory.getLogger(HttpsProxySocketFactory.class);

    @Override
    public Socket createSocket() throws IOException {
        Socket plainSocket = plainFactory.createSocket();
        if (plainSocket instanceof HttpProxySocket)
            return new HttpsProxySocket();
        else
            return plainSocket;
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        throw new UnsupportedOperationException(new SocketException("Unconnected sockets not implemented"));
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
