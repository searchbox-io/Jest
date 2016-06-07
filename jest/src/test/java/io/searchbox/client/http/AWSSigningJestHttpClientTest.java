package io.searchbox.client.http;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Dogukan Sonmez
 */
@RunWith(Parameterized.class)
public class AWSSigningJestHttpClientTest extends JestHttpClientTestBase {
    public AWSSigningJestHttpClientTest(JestHttpClient client) {
        super(client);
    }

    @Parameters
    public static Collection<Object[]> parameters() {
        // The tokens are pulled from examples
        // http://docs.aws.amazon.com/general/latest/gr/sigv4_signing.html
        return Arrays.asList(new Object[][] {     
            { new AWSSigningJestHttpClient("AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY", "us-east-1") }
        });
    }    
}
