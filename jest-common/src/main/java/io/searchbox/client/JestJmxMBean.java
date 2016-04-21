package io.searchbox.client;

/**
 * Interface for exposing some client info via jmx.
 *
 * @author Robert Gruendler
 */
public interface JestJmxMBean {

  boolean isRequestCompressionEnabled();

  String getNextServer();

  int getServerPoolSize();

}
