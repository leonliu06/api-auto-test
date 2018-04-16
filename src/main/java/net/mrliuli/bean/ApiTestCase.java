package net.mrliuli.bean;

import net.mrliuli.annotation.Excel;

/**
 * Created by leon on 2018/4/15.
 */
@Excel
public class ApiTestCase extends BaseTestCase {

    @Excel(column = "method")
    private String method;

    @Excel(column = "url")
    private String url;

    @Excel(column = "param")
    private String param;

    @Excel(column = "responseStatus")
    private String responseStatus;

    @Excel(column = "response")
    private String response;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
