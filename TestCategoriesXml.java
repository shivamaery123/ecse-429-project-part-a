import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.Random.class)
public class TestCategoriesXml {

  @BeforeEach
  public void start_app() throws InterruptedException {
    try {

      String directory_of_app = "C:\\Users\\shiva\\Downloads\\Application_Being_Tested";
      String command = "java -jar runTodoManagerRestAPI-1.5.5.jar";
      ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", command);
      processBuilder.directory(new File(directory_of_app));
      Process process = processBuilder.start();

    } catch (Exception e) {
      fail();
      e.printStackTrace();
    }

    Thread.sleep(500);
  }

  @AfterEach
  public void close_app() {
    try {
      String command = "netstat -ano | findstr :4567";
      ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", command);

      Process process = processBuilder.start();

      String output = new String(process.getInputStream().readAllBytes());
      String[] lines = output.split("\n");
      String line1 = lines[0];
      String[] line_1_words = line1.split("\\s+");
      String process_id = line_1_words[line_1_words.length - 1];

      String kill_command = "taskkill /F /PID " + process_id;
      ProcessBuilder processBuilder1 =
          new ProcessBuilder("cmd", "/c", "taskkill /F /PID " + process_id);
      Process kill_process = processBuilder1.start();

      kill_process.waitFor();

    } catch (Exception e) {
      fail();
      e.printStackTrace();
    }
  }

  @Test
  public void get_all_categories() {
    try {
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(new URI("http://localhost:4567/categories"))
              .header("Accept", "application/xml")
              .GET()
              .build();
      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      int responseCode = response.statusCode();
      String responseBody = response.body();
      assertEquals(200, responseCode);
      System.out.println(responseBody);
      String expected_response1 =
          "<categories><category><description/><id>1</id>"
              + "<title>Office</title></category><category><description/><id>2</id>"
              + "<title>Home</title></category></categories>";
      String expected_response2 =
          "<categories><category><description/><id>2</id><title>Home</title></category><category><description/><id>1</id><title>Office</title></category></categories>";
      String actual_response = responseBody;
      assertTrue(
          responseBody.equals(expected_response1) || responseBody.equals(expected_response2));

    } catch (java.net.URISyntaxException e) {
      System.out.println("Caught URI Syntax Exception: " + e.getMessage());
      fail();
    } catch (IOException e) {
      System.out.println("Caught IO Exception: " + e.getMessage());
      fail();
    } catch (InterruptedException e) {
      System.out.println("Caught Interrupted Exception: " + e.getMessage());
      fail();
    }
  }

  @Test
  public void get_category_by_id() {
    try {
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(new URI("http://localhost:4567/categories/1"))
              .header("Accept", "application/xml")
              .GET()
              .build();
      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      int responseCode = response.statusCode();
      String responseBody = response.body();
      assertEquals(200, responseCode);
      String actual_response = responseBody;
      String expected_response =
          "<categories><category><description/><id>1</id><title>Office</title>"
              + "</category></categories>";
      assertEquals(expected_response, actual_response);
    } catch (java.net.URISyntaxException e) {
      System.out.println("Caught URI Syntax Exception: " + e.getMessage());
      fail();
    } catch (IOException e) {
      System.out.println("Caught IO Exception: " + e.getMessage());
      fail();
    } catch (InterruptedException e) {
      System.out.println("Caught Interrupted Exception: " + e.getMessage());
      fail();
    }
  }

  @Test
  public void delete_category_by_id() {
    try {
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(new URI("http://localhost:4567/categories/1"))
              .header("Accept", "application/xml")
              .DELETE()
              .build();
      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      int responseCode = response.statusCode();
      String responseBody = response.body();
      assertEquals(200, responseCode);
      String expected_response_body = "";
      assertEquals(expected_response_body, responseBody);

    } catch (java.net.URISyntaxException e) {
      System.out.println("Caught URI Syntax Exception: " + e.getMessage());
      fail();
    } catch (IOException e) {
      fail();
      System.out.println("Caught IO Exception: " + e.getMessage());
    } catch (InterruptedException e) {
      System.out.println("Caught Interrupted Exception: " + e.getMessage());
      fail();
    }
  }

