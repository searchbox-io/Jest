package io.searchbox.client.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class FailingProxy {
    private final HttpProxyServer server;

    private String errorContentType = "text/html";
    private HttpResponseStatus errorStatus = new HttpResponseStatus(500, "This proxy always fails");
    private String errorMessage = "<html>"
            + "  <head><title>This proxy always fails</title></head>"
            + "<body>"
            + "  <h1>This proxy always fails</h1>"
            + "</body>"
            + "</html>";

    public FailingProxy() throws IOException {
        final HttpProxyServerBootstrap bootstrap = DefaultHttpProxyServer
                .bootstrap()
                .withPort(getUnusedPort())
                .withTransparent(true)
                .withFiltersSource(new FailingSourceAdapter())
                ;
        server = bootstrap.start();
    }


    public void setErrorStatus(final HttpResponseStatus errorStatus) {
        this.errorStatus = errorStatus;
    }

    public void setErrorContentType(final String errorContentType) {
        this.errorContentType = errorContentType;
    }

    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getUrl() {
        final InetSocketAddress listenAddress = server.getListenAddress();
        final String host = listenAddress.getHostName();
        final int port = listenAddress.getPort();
        return String.format("http://%s:%d/", host, port);
    }

    public void stop() {
        server.stop();
    }

    private static int getUnusedPort() throws IOException {
        try (Socket deadSocket = new Socket()) {
            deadSocket.bind(null);
            final int port = deadSocket.getLocalPort();
            return port;
        }
    }

    private class FailingSourceAdapter extends HttpFiltersSourceAdapter {
        @Override
        public HttpFilters filterRequest(final HttpRequest originalRequest) {
            return new FailingFilterAdapter(originalRequest);
        }
    }

    private class FailingFilterAdapter extends HttpFiltersAdapter {

        public FailingFilterAdapter(final HttpRequest originalRequest) {
            super(originalRequest);
        }

        @Override
        public HttpResponse proxyToServerRequest(final HttpObject httpObject) {

            ByteBuf buf = Unpooled.wrappedBuffer(errorMessage.getBytes(Charset.forName("UTF-8")));

            final DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, errorStatus, buf);
            response.headers().set("Content-Type", errorContentType);

            return response;
        }
    }

}
