package io.searchbox.client;

/**
 * @author Robert Gruendler
 */
public class JestJmx implements JestJmxMBean {

  private final AbstractJestClient client;

  public JestJmx(final AbstractJestClient client) {
    this.client = client;
  }

  @Override
  public boolean isRequestCompressionEnabled() {
    return client.isRequestCompressionEnabled();
  }

  @Override
  public String getNextServer() {
    return client.getNextServer();
  }

  @Override
  public int getServerPoolSize() {
    return client.getServerPoolSize();
  }
}
