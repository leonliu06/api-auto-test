package net.mrliuli;

import net.mrliuli.bean.ApiTestCase;
import net.mrliuli.bean.BaseTestCase;
import net.mrliuli.exception.ExcelException;
import net.mrliuli.utils.ExcelUtil;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest
{

    private static HttpClient client;

    /**
     * Api 测试用命
     */
    private List<ApiTestCase> apiTestCaseList = new ArrayList<>();

    @BeforeSuite
    public void init() throws Exception {

        client = new DefaultHttpClient();
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);    // 请求超时
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);            // 读取超时

        //client.getParams().setParameter("key", "c099c789aa51d73afb534f331312cf33");

    }

    @BeforeTest
    public void readData() throws Exception {

        apiTestCaseList = readExcelData(ApiTestCase.class, System.getProperty("user.dir") + "/resources/api-test-case.xls");

    }


    /**
     * 读取测试用例
     * @param type
     * @param excelPath
     * @param <T>
     * @return
     */
    private <T extends BaseTestCase> List<T> readExcelData(Class<T> type, String excelPath) throws ExcelException{

        File file = new File(excelPath);

        // TODO: 2018/4/15
        if(!file.exists()){
            System.out.println("File doesn't exit!");
            System.exit(1);
        }

        List<T> apiTestCaseList = ExcelUtil.importSheet(file, "sheet1", type);

        return apiTestCaseList;

    }

    @Test
    public void apiTest() throws Exception {
        for(ApiTestCase item : apiTestCaseList) {

            // 封装请求方法
            HttpUriRequest method = parseHttpRequest(item);

            String responseContent = null;

            try{

                HttpResponse response = client.execute(method);

                int responseStatus = response.getStatusLine().getStatusCode();

                if(item.getResponseStatus() != null && !item.getResponseStatus().equals("")){
                    Assert.assertEquals(responseStatus, Integer.valueOf(item.getResponseStatus()).intValue(), "返回状态码与期望不符！");
                }

                HttpEntity httpEntity = response.getEntity();

                Header contentType = response.getFirstHeader("Content-Type");

                if(contentType != null && contentType.getValue() != null && (contentType.getValue().contains("download") || contentType.getValue().contains("octet-stream"))){
                    // TODO: 2018/4/15
                }else{
                    responseContent = EntityUtils.toString(httpEntity);
                }

            }catch (Exception e){

                e.printStackTrace();

            }finally {

                method.abort();

            }


            verifyResponse(responseContent);

        }
    }

    private HttpUriRequest parseHttpRequest(ApiTestCase apiTestCase) throws Exception {

        if(apiTestCase.getMethod().equalsIgnoreCase("post")){
            HttpPost postMethod = new HttpPost(apiTestCase.getUrl());
            postMethod.setEntity(new StringEntity(apiTestCase.getParam(), "UTF-8"));
            return postMethod;
        }else if(apiTestCase.getMethod().equalsIgnoreCase("put")){
            HttpPut putMethod = new HttpPut(apiTestCase.getUrl());
            putMethod.setEntity(new StringEntity(apiTestCase.getParam(), "UTF-8"));
            return putMethod;
        }else if(apiTestCase.getMethod().equalsIgnoreCase("delete")){
            HttpDelete deleteMethod = new HttpDelete(apiTestCase.getUrl());
            return deleteMethod;
        }else{
            HttpGet getMethod = new HttpGet(apiTestCase.getUrl());
            return getMethod;
        }

    }
    
    private void verifyResponse(String response){
        
        if(response == null || response.equals("")){
            return;
        }

        // TODO: 2018/4/15
    }
}
