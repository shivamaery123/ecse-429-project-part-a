import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.Random.class)
public class TestCategories {

  public boolean check_json_is_formed_correctly(String response_json) {
    try {
      new JSONObject(response_json);

    }

    catch (JSONException e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }
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
    //        catch (InterruptedException | IOException e) {
    //            e.printStackTrace();
    //
    //        }
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
    //        catch (IOException | InterruptedException e) {
    //            e.printStackTrace();
    //        }

  }

  @Test
  public void get_all_categories() {
    try {
      HttpRequest request =
          HttpRequest.newBuilder().uri(new URI("http://localhost:4567/categories")).GET().build();
      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      int responseCode = response.statusCode();
      String responseBody = response.body();
      assertEquals(200, responseCode);
      assertTrue(check_json_is_formed_correctly(responseBody));
      JSONObject json_response = new JSONObject(responseBody);
      JSONArray list_categories = json_response.getJSONArray("categories");
      JSONArray expected_response = new JSONArray();
      JSONObject category1 = new JSONObject();
      category1.put("id", "1");
      category1.put("title", "Office");
      category1.put("description", "");
      JSONObject category2 = new JSONObject();
      category2.put("id", "2");
      category2.put("title", "Home");
      category2.put("description", "");
      expected_response.put(category1);
      expected_response.put(category2);
      List<JSONObject> jsonList = new ArrayList<>();
      for (int i = 0; i < list_categories.length(); i++) {
        jsonList.add(list_categories.getJSONObject(i));
      }
      Collections.sort(
          jsonList,
          (Category1, Category2) ->
              Integer.compare(Category1.getInt("id"), Category2.getInt("id")));
      JSONArray actual_response = new JSONArray(jsonList);


      assertEquals(expected_response.get(0).toString(), actual_response.get(0).toString());
      assertEquals(expected_response.get(1).toString(), actual_response.get(1).toString());

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
          HttpRequest.newBuilder().uri(new URI("http://localhost:4567/categories/1")).GET().build();
      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      int responseCode = response.statusCode();
      String responseBody = response.body();
      assertTrue(check_json_is_formed_correctly(responseBody));
      assertEquals(200, responseCode);
      JSONObject category_found =
          new JSONObject(responseBody).getJSONArray("categories").getJSONObject(0);
      JSONObject category_expected = new JSONObject();
      category_expected.put("id", "1");
      category_expected.put("title", "Office");
      category_expected.put("description", "");
      assertEquals(category_expected.toString(), category_found.toString());
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
      System.out.println("Caught IO Exception: " + e.getMessage());
      fail();
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
              .PUT(modified_category_to_send)
              .build();
      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      int responseCode = response.statusCode();
      String responseBody = response.body();
      assertTrue(check_json_is_formed_correctly(responseBody));
      assertEquals(200, responseCode);
      JSONObject expected_response_body = new JSONObject();
      expected_response_body.put("id", "2");
      expected_response_body.put("description", "new description");
      expected_response_body.put("title", "new title");
      JSONObject actual_response_body = new JSONObject(responseBody);
      assertEquals(
          expected_response_body.toString(), actual_response_body.toString());

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
      HttpRequest.BodyPublisher modified_category_to_send =
          HttpRequest.BodyPublishers.ofString(category2.toString());
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(new URI("http://localhost:4567/categories/3"))
              .PUT(modified_category_to_send)
              .build();
      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      int responseCode = response.statusCode();
      String responseBody = response.body();
      assertTrue(check_json_is_formed_correctly(responseBody));
      assertEquals(404, responseCode);
      JSONObject expected_response_body = new JSONObject();
      JSONArray error_message = new JSONArray();
      error_message.put("Invalid GUID for 3 entity category");
      expected_response_body.put("errorMessages", error_message);
      JSONObject actual_response_body = new JSONObject(responseBody);
      assertEquals(
          expected_response_body.toString(), actual_response_body.toString());

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
              .POST(modified_category_to_send)
              .build();
      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      int responseCode = response.statusCode();
      String responseBody = response.body();
      assertTrue(check_json_is_formed_correctly(responseBody));
      assertEquals(404, responseCode);
      JSONObject expected_response_body = new JSONObject();
      JSONArray error_message = new JSONArray();
      error_message.put("No such category entity instance with GUID or ID 3 found");
      expected_response_body.put("errorMessages", error_message);
      JSONObject actual_response_body = new JSONObject(responseBody);
      assertEquals(
          expected_response_body.toString(), actual_response_body.toString());

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
              .POST(modified_category_to_send)
              .build();
      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      int responseCode = response.statusCode();
      String responseBody = response.body();
      assertTrue(check_json_is_formed_correctly(responseBody));
      assertEquals(200, responseCode);
      JSONObject expected_response_body = new JSONObject();
      expected_response_body.put("id", "2");
      expected_response_body.put("title", "newly created category");
      expected_response_body.put("description", "newly created category description");
      JSONObject actual_response_body = new JSONObject(responseBody);
      assertEquals(
          expected_response_body.toString(), actual_response_body.toString());

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
              .POST(modified_category_to_send)
              .build();
      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      int responseCode = response.statusCode();
      String responseBody = response.body();
      assertTrue(check_json_is_formed_correctly(responseBody));
      JSONObject expected_response_body = new JSONObject();
      JSONArray error_message = new JSONArray();
      error_message.put("title : field is mandatory");
      expected_response_body.put("errorMessages", error_message);
      JSONObject actual_response_body = new JSONObject(responseBody);
      assertEquals(
          expected_response_body.toString(), actual_response_body.toString());
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
              .POST(modified_category_to_send)
              .build();
      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      int responseCode = response.statusCode();
      String responseBody = response.body();
      assertTrue(check_json_is_formed_correctly(responseBody));
      assertEquals(201, responseCode);
      JSONObject expected_response_body = new JSONObject();
      expected_response_body.put("id", "3");
      expected_response_body.put("title", "newest category");
      expected_response_body.put("description", "");
      JSONObject actual_response_body = new JSONObject(responseBody);
      assertEquals(
          expected_response_body.toString(), actual_response_body.toString());
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
              .POST(modified_category_to_send)
              .build();
      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      int responseCode = response.statusCode();
      String responseBody = response.body();
      assertTrue(check_json_is_formed_correctly(responseBody));
      assertEquals(400, responseCode);
      JSONObject expected_response_body = new JSONObject();
      JSONArray error_message = new JSONArray();
      error_message.put("Invalid Creation: Failed Validation: Not allowed to create with id");
      expected_response_body.put("errorMessages", error_message);
      JSONObject actual_response_body = new JSONObject(responseBody);
      assertEquals(
          expected_response_body.toString(), actual_response_body.toString());
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
              .POST(modified_category_to_send)
              .build();
      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      int responseCode = response.statusCode();
      String responseBody = response.body();
      assertTrue(check_json_is_formed_correctly(responseBody));
      assertEquals(201, responseCode);
      JSONObject expected_response_body = new JSONObject();
      expected_response_body.put("id", "3");
      expected_response_body.put("title", "newest category");
      expected_response_body.put("description", "random description");
      JSONObject actual_response_body = new JSONObject(responseBody);
      assertEquals(
          expected_response_body.toString(), actual_response_body.toString());
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
      JSONObject category = new JSONObject();
      category.put("title", "newest category");
      category.put("description", "random description");
      HttpRequest.BodyPublisher modified_category_to_send =
              HttpRequest.BodyPublishers.ofString(category.toString().substring(1));
      HttpRequest request =
              HttpRequest.newBuilder()
                      .uri(new URI("http://localhost:4567/categories"))
                      .POST(modified_category_to_send)
                      .build();
      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      int responseCode = response.statusCode();
      String responseBody = response.body();
      String error = "java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 2 path $";
      JSONObject expected_response_body = new JSONObject();
      JSONArray error_message = new JSONArray();
      error_message.put(error);
      expected_response_body.put("errorMessages", error_message);
      assertEquals(
              expected_response_body.toString(), responseBody);
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
