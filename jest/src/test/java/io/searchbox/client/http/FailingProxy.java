package io.searchbox.client.http;

import java.net.InetSocketAddress;
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
    public static final int PROXY_PORT = 54321;

    private final HttpProxyServer server;

    public FailingProxy() {
        final HttpProxyServerBootstrap bootstrap = DefaultHttpProxyServer
                .bootstrap()
                .withPort(PROXY_PORT)
                .withTransparent(true)
                .withFiltersSource(new FailingSourceAdapter())
                ;
        server = bootstrap.start();
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

    private static class FailingSourceAdapter extends HttpFiltersSourceAdapter {
        @Override
        public HttpFilters filterRequest(final HttpRequest originalRequest) {
            return new FailingFilterAdapter(originalRequest);
        }
    }

    private static class FailingFilterAdapter extends HttpFiltersAdapter {

        public FailingFilterAdapter(final HttpRequest originalRequest) {
            super(originalRequest);
        }

        @Override
        public HttpResponse requestPre(final HttpObject httpObject) {

            final HttpResponseStatus status = new HttpResponseStatus(500, "This proxy always fails");

            final String message = "<html>"
                    + "  <head><title>This proxy always fails</title></head>"
                    + "<body>"
                    + "  <h1>This proxy always fails</h1>"
                    + "</body>"
                    + "</html>";
            ByteBuf buf = Unpooled.wrappedBuffer(message.getBytes(Charset.forName("UTF-8")));

            final DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buf);
            response.headers().set("Content-Type", "text/html");

            return response;
        }
    }

}
