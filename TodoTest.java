/*import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;*/

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


//@TestMethodOrder(MethodOrderer.Random.class)
public class TodoTest {

    @Test
    public void createTodoSuccess() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:4567/todos");
        httpPost.setHeader("Content-Type", "application/json");

        JSONObject requestData = new JSONObject();
        requestData.put("title", "Todo 1");
        requestData.put("description", "Testing todos");
        StringEntity requestEntity = new StringEntity(requestData.toString());
        httpPost.setEntity(requestEntity);

        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        String id = "";
        try{
            String responseString = EntityUtils.toString(responseEntity);
            JSONObject jsonResponse = new JSONObject(responseString);

            assertEquals("HTTP/1.1 201 Created", response.getStatusLine().toString());
            assertEquals( "Todo 1", jsonResponse.getString("title"));
            assertEquals( "Testing todos", jsonResponse.getString("description"));
            id = jsonResponse.getString("id");
            assertNotNull(id);
        }catch (Exception e){
            System.out.println(e.getMessage());
            fail();
        }finally {
            HttpDelete httpDelete = new HttpDelete("http://localhost:4567/todos/" + id);
            httpClient.execute(httpDelete);
        }
    }

    @Test
    public void createTodoFail() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:4567/todos");
        httpPost.setHeader("Content-Type", "application/json");

        JSONObject requestData = new JSONObject();
        requestData.put("description", "Testing todos");
        StringEntity requestEntity = new StringEntity(requestData.toString());
        httpPost.setEntity(requestEntity);

        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        try{
            String responseString = EntityUtils.toString(responseEntity);
            assertEquals("HTTP/1.1 400 Bad Request", response.getStatusLine().toString());
            assertEquals( "{\"errorMessages\":[\"title : field is mandatory\"]}", responseString);
        }catch (Exception e){
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void getTodoSuccess() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:4567/todos/2");
        httpGet.setHeader("Content-Type", "application/json");
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity responseEntity = response.getEntity();

        try{
            String responseString = EntityUtils.toString(responseEntity);
            JSONObject jsonResponse = new JSONObject(responseString);
            JSONArray todosArray = jsonResponse.getJSONArray("todos");
            JSONObject todoObject = todosArray.getJSONObject(0);

            assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());
            assertEquals( "file paperwork", todoObject.getString("title"));
            assertEquals( "", todoObject.getString("description"));
            assertEquals("false", todoObject.getString("doneStatus"));
        }catch (Exception e){
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void getTodoFail() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:4567/todos/22");
        httpGet.setHeader("Content-Type", "application/json");
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity responseEntity = response.getEntity();

        try{
            String responseString = EntityUtils.toString(responseEntity);
            assertEquals("HTTP/1.1 404 Not Found", response.getStatusLine().toString());
            assertEquals( "{\"errorMessages\":[\"Could not find an instance with todos/22\"]}", responseString);
        }catch (Exception e){
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void getAllTodos() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:4567/todos");
        httpGet.setHeader("Content-Type", "application/json");
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity responseEntity = response.getEntity();

        try{
            String responseString = EntityUtils.toString(responseEntity);
            JSONObject jsonResponse = new JSONObject(responseString);
            JSONArray todosArray = jsonResponse.getJSONArray("todos");

            assertEquals(2, todosArray.length());
            assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());

        }catch (Exception e){
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void headTodos() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpHead httpHead = new HttpHead("http://localhost:4567/todos");
        httpHead.setHeader("Content-Type", "application/json");
        HttpResponse response = httpClient.execute(httpHead);
        HttpEntity responseEntity = response.getEntity();

        try{
            assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());

        }catch (Exception e){
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void headTodoByID() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpHead httpHead = new HttpHead("http://localhost:4567/todos/1");
        httpHead.setHeader("Content-Type", "application/json");
        HttpResponse response = httpClient.execute(httpHead);
        HttpEntity responseEntity = response.getEntity();

        try{
            assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());

        }catch (Exception e){
            System.out.println(e.getMessage());
            fail();
        }
    }


    @Test
    public void updateTodoSuccess() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:4567/todos");
        httpPost.setHeader("Content-Type", "application/json");

        //create first
        JSONObject requestData = new JSONObject();
        requestData.put("title", "Todo 1");
        requestData.put("description", "Testing todos");
        StringEntity requestEntity = new StringEntity(requestData.toString());
        httpPost.setEntity(requestEntity);
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        String id = "";
        try{
            String responseString = EntityUtils.toString(responseEntity);
            JSONObject jsonResponse = new JSONObject(responseString);
            assertEquals("HTTP/1.1 201 Created", response.getStatusLine().toString());
            assertEquals( "Todo 1", jsonResponse.getString("title"));
            assertEquals( "Testing todos", jsonResponse.getString("description"));
            id = jsonResponse.getString("id");
            assertNotNull(id);
        }catch (Exception e){
            System.out.println(e.getMessage());
            fail();
        }finally {
            response = null;
        }

        //then update
        HttpPut httpPut = new HttpPut("http://localhost:4567/todos/" + id);
        httpPut.setHeader("Content-Type", "application/json");
        requestData = new JSONObject();
        requestData.put("title", "Todo 1 update");
        requestData.put("description", "Testing todos update");
        requestEntity = new StringEntity(requestData.toString());
        httpPut.setEntity(requestEntity);
        response = httpClient.execute(httpPut);
        responseEntity = response.getEntity();
        try{
            assertNotNull(response);
            String responseString = EntityUtils.toString(responseEntity);
            JSONObject jsonResponse = new JSONObject(responseString);
            assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());
            assertEquals( "Todo 1 update", jsonResponse.getString("title"));
            assertEquals( "Testing todos update", jsonResponse.getString("description"));
            assertEquals(id, jsonResponse.getString("id"));
        }catch (Exception e){
            System.out.println(e.getMessage());
            fail();
        }
        finally {
            HttpDelete httpDelete = new HttpDelete("http://localhost:4567/todos/" + id);
            httpClient.execute(httpDelete);
        }
    }

    @Test
    public void updateTodoFail() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut("http://localhost:4567/todos/74");
        httpPut.setHeader("Content-Type", "application/json");
        JSONObject requestData = new JSONObject();
        requestData.put("title", "Todo 1 update");
        requestData.put("description", "Testing todos update");
        StringEntity requestEntity = new StringEntity(requestData.toString());
        httpPut.setEntity(requestEntity);
        HttpResponse response = httpClient.execute(httpPut);
        HttpEntity responseEntity = response.getEntity();

        try{
            String responseString = EntityUtils.toString(responseEntity);
            assertEquals("HTTP/1.1 404 Not Found", response.getStatusLine().toString());
            assertEquals( "{\"errorMessages\":[\"Invalid GUID for 74 entity todo\"]}", responseString);
        }catch (Exception e){
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void deleteTodoSuccess() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:4567/todos");
        httpPost.setHeader("Content-Type", "application/json");

        //create first
        JSONObject requestData = new JSONObject();
        requestData.put("title", "Todo 1");
        requestData.put("description", "Testing todos");
        StringEntity requestEntity = new StringEntity(requestData.toString());
        httpPost.setEntity(requestEntity);
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        String id = "";
        try{
            String responseString = EntityUtils.toString(responseEntity);
            JSONObject jsonResponse = new JSONObject(responseString);
            assertEquals("HTTP/1.1 201 Created", response.getStatusLine().toString());
            assertEquals( "Todo 1", jsonResponse.getString("title"));
            assertEquals( "Testing todos", jsonResponse.getString("description"));
            id = jsonResponse.getString("id");
            assertNotNull(id);
        }catch (Exception e){
            System.out.println(e.getMessage());
            fail();
        }finally {
            response = null;
        }

        //then delete
        HttpDelete httpDelete = new HttpDelete("http://localhost:4567/todos/" + id);
        httpClient.execute(httpDelete);

        //then try to get
        HttpGet httpGet = new HttpGet("http://localhost:4567/todos/" + id);
        httpGet.setHeader("Content-Type", "application/json");
        response = httpClient.execute(httpGet);
        responseEntity = response.getEntity();

        try{
            String responseString = EntityUtils.toString(responseEntity);
            assertEquals("HTTP/1.1 404 Not Found", response.getStatusLine().toString());
            assertEquals( "{\"errorMessages\":[\"Could not find an instance with todos/" + id + "\"]}", responseString);
        }catch (Exception e){
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void deleteTodoFail() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete("http://localhost:4567/todos/74");
        HttpResponse response = httpClient.execute(httpDelete);
        HttpEntity responseEntity = response.getEntity();

        try{
            String responseString = EntityUtils.toString(responseEntity);
            assertEquals("HTTP/1.1 404 Not Found", response.getStatusLine().toString());
            assertEquals( "{\"errorMessages\":[\"Could not find any instances with todos/74\"]}", responseString);
        }catch (Exception e){
            System.out.println(e.getMessage());
            fail();
        }

    }

    @Test
    public void testMalformedJSON() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:4567/todos");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Accept", "application/json");

        String jsonData = "{\"title\": \"Todo 1\", \"description\": \"Testing todos\", \"doneStatus\": false";
        StringEntity requestEntity = new StringEntity(jsonData);
        httpPost.setEntity(requestEntity);

        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();

        try {
            String responseString = EntityUtils.toString(responseEntity);
            assertEquals("HTTP/1.1 400 Bad Request", response.getStatusLine().toString());
            assertEquals("{\"errorMessages\":[\"java.io.EOFException: End of input at line 1 column 72 path $.\"]}", responseString);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

}
