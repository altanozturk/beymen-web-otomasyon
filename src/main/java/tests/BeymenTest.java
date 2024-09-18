package tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.BeymenHomePage;
import utils.Excel;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class BeymenTest {
    WebDriver driver;
    BeymenHomePage beymenHomePage;
    String excelPath = "src/main/resources/beymen.xlsx";
    Logger logger = LogManager.getLogger(BeymenHomePage.class);

    @Before
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*"); // 403 hatasına çözüm
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\ASUS\\Desktop\\chromedriver-win64\\chromedriver.exe");
        driver = new ChromeDriver(options);
        beymenHomePage = new BeymenHomePage(driver);
    }


    @Test
    public void testSearchFunctionality() throws IOException, InterruptedException {

        // 1. Ana sayfanın açılması ve sayfanın yüklendiğinin kontrolü
        beymenHomePage.goToHomePage();
        Thread.sleep(5000);
        Assert.assertTrue(beymenHomePage.isHomePageDisplayed());

        // 2. Çerezleri kabul et, cinsiyet seçimi yap ve latest news pop-up'ı reddet
        beymenHomePage.acceptCookies();

        // 3. Arama alanına tıklanır
        beymenHomePage.clickSearchArea();

        // 4. Excelden keyword çekilerek arama alanına gönderilir
        String shortKeyword = Excel.getSearchTerm(excelPath, 0, 0);
        beymenHomePage.enterSearchText(shortKeyword);
        Thread.sleep(2000);

        // 5. Arama alanı temizlenir
        beymenHomePage.clearSearchText();

        // 6. Excelden ikinci kelime alınır ve arama alanına gönderilir
        String gomlekKeyword = Excel.getSearchTerm(excelPath, 1, 0);
        beymenHomePage.enterSearchText(gomlekKeyword);

        // 7. Enter tuşuna basılır
        beymenHomePage.submitSearch();

        // 8. Rastgele bir ürün seçilir
        WebElement product = beymenHomePage.selectRandomProduct();

        // 9. Ürün bilgileri ve tutar bilgisi txt dosyasına yazılır
        String productInfo = beymenHomePage.getProductInfo(product);
        writeProductInfoToFile(productInfo);

        // 10. Ürün sepete eklenir
        beymenHomePage.addProductToCart(product);
        Thread.sleep(4000);

        // 11. Sepetteki ürün fiyatı ile ürün sayfasındaki fiyat karşılaştırılır
        String productPriceInCart = beymenHomePage.getCartProductPrice();

        String formattedProductPriceInCart = removeTL(productPriceInCart);  // Sepetteki fiyatdan TL, nokta ve virgülü çıkartır
        String formattedProductInfoPrice = removeTL(productInfo); //  Ürün sayfasındaki fiyatdan TL, nokta ve virgülü çıkartır

        double updatedProductPriceInCart = Double.parseDouble(formattedProductPriceInCart); // Tam sayı elde edebilmek için önce Stringi double'a daha sonra tekrar Stringe çevir
        String updateBackProductPriceInCart = formatPrice(updatedProductPriceInCart);
        Assert.assertTrue(formattedProductInfoPrice.contains(updateBackProductPriceInCart));

        // 12. Ürün adedi 2 olarak güncellenir ve doğrulanır
        beymenHomePage.selectQuantity(2);

        // Her zaman 2 veya istenilen adet kadar stok olmayabilir, bu gibi durumlarda alternatif çözüm olarak default olarak adet 1 seçilir
        // Adet 1 seçildiğinde, adet 2 seçildi mi doğrulamasının fail almaması için bu kontrol sadece stok varsa çalışır

        if (beymenHomePage.isQuantityAvailable(2)) {
            Assert.assertTrue(beymenHomePage.isProductQuantityCorrect(2));
        } else {
            System.out.println("Quantity 2 not available. Skipping the assertion.");
        }
        Thread.sleep(2000);

        // 13. Ürün sepetten silinir ve sepetin boş olduğu kontrol edilir
        beymenHomePage.removeProductFromCart();
        Thread.sleep(4000);
        Assert.assertTrue(beymenHomePage.isCartEmpty());
        logger.info("test tamamlandı");
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    private void writeProductInfoToFile(String productInfo) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/output.txt"))) {
            writer.write(productInfo);
        }
    }

    public String removeTL(String price) {
        return price.replace(" TL", "").replace(".", "").replace(",", ".").trim();

    }

    public static String formatPrice(double price) {
        DecimalFormat df = new DecimalFormat("###");
        return df.format(price);
    }
}