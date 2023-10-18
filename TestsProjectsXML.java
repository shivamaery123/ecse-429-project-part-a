import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.Random.class)
public class TestsProjectsXML {

    // 2.9 Test GET request on projects with invalid xml payload
    @Test
    public void SuccessfulGETProjectsXML() throws Exception {
        String urlLink = "http://localhost:4567/gui/instances?entity=project";

        // Create a URL object
        URL url = new URL(urlLink);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/xml");
        connection.setRequestProperty("Accept", "application/xml");

        // Get the response code
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code:"+ responseCode);

        connection.disconnect();

        // Assert that the response code is 200
        assertEquals(HttpURLConnection.HTTP_OK, responseCode);
    }

    // 2.10 Test POST request with valid inputs
    @Test
    public void SuccessfulPOSTProjectsXML() throws Exception {

        URL url = new URL("http://localhost:4567/projects");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set the request method to POST
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/xml");
        connection.setRequestProperty("Accept", "application/xml");

        String projectData = "<project>\n" +
                "    <title>New Project</title>\n" +
                "    <completed>false</completed>\n" +
                "    <active>true</active>\n" +
                "    <description>A new project</description>\n" +
                "</project>";

        // Write the data to the request body
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = projectData.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Read the response data
        String id = "";
        try (InputStream is = connection.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            System.out.println("Response Data: " + response.toString());
            id = extractIdFromXml(response.toString());
        }

        // Get the response code
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code:"+ responseCode);

        connection.disconnect();

        // Assert that the response code is 201, CREATED
        assertEquals(HttpURLConnection.HTTP_CREATED, responseCode);

        //Revert environment to initial state by deleting the project that was just created
        URL urlDelete = new URL(url + "/" +id);
        HttpURLConnection connectionDelete = (HttpURLConnection) urlDelete.openConnection();
        connectionDelete.setRequestMethod("DELETE");
        int responseCodeDelete = connectionDelete.getResponseCode();
        connectionDelete.disconnect();
    }

