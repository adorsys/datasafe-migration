package de.adorsys.datasafemigration;

import lombok.extern.slf4j.Slf4j;
import org.hsqldb.Server;

@Slf4j
public class JDBCServerFactory {
    private Server server = new Server();
    public void start() {
        log.info("START HSQLSERVER");
        server.setSilent(true);
        server.setDatabaseName(0, "test");
        server.setDatabasePath(0, "./test");
        server.start();
    }
    public void stop() {
        log.info("STOP HSQLSERVER");
        server.stop();
    }
}
