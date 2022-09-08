package com.example.opswat_engineeringassessment;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.opswat.metadefender.core.client.HttpConnector;
import com.opswat.metadefender.core.client.exceptions.MetadefenderClientException;
import com.opswat.metadefender.core.client.responses.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class metadefenderClient {

    private HttpConnector httpConnector = new HttpConnector();

    private final String apiEndUrl;

    private final String agent = null;

    private final String sessId = null;

    public metadefenderClient(String apiEndUrl){
        this.apiEndUrl = apiEndUrl;
    }

    public metadefenderClient(String apiEndUrl, String apiKey) {
        this.apiEndUrl = apiEndUrl;

        this.sessId = apiKey;
    }

    public metadefenderClient(String apiEndUrl, String un, String pw) throws MetadefenderClientException {
        this.apiEndUrl = apiEndUrl;

        signIn(un, pw);
    }

    void signIn(String un, String pw) throws MetadefenderClientException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode signInJson = mapper.createObjectNode();
        signInJson.put("Username", un);
        signInJson.put("Password", pw);

        String siRequest = signInJson.toString();

        HttpConnector.HttpResponse response = HttpConnector.sendRequest(this.apiEndUrl + "/login", "POST", siRequest.getBytes());

        if (response == 200){

        }
        else{
            requestError(response);
        }
    }

    private Map<String, String> getSigninHeader() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("apikey", this.sessId);
        return headers;
    }
    private void requestError(HttpConnector.HttpResponse response) throws MetadefenderClientException {
        JsonNode object = getJsonString(response.response);

        String errStr = object.get("error").asText();
        throw new MetadefenderClientException(errStr, response.responseCode);
    }
    private void validateSess() throws MetadefenderClientException {
        if (this.sessId == null || this.sessId.trim().length() <= 0) {
            throw new MetadefenderClientException("You must sign in to gain access...");
        }
    }

    private ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }
    private JsonNode getJsonString(String json) throws MetadefenderClientException {
        ObjectMapper mapper = getMapper();

        try {
            return mapper.readTree(json);
        }
        catch(IOException e) {
            throw new MetadefenderClientException("Json unparsable" + e.getMessage());
        }
    }

    private <T> T getJsonObject(String json, Class<T> tClass) throws MetadefenderClientException {

    }
}

