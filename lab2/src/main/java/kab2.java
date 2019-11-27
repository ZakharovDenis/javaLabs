import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.text.SimpleDateFormat;
import java.util.*;

public class kab2  {
    private static double separateSalary (String insalary, String currency, double multiplyCoeff){
        List<Double> finalResult = new ArrayList<Double>();
        if (insalary.contains("-")){
            String[] two = insalary.split("-");
            two[0]=two[0].replace(" ","");
            two[1] = two[1].replace(currency,"");
            two[1] = two[1].replace(" ","");
            finalResult.add(Integer.parseInt(two[0])*multiplyCoeff);
            finalResult.add(Integer.parseInt(two[1])*multiplyCoeff);
            return (finalResult.get(0)+finalResult.get(1))/2;
        } else {
            insalary = insalary.replace("от", "");
            insalary = insalary.replace("до", "");
            insalary = insalary.replace(" ", "");
            insalary = insalary.replace(currency, "");
            if (insalary.equals("")) {
                return (Integer.parseInt("0")*multiplyCoeff);
            } else {
                return (Integer.parseInt(insalary)*multiplyCoeff);
            }
        }
    }



    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "/home/denis/Documents/учеба/java/lab2/chromedriver_linux64/chromedriver");
        WebDriver driver = new ChromeDriver();

        String homeurl = "https://hh.ru/";
        driver.get(homeurl);
        List<Map<String, String>> database = new ArrayList<Map<String, String>>();

        double allSalaries = 0;
        int salarCount = 0;
        List<Double> salariesMass = new ArrayList<Double>();

        WebElement element = driver.findElement(By.className("bloko-input"));

        element.sendKeys("Java developer");
        double eur = 70.50;
        double usd = 64.01;
        double rub = 1;

        element.submit();
        for (int i =0; i<10;i++) {
            SimpleDateFormat formatter= new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
            Date date = new Date(System.currentTimeMillis());

            System.out.println("Page" + i);
            List<WebElement> salaries = driver.findElements(By.className("vacancy-serp-item__row_header"));
            for (WebElement el : salaries) {
                Map<String, String> dbline = new HashMap<String,String>();

                String vacLink = el.findElement(By.className("HH-LinkModifier")).getAttribute("href");
                String vacsal = el.findElement(By.className("vacancy-serp-item__sidebar")).getText();
                dbline.put("Link",vacLink);
                dbline.put("Date",formatter.format(date));

                if (vacsal.contains("EUR")){
                    dbline.put("Salary", String.valueOf(separateSalary(vacsal,"EUR",eur)));

                } else if (vacsal.contains("USD")){
                    dbline.put("Salary", String.valueOf(separateSalary(vacsal,"USD",usd)));

                } else {
                    dbline.put("Salary", String.valueOf(separateSalary(vacsal,"руб.",rub)));
                }

                database.add(dbline);
                if (!dbline.get("Salary").equals("0.0")){
                    salarCount+=1;
                    allSalaries += Double.parseDouble(dbline.get("Salary"));
                    salariesMass.add(Double.parseDouble(dbline.get("Salary")));
                }
                if (!dbline.get("Salary").equals("0.0")){
                    System.out.println(dbline);
                }
            }
            Collections.sort(salariesMass);
            System.out.println("Current middle: " +allSalaries/salarCount);
            System.out.println("Current median: " +salariesMass.get(salariesMass.size()/2));
            String nextpage = driver.findElement(By.cssSelector("a[data-qa='pager-next']")).getAttribute("href");
            driver.get(nextpage);
        }
        driver.quit();
    }
}