package io.searchbox.client.aws;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.Logger;

/**
 * Implements AWS Request Signature Version 4 using SHA-256.
 * 
 * @see http://docs.aws.amazon.com/general/latest/gr/sigv4_signing.html
 */
public class AWSSignatureV4 {
    private static final Logger LOGGER=Logger.getLogger(AWSSignatureV4.class);
    
    public static class SigningException extends RuntimeException {
        private static final long serialVersionUID = 6291813149619522636L;

        public SigningException(String message, Throwable cause) {
            super(message, cause);
        }

        public SigningException(String message) {
            super(message);
        }
    }
    
    private static final Pattern SPACES=Pattern.compile("\\s{2,}");

    private static final TimeZone UTC=TimeZone.getTimeZone("UTC");
    
    private static final String AWS_ALGORITHM="AWS4-HMAC-SHA256";
    
    private static final String MESSAGE_DIGEST_ALGORITHM="SHA-256";
    
    private static final String HMAC_ALGORITHM="HmacSHA256";
    
    /**
     * Arbitrary credential terminator chosen by AWS
     */
    private static final String TERMINATOR="aws4_request";
    
    private static final ThreadLocal<DateFormat> timestampFormat=new ThreadLocal<DateFormat>() {
        protected DateFormat initialValue() {
            SimpleDateFormat result=new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
            result.setTimeZone(UTC);
            return result;
        }
    };

    private static final ThreadLocal<DateFormat> datestampFormat=new ThreadLocal<DateFormat>() {
        protected DateFormat initialValue() {
            SimpleDateFormat result=new SimpleDateFormat("yyyyMMdd");
            result.setTimeZone(UTC);
            return result;
        }
    };
    
    private static class Parameter {
        public final String name;
        public final String value;
        
        public Parameter(String name, String value) {
            this.name = name;
            this.value = value;
        }
        
        public String toString() {
            return name+"="+value;
        }
    }
    
    /* default */ static class CanonicalRequest {
        public final String method;
        public final String canonicalUri;
        public final String canonicalQueryString;
        public final String canonicalHeaders;
        public final String signedHeaders;
        public final String hashedPayload;
        
        public CanonicalRequest(String method, String canonicalUri, String canonicalQueryString, String canonicalHeaders, String signedHeaders, String hashedPayload) {
            this.method = method;
            this.canonicalUri = canonicalUri;
            this.canonicalQueryString = canonicalQueryString;
            this.canonicalHeaders = canonicalHeaders;
            this.signedHeaders = signedHeaders;
            this.hashedPayload = hashedPayload;
        }
        
        public String toString() {
            return
                method + "\n" +
                canonicalUri + "\n" +
                canonicalQueryString + "\n" +
                canonicalHeaders + "\n" +
                "\n" +
                signedHeaders + "\n" +
                hashedPayload;     
        }
    }
    
    /* default */ static class Plaintext {
        public final String algorithm;
        public final String timestamp;
        public final String credentialScope;
        public final String hashedRequest;
        
        public Plaintext(String algorithm, String timestamp, String credentialScope, String hashedRequest) {
            this.algorithm = algorithm;
            this.timestamp = timestamp;
            this.credentialScope = credentialScope;
            this.hashedRequest = hashedRequest;
        }

        public String toString() {
            return
                algorithm + "\n" +
                timestamp + "\n" +
                credentialScope + "\n" +
                hashedRequest;     
        }
    }
    
    private final String timestamp;
    private final String datestamp;
    private final String awsService;
    private final String awsRegion;
    private final String awsAccessKey;
    private final String awsSecretKey;

    /**
     * @param awsService The AWS service this request is for, e.g. "es" for ElasticSearch, "ec2" for EC2, etc.
     * @param awsRegion The AWS region this requst is for, e.g. "us-east-1"
     * @param awsAccessKey Your AWS account's access key, e.g. AKIDEXAMPLE
     * @param awsSecretKey Your AWS account's secret key, e.g. wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY
     */
    public AWSSignatureV4(String awsService, String awsRegion, String awsAccessKey, String awsSecretKey) {
        this.awsService = awsService;
        this.awsRegion = awsRegion;
        this.awsAccessKey = awsAccessKey;
        this.awsSecretKey = awsSecretKey;
        
        Date now=new Date();
        
        this.timestamp = timestampFormat.get().format(now);
        this.datestamp = datestampFormat.get().format(now);
    }

    /* default */ AWSSignatureV4(String awsService, String awsRegion, String awsAccessKey, String awsSecretKey, String timestamp, String datestamp) {
        this.awsService = awsService;
        this.awsRegion = awsRegion;
        this.awsAccessKey = awsAccessKey;
        this.awsSecretKey = awsSecretKey;
        this.timestamp = timestamp;
        this.datestamp = datestamp;
    }
    
