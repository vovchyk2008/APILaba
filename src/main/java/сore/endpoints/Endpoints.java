package сore.endpoints;

public interface Endpoints {

   //Pets
   String PET = "pet/";
   String PET_BY_ID = "pet/{id}/";
   String PET_BY_STATUS = "pet/findByStatus/";

   //Users
   String USER = "user/";
   String USER_BY_USERNAME = "user/{userName}";
   String USER_LOGIN = "user/login";
}
