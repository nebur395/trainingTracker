package org.trainingTracker.selenium;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.trainingTracker.database.dataAccesObject.UsersDAO;

import java.util.Iterator;

import static org.junit.Assert.assertFalse;
import static org.trainingTracker.selenium.TestUtils.*;

public class AddCardiovascularExerciseTest {

    private static WebDriver driver;
    private static final String ADD_EXERCISE_BUTTON = "addCardioButton";
    private static final String TYPE_SELECT = "cardiovascularType";
    private static final String EXERCISE_SELECT = "cardiovascularName";
    private static final String SUCCESS_MESSAGE = "successAddingExercise";

    @BeforeClass
    public static void setUp(){
        boolean res = UsersDAO.addUser(USERNAME, PASS, EMAIL);
        System.out.println("***** EJECUTA CREATE EN CARD: " + res);
        driver = new FirefoxDriver();
        driver.get(STARTER_URL);
        try{
            goToStarter(driver);
            login(driver);
            goToAddExercise(driver);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    /*
     * Tests the process of adding a new predetermined
     * exercise to the home page. The method test all
     * the existing muscle groups and the exercises
     * with that muscle groups.
     */
    @Test
    public void addCardiovascularExerciseTest(){
        try{
            selectCardiovascular();
            WebElement addButton = driver.findElement(By.name(ADD_EXERCISE_BUTTON));
            // Finds the select with the type options
            Select select = new Select(driver.findElement(By.id(TYPE_SELECT)));
            Iterator<WebElement> iter1 = select.getOptions().iterator();
            // Skips the first option in the select (which is blank)
            iter1.next();
            // Iterates all the type options
            while (iter1.hasNext()){
                iter1.next().click();
                Thread.sleep(SLEEP_FOR_DISPLAY);
                // Finds the select with the exercise options for the selected type
                select = new Select(driver.findElement(By.id(EXERCISE_SELECT)));
                Iterator<WebElement> iter2 = select.getOptions().iterator();
                // Skips the first option in the select (which is blank)
                iter2.next();
                // Iterates all the exercise options for that type
                while (iter2.hasNext()){
                    // Tries to add a new exercise with the selected options and check if the process has been successful.
                    iter2.next().click();
                    Thread.sleep(SLEEP_FOR_DISPLAY);
                    addButton.click();
                    Thread.sleep(SLEEP_FOR_DISPLAY);
                    assertFalse(driver.findElements(By.name(SUCCESS_MESSAGE)).isEmpty());
                }
            }
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    /*
    * Selects the "Cardiovascular" option on the "addExercise" page.
    */
    private static void selectCardiovascular() throws InterruptedException{
        WebElement element;
        element = driver.findElement(By.name("cardioButton"));
        element.click();
        Thread.sleep(SLEEP_FOR_DISPLAY);
    }

    @AfterClass
    public static void tearDown(){
        driver.close();
        driver.quit();
        System.out.println("EJECUTA DELETE EN CARD: " + UsersDAO.deleteUser(USERNAME));
    }
}