  @Test
  public void modify_category_by_id() {
    try {
      JSONObject category2 = new JSONObject();
      category2.put("title", "new title");
      category2.put("description", "new description");
      HttpRequest.BodyPublisher modified_category_to_send =
          HttpRequest.BodyPublishers.ofString(category2.toString());
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(new URI("http://localhost:4567/categories/2"))
              .header("Accept", "application/xml")
              .PUT(modified_category_to_send)
              .build();
      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      int responseCode = response.statusCode();
      String responseBody = response.body();
      assertEquals(200, responseCode);
      String actual_response = responseBody;
      System.out.println(actual_response);
      String expected_response =
          "<category><description>new description</description><id>"
              + "2</id><title>new title</title></category>";
      assertEquals(expected_response, actual_response);

    } catch (java.net.URISyntaxException e) {
      System.out.println("Caught URI Syntax Exception: " + e.getMessage());
      fail();
    } catch (IOException e) {
      System.out.println("Caught IO Exception: " + e.getMessage());
      fail();
    } catch (InterruptedException e) {
      System.out.println("Caught Interrupted Exception: " + e.getMessage());
      fail();
    }
  }

  @Test
  public void modify_non_existing_category_by_id() {
    try {
      JSONObject category2 = new JSONObject();
      category2.put("title", "new title");
      category2.put("description", "new description");
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(new URI("http://localhost:4567/categories/3"))
              .header("Accept", "application/xml")
              .PUT(HttpRequest.BodyPublishers.ofString(category2.toString()))
              .build();
      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      int responseCode = response.statusCode();
      String responseBody = response.body();
      assertEquals(404, responseCode);
      assertEquals(
          "<errorMessages><errorMessage>Invalid GUID for 3 entity category</errorMessage></errorMessages>",
          responseBody);

    } catch (java.net.URISyntaxException e) {
      System.out.println("Caught URI Syntax Exception: " + e.getMessage());
      fail();
    } catch (IOException e) {
      System.out.println("Caught IO Exception: " + e.getMessage());
      fail();
    } catch (InterruptedException e) {
      System.out.println("Caught Interrupted Exception: " + e.getMessage());
      fail();
    }
  }

  @Test
  public void modify_category_via_post_by_invalid_id() {
    try {
      JSONObject category2 = new JSONObject();
      category2.put("id", "3");
      category2.put("title", "new title");
      category2.put("description", "new description");
      HttpRequest.BodyPublisher modified_category_to_send =
          HttpRequest.BodyPublishers.ofString(category2.toString());
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(new URI("http://localhost:4567/categories/3"))
              .header("Accept", "application/xml")
              .POST(modified_category_to_send)
              .build();
      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      int responseCode = response.statusCode();
      String responseBody = response.body();
      assertEquals(404, responseCode);
      String expected_response =
          "<errorMessages><errorMessage>No such category entity instance with GUID or ID 3 found</errorMessage></errorMessages>";
      assertEquals(expected_response, responseBody);

    } catch (java.net.URISyntaxException e) {
      System.out.println("Caught URI Syntax Exception: " + e.getMessage());
      fail();
    } catch (IOException e) {
      System.out.println("Caught IO Exception: " + e.getMessage());
      fail();
    } catch (InterruptedException e) {
      System.out.println("Caught Interrupted Exception: " + e.getMessage());
      fail();
    }
  }

  @Test
  public void modify_category_via_post_by_id() {
    try {
      JSONObject category2 = new JSONObject();
      category2.put("title", "newly created category");
      category2.put("description", "newly created category description");
      HttpRequest.BodyPublisher modified_category_to_send =
          HttpRequest.BodyPublishers.ofString(category2.toString());
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(new URI("http://localhost:4567/categories/2"))
              .header("Accept", "application/xml")
              .POST(modified_category_to_send)
              .build();
      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      int responseCode = response.statusCode();
      String responseBody = response.body();
      assertEquals(200, responseCode);
      String expected_response =
          "<category><description>newly created category description</description><id>2</id><title>newly created category</title></category>";
      assertEquals(expected_response, responseBody);

    } catch (java.net.URISyntaxException e) {
      System.out.println("Caught URI Syntax Exception: " + e.getMessage());
      fail();
    } catch (IOException e) {
      System.out.println("Caught IO Exception: " + e.getMessage());
      fail();
    } catch (InterruptedException e) {
      System.out.println("Caught Interrupted Exception: " + e.getMessage());
      fail();
    }
  }

  @Test
  public void create_categories_with_empty_body() {
    try {
      JSONObject category = new JSONObject();
      HttpRequest.BodyPublisher modified_category_to_send =
          HttpRequest.BodyPublishers.ofString(category.toString());
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(new URI("http://localhost:4567/categories"))
              .header("Accept", "application/xml")
              .POST(modified_category_to_send)
              .build();
      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      int responseCode = response.statusCode();
      String responseBody = response.body();
      assertEquals(
          "<errorMessages><errorMessage>title : field is mandatory</errorMessage></errorMessages>",
          responseBody);
    } catch (java.net.URISyntaxException e) {
      System.out.println("Caught URI Syntax Exception: " + e.getMessage());
      fail();
    } catch (IOException e) {
      System.out.println("Caught IO Exception: " + e.getMessage());
      fail();
    } catch (InterruptedException e) {
      System.out.println("Caught Interrupted Exception: " + e.getMessage());
      fail();
    }
  }

