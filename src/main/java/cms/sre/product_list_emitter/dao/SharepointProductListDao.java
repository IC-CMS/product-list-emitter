package cms.sre.product_list_emitter.dao;

import cms.sre.dna_common_data_model.product_list.Product;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SharepointProductListDao implements ProductListDao {

    private static List<String> parseDevelopers(JsonNode result){
        List<String> ret = new LinkedList<>();
        if(result != null && result.has("Developer_x0028_s_x0029_") && result.get("Developer_x0028_s_x0029_").has("results")){
            result.get("Developer_x0028_s_x0029_").get("results")
                    .forEach(r -> {
                        ret.add(r.get("UserName").asText());
                    });
        }
        return ret;
    }

    private static String parseProgram(JsonNode result){
        String ret = null;
        if(result != null && result.has("Program") && result.get("Program").has("Title")){
            ret = result.get("Program")
                    .get("Title")
                    .asText();
        }
        return ret;
    }

    private static String parseSection(JsonNode result){
        String ret = null;
        if(result != null && result.has("Section") && result.get("Section").has("Title")){
            ret = result.get("Section")
                    .get("Title")
                    .asText();
        }
        return ret;
    }

    private static String parseTitle(JsonNode result){
        String ret = null;
        if(result != null && result.has("Title")){
            ret = result.get("Title")
                    .asText();
        }
        return ret;
    }

    private static String parseClassification(JsonNode result){
        String ret = null;
        if(result != null && result.has("Text_x0020_Classification")){
            ret = result.get("Text_x0020_Classification")
                    .asText();
        }
        return ret;
    }

    private static String parseSSPName(JsonNode result){
        String ret = null;
        if(result != null && result.has("SSP_x0020_Name")){
            ret = result.get("SSP_x0020_Name")
                    .asText();
        }
        return ret;
    }

    private static String parseLifecycle(JsonNode result){
        String ret = null;
        if(result != null && result.has("Livecycle_x0020_Status")){
            ret = result.get("Livecycle_x0020_Status")
                    .asText();
        }
        return ret;
    }

    private static String parseProductStatus(JsonNode result){
        String ret = null;
        if(result != null && result.has("Active")){
            ret = result.get("Active")
                    .asText();
        }
        return ret;
    }

    private static String parseLane(JsonNode result){
        String ret = null;
        if(result != null && result.has("Lane")){
            ret = result.get("Lane")
                    .asText();
        }
        return ret;
    }

    private static String parseOrg(JsonNode result){
        String ret = null;
        if(result != null && result.has("Org")){
            ret = result.get("Org")
                    .asText();
        }
        return ret;
    }

    private static String parseDivision(JsonNode result){
        String ret = null;
        if(result != null && result.has("Division")){
            ret = result.get("Division")
                    .asText();
        }
        return ret;
    }

    private static Boolean parseNeedsSCM(JsonNode result){
        Boolean ret = null;
        if(result != null && result.has("NoCode") && !(result.get("NoCode").isNull() || result.get("NoCode").asText().equals("null"))){
            ret =  !result.get("NoCode")
                    .asBoolean();
        }
        return ret;
    }

    private static String parseScmLocation(JsonNode result){
        String ret = null;
        if(result != null && result.has("SCMLocation") && !result.get("SCMLocation").asText().equals("null")){
            ret = result.get("SCMLocation")
                    .asText();
        }
        return ret;
    }

    private static List<Product> parse(String rawResponse) throws IOException {
        List<Product> ret = new LinkedList<>();

        new ObjectMapper().readTree(rawResponse)
                .get("d")
                .get("results")
                .forEach(result -> {
                    Product product = new Product()
                            .setDevelopers(parseDevelopers(result))
                            .setProgram(parseProgram(result))
                            .setTitle(parseTitle(result))
                            .setClassification(parseClassification(result))
                            .setSspName(parseSSPName(result))
                            .setLifecycleStatus(parseLifecycle(result))
                            .setProductStatus(parseProductStatus(result))
                            .setLane(parseLane(result))
                            .setOrg(parseOrg(result))
                            .setDivision(parseDivision(result))
                            .setNeedsSCM(parseNeedsSCM(result))
                            .setSCMLocation(parseScmLocation(result))
                            .setSection(parseSection(result));

                    ret.add(product);
                });

        return ret;
    }

    private HttpClient httpClient;
    private String productListLocation;

    public SharepointProductListDao(HttpClient httpClient, String productListLocation){
        this.httpClient = httpClient;
        this.productListLocation = productListLocation;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public SharepointProductListDao setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    public String getProductListLocation() {
        return productListLocation;
    }

    public SharepointProductListDao setProductListLocation(String productListLocation) {
        this.productListLocation = productListLocation;
        return this;
    }

    @Override
    public List<Product> getProducts() throws IOException {
        if(this.productListLocation == null || !this.productListLocation.startsWith("http")){
            throw new IOException("Invalid Product List Location: " + this.productListLocation);
        }

        HttpGet get = new HttpGet(this.productListLocation);
        get.addHeader("Content-Type", "application/json;odata=verbose");
        get.addHeader("Accept", "application/json;odata=verbose");

        return this.httpClient.execute(get, (httpResponse -> {
                List<Product> ret = new LinkedList<>();
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                if(statusCode >= 200 && statusCode < 300){
                    String responseBody = EntityUtils.toString(httpResponse.getEntity());
                    ret = parse(responseBody);
                }else if(statusCode > 400 && statusCode < 500){
                    throw new IOException("StatusCode: " + statusCode + ".  Looks like a connection error.");
                }else if(statusCode == 400 || statusCode > 500){
                    throw new IOException("StatusCode: " + statusCode + ".  Looks like an application error with sharepoint.  ResponseBody: " + EntityUtils.toString(httpResponse.getEntity()));
                }else{
                    throw new IOException("StatusCode: " + statusCode);
                }
                return ret;
        }));
    }
}