    /**
     * Signs the given request using AWS Signature V4.
     * 
     * Note: If this request has an entity, then the entity must be repeatable.
     * Otherwise, the entity cannot be hashed, and the requst cannot be signed.
     * In-memory and file entities are repeatable.
     * 
     * @param request The HTTP request to sign using AWS Signature Version 4
     * 
     * @see http://docs.aws.amazon.com/general/latest/gr/sigv4_signing.html
     */
    public void sign(HttpUriRequest request) {
        request = prepareRequest(request);
        
        CanonicalRequest canonicalRequest=canonicalRequest(request);
        
        // AWS reports the expected canonical request on 403, so make it easy
        // to pull that out with logging in case of errors
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("CANONICAL REQUEST");
            LOGGER.debug("=========================");
            LOGGER.debug(canonicalRequest.toString());
            LOGGER.debug("=========================");
        }
        
        Plaintext plaintext=plaintext(canonicalRequest.toString());
        
        // AWS reports the expected string to sign on 403, so make it easy
        // to pull that out with logging in case of errors
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("PLAINTEXT");
            LOGGER.debug("=========================");
            LOGGER.debug(plaintext.toString());
            LOGGER.debug("=========================");
        }
        
        byte[] signingKey=signingKey();
        
        String signature=toHexString(sign(plaintext.toString().getBytes(StandardCharsets.UTF_8), signingKey));
        
        String authorization=authorization(canonicalRequest, plaintext, signature);
        
        // While we're in here, make authorization available too
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("AUTHORIZATION");
            LOGGER.debug("=========================");
            LOGGER.debug(authorization.toString());
            LOGGER.debug("=========================");
        }
        
        request.setHeader("Authorization", authorization);
    }
    
    private HttpUriRequest prepareRequest(HttpUriRequest request) {
        return prepareRequest(request, getTimestamp());
    }
    
    /* default */ static HttpUriRequest prepareRequest(HttpUriRequest request, String timestamp) {
        if(request.getFirstHeader("Host") == null) {
            if(request.getURI().getHost() != null)
                request.setHeader("Host", request.getURI().getHost());
            else
                throw new SigningException("Unable to determine request hostname");
        }
        request.setHeader("X-Amz-Date", timestamp);
        if(entity(request)!=null && !request.containsHeader("Content-Type"))
            request.setHeader("Content-Type", contentType(entity(request)));
        return request;
    }
    
    private String authorization(CanonicalRequest canonicalRequest, Plaintext plaintext, String signature) {
        return authorization(canonicalRequest, plaintext, signature, getAwsAccessKey());
    }
    
    /* default */ static String authorization(CanonicalRequest canonicalRequest, Plaintext plaintext, String signature, String accessKey) {
        return AWS_ALGORITHM+" Credential="+accessKey+"/"+plaintext.credentialScope+", SignedHeaders="+canonicalRequest.signedHeaders+", Signature="+signature;
    }
    
    private byte[] signingKey() {
        return signingKey(getAwsRegion(), getAwsService(), getAwsSecretKey(), getDatestamp());
    }
    
    /* default */ static byte[] signingKey(String awsRegion, String awsService, String awsSecretKey, String datestamp) {
        byte[] signingKey1=("AWS4"+awsSecretKey).getBytes(StandardCharsets.UTF_8);
        byte[] signingKey2=sign(datestamp.getBytes(StandardCharsets.UTF_8), signingKey1);
        byte[] signingKey3=sign(awsRegion.getBytes(StandardCharsets.UTF_8), signingKey2);
        byte[] signingKey4=sign(awsService.getBytes(StandardCharsets.UTF_8), signingKey3);
        byte[] signingKey=sign(TERMINATOR.getBytes(StandardCharsets.UTF_8), signingKey4);
        return signingKey;
    }
    
    private Plaintext plaintext(String canonicalRequest) {
        return plaintext(canonicalRequest, getAwsRegion(), getAwsService(), getDatestamp(), getTimestamp());
    }
    
    /* default */ static Plaintext plaintext(String canonicalRequest, String awsRegion, String awsService, String datestamp, String timestamp) {
        String algorithm=AWS_ALGORITHM;
        String credentialScope=datestamp+"/"+awsRegion+"/"+awsService+"/"+TERMINATOR;
        String hashedRequest=hash(canonicalRequest.getBytes(StandardCharsets.UTF_8));
        return new Plaintext(algorithm, timestamp, credentialScope, hashedRequest);
    }
    
    /* default */ static CanonicalRequest canonicalRequest(HttpUriRequest request) {
        String canonicalUri=request.getURI().getPath();
        
        String canonicalQueryString;
        if(request.getURI().getRawQuery() != null) {
            List<Parameter> parameters=new ArrayList<Parameter>();
            for(String kv : request.getURI().getRawQuery().split("&")) {
                String[] ps=kv.split("=", 2);
                String k=ps[0];
                String v=ps.length==2 ? ps[1] : "";
                parameters.add(new Parameter(k, v));
            }
            Collections.sort(parameters, new Comparator<Parameter>() {
                public int compare(Parameter a, Parameter b) {
                    return a.name.compareTo(b.name);
                }
            });
            canonicalQueryString = parameters.get(0).toString();
            for(int i=1;i<parameters.size();i++)
                canonicalQueryString += "&" + parameters.get(i).toString();
        }
        else
            canonicalQueryString = "";
        
        String method=request.getMethod();
        
        String canonicalHeaders, signedHeaders;
        {
            SortedMap<String,List<String>> headers=new TreeMap<String,List<String>>();
            for(Header header : request.getAllHeaders()) {
                String name=header.getName().toLowerCase();
                String lowername=name.toLowerCase();
                if(lowername.equals("host") || lowername.equals("accept") || lowername.startsWith("x-") || lowername.startsWith("content-") || lowername.startsWith("user-")) {
                    List<String> vs=headers.get(name);
                    if(vs == null)
                        headers.put(name, vs = new ArrayList<String>());
                    vs.add(replaceAll(SPACES, header.getValue().toString().trim(), " "));
                }
            }
            
            canonicalHeaders = "";
            for(Map.Entry<String,List<String>> e : headers.entrySet()) {
                if(canonicalHeaders.length() != 0)
                    canonicalHeaders += "\n";
                String k=e.getKey();
                List<String> vs=e.getValue();
                canonicalHeaders += k+":"+vs.get(0);
                for(int i=1;i<vs.size();i++)
                    canonicalHeaders += ","+vs.get(i);
            }
            
            Iterator<String> iterator=headers.keySet().iterator();
            signedHeaders = iterator.next();
            while(iterator.hasNext())
                signedHeaders += ";"+iterator.next();
        }
        
        String hashedPayload;
        HttpEntity entity=entity(request);
        if(entity != null) {
            if(!entity.isRepeatable())
                throw new SigningException("Cannot sign non-repeatable entity");
            try {
                InputStream input=entity.getContent();
                try {
                    hashedPayload = hash(input);
                }
                finally {
                    input.close();
                }
            }
            catch(IOException e) {
                throw new SigningException("payload hashing failed", e);
            }
        }
        else
            hashedPayload = hash(new byte[0]);
        
        return new CanonicalRequest(method, canonicalUri, canonicalQueryString, canonicalHeaders, signedHeaders, hashedPayload);
    }
    
    private String getTimestamp() {
        return timestamp;
    }

    private String getDatestamp() {
        return datestamp;
    }

    private String getAwsService() {
        return awsService;
    }

    private String getAwsRegion() {
        return awsRegion;
    }

    private String getAwsAccessKey() {
        return awsAccessKey;
    }

    private String getAwsSecretKey() {
        return awsSecretKey;
    }
    
    // REQUEST HANDLING ///////////////////////////////////////////////////////
    private static HttpEntity entity(HttpUriRequest request) {
        HttpEntity result;
        if(request instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest entityRequest=(HttpEntityEnclosingRequest) request;
            result = entityRequest.getEntity();
        }
        else
            result = null;
        return result;
    }
    
    private static String contentType(HttpEntity entity) {
        return entity.getContentType()!=null ? entity.getContentType().getValue() : "application/x-www-form-urlencoded; charset=utf-8";
    }

    // HASHING HANDLING ///////////////////////////////////////////////////////
    /* default */ static String hash(byte[] data) {
        return hash(new ByteArrayInputStream(data));
    }
    
    /* default */ static String hash(InputStream stream) {
        byte[] result;
        try {
            MessageDigest d=MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM);
            for(int b=stream.read();b!=-1;b=stream.read())
                d.update((byte)(b & 0xFF));
            result = d.digest();
        }
        catch(Exception e) {
            throw new SigningException("hashing failed", e);
        }
        return toHexString(result);
    }
    
    // SIGNING HANDLING ///////////////////////////////////////////////////////
    /* default */ static byte[] sign(byte[] data, byte[] key) {
        byte[] result;
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(key, HMAC_ALGORITHM));
            result = mac.doFinal(data);
        }
        catch(Exception e) {
            throw new SigningException("signing failed", e);
        }
        return result;
    }
    
    // PATTERN HANDLING ///////////////////////////////////////////////////////
    private static String replaceAll(Pattern needle, String haystack, String replacement) {
        return needle.matcher(haystack).replaceAll(replacement);
    }

    // HEXADECIMAL HANDLING ///////////////////////////////////////////////////
    private static String toHexString(byte b) {
        char[] cs=new char[2];
        int n=b & 0x000000FF;
        cs[0] = hex((n & 0x000000F0) >> 4);
        cs[1] = hex((n & 0x0000000F) >> 0);
        return new String(cs);
    }
    
    /* default */ static String toHexString(byte[] bytes) {
        return toHexString(bytes, 0, bytes.length);
    }
    
    private static String toHexString(byte[] bytes, int start, int len) {
        StringBuilder result=new StringBuilder();
        
        for(int i=start;i<start+len;i++)
            result.append(toHexString(bytes[i]));
        
        return result.toString();
    }

    private static char hex(int v) {
        char result;
        if(v>=0 && v<=9)
            result = (char)('0'+v);
        else
        if(v>=10 && v<=15)
            result = (char)('a'+(v-10));
        else
            throw new IllegalArgumentException("cannot encode as hex digit: "+v);
        return result;
    }        
}
