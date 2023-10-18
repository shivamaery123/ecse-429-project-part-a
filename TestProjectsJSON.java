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
public class TestProjectsJSON {

    // 2.1 Successful GET request on projects
    @Test
    public void SuccessfulGETProjects() throws Exception {
        String urlLink = "http://localhost:4567/gui/instances?entity=project";

        // Create a URL object
        URL url = new URL(urlLink);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Get the response code
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code:"+ responseCode);

        connection.disconnect();

        // Assert that the response code is 200
        assertEquals(HttpURLConnection.HTTP_OK, responseCode);
    }

    // 2.2 Test GET with invalid URL
    @Test
    public void UnsuccessfulGETProjects() throws Exception {
        //Non-existent URL
        String urlLink = "http://localhost:4567/nonexistentlink";

        // Create a URL object
        URL url = new URL(urlLink);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set the HTTP GET method
        connection.setRequestMethod("GET");

        // Get the response code
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code:"+ responseCode);

        connection.disconnect();

        // Assert that the response code is 404
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, responseCode);
    }

    // 2.3 Test POST request with valid inputs
    @Test
    public void SuccessfulPOSTProjects() throws Exception {

        URL url = new URL("http://localhost:4567/projects");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set the request method to POST
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        connection.setRequestProperty("Content-Type", "application/json");
        String projectData = "{ \"title\": \"New Project\", \"completed\": false, \"active\": true, \"description\": \"A new project\" }";

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
            id = extractIdFromJson(response.toString());
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

    // 2.4 Test with invalid value for completed, should return code 400
    @Test
    public void UnsuccessfulPOSTProjects() throws Exception {

        URL url = new URL("http://localhost:4567/projects");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set the request method to POST
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        // Set the content type to JSON for these tests
        connection.setRequestProperty("Content-Type", "application/json");

        // Invalid value for completed (should be boolean, but is INT)
        String projectData = "{ \"title\": \"New Project\", \"completed\": 123, \"active\": true, \"description\": \"A new project\" }";

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

    // 2.5 Test Successful DELETE Request
    @Test
    public void SuccessfulDeleteRequest() throws Exception {

        //Arrange new Project for test
        URL url = new URL("http://localhost:4567/projects");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set the request method to POST
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        // Set the content type to JSON
        connection.setRequestProperty("Content-Type", "application/json");
        String projectData = "{ \"title\": \"New Project\", \"completed\": false, \"active\": true, \"description\": \"A new project\" }";

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
            id = extractIdFromJson(response.toString());
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

        // Assert that the response code is 200, request was successful
        assertEquals(HttpURLConnection.HTTP_OK, responseCodeDelete);
    }

    // 2.6 Test invalid DELETE Request
    @Test
    public void NonExistentIdDeleteRequest() throws Exception {

        // Attempt to delete project with id 123 (non-existent)
        URL url = new URL("http://localhost:4567/projects");
        URL urlDelete = new URL(url + "/" + "123");
        System.out.println(urlDelete);
        HttpURLConnection connectionDelete = (HttpURLConnection) urlDelete.openConnection();

        // Set the request method to DELETE
        connectionDelete.setRequestMethod("DELETE");

        // Get the response code
        int responseCodeDelete = connectionDelete.getResponseCode();
        connectionDelete.disconnect();

        // Assert that the response code is 404 NOT FOUND, id does not exist
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, responseCodeDelete);
    }

    // 2.7 Successful PUT request
    @Test
    public void ValidPutRequest() throws Exception {

        // Arrange new Project for test
        URL url = new URL("http://localhost:4567/projects");
        HttpURLConnection postConnection = (HttpURLConnection) url.openConnection();

        // Set the request method to POST (first create the project)
        postConnection.setRequestMethod("POST");
        postConnection.setDoOutput(true);
        postConnection.setRequestProperty("Content-Type", "application/json");

        String projectData = "{ \"title\": \"New Project\", \"completed\": false, \"active\": true, \"description\": \"A new project\" }";

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
            id = extractIdFromJson(response.toString());
        }

        int postResponseCode = postConnection.getResponseCode();
        System.out.println("POST Response Code:" + postResponseCode);

        postConnection.disconnect();

        // Attempt to update the project
        URL updateUrl = new URL("http://localhost:4567/projects/" + id);
        HttpURLConnection putConnection = (HttpURLConnection) updateUrl.openConnection();
        putConnection.setRequestMethod("PUT");
        putConnection.setDoOutput(true);

        // Set the content type to JSON
        putConnection.setRequestProperty("Content-Type", "application/json");

        // Update project title
        String updatedProjectData = "{ \"title\": \"New Name Project\"}";

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


    // 2.8 Unsuccesful PUT request
    @Test
    public void InvalidPutRequest() throws Exception {

        // Arrange new Project for test
        URL url = new URL("http://localhost:4567/projects");
        HttpURLConnection postConnection = (HttpURLConnection) url.openConnection();

        // Set the request method to POST
        postConnection.setRequestMethod("POST");
        postConnection.setDoOutput(true);
        postConnection.setRequestProperty("Content-Type", "application/json");

        String projectData = "{ \"title\": \"New Project\", \"completed\": false, \"active\": true, \"description\": \"A new project\" }";

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
            id = extractIdFromJson(response.toString());
        }

        int postResponseCode = postConnection.getResponseCode();
        System.out.println("POST Response Code:" + postResponseCode);

        postConnection.disconnect();

        // Attempt to update the project
        URL updateUrl = new URL("http://localhost:4567/projects/" + id);
        HttpURLConnection putConnection = (HttpURLConnection) updateUrl.openConnection();
        putConnection.setRequestMethod("PUT");
        putConnection.setDoOutput(true);

        // Set the content type to JSON
        putConnection.setRequestProperty("Content-Type", "application/json");

        // Update with PUT with invalid input (active is boolean, no is invalid)
        String updatedProjectData = "{ \"title\": \"New Name Project\", \"active\": \"no\"}";

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

    //Helper method used to extract ID from JSON payload to revert to initial state upon end of test
    private String extractIdFromJson(String jsonResponse) {
        int idIndex = jsonResponse.indexOf("\"id\":\"");
        if (idIndex != -1) {
            int startIndex = idIndex + 6;
            int endIndex = jsonResponse.indexOf("\"", startIndex);
            if (endIndex != -1) {
                return jsonResponse.substring(startIndex, endIndex);
            }
        }
        return null;
    }
}
