package org.trainingTracker.selenium;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.trainingTracker.database.dataAccesObject.UsersDAO;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.trainingTracker.selenium.TestUtils.*;

/**
 * Test class to check if the login process works correctly.
 */
public class SignInTest {

    private static WebDriver driver;
    private static final String ERROR_MESSAGE = "errorSignIn";

    @BeforeClass
    public static void setUp(){
        boolean res = UsersDAO.addUser(USERNAME, PASS, EMAIL);
        System.out.println("***** EJECUTA CREATE EN SIGNIN: " + res);
        driver = new FirefoxDriver();
        driver.get(STARTER_URL);
        try{
            goToStarter(driver);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    /*
     * Tests the login process with correct inputs
     * in the form.
     */
    @Test
    public void signInTest(){
        WebElement element;
        try{
            element = driver.findElement(By.name(USERNAME_FIELD));
            element.sendKeys(USERNAME);
            Thread.sleep(SLEEP_FOR_DISPLAY);
            element = driver.findElement(By.name(PASSWORD_FIELD));
            element.sendKeys(PASS);
            Thread.sleep(SLEEP_FOR_DISPLAY);
            element = driver.findElement(By.name(LOGIN_BUTTON));
            element.click();
            Thread.sleep(SLEEP_FOR_LOAD);
            // Tries to find an error message. If there's an error, test will fail.
            assertTrue((driver.findElements(By.name(ERROR_MESSAGE))).isEmpty());
            // If there's no error, the process has been successful and checks wheter the redirection has been made.
            assertTrue((driver.getCurrentUrl().equals(HOME_URL)));
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        finally {
            try{
                goToStarter(driver);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    /*
     * Tests the login process with a wrong password.
     */
    @Test
    public void wrongPassTest(){
        WebElement element;
        try{
            element = driver.findElement(By.name(USERNAME_FIELD));
            element.sendKeys(USERNAME);
            Thread.sleep(SLEEP_FOR_DISPLAY);
            element = driver.findElement(By.name(PASSWORD_FIELD));
            element.sendKeys("pas");
            Thread.sleep(SLEEP_FOR_DISPLAY);
            element = driver.findElement(By.name(LOGIN_BUTTON));
            element.click();
            Thread.sleep(SLEEP_FOR_LOAD);
            // Tries to find an error message. If there's no error, test will fail.
            assertFalse((driver.findElements(By.name(ERROR_MESSAGE))).isEmpty());
            // If there's an error, the process has failed and checks wheter the redirection has been made, which should not.
            assertFalse((driver.getCurrentUrl().equals(HOME_URL)));
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        finally {
            driver.navigate().refresh();
        }
    }

    /*
     * Tests the login process with a non existent user.
     */
    @Test
    public void nonExistentUserTest(){
        WebElement element;
        try{
            element = driver.findElement(By.name(USERNAME_FIELD));
            element.sendKeys("doesNotExists");
            Thread.sleep(SLEEP_FOR_DISPLAY);
            element = driver.findElement(By.name(PASSWORD_FIELD));
            element.sendKeys(PASS);
            Thread.sleep(SLEEP_FOR_DISPLAY);
            element = driver.findElement(By.name(LOGIN_BUTTON));
            element.click();
            Thread.sleep(SLEEP_FOR_LOAD);
            // Tries to find an error message. If there's no error, test will fail.
            assertFalse((driver.findElements(By.name(ERROR_MESSAGE))).isEmpty());
            // If there's an error, the process has failed and checks wheter the redirection has been made, which should not.
            assertFalse((driver.getCurrentUrl().equals(HOME_URL)));
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        finally {
            driver.navigate().refresh();
        }
    }

    /*
     * Tests the login process with some or all fields in blank.
     */
    @Test
    public void inputFieldsAreBlankTest(){
        WebElement element;
        WebElement login = driver.findElement(By.name(LOGIN_BUTTON));
        try{
            // Checks whether the user logs in with all fields blank.
            login.click();
            Thread.sleep(SLEEP_FOR_DISPLAY);
            assertFalse((driver.getCurrentUrl().equals(HOME_URL)));

            // Checks whether the user logs in with password field blank
            element = driver.findElement(By.name(USERNAME_FIELD));
            element.sendKeys(USERNAME);
            Thread.sleep(SLEEP_FOR_DISPLAY);
            login.click();
            Thread.sleep(SLEEP_FOR_DISPLAY);
            assertFalse((driver.getCurrentUrl().equals(HOME_URL)));

            // Checks whether the user logs in with username field blank.
            element.clear();
            Thread.sleep(SLEEP_FOR_DISPLAY);
            element = driver.findElement(By.name(PASSWORD_FIELD));
            element.sendKeys(PASS);
            Thread.sleep(SLEEP_FOR_DISPLAY);
            login.click();
            Thread.sleep(SLEEP_FOR_DISPLAY);
            assertFalse((driver.getCurrentUrl().equals(HOME_URL)));
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        finally {
            driver.navigate().refresh();
        }
    }

    @AfterClass
    public static void tearDown(){
        driver.close();
        driver.quit();
        boolean res = UsersDAO.deleteUser(USERNAME);
        System.out.println("***** EJECUTA DELETE EN SIGNIN: " + res);
    }
}
