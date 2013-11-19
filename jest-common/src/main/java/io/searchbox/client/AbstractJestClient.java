package io.searchbox.client;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.searchbox.Action;
import io.searchbox.client.config.RoundRobinServerList;
import io.searchbox.client.config.ServerList;
import io.searchbox.client.config.discovery.NodeChecker;
import io.searchbox.client.config.exception.NoServerConfiguredException;
import io.searchbox.client.util.PaddedAtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Dogukan Sonmez
 */
public abstract class AbstractJestClient implements JestClient {

    final static Logger log = LoggerFactory.getLogger(AbstractJestClient.class);
    private final PaddedAtomicReference<ServerList> listOfServers = new PaddedAtomicReference<ServerList>();
    protected Gson gson = new Gson();
    private NodeChecker nodeChecker;

    public void setNodeChecker(NodeChecker nodeChecker) {
        this.nodeChecker = nodeChecker;
    }

    public LinkedHashSet<String> getServers() {
        ServerList server = listOfServers.get();
        if (server != null) return new LinkedHashSet<String>(server.getServers());
        else return null;
    }

    public void setServers(ServerList list) {
        listOfServers.set(list);
    }

    public void setServers(Set<String> servers) {
        try {
            RoundRobinServerList serverList = new RoundRobinServerList(servers);
            listOfServers.set(serverList);
        } catch (NoServerConfiguredException noServers) {
            listOfServers.set(null);
            log.warn("No servers are currently available for the client to talk to.");
        }
    }

    public void shutdownClient() {
        if (null != nodeChecker)
            nodeChecker.stop();
    }

    protected String getElasticSearchServer() {
        ServerList serverList = listOfServers.get();
        if (serverList != null) return serverList.getServer();
        else throw new NoServerConfiguredException("No Server is assigned to client to connect");
    }

    protected JestResult createNewElasticSearchResult(String json, int statusCode, String reasonPhrase, Action clientRequest) {
        JestResult result = new JestResult(gson);
        JsonObject jsonMap = convertJsonStringToMapObject(json);
        result.setJsonString(json);
        result.setJsonObject(jsonMap);
        result.setPathToResult(clientRequest.getPathToResult());

        if ((statusCode / 100) == 2) {
            if (!clientRequest.isOperationSucceed(jsonMap)) {
                result.setSucceeded(false);
                log.debug("http request was success but operation is failed Status code in 200");
            } else {
                result.setSucceeded(true);
                log.debug("Request and operation succeeded");
            }
        } else {
            result.setSucceeded(false);
            // provide the generic HTTP status code error, if one hasn't already come in via the JSON response...
            // eg.
            //  IndicesExist will return 404 (with no content at all) for a missing index, but:
            //  Update will return 404 (with an error message for DocumentMissingException)
            if (result.getErrorMessage() == null) {
                result.setErrorMessage(statusCode + " " + (reasonPhrase == null ? "null" : reasonPhrase));
            }
            log.debug("Response is failed");
        }
        return result;
    }

    protected JsonObject convertJsonStringToMapObject(String jsonTxt) {
        if (jsonTxt != null && !jsonTxt.trim().isEmpty()) {
            try {
                return new JsonParser().parse(jsonTxt).getAsJsonObject();
            } catch (Exception e) {
                log.error("An exception occurred while converting json string to map object");
            }
        }
        return new JsonObject();
    }

    protected String getRequestURL(String elasticSearchServer, String uri) {
        StringBuilder sb = new StringBuilder(elasticSearchServer);

        if (uri.length() > 0 && uri.charAt(0) == '/') sb.append(uri);
        else sb.append('/').append(uri);

        return sb.toString();
    }
}