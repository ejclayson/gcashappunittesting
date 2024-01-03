import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GcashAppBasicTests {

    @Test
    @DisplayName("Valid Login")
    public void userAuthenticate(){
        GcashAppClasses GcashAppClassesBasicTests = new GcashAppClasses();
        String mobile = "09661871435";
        String pin = "1111";
        int value = (int) GcashAppClassesBasicTests.loginUser(mobile, pin);
        Assertions.assertEquals(1, value);
    }

    @Test
    @DisplayName("Invalid Login")
    public void userAuthenticateFail(){
        GcashAppClasses GcashAppBasicTests = new GcashAppClasses();
        String mobile = "09087687166";
        String pin = "4321";
       String value = GcashAppBasicTests.loginUser(mobile, pin).toString();
        Assertions.assertEquals(1, value);
    }

    @Test
    @DisplayName("Check Balance")
    public void checkBalance(){
        GcashAppClasses GcashAppBasicTests = new GcashAppClasses();
        int value = GcashAppBasicTests.checkBalance(2);
        Assertions.assertEquals(1, value);
    }


    @Test
    @DisplayName("Cash In")
    public void cashIn(){
        GcashAppClasses GcashAppBasicTests = new GcashAppClasses();
        int value = GcashAppBasicTests.cashIn(4, 5000.00);
        Assertions.assertEquals(1, value);
    }


    @Test
    @DisplayName("Cash Transfer")
    public void cashTransfer(){
        GcashAppClasses GcashAppBasicTests = new GcashAppClasses();
        int value = GcashAppBasicTests.cashTransfer("09313265786", "09578374513", 3000.00);
        Assertions.assertEquals(1, value);
    }



    @Test
    @DisplayName("View Transaction")
    public void viewTransaction(){
        GcashAppClasses GcashAppBasicTests = new GcashAppClasses();
        int value = GcashAppBasicTests.viewTransaction(3, "2023-12-27 13:09:47");
        Assertions.assertEquals(1, value);
    }

    @Test
    @DisplayName("View User Transaction")
    public void viewUserTransaction(){
        GcashAppClasses GcashAppBasicTests = new GcashAppClasses();
        int value = GcashAppBasicTests.viewUserTransaction(4);
        Assertions.assertEquals(1, value);
    }

    @Test
    @DisplayName("View All User's Transaction")
    public void viewAllTransaction(){
        GcashAppClasses GcashAppBasicTests = new GcashAppClasses();
        int value = GcashAppBasicTests.viewAllTransaction(4);
        Assertions.assertEquals(1, value);
    }

}
