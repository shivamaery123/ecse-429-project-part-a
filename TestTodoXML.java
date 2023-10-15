import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.Random.class)
public class TestTodoXML {

    @Test
    public void createTodoSuccessXML() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:4567/todos");
        httpPost.setHeader("Content-Type", "application/xml");
        httpPost.setHeader("Accept", "application/xml");

        String xmlData = "<todo>" +
                "<title>Todo 1</title>" +
                "<description>Testing todos</description>" +
                "</todo>";
        StringEntity requestEntity = new StringEntity(xmlData);
        httpPost.setEntity(requestEntity);

        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        String id = "";

        try{
            String responseString = EntityUtils.toString(responseEntity);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document responseDoc = builder.parse(new InputSource(new StringReader(responseString)));

            assertEquals("HTTP/1.1 201 Created", response.getStatusLine().toString());
            assertEquals("Todo 1", responseDoc.getElementsByTagName("title").item(0).getTextContent());
            assertEquals("Testing todos", responseDoc.getElementsByTagName("description").item(0).getTextContent());
            id = responseDoc.getElementsByTagName("id").item(0).getTextContent();
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
    public void createTodoFailXML() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:4567/todos");
        httpPost.setHeader("Content-Type", "application/xml");
        httpPost.setHeader("Accept", "application/xml");

        String xmlData = "<todo>" +
                "<description>Testing todos</description>" +
                "</todo>";
        StringEntity requestEntity = new StringEntity(xmlData);
        httpPost.setEntity(requestEntity);

        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        try{
            String responseString = EntityUtils.toString(responseEntity);
            assertEquals("HTTP/1.1 400 Bad Request", response.getStatusLine().toString());
            assertEquals( "<errorMessages><errorMessage>title : field is mandatory</errorMessage></errorMessages>", responseString);
        }catch (Exception e){
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void getTodoSuccessXML() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:4567/todos/1");
        httpGet.setHeader("Content-Type", "application/xml");
        httpGet.setHeader("Accept", "application/xml");

        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity responseEntity = response.getEntity();

        try{
            String responseString = EntityUtils.toString(responseEntity);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document responseDoc = builder.parse(new InputSource(new StringReader(responseString)));
            NodeList todoNodes = responseDoc.getElementsByTagName("todo");
            Element todoElement = (Element) todoNodes.item(0);

            assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());
            assertEquals("scan paperwork", todoElement.getElementsByTagName("title").item(0).getTextContent());
            assertEquals("false", todoElement.getElementsByTagName("doneStatus").item(0).getTextContent());
        }catch (Exception e){
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void getTodoFailXML() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:4567/todos/7");
        httpGet.setHeader("Content-Type", "application/xml");
        httpGet.setHeader("Accept", "application/xml");
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity responseEntity = response.getEntity();

        try{
            String responseString = EntityUtils.toString(responseEntity);
            assertEquals("HTTP/1.1 404 Not Found", response.getStatusLine().toString());
            assertEquals( "<errorMessages><errorMessage>Could not find an instance with todos/7</errorMessage></errorMessages>", responseString);
        }catch (Exception e){
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void getAllTodosXML() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:4567/todos");
        httpGet.setHeader("Content-Type", "application/xml");
        httpGet.setHeader("Accept", "application/xml");

        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity responseEntity = response.getEntity();

        try{
            String responseString = EntityUtils.toString(responseEntity);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document responseDoc = builder.parse(new InputSource(new StringReader(responseString)));
            NodeList todoNodes = responseDoc.getElementsByTagName("todo");

            assertEquals(2, todoNodes.getLength());
            assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());

        }catch (Exception e){
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void headTodosXML() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpHead httpHead = new HttpHead("http://localhost:4567/todos");
        httpHead.setHeader("Content-Type", "application/xml");
        httpHead.setHeader("Accept", "application/xml");
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
    public void headTodoByIDXML() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpHead httpHead = new HttpHead("http://localhost:4567/todos/1");
        httpHead.setHeader("Content-Type", "application/xml");
        httpHead.setHeader("Accept", "application/xml");
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
    public void updateTodoSuccessXML() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:4567/todos");
        httpPost.setHeader("Content-Type", "application/xml");
        httpPost.setHeader("Accept", "application/xml");

        //create first
        String xmlData = "<todo>" +
                "<title>Todo 1</title>" +
                "<description>Testing todos</description>" +
                "</todo>";
        StringEntity requestEntity = new StringEntity(xmlData);
        httpPost.setEntity(requestEntity);
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        String id = "";

        try{
            String responseString = EntityUtils.toString(responseEntity);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document responseDoc = builder.parse(new InputSource(new StringReader(responseString)));
            assertEquals("HTTP/1.1 201 Created", response.getStatusLine().toString());
            assertEquals("Todo 1", responseDoc.getElementsByTagName("title").item(0).getTextContent());
            assertEquals("Testing todos", responseDoc.getElementsByTagName("description").item(0).getTextContent());
            id = responseDoc.getElementsByTagName("id").item(0).getTextContent();
            assertNotNull(id);
        }catch (Exception e){
            System.out.println(e.getMessage());
            fail();
        }finally {
            response = null;
        }

        //then update
        HttpPut httpPut = new HttpPut("http://localhost:4567/todos/" + id);
        httpPut.setHeader("Content-Type", "application/xml");
        httpPut.setHeader("Accept", "application/xml");
        xmlData = "<todo>" +
                "<title>Todo 1</title>" +
                "<description>Testing todos</description>" +
                "</todo>";
        requestEntity = new StringEntity(xmlData.toString());
        httpPut.setEntity(requestEntity);
        response = httpClient.execute(httpPut);
        responseEntity = response.getEntity();

        try{
            assertNotNull(response);
            String responseString = EntityUtils.toString(responseEntity);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document responseDoc = builder.parse(new InputSource(new StringReader(responseString)));
            assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());
            assertEquals("Todo 1", responseDoc.getElementsByTagName("title").item(0).getTextContent());
            assertEquals("Testing todos", responseDoc.getElementsByTagName("description").item(0).getTextContent());
            assertEquals(id,responseDoc.getElementsByTagName("id").item(0).getTextContent());
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
    public void updateTodoFailXML() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut("http://localhost:4567/todos/74");
        httpPut.setHeader("Content-Type", "application/xml");
        httpPut.setHeader("Accept", "application/xml");

        String xmlData = "<todo>" +
                "<title>Todo 1</title>" +
                "<description>Testing todos</description>" +
                "</todo>";
        StringEntity requestEntity = new StringEntity(xmlData);
        httpPut.setEntity(requestEntity);
        HttpResponse response = httpClient.execute(httpPut);
        HttpEntity responseEntity = response.getEntity();

        try{
            String responseString = EntityUtils.toString(responseEntity);
            assertEquals("HTTP/1.1 404 Not Found", response.getStatusLine().toString());
            assertEquals( "<errorMessages><errorMessage>Invalid GUID for 74 entity todo</errorMessage></errorMessages>", responseString);
        }catch (Exception e){
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void deleteTodoSuccessXML() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:4567/todos");
        httpPost.setHeader("Content-Type", "application/xml");
        httpPost.setHeader("Accept", "application/xml");

        //create first
        String xmlData = "<todo>" +
                "<title>Todo 1</title>" +
                "<description>Testing todos</description>" +
                "</todo>";
        StringEntity requestEntity = new StringEntity(xmlData);
        httpPost.setEntity(requestEntity);
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        String id = "";
        try{
            String responseString = EntityUtils.toString(responseEntity);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document responseDoc = builder.parse(new InputSource(new StringReader(responseString)));
            assertEquals("HTTP/1.1 201 Created", response.getStatusLine().toString());
            assertEquals("Todo 1", responseDoc.getElementsByTagName("title").item(0).getTextContent());
            assertEquals("Testing todos", responseDoc.getElementsByTagName("description").item(0).getTextContent());
            id = responseDoc.getElementsByTagName("id").item(0).getTextContent();
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
        httpGet.setHeader("Content-Type", "application/xml");
        httpGet.setHeader("Accept", "application/xml");
        response = httpClient.execute(httpGet);
        responseEntity = response.getEntity();

        try{
            String responseString = EntityUtils.toString(responseEntity);
            assertEquals("HTTP/1.1 404 Not Found", response.getStatusLine().toString());
            assertEquals( "<errorMessages><errorMessage>Could not find an instance with todos/" + id + "</errorMessage></errorMessages>", responseString);
        }catch (Exception e){
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void deleteTodoFailXML() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete("http://localhost:4567/todos/74");
        httpDelete.setHeader("Content-Type", "application/xml");
        httpDelete.setHeader("Accept", "application/xml");
        HttpResponse response = httpClient.execute(httpDelete);
        HttpEntity responseEntity = response.getEntity();

        try{
            String responseString = EntityUtils.toString(responseEntity);
            assertEquals("HTTP/1.1 404 Not Found", response.getStatusLine().toString());
            assertEquals( "<errorMessages><errorMessage>Could not find any instances with todos/74</errorMessage></errorMessages>", responseString);
        }catch (Exception e){
            System.out.println(e.getMessage());
            fail();
        }

    }

    @Test
    public void testMalformedXML() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:4567/todos");
        httpPost.setHeader("Content-Type", "application/xml");
        httpPost.setHeader("Accept", "application/xml");

        String xmlData = "<todo>" +
                "<title>Todo 1</title>" +
                "<description>Testing todos</description>" +
                "todo>";
        StringEntity requestEntity = new StringEntity(xmlData);
        httpPost.setEntity(requestEntity);

        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        String id = "";

        try{
            String responseString = EntityUtils.toString(responseEntity);
            assertEquals("HTTP/1.1 400 Bad Request", response.getStatusLine().toString());
            assertEquals( "<errorMessages><errorMessage>Unclosed tag todo at 73 [character 74 line 1]</errorMessage></errorMessages>", responseString);
        }catch (Exception e){
            System.out.println(e.getMessage());
            fail();
        }
    }

}
