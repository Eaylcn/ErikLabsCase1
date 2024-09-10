import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;

public class OMDBApiTest {

    String actualMovieId;
    String mainMovieName = "Harry Potter";
    String apiKey = "a4121eae";
    String url = "http://www.omdbapi.com/";
    String expectedMovieId = "tt0241527";
    String actualMovieName = "Harry Potter and the Sorcerer\\'s Stone";

    public String normalizeText(String text) {
        return text.replace("\\'", "'");
    }

    @Before
    public void SetUp() {
        Response response = RestAssured
                .given()
                .queryParam("s", mainMovieName)
                .queryParam("apikey", apiKey)
                .when()
                .get(url);

        response.then().statusCode(200);

        actualMovieId = response.path("Search.find { it.Title == '" + actualMovieName +"' }.imdbID");
    }

    @Test
    public void verifyMovieIdMatchesExpected() {
        Assert.assertEquals("Beklenen film ID'si " + expectedMovieId + ", fakat API'den gelen ID farklı!",
                actualMovieId, expectedMovieId);

        System.out.println("API'den dönen film ID'si ile beklenen ID eşleşti: " + actualMovieId);
    }

    @Test
    public void verifyMovieDetailsById() {

        RestAssured
                .given()
                .queryParam("i", actualMovieId)
                .queryParam("apikey", apiKey)
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .body("Title", equalTo(normalizeText(actualMovieName)))
                .body("Year", notNullValue())
                .body("Released", notNullValue());

        System.out.println("Film detayları başarıyla doğrulandı: Title, Year, Released alanları mevcut.");
    }
}