    // 2.11 Test with invalid value for completed, should return code 400
    @Test
    public void UnsuccessfulPOSTProjectsXML() throws Exception {

        URL url = new URL("http://localhost:4567/projects");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set the request method to POST
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        // Set the content type to JSON for these tests
        connection.setRequestProperty("Content-Type", "application/xml");
        connection.setRequestProperty("Accept", "application/xml");

        // Invalid value for completed (should be boolean, but is INT)
        String projectData = "<project>\n" +
                "    <title>New Project</title>\n" +
                "    <completed>123</completed>\n" +
                "    <active>true</active>\n" +
                "    <description>A new project</description>\n" +
                "</project>";

        // Write the data to the request body
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = projectData.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Get the response code
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code:"+ responseCode);

        connection.disconnect();

        // Assert that the response code is 400, BAD REQUEST
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, responseCode);
    }

    // 2.12 Test Successful DELETE Request
    @Test
    public void SuccessfulDeleteRequestXML() throws Exception {

        //Arrange new Project for test
        URL url = new URL("http://localhost:4567/projects");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set the request method to POST
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/xml");
        connection.setRequestProperty("Accept", "application/xml");
        String projectData = "<project>\n" +
                "    <title>New Project</title>\n" +
                "    <completed>false</completed>\n" +
                "    <active>true</active>\n" +
                "    <description>A new project</description>\n" +
                "</project>";

        // Write the data to the request body
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = projectData.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Get the response code
        String id = "";
        try (InputStream is = connection.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            System.out.println("Response Data: " + response.toString());
            id = extractIdFromXml(response.toString());
        }
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code:"+ responseCode);

        connection.disconnect();

        //Delete project that was just created
        URL urlDelete = new URL(url + "/" +id);
        System.out.println(urlDelete);
        HttpURLConnection connectionDelete = (HttpURLConnection) urlDelete.openConnection();

        // Set the request method to DELETE
        connectionDelete.setRequestMethod("DELETE");

        // Get the response code
        int responseCodeDelete = connectionDelete.getResponseCode();
        connectionDelete.disconnect();

        // Assert that the response code is 201, CREATED
        assertEquals(HttpURLConnection.HTTP_OK, responseCodeDelete);
    }

    // 2.13 Test unsuccessful DELETE Request
    @Test
    public void UnuccessfulDeleteRequestXML() throws Exception {

        //Delete project that doesn't exist (id=123)
        URL urlDelete = new URL("http://localhost:4567/projects" + "/" +123);
        HttpURLConnection connectionDelete = (HttpURLConnection) urlDelete.openConnection();
        connectionDelete.setRequestProperty("Content-Type", "application/xml");
        connectionDelete.setRequestProperty("Accept", "application/xml");

        // Set the request method to DELETE
        connectionDelete.setRequestMethod("DELETE");

        // Get the response code
        int responseCodeDelete = connectionDelete.getResponseCode();
        connectionDelete.disconnect();

        // Assert that the response code is 404, NOT_FOUND
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, responseCodeDelete);
    }

    // 2.14 Successful PUT request
    @Test
    public void ValidPutRequestXML() throws Exception {

        // Arrange new Project for test
        URL url = new URL("http://localhost:4567/projects");
        HttpURLConnection postConnection = (HttpURLConnection) url.openConnection();

        // Set the request method to POST (first create the project)
        postConnection.setRequestMethod("POST");
        postConnection.setDoOutput(true);
        postConnection.setRequestProperty("Content-Type", "application/xml");
        postConnection.setRequestProperty("Accept", "application/xml");

        String projectData = "<project>\n" +
                "    <title>New Project</title>\n" +
                "    <completed>false</completed>\n" +
                "    <active>true</active>\n" +
                "    <description>A new project</description>\n" +
                "</project>";

        String id = "";
        try (OutputStream os = postConnection.getOutputStream()) {
            byte[] input = projectData.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Extract id from response of POST to create new link for PUT
        try (InputStream is = postConnection.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            System.out.println("POST Response Data: " + response.toString());
            id = extractIdFromXml(response.toString());
        }

        int postResponseCode = postConnection.getResponseCode();
        System.out.println("POST Response Code:" + postResponseCode);

        postConnection.disconnect();

        // Attempt to update the project
        URL updateUrl = new URL("http://localhost:4567/projects/" + id);
        HttpURLConnection putConnection = (HttpURLConnection) updateUrl.openConnection();
        putConnection.setRequestMethod("PUT");
        putConnection.setDoOutput(true);

        // Set the content type to XML
        putConnection.setRequestProperty("Content-Type", "application/xml");
        putConnection.setRequestProperty("Accept", "application/xml");

        // Update project title
        String updatedProjectData =
                "<project>\n" +
                        "    <title>Updated Title</title>\n"+
                        "</project>";


        // Write the updated data to the request body
        try (OutputStream os = putConnection.getOutputStream()) {
            byte[] input = updatedProjectData.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int updateResponseCode = putConnection.getResponseCode();
        System.out.println("PUT Response Code:" + updateResponseCode);

        putConnection.disconnect();

        // Assert that the PUT request returned 200
        assertEquals(HttpURLConnection.HTTP_OK, updateResponseCode);

        //Revert to initial state
        URL urlDelete = new URL(url + "/" +id);
        HttpURLConnection connectionDelete = (HttpURLConnection) urlDelete.openConnection();
        connectionDelete.setRequestMethod("DELETE");
        int responseCodeDelete = connectionDelete.getResponseCode();
        connectionDelete.disconnect();
    }


    // 2.15 Unsuccesful PUT request
    @Test
    public void InvalidPutRequestXML() throws Exception {

        // Arrange new Project for test
        URL url = new URL("http://localhost:4567/projects");
        HttpURLConnection postConnection = (HttpURLConnection) url.openConnection();

        // Set the request method to POST
        postConnection.setRequestMethod("POST");
        postConnection.setDoOutput(true);
        postConnection.setRequestProperty("Content-Type", "application/xml");
        postConnection.setRequestProperty("Accept", "application/xml");

        String projectData = "<project>\n" +
                "    <title>New Project</title>\n" +
                "    <completed>false</completed>\n" +
                "    <active>true</active>\n" +
                "    <description>A new project</description>\n" +
                "</project>";

        String id = "";
        try (OutputStream os = postConnection.getOutputStream()) {
            byte[] input = projectData.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Extract id from response of POST to create new link for PUT
        try (InputStream is = postConnection.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            System.out.println("POST Response Data: " + response.toString());
            id = extractIdFromXml(response.toString());
        }

        int postResponseCode = postConnection.getResponseCode();
        System.out.println("POST Response Code:" + postResponseCode);

        postConnection.disconnect();

        // Attempt to update the project
        URL updateUrl = new URL("http://localhost:4567/projects/" + id);
        HttpURLConnection putConnection = (HttpURLConnection) updateUrl.openConnection();
        putConnection.setRequestMethod("PUT");
        putConnection.setDoOutput(true);

        // Set the content type to XML
        putConnection.setRequestProperty("Content-Type", "application/xml");
        putConnection.setRequestProperty("Accept", "application/xml");

        // Update with PUT with invalid input (active is boolean, no is invalid)
        String updatedProjectData = "<project>\n" +
                "    <completed>123</completed>\n" +
                "    <active>123</active>\n"+
                "</project>";

        // Write the updated data to the request body
        try (OutputStream os = putConnection.getOutputStream()) {
            byte[] input = updatedProjectData.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int putResponseCode = putConnection.getResponseCode();
        System.out.println("PUT Response Code:" + putResponseCode);

        putConnection.disconnect();

        // Assert that the PUT request returned 400
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, putResponseCode);

        //Revert to initial state
        URL urlDelete = new URL(url + "/" +id);
        HttpURLConnection connectionDelete = (HttpURLConnection) urlDelete.openConnection();
        connectionDelete.setRequestMethod("DELETE");
        int responseCodeDelete = connectionDelete.getResponseCode();
        connectionDelete.disconnect();
    }

    private String extractIdFromXml(String xmlResponse) {
        String idTag = "<id>";
        int idStartIndex = xmlResponse.indexOf(idTag);
        if (idStartIndex != -1) {
            int startIndex = idStartIndex + idTag.length();
            int endIndex = xmlResponse.indexOf("</id>", startIndex);
            if (endIndex != -1) {
                return xmlResponse.substring(startIndex, endIndex);
            }
        }
        return null;
    }
}
