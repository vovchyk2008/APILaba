package tests;


import static сore.endpoints.Endpoints.PET;
import static сore.endpoints.Endpoints.PET_BY_ID;
import static сore.endpoints.Endpoints.PET_BY_STATUS;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Test;
import сore.models.pet.DeletePetModel;
import сore.models.pet.FindByStatusPetModel;
import сore.models.pet.NotFoundModel;
import сore.models.pet.PetModel;
import сore.models.pet.PetModel.Category;
import сore.models.pet.PetModel.Tag;
import сore.models.pet.UpdatePetModel;

public class PetTest extends BaseTest {

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
        .as("We are waiting that pets name is: " + petName)
        .isEqualTo(petName);

    //Check that status available
    softAssertions.assertThat(responsePetModel.getStatus())
        .as("We are waiting that status is available" )
        .isEqualTo("available");

    softAssertions.assertAll();
  }

  @Test(dependsOnMethods = "checkPetsDataRequest")
  public void checkThatUpdatePetNameAndStatusTest() {

    String newPetsName = "Sky";
    String newStatus = "sold";

    ValidatableResponse updatePetResponse = RestAssured
        .given()
        .contentType("application/x-www-form-urlencoded")
        .pathParam("id", petId)
        .formParam("name", newPetsName)
        .formParam("status", newStatus)
        .when()
        .post(PET_BY_ID)
        .then()
        .statusCode(200);

    UpdatePetModel updateResponse = updatePetResponse.extract().as(UpdatePetModel.class);

    SoftAssertions softAssertions = new SoftAssertions();

    softAssertions.assertThat(updateResponse.getMessage())
        .as("Expected that value of message field in response equals to pet Id")
        .isEqualTo(petId);

    ValidatableResponse petAfterUpdateResponse = RestAssured
        .given()
        .pathParam("id", petId)
        .when()
        .get(PET_BY_ID)
        .then()
        .statusCode(200);

    PetModel actualUpdateModel = petAfterUpdateResponse.extract().as(PetModel.class);

    softAssertions.assertThat(actualUpdateModel.getName())
        .as("Expected that new name of dog: " + newPetsName)
        .isEqualTo("Sky");
    softAssertions.assertThat(actualUpdateModel.getStatus())
        .as("Expected that new status: " + newStatus)
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

    //Check that value of message field in response equals to pet Id
    softAssertions.assertThat(notFoundModelResponse.getCode())
        .as("We are waiting that code in body is [1]")
        .isEqualTo(1);

    softAssertions.assertThat(notFoundModelResponse.getType())
        .as("We are waiting that type in body is [error]")
        .isEqualTo("error");

    softAssertions.assertThat(notFoundModelResponse.getMessage())
        .as("We are waiting that message in body is [Pet not found]")
        .isEqualTo("Pet not found");

    softAssertions.assertAll();
  }

  @Test
  public void checkThatCreatedDogExistInResultBody() {

    String petStatus = "sold";
    String petName = "Sharik";
    Category petCategory = new Category(15, "Dogs");

    List<Tag> listTags = new ArrayList<>();
    listTags.add(new Tag(12, "Angry"));
    listTags.add(new Tag(14, "Big"));
    listTags.add(new Tag(16, "Slow"));

    List<String> listUrl = new ArrayList<>();
    listUrl.add("https://unsplash.com/photos/v3-zcCWMjgM");
    listUrl.add("https://unsplash.com/photos/T-0EW-SEbsE");
    listUrl.add("https://unsplash.com/photos/BJaqPaH6AGQ");

    PetModel petModel = PetModel.builder()
        .name(petName)
        .category(petCategory)
        .tags(listTags)
        .photoUrls(listUrl)
        .status(petStatus)
        .build();
    //Create new dog with random data (POST /pet) and status sold

    ValidatableResponse creatingPetResponse = RestAssured
        .given()
        .body(petModel)
        .when()
        .post(PET)
        .then()
        .statusCode(200);

    //Find pet by status (GET /pet/findByStatus) sold

    ValidatableResponse findPetResponse = RestAssured
        .given()
        .queryParam("status", petStatus)
        .when()
        .get(PET_BY_STATUS)
        .then()
        .statusCode(200);

    List<FindByStatusPetModel> findByStatusPetModels = Arrays
        .asList(findPetResponse.extract().as(FindByStatusPetModel[].class));
    List<String> petsName = FindByStatusPetModel.getPetsName(findByStatusPetModels);

    //Check that created dog exist in result body

    Assertions.assertThat(petsName)
        .as("We are waiting that: " + petName + "exists in result body")
        .contains(petName);
  }
  }
