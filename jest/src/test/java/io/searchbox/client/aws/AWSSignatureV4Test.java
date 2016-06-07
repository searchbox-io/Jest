package io.searchbox.client.aws;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.entity.ContentType;
import org.junit.Test;

import io.searchbox.client.aws.AWSSignatureV4.CanonicalRequest;
import io.searchbox.client.http.apache.HttpGetWithEntity;

public class AWSSignatureV4Test {
    /**
     * We should match the AWS example for generating canonical request. Note that we've
     * adjusted slightly for the case of the charset, which the underlying HTTP
     * implementation capitalizes.
     * 
     * @see http://docs.aws.amazon.com/general/latest/gr/sigv4-create-canonical-request.html
     */
    @Test
    public void canonicalRequestSignTest() throws URISyntaxException {
        HttpGetWithEntity request=new HttpGetWithEntity();
        
        request.setURI(new URI("https://iam.amazonaws.com/?Version=2010-05-08&Action=ListUsers"));
        request.setEntity(EntityBuilder.create()
            .setText("")
            .setContentType(ContentType.APPLICATION_FORM_URLENCODED.withCharset("utf-8"))
            .build());
        request = (HttpGetWithEntity) AWSSignatureV4.prepareRequest(request, "20150830T123600Z");
        
        AWSSignatureV4.CanonicalRequest canonicalRequest=AWSSignatureV4.canonicalRequest(request);
        
        String example=
            "GET" + "\n" +
            "/" + "\n" +
            "Action=ListUsers&Version=2010-05-08" + "\n" +
            "content-type:application/x-www-form-urlencoded; charset=UTF-8" + "\n" +
            "host:iam.amazonaws.com" + "\n" +
            "x-amz-date:20150830T123600Z" + "\n" +
            "" + "\n" +
            "content-type;host;x-amz-date" + "\n" +
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
            
        String hash1=AWSSignatureV4.hash(canonicalRequest.toString().getBytes(StandardCharsets.UTF_8));
        
        String hash2=AWSSignatureV4.hash(example.getBytes(StandardCharsets.UTF_8));
        
        assertThat(hash1, is(hash2));
    }
    
    /**
     * We should match the AWS example for signing a given request
     * 
     * @see http://docs.aws.amazon.com/general/latest/gr/sigv4-calculate-signature.html
     */
    @Test
    public void plaintextSignTest() {
        String awsSecretKey="wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY";
        String awsRegion="us-east-1";
        String awsService="iam";
        
        String datestamp="20150830";
        String timestamp="20150830T123600Z";
        
        String canonicalRequest=
            "GET" + "\n" +
            "/" + "\n" +
            "Action=ListUsers&Version=2010-05-08" + "\n" +
            "content-type:application/x-www-form-urlencoded; charset=utf-8" + "\n" +
            "host:iam.amazonaws.com" + "\n" +
            "x-amz-date:20150830T123600Z" + "\n" +
            "" + "\n" +
            "content-type;host;x-amz-date" + "\n" +
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
        
        AWSSignatureV4.Plaintext plaintext=AWSSignatureV4.plaintext(canonicalRequest, awsRegion, awsService, datestamp, timestamp);
                
        byte[] signingKey=AWSSignatureV4.signingKey(awsRegion, awsService, awsSecretKey, datestamp);
        
        String signature1=AWSSignatureV4.toHexString(AWSSignatureV4.sign(plaintext.toString().getBytes(StandardCharsets.UTF_8), signingKey));
        
        String examplePlaintext=
            "AWS4-HMAC-SHA256" + "\n" +
            "20150830T123600Z" + "\n" +
            "20150830/us-east-1/iam/aws4_request" + "\n" +
            "f536975d06c0309214f805bb90ccff089219ecd68b2577efef23edd43b7e1a59";

        byte[] exampleKey=new byte[] {
            (byte)(196 & 0xFF), (byte)(175 & 0xFF), (byte)(177 & 0xFF), (byte)(204 & 0xFF), 
            (byte)(87 & 0xFF), (byte)(113 & 0xFF), (byte)(216 & 0xFF), (byte)(113 & 0xFF), 
            (byte)(118 & 0xFF), (byte)(58 & 0xFF), (byte)(57 & 0xFF), (byte)(62 & 0xFF), 
            (byte)(68 & 0xFF), (byte)(183 & 0xFF), (byte)(3 & 0xFF), (byte)(87 & 0xFF), 
            (byte)(27 & 0xFF), (byte)(85 & 0xFF), (byte)(204 & 0xFF), (byte)(40 & 0xFF), 
            (byte)(66 & 0xFF), (byte)(77 & 0xFF), (byte)(26 & 0xFF), (byte)(94 & 0xFF), 
            (byte)(134 & 0xFF), (byte)(218 & 0xFF), (byte)(110 & 0xFF), (byte)(211 & 0xFF), 
            (byte)(193 & 0xFF), (byte)(84 & 0xFF), (byte)(164 & 0xFF), (byte)(185 & 0xFF) 
        };
        
        String signature2=AWSSignatureV4.toHexString(AWSSignatureV4.sign(examplePlaintext.getBytes(StandardCharsets.UTF_8), exampleKey));
        
        System.err.println(signature1);
        
        assertThat(signature1, is(signature2));
    }
    
    /**
     * We should match the AWS example for authorization
     * @throws URISyntaxException 
     * 
     * @see http://docs.aws.amazon.com/general/latest/gr/sigv4-add-signature-to-request.html
     */
    @Test
    public void authorizationTest() throws URISyntaxException {
        String awsAccessKey="AKIDEXAMPLE";
        String awsSecretKey="wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY";
        String awsRegion="us-east-1";
        String awsService="iam";
        
        String datestamp="20150830";
        String timestamp="20150830T123600Z";
        
        CanonicalRequest canonicalRequest=new CanonicalRequest(
            "GET",
            "/",
            "Action=ListUsers&Version=2010-05-08",
            "content-type:application/x-www-form-urlencoded; charset=utf-8\nhost:iam.amazonaws.com\nx-amz-date:20150830T123600Z",
            "content-type;host;x-amz-date",
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
        
        AWSSignatureV4.Plaintext plaintext=AWSSignatureV4.plaintext(canonicalRequest.toString(), awsRegion, awsService, datestamp, timestamp);
                
        byte[] signingKey=AWSSignatureV4.signingKey(awsRegion, awsService, awsSecretKey, datestamp);
        
        String signature=AWSSignatureV4.toHexString(AWSSignatureV4.sign(plaintext.toString().getBytes(StandardCharsets.UTF_8), signingKey));
        
        String authorization=AWSSignatureV4.authorization(canonicalRequest, plaintext, signature, awsAccessKey);
        
        String example="AWS4-HMAC-SHA256 Credential=AKIDEXAMPLE/20150830/us-east-1/iam/aws4_request, SignedHeaders=content-type;host;x-amz-date, Signature=5d672d79c15b13162d9279b0855cfba6789a8edb4c82c400e06b5924a6f2b5d7";

        assertThat(authorization, is(example));
    }
}
