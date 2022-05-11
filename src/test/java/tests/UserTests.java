package tests;

import static сore.endpoints.Endpoints.USER;
import static сore.endpoints.Endpoints.USER_BY_USERNAME;
import static сore.endpoints.Endpoints.USER_LOGIN;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Test;
import сore.models.user.StatusUserModel;
import сore.models.user.UserModel;

public class UserTests extends BaseTest {

  String userId;
  String userName = faker.name().username();
  String firstName = faker.name().firstName();
  String lastName = faker.name().lastName();
  String email = faker.internet().emailAddress();
  String password = faker.internet().password();
  String phone = faker.phoneNumber().phoneNumber();

  UserModel userModel;

  @Test
  public void checkThatNewUserWasCreated() {
    userModel = UserModel.builder()
        .username(userName)
        .firstName(firstName)
        .lastName(lastName)
        .email(email)
        .password(password)
        .phone(phone)
        .userStatus(8)
        .build();

    ValidatableResponse createUserResponse = RestAssured
        .given()
        .body(userModel)
        .when()
        .post(USER)
        .then()
        .statusCode(200);

    StatusUserModel createUserModelResponse = createUserResponse.extract().as(
        StatusUserModel.class);
    userId = createUserModelResponse.getMessage();

    //Check that message body not equals to 0
    Assertions.assertThat(createUserModelResponse.getMessage())
        .as("We are waiting that message body not 0")
        .isNotEqualTo("0");
  }


  @Test(dependsOnMethods = "checkThatNewUserWasCreated")
  public void checkThatAllUsersDataWasSavedTest() {

    ValidatableResponse userResponse = RestAssured
        .given()
        .pathParam("userName", userName)
        .when()
        .get(USER_BY_USERNAME)
        .then()
        .statusCode(200);

    UserModel getUserResponse = userResponse.extract().as(UserModel.class);

    SoftAssertions softAssertions = new SoftAssertions();

    //Check that all data from test #6 was saved
    softAssertions.assertThat(userModel)
        .as("We are waiting that all data from test #6 was saved")
        .isEqualToIgnoringGivenFields(getUserResponse, "id");

    //Check that user id = value of massage field from previous test (#6)
    softAssertions.assertThat(getUserResponse.getId())
        .as("We are waiting that user id: " + userId + "=" + getUserResponse.getId())
        .isEqualTo(userId);

    softAssertions.assertAll();
  }

  @Test(dependsOnMethods = "checkThatNewUserWasCreated")
  public void checkMessageAfterLoginTest() {

    String message = "logged in user session:";

    ValidatableResponse userResponse = RestAssured
        .given()
        .queryParam("username", userName)
        .queryParam("password", password)
        .when()
        .get(USER_LOGIN)
        .then()
        .statusCode(200);

    StatusUserModel statusUserModel = userResponse.extract().as(StatusUserModel.class);

    //Check that message contains value is "message": "logged in user session:"
    Assertions.assertThat(statusUserModel.getMessage())
        .as("We are waiting that message contains: " + message)
        .contains(message);

  }
//Login with created user (GET /user/login)
//Check that status code 200
//Check that message contains value is "message": "logged in user session:"
}