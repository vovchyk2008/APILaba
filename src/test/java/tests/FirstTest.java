package tests;


import static сore.model.Endpoints.PET;
import static сore.model.Endpoints.PET_BY_ID;
import static сore.model.Endpoints.PET_BY_STATUS;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Test;
import сore.model.DeletePetModel;
import сore.model.NotFoundModel;
import сore.model.PetModel;
import сore.model.PetModel.Category;
import сore.model.PetModel.Tag;
import сore.model.UpdatePetModel;

public class FirstTest extends BaseTest {

  static Long petId;
  static String petName = "Rex";

  @Test
  public void checkPetsDataRequest() {

    List<Tag> listTags = new ArrayList<>();
    listTags.add(new Tag(31, "Small dog"));
    listTags.add(new Tag(30, "Cute"));
    listTags.add(new Tag(20, "Silent"));

    List<String> listUrl = new ArrayList<>();
    listUrl.add("https://unsplash.com/photos/v3-zcCWMjgM");
    listUrl.add("https://unsplash.com/photos/T-0EW-SEbsE");
    listUrl.add("https://unsplash.com/photos/BJaqPaH6AGQ");

    PetModel expectedPetModel = PetModel.builder()
        .name("Rex")
        .category(new Category(10, "Dogs"))
        .tags(listTags)
        .photoUrls(listUrl)
        .status("available")
        .build();

    ValidatableResponse petResponse = RestAssured
        .given()
        .body(expectedPetModel)
        .when()
        .post(PET)
        .then()
        .statusCode(200);

    PetModel responsePetModel = petResponse.extract().as(PetModel.class);
    petId = responsePetModel.getId();//get ID from created pet
    SoftAssertions softAssertions = new SoftAssertions();

    //Check that after creation pet id not equals 0
    softAssertions.assertThat(responsePetModel.getId())
        .as("We are waiting that pet id not 0")
        .isNotEqualTo(0);

    //Check that all data in response the same as in request
    softAssertions.assertThat(responsePetModel)
        .as("")
        .isEqualToIgnoringGivenFields(expectedPetModel, "id");

    softAssertions.assertAll();
  }

  @Test(dependsOnMethods = "checkPetsDataRequest")
  public void checkPetsNameAndStatusInResponse() {
    ValidatableResponse petResponse = RestAssured
        .given()
        .pathParam("id", petId)
        .when()
        .get(PET_BY_ID)
        .then()
        .statusCode(200);

    PetModel responsePetModel = petResponse.extract().as(PetModel.class);

    SoftAssertions softAssertions = new SoftAssertions();

    //Check that pet name in response Rex
    softAssertions.assertThat(responsePetModel.getName())
        .as("")
        .isEqualTo(petName);

    //Check that status available
    softAssertions.assertThat(responsePetModel.getStatus())
        .as("")
        .isEqualTo("available");

    softAssertions.assertAll();
  }

  @Test(dependsOnMethods = "checkPetsDataRequest")
  public void checkThatUpdatePetNameAndStatusTest() {

    ValidatableResponse petResponse = RestAssured
        .given()
        .contentType("application/x-www-form-urlencoded")
        .pathParam("id", petId)
        .formParam("name", "Sky")
        .formParam("status", "sold")
        .when()
        .post(PET_BY_ID)
        .then()
        .statusCode(200);

    UpdatePetModel updateResponse = petResponse.extract().as(UpdatePetModel.class);

    SoftAssertions softAssertions = new SoftAssertions();

    softAssertions.assertThat(updateResponse.getMessage())
        .as("Expected that value of message field in response equals to pet Id")
        .isEqualTo(petId);

    PetModel afterUpdatePetModal = RestAssured
        .given()
        .pathParam("id", petId)
        .when()
        .get(PET_BY_ID)
        .then()
        .statusCode(200).extract().as(PetModel.class);

    softAssertions.assertThat(afterUpdatePetModal.getName())
        .as("Expected that new name of dog [Sky]")
        .isEqualTo("Sky");
    softAssertions.assertThat(afterUpdatePetModal.getStatus())
        .as("Expected that new status [sold]")
        .isEqualTo("sold");

    softAssertions.assertAll();

  }

  @Test(dependsOnMethods = "checkPetsDataRequest")
  public void checkThatPetIsDeleteTest() {

    ValidatableResponse petResponse = RestAssured
        .given()
        .pathParam("id", petId)
        .when()
        .delete(PET_BY_ID)
        .then()
        .statusCode(200);

    DeletePetModel deleteResponse = petResponse.extract().as(DeletePetModel.class);

    SoftAssertions softAssertions = new SoftAssertions();

    softAssertions.assertThat(deleteResponse.getMessage())
        .as("Expected that value of message field in response equals to pet Id")
        .isEqualTo(String.valueOf(petId));

    ValidatableResponse petAfterDeleteResponse = RestAssured
        .given()
        .pathParam("id", petId)
        .when()
        .get(PET_BY_ID)
        .then()
        .statusCode(404);

    NotFoundModel notFoundModelResponse = petAfterDeleteResponse.extract().as(NotFoundModel.class);

    softAssertions.assertThat(notFoundModelResponse.getCode())
        .as("Expected that body equals to [1]")
        .isEqualTo(1);

    softAssertions.assertThat(notFoundModelResponse.getType())
        .as("Expected that body equals to [error]")
        .isEqualTo("error");

    softAssertions.assertThat(notFoundModelResponse.getMessage())
        .as("Expected that body equals to [Pet not found]")
        .isEqualTo("Pet not found");

    softAssertions.assertAll();
  }

  @Test
  public void checkThatCreatedDogExistInResultBody() {
    List<Tag> listTags = new ArrayList<>();
    listTags.add(new Tag(12, "Angry"));
    listTags.add(new Tag(14, "Big"));
    listTags.add(new Tag(16, "Slow"));

    List<String> listUrl = new ArrayList<>();
    listUrl.add("https://unsplash.com/photos/v3-zcCWMjgM");
    listUrl.add("https://unsplash.com/photos/T-0EW-SEbsE");
    listUrl.add("https://unsplash.com/photos/BJaqPaH6AGQ");

    PetModel petModel = PetModel.builder()
        .name("Sharik")
        .category(new Category(10, "Dogs"))
        .tags(listTags)
        .photoUrls(listUrl)
        .status("sold")
        .build();

    ValidatableResponse petResponse = RestAssured
        .given()
        .body(petModel)
        .when()
        .post(PET)
        .then()
        .statusCode(200);

    PetModel responsePetModel = petResponse.extract().as(PetModel.class);

    SoftAssertions softAssertions = new SoftAssertions();

    PetModel foundedPetModel = RestAssured
        .given()
        .queryParam("status", "sold")
        .when()
        .get(PET_BY_STATUS)
        .then()
        .extract().as(PetModel.class);

    Assertions.assertThat(foundedPetModel)
        .as("")
        .
  }


    //Create new dog with random data (POST /pet) and status sold
  //Find pet by status (GET /pet/findByStatus) sold
  //Check that created dog exist in result body


  }
