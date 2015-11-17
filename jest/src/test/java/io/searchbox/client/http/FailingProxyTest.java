package io.searchbox.client.http;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.indices.Stats;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/** Test the situation where there's a misconfigured proxy between the Jest
 * client and the server.  If the proxy speaks text/html instead of
 * application/json, we should not throw a generic JsonSyntaxException.
 */
public class FailingProxyTest {
    JestClientFactory factory = new JestClientFactory();
    private FailingProxy proxy;
    private JestHttpClient client;
    private Stats status;

    @Before
    public void setUp() throws IOException {
        proxy = new FailingProxy();

        String url = proxy.getUrl();
        factory.setHttpClientConfig(new HttpClientConfig.Builder(url).build());
        client = (JestHttpClient) factory.getObject();
        assertThat(client, is(not(nullValue())));

        status = new Stats.Builder().build();
    }

    @After
    public void tearDown() throws IOException {
        client.shutdownClient();
        proxy.stop();
    }

    @Test
    public void testWithFailingProxy() throws InterruptedException, IOException {
        Exception exception = null;
        try {
            final JestResult result = client.execute(status);
        } catch (Exception e) {
            exception = e;
        }
        validateException(exception);
    }

    @Test
    public void testAsyncWithFailingProxy() throws InterruptedException, IOException {
        final ResultHandler resultHandler = new ResultHandler();
        client.executeAsync(status, resultHandler);
        Exception exception = resultHandler.get();
        validateException(exception);
    }

    private void validateException(final Exception e) {
        assertThat(e, is(not(Matchers.nullValue())));
        final String message = e.toString();
        assertThat(message, not(containsString("Use JsonReader.setLenient(true)")));
        assertThat(message, containsString("text/html"));
        assertThat(message, containsString("should be json"));
    }

    private class ResultHandler implements JestResultHandler {
        private final Semaphore sema = new Semaphore(0);
        private Exception exception = null;

        @Override
        public void completed(final Object result) {
            sema.release();
        }

        @Override
        public void failed(final Exception ex) {
            exception = ex;
            sema.release();
        }

        public Exception get() throws InterruptedException {
            sema.acquire();
            return exception;
        }
    }
}
