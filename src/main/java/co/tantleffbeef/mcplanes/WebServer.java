package co.tantleffbeef.mcplanes;

import com.sun.net.httpserver.HttpServer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
//import com.sun.net.httpserver.SimpleFileServer;

import java.io.File;
import java.net.InetSocketAddress;

public class WebServer {
    /*private final HttpServer server;

    public WebServer(File webserverDirectory, String bind, int port) {
        server = SimpleFileServer.createFileServer(new InetSocketAddress(bind, port), webserverDirectory
                        .getAbsoluteFile().toPath(),
                SimpleFileServer.OutputLevel.VERBOSE);
    }

    /**
     * Starts the web server
     *
    public void startup() {
        server.start();
    }

    /**
     * Stops the web server, waiting 2 seconds for connections to end before
     * forcing them closed.
     *
    public void stop() {
        server.stop(2);
    }*/

    private final Server server;

    public WebServer(File webserverDirectory, String bind, int port) {
        server = new Server(new InetSocketAddress(bind, port));

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirAllowed(true);
        resourceHandler.setResourceBase(webserverDirectory.getAbsolutePath());

        server.setHandler(resourceHandler);
    }

    public void start() {
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        try {
            server.stop();
            server.join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