  @Test
  public void create_categories_with_only_title() {
    try {
      JSONObject category = new JSONObject();
      category.put("title", "newest category");
      HttpRequest.BodyPublisher modified_category_to_send =
          HttpRequest.BodyPublishers.ofString(category.toString());
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(new URI("http://localhost:4567/categories"))
              .header("Accept", "application/xml")
              .POST(modified_category_to_send)
              .build();
      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      int responseCode = response.statusCode();
      String responseBody = response.body();
      assertEquals(201, responseCode);
      String expected_response =
          "<category><description/><id>3</id><title>newest category</title></category>";
      assertEquals(expected_response, responseBody);

    } catch (java.net.URISyntaxException e) {
      System.out.println("Caught URI Syntax Exception: " + e.getMessage());
      fail();
    } catch (IOException e) {
      System.out.println("Caught IO Exception: " + e.getMessage());
      fail();
    } catch (InterruptedException e) {
      System.out.println("Caught Interrupted Exception: " + e.getMessage());
      fail();
    }
  }

  @Test
  public void create_category_with_all_fields() {
    try {
      JSONObject category = new JSONObject();
      category.put("title", "newest category");
      category.put("description", "random description");
      category.put("id", "70");
      HttpRequest.BodyPublisher modified_category_to_send =
          HttpRequest.BodyPublishers.ofString(category.toString());
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(new URI("http://localhost:4567/categories"))
              .header("Accept", "application/xml")
              .POST(modified_category_to_send)
              .build();
      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      int responseCode = response.statusCode();
      String responseBody = response.body();
      assertEquals(400, responseCode);
      String expected_response =
          "<errorMessages><errorMessage>Invalid Creation: Failed Validation: Not allowed to create with id</errorMessage></errorMessages>";
      assertEquals(expected_response, responseBody);
    } catch (java.net.URISyntaxException e) {
      System.out.println("Caught URI Syntax Exception: " + e.getMessage());
      fail();
    } catch (IOException e) {
      System.out.println("Caught IO Exception: " + e.getMessage());
      fail();
    } catch (InterruptedException e) {
      System.out.println("Caught Interrupted Exception: " + e.getMessage());
      fail();
    }
  }

  @Test
  public void create_categories_with_only_title_and_description() {
    try {
      JSONObject category = new JSONObject();
      category.put("title", "newest category");
      category.put("description", "random description");
      HttpRequest.BodyPublisher modified_category_to_send =
          HttpRequest.BodyPublishers.ofString(category.toString());
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(new URI("http://localhost:4567/categories"))
              .header("Accept", "application/xml")
              .POST(modified_category_to_send)
              .build();
      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      int responseCode = response.statusCode();
      String responseBody = response.body();
      assertEquals(201, responseCode);
      String expected_response =
          "<category><description>random description</description><id>3</id><title>newest category</title></category>";
      assertEquals(expected_response, responseBody);
    } catch (java.net.URISyntaxException e) {
      System.out.println("Caught URI Syntax Exception: " + e.getMessage());
      fail();
    } catch (IOException e) {
      System.out.println("Caught IO Exception: " + e.getMessage());
      fail();
    } catch (InterruptedException e) {
      System.out.println("Caught Interrupted Exception: " + e.getMessage());
      fail();
    }
  }

  @Test
  public void create_categories_with_only_title_and_description_malformed() {
    try {
      String category =
          "title>newest category</title><description>random description</description>";

      HttpRequest.BodyPublisher modified_category_to_send =
          HttpRequest.BodyPublishers.ofString(category);
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(new URI("http://localhost:4567/categories"))
              .header("Accept", "application/xml")
              .POST(modified_category_to_send)
              .header("Content-Type", "application/xml")
              .build();
      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      int responseCode = response.statusCode();
      String responseBody = response.body();
      assertEquals(400, responseCode);
      String expected_response =
          "<errorMessages><errorMessage>Mismatched close tag title at 28 [character 29 line 1]</errorMessage></errorMessages>";
      assertEquals(expected_response, responseBody);
    } catch (java.net.URISyntaxException e) {
      System.out.println("Caught URI Syntax Exception: " + e.getMessage());
      fail();
    } catch (IOException e) {
      System.out.println("Caught IO Exception: " + e.getMessage());
      fail();
    } catch (InterruptedException e) {
      System.out.println("Caught Interrupted Exception: " + e.getMessage());
      fail();
    }
  }
}
