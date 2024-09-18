package pages;


import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;
import java.util.Random;

public class BeymenHomePage {
    WebDriver driver;


    // Locators
    By searchBoxClick = By.xpath("//input[@class='o-header__search--input']");
    By searchBoxInput = By.xpath("//input[@id='o-searchSuggestion__input']");
    By acceptCookiesButton = By.xpath("//button[@id='onetrust-accept-btn-handler']");
    By genderManButton = By.xpath("//button[@id='genderManButton']");
    By notificationDenyButton = By.xpath("//div[@class='dn-slide-buttons horizontal']//button[@class='dn-slide-deny-btn']");
    By productList = By.xpath("(//div[@class='m-productCard__detail'])[1]");
    By availableSizeButton = By.xpath("(//span[@class='m-variation__item'])[1]");
    By criticalStockButton = By.xpath("//span[@class='m-variation__item -criticalStock']");
    By addToCart = By.xpath("//button[@id='addBasket']");
    By goToCartButton = By.xpath("//button[@class='m-notification__button btn']");
    By salePrice = By.xpath("//span[@class='priceBox__salePrice']");
    By quantityArea = By.xpath("//select[@id='quantitySelect0-key-0']");
    By removeButton = By.xpath("//button[@id='removeCartItemBtn0-key-0']");
    By emptyCartMessage = By.xpath("//strong[@class='m-empty__messageTitle' and text()='Sepetinizde Ürün Bulunmamaktadır']");


    public BeymenHomePage(WebDriver driver) {
        this.driver = driver;
    }

    public void goToHomePage() {
        driver.get("https://www.beymen.com");
    }

    public boolean isHomePageDisplayed() {
        return driver.getTitle().contains("Beymen");
    }

    public void acceptCookies() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement acceptCookies = wait.until(ExpectedConditions.visibilityOfElementLocated(acceptCookiesButton));
        acceptCookies.click();
        WebElement genderMan = wait.until(ExpectedConditions.visibilityOfElementLocated(genderManButton));
        genderMan.click();
        WebElement notificationDeny = wait.until(ExpectedConditions.visibilityOfElementLocated(notificationDenyButton));
        notificationDeny.click();
    }

    public void clickSearchArea() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(searchBoxClick));
        searchInput.click();
    }

    public void enterSearchText(String text) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(searchBoxInput));
        searchInput.sendKeys(text);
    }

    public void clearSearchText() {
        driver.findElement(searchBoxInput).clear();
    }

    public void submitSearch() {
        driver.findElement(searchBoxInput).submit();
    }

    public WebElement selectRandomProduct() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(productList));
        List<WebElement> products = driver.findElements(By.xpath("//div[@class='m-productCard__detail']"));
        Random random = new Random();
        return products.get(random.nextInt(products.size()));
    }

    public String getProductInfo(WebElement product) {
        return product.getText();
    }

    public void addProductToCart(WebElement product) {
        WebElement addToCartButton = product.findElement(By.xpath(".//span[@class='m-productCard__desc']"));
        addToCartButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));


        try {
            WebElement availableSize = wait.until(ExpectedConditions.visibilityOfElementLocated(availableSizeButton));
            availableSize.click();
        } catch (TimeoutException e) {
            // availableSizeButton bulunamazsa, criticalStockButton'a tıklanır
            WebElement criticalStock = wait.until(ExpectedConditions.visibilityOfElementLocated(criticalStockButton));
            criticalStock.click();
        }

        WebElement addCart = wait.until(ExpectedConditions.visibilityOfElementLocated(addToCart));
        addCart.click();

        WebElement goToCart = wait.until(ExpectedConditions.visibilityOfElementLocated(goToCartButton));
        goToCart.click();
    }

    public String getCartProductPrice() {
        return driver.findElement(salePrice).getText();
    }

    public void selectQuantity(int quantityValue) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement quantity = wait.until(ExpectedConditions.visibilityOfElementLocated(quantityArea));

        // Her zaman 2 veya istenilen adet kadar stok olmayabilir ve //option[@value='2'] diye bir element olmayabilir
        // Bu soruna çözüm olarak alternativeQuantityOption seçeneği oluşturulur
        By quantityOption = By.xpath("//option[@value='" + quantityValue + "']");
        By alternativeQuantityOption = By.xpath("//option[@value='1']");

        WebElement quantityToSelect = null;

        try {
            // İlk olarak, belirtilen değeri içeren seçenek aranır
            quantityToSelect = wait.until(ExpectedConditions.visibilityOfElementLocated(quantityOption));
        } catch (TimeoutException e) {
            // Eğer belirtilen değer bulunamazsa, alternatif seçenek seçilir
            System.out.println("Desired quantity option not found, selecting alternative.");
            quantityToSelect = wait.until(ExpectedConditions.visibilityOfElementLocated(alternativeQuantityOption));
        }

        quantity.click();
        quantityToSelect.click();
    }

    public boolean isProductQuantityCorrect(int quantity) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement quantityInput = wait.until(ExpectedConditions.visibilityOfElementLocated(quantityArea));
        return Integer.parseInt(quantityInput.getAttribute("value")) == quantity;
    }

    public boolean isQuantityAvailable(int quantityValue) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            By quantityOption = By.xpath("//option[@value='" + quantityValue + "']");
            wait.until(ExpectedConditions.presenceOfElementLocated(quantityOption));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void removeProductFromCart(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement remove = wait.until(ExpectedConditions.visibilityOfElementLocated(removeButton));
        remove.click();
    }

    public boolean isCartEmpty() {
        return driver.findElement(emptyCartMessage).isDisplayed();
    }
}