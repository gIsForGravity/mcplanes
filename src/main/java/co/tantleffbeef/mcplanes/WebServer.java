package co.tantleffbeef.mcplanes;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;

import java.io.File;
import java.net.InetSocketAddress;

public class WebServer {
    private final HttpServer server;

    public WebServer(File webserverDirectory, String bind, int port) {
        server = SimpleFileServer.createFileServer(new InetSocketAddress(bind, port), webserverDirectory.toPath(),
                SimpleFileServer.OutputLevel.VERBOSE);
    }

    public void startup() {
        server.start();
        server.stop(5);
    }
}
