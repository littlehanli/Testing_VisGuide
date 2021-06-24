package tests;

import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.*;
import environment.EnvironmentManager;
import environment.RunEnvironment;

import org.junit.Before;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class LogicTest {
    ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
        public Boolean apply(WebDriver driver) {
            return ((JavascriptExecutor)driver).executeScript("return document.readyState").equals("complete");
        }
    };

    public static WebDriver driver;
    public static WebDriverWait wait;

    // Embedding javascript to access the canvas element
    public static String clickOnDataPoint =
                    "function clickElement(chart, datasetIndex, index) {\n" +
                    "    var node = chart.canvas;\n" +
                    "    var rect = node.getBoundingClientRect();\n" +
                    "    var el = chart.getDatasetMeta(datasetIndex).data[index];\n" +
                    "    var point = el.getCenterPoint();\n" +
                    "    var event = new MouseEvent('click', {\n" +
                    "        clientX: rect.left + point.x,\n" +
                    "        clientY: rect.top + point.y,\n" +
                    "        cancelable: true,\n" +
                    "        bubbles: true,\n" +
                    "        view: window\n" +
                    "    });\n" +
                    "    node.dispatchEvent(event);\n" +
                    "}";

    ////////////// Function for interacting with each element ///////////////////
    public void selectDataset(String dataset_name){
        WebElement datasets_dropDown = driver.findElement(By.id("dataset_dropdown"));
        datasets_dropDown.click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[contains(text(), '" + dataset_name + "')]")));
        datasets_dropDown.findElement(By.xpath("//*[contains(text(), '" + dataset_name + "')]")).click();  // change dataset name

        System.out.println("Select Dataset: " + dataset_name);
    }

    public void selectModel(String model_name){
        WebElement models_dropDown = driver.findElement(By.id("model_dropdown"));
        models_dropDown.click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[contains(text(), '" + model_name + "')]")));
        models_dropDown.findElement(By.xpath("//*[contains(text(), '" + model_name + "')]")).click();  // change model name

        System.out.println("Select Model: " + model_name);
    }

    public String selectFirstAxis(String x_name, String y_name){
        driver.findElement(By.id("add_a_chart_btn")).click();
        if(!x_name.equals("")) {
            WebElement xAxis_dropDown = driver.findElement(By.id("x_axis"));
            xAxis_dropDown.click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[contains(text(), '" + x_name + "')]")));
            xAxis_dropDown.findElement(By.xpath("//*[contains(text(), '" + x_name + "')]")).click();  // change x axis
        }
        if(!y_name.equals("")) {
            WebElement yAxis_dropDown = driver.findElement(By.id("y_axis"));
            yAxis_dropDown.click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[contains(text(), '" + y_name + "')]")));
            yAxis_dropDown.findElement(By.xpath("//*[contains(text(), '" + y_name + "')]")).click();  // change y axis
        }
        driver.findElement(By.id("add_a_chart_confirm_btn")).click();

        System.out.println("Select Axis: " + x_name + ", " + y_name);

        WebElement seq_view = driver.findElement(By.id("seq_view_1"));
        WebElement chart = seq_view.findElement(By.id("a_chart_1-clone"));
        String this_title = chart.findElement(By.className("title")).findElement(By.tagName("div")).getText();

        return this_title;
    }

    public void selectDataPoint(String chart_id, String datapoint_idx){

        //WebElement canvas = driver.findElement(By.xpath("//*[@id='seq_chart_1']//canvas"));
        WebElement canvas = wait.until(ExpectedConditions.elementToBeClickable(By.id("seq_chart_1")));
        //System.out.println(canvas.getSize());

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(clickOnDataPoint + "c = chart_objects['" + chart_id + "'];\n" +  // change chart_id
                "console.log(c); \n clickElement(c, 0, " + datapoint_idx + ");");  // change data point index

        System.out.println("Click on data point index: " + datapoint_idx);
    }

    public String addRecommendedChart(String rec_list, String rec_chart_id, String chart_id){

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(rec_chart_id)));  // change drill_down_list or comparison_list
        WebElement recommend_list = driver.findElement(By.id(rec_list));
        WebElement rec_canvas = recommend_list.findElement(By.cssSelector("canvas[id='" + rec_chart_id + "']"));  // change chart recommended order (see in advance)
        WebElement content = rec_canvas.findElement(By.xpath("./../..")).findElement(By.className("content"));
        WebElement add_to_sequence = content.findElement(By.cssSelector("button[data-content='Add to Sequence view']"));
        add_to_sequence.click();

        System.out.println("Click on recommended chart index: " + rec_chart_id);

        WebElement seq_view = driver.findElement(By.id("seq_view_1"));
        WebElement chart = seq_view.findElement(By.id(chart_id + "-clone"));

        String this_title = chart.findElement(By.className("title")).findElement(By.tagName("div")).getText();

        return this_title;
    }
    ///////////////////////////////////////////////////////////

    @Before
    public void startBrowser() {
        EnvironmentManager.initWebDriver();
        System.out.println("Before each test case");

        driver = RunEnvironment.getWebDriver();
        driver.get("file:///D:/_NCTU_/1092/Software%20Testing/FinalProject/Test-VisGuide/client/index.html");
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, 10);  // Wait for 10 seconds at most
        wait.until(pageLoadCondition);
    }

    // a. Select a dataset (Transaction)
    // b. Select X (branch_name)
    // c. Select Y (points_gained)
    // d. Click the 1st value data point (branch_name: center)
    // e. Add the 1st drill-down chart (x: category, y: points_gained)
    // f. Add the 1st comparison chart (percentage)

    // TestCase1 (predicate: T) - a = t, b = t, c = t, d = t, e = t, f = t
    @Test
    public void TestCase_1() {
        // Ground truth
        ArrayList<String> titles = new ArrayList<String>();
        titles.add("PointsGained in overall Branch"); titles.add("PointsGained in Center (Category)"); titles.add("Percentage in overall Branch");

        // Assert Chart Titles
        ArrayList<String> chart_titles = new ArrayList<String>();

        // Select a dataset (Transaction) = T
        selectDataset("Transaction");

        // Select X (branch_name) = T, Select Y (points_gained) = T
        String title1 = selectFirstAxis("branch_name", "points_gained");
        chart_titles.add(title1);

        // Click the 1st value data point = T
        selectDataPoint("a_chart_1", "0");

        // Add the 1st drill-down chart = T
        String title2 = addRecommendedChart("drill_down_list", "drill_canvas_0", "a_chart_2");
        chart_titles.add(title2);

        // Add the 1st drill-down chart = T
        String title3 = addRecommendedChart("comparison_list", "comparison_canvas_0", "a_chart_3");
        chart_titles.add(title3);

        // assert not equals
        assertEquals(titles, chart_titles);
    }

    // TestCase2 (major clause: a, predicate: F) - a = F, b = t, c = t, d = t, e = t, f = t
    @Test(expected = TimeoutException.class)
    public void TestCase_2() {
        // Select a dataset (Transaction) = F

        // Select X (branch_name) = T, Select Y (points_gained) = T
        String title1 = selectFirstAxis("branch_name", "points_gained");

        // Click the 1st value data point = T
        selectDataPoint("a_chart_1", "0");

        // Add the 1st drill-down chart = T
        String title2 = addRecommendedChart("drill_down_list", "drill_canvas_0", "a_chart_2");

        // Add the 1st drill-down chart = T
        String title3 = addRecommendedChart("comparison_list", "comparison_canvas_0", "a_chart_3");
    }

    // TestCase3 (major clause: b, predicate: F) - a = t, b = F, c = t, d = t, e = t, f = t
    @Test
    public void TestCase_3() {
        // Ground truth
        ArrayList<String> titles = new ArrayList<String>();
        titles.add("PointsGained in overall Branch"); titles.add("PointsGained in Center (Category)"); titles.add("Percentage in overall Branch");

        // Assert Chart Titles
        ArrayList<String> chart_titles = new ArrayList<String>();

        // Select a dataset (Transaction) = T
        selectDataset("Transaction");

        // Select X (branch_name) = F, Select Y (points_gained) = T
        String title1 = selectFirstAxis("", "points_gained");
        chart_titles.add(title1);

        // Click the 1st value data point = T
        selectDataPoint("a_chart_1", "0");

        // Add the 1st drill-down chart = T
        String title2 = addRecommendedChart("drill_down_list", "drill_canvas_0", "a_chart_2");
        chart_titles.add(title2);

        // Add the 1st drill-down chart = T
        String title3 = addRecommendedChart("comparison_list", "comparison_canvas_0", "a_chart_3");
        chart_titles.add(title3);

        // assert not equals
        assertNotEquals(titles, chart_titles);
    }

    // TestCase4 (major clause: c, predicate: F) - a = t, b = t, c = F, d = t, e = t, f = t
    @Test
    public void TestCase_4() {
        // Ground truth
        ArrayList<String> titles = new ArrayList<String>();
        titles.add("PointsGained in overall Branch"); titles.add("PointsGained in Center (Category)"); titles.add("Percentage in overall Branch");

        // Assert Chart Titles
        ArrayList<String> chart_titles = new ArrayList<String>();

        // Select a dataset (Transaction) = T
        selectDataset("Transaction");

        // Select X (branch_name) = T, Select Y (points_gained) = F
        String title1 = selectFirstAxis("branch_name", "");
        chart_titles.add(title1);

        // Click the 1st value data point = T
        selectDataPoint("a_chart_1", "0");

        // Add the 1st drill-down chart = T
        String title2 = addRecommendedChart("drill_down_list", "drill_canvas_0", "a_chart_2");
        chart_titles.add(title2);

        // Add the 1st drill-down chart = T
        String title3 = addRecommendedChart("comparison_list", "comparison_canvas_0", "a_chart_3");
        chart_titles.add(title3);

        // assert not equals
        assertNotEquals(titles, chart_titles);
    }

    // TestCase5 (major clause: d, predicate: F) - a = t, b = t, c = t, d = F, e = t, f = t
    @Test(expected = TimeoutException.class)
    public void TestCase_5() {
        // Assert Chart Titles
        ArrayList<String> chart_titles = new ArrayList<String>();

        // Select a dataset (Transaction) = T
        selectDataset("Transaction");

        // Select X (branch_name) = T, Select Y (points_gained) = T
        String title1 = selectFirstAxis("branch_name", "points_gained");

        // Click the 1st value data point = F

        // Add the 1st drill-down chart = T
        String title2 = addRecommendedChart("drill_down_list", "drill_canvas_0", "a_chart_2");

        // Add the 1st drill-down chart = T
        String title3 = addRecommendedChart("comparison_list", "comparison_canvas_0", "a_chart_3");
    }

    // TestCase6 (major clause: e, predicate: F) - a = t, b = t, c = t, d = t, e = F, f = t
    @Test
    public void TestCase_6() {
        // Ground truth
        ArrayList<String> titles = new ArrayList<String>();
        titles.add("PointsGained in overall Branch"); titles.add("PointsGained in Center (Category)"); titles.add("Percentage in overall Branch");

        // Assert Chart Titles
        ArrayList<String> chart_titles = new ArrayList<String>();

        // Select a dataset (Transaction) = T
        selectDataset("Transaction");

        // Select X (branch_name) = T, Select Y (points_gained) = F
        String title1 = selectFirstAxis("branch_name", "points_gained");
        chart_titles.add(title1);

        // Click the 1st value data point = T
        selectDataPoint("a_chart_1", "0");

        // Add the 1st drill-down chart = F

        // Add the 1st drill-down chart = T
        String title3 = addRecommendedChart("comparison_list", "comparison_canvas_0", "a_chart_2");
        chart_titles.add(title3);

        // Assert not equals
        assertNotEquals(titles, chart_titles);
    }

    // TestCase7 (major clause: f, predicate: F) - a = t, b = t, c = t, d = t, e = t, f = F
    @Test
    public void TestCase_7() {
        // Ground truth
        ArrayList<String> titles = new ArrayList<String>();
        titles.add("PointsGained in overall Branch"); titles.add("PointsGained in Center (Category)"); titles.add("Percentage in overall Branch");

        // Assert Chart Titles
        ArrayList<String> chart_titles = new ArrayList<String>();

        // Select a dataset (Transaction) = T
        selectDataset("Transaction");

        // Select X (branch_name) = T, Select Y (points_gained) = T
        String title1 = selectFirstAxis("branch_name", "points_gained");
        chart_titles.add(title1);

        // Click the 1st value data point = T
        selectDataPoint("a_chart_1", "0");

        // Add the 1st drill-down chart = T
        String title2 = addRecommendedChart("drill_down_list", "drill_canvas_0", "a_chart_2");
        chart_titles.add(title2);

        // Add the 1st drill-down chart = F

        // assert not equals
        assertNotEquals(titles, chart_titles);
    }

    @After
    public void tearDown() {
        //EnvironmentManager.shutDownDriver();
        System.out.println("Finish Test");
    }


}
