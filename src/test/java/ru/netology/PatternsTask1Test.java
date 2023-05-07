package ru.netology;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Selenide.*;
import static java.lang.Integer.parseInt;
import static ru.netology.DataGenerator.*;


class PatternsTask1Test {
    String locale = "ru";
    String errorMessageEmptyField = "обязательно для заполнения";
    String errorMessageIncorrectCity = "Доставка в выбранный город недоступна";
    String errorMessageIncorrectName = "Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы";
    String errorMessageIncorrectDate = "Заказ на выбранную дату невозможен";
    String errorMessageIncorrectPhone = "Телефон указан неверно";
    String successMessage = "Встреча успешно запланирована на ";

    SelenideElement cityInput = $("span[data-test-id='city'] input");
    SelenideElement dateInput = $("span[data-test-id='date'] input");
    SelenideElement dateInputIcon = $x("//span[@class='input__icon']/button");
    SelenideElement nameInput = $("input[name='name']");
    SelenideElement phoneInput = $("input[name='phone']");
    SelenideElement agreementCheckbox = $("label[data-test-id='agreement']");
    SelenideElement okButton = $x("//span[@class='button__text']/../..");
    SelenideElement successPopUp =
            $x("//div[@data-test-id='success-notification']//div[@class='notification__content']");
    SelenideElement replanningPopUp =
            $("//div[@data-test-id='replan-notification']//span[@class='button__text']");

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }
/*
    @Test
    public void test() {
        System.out.println("generateFirstName('ru'): " + generateFirstName(locale));
        System.out.println("generateLastName('ru'): " + generateLastName(locale));
        System.out.println("generateFirstAndLastNames('ru'): " + generateFirstAndLastNames(locale));
        System.out.println("generateFullName('ru'): " + generateFullName(locale));
        System.out.println("generatePhone('ru', '+' ): " + generatePhone(locale));

        int rand = randomPeriod(4, 365);
        System.out.println("rand- " + rand);
        System.out.println("generateDate(" + rand + ", ddMMyyyy): " + generateDate(rand,"ddMMyyyy"));
        System.out.println("generateDate(" + rand + ", d): " + generateDate(rand,"d"));
        System.out.println("generateDate(" + rand + ", dd): " + generateDate(rand,"dd"));
        System.out.println("generateDate(" + rand + ", MM): " + generateDate(rand,"MM"));
        System.out.println("generateDate(" + rand + ", yyyy): " + generateDate(rand,"yyyy"));
        System.out.println("generateDate(" + rand + ", MMM): " + generateDate(rand,"MMM"));

        System.out.println("generateCity(): " + generateCity());
        System.out.println("generateCity('ru'): " + generateCity(locale));
    }
*/
    @Test
    public void testValidData() {
        String city = generateCity();
        String deliveryDate = generateDate(randomPeriod(4, 365),"dd.MM.yyyy");
        String personFullName = generateFirstAndLastNames(locale);
        String phone = generatePhone(locale);

        cityInput.setValue(city);
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(deliveryDate);
        nameInput.setValue(personFullName);
        phoneInput.setValue(phone);
        agreementCheckbox.click();
        okButton.click();

        successPopUp
                .shouldHave(Condition.text(successMessage + deliveryDate), Duration.ofSeconds(15))
                .shouldBe(Condition.visible);
    }

    @Test
    public void testValidDataListChoice() {
        String city = generateCity();
        String cityShot = city.substring(0, 2);
        String personFullName = generateFirstAndLastNames(locale);
        String phone = generatePhone(locale);

        int rand = randomPeriod(4, 365);
        String deliveryDate = generateDate(rand, "dd.dMM.yyyy");
        String deliveryDay = generateDate(rand,"d");
        int deliveryMonthDigit = parseInt(generateDate(rand,"MM"));
        int deliveryYear = parseInt(generateDate(rand,"yyyy"));

        int currentMonthDigit = parseInt(generateDate(0,"MM"));

        cityInput.setValue(cityShot);
        $x("//span[text()='" + city + "']/..").click();

        dateInputIcon.click();

        String[] monthYear = $("div.calendar__name").text().split(" ", 2);
        int calendarYear = parseInt(monthYear[1]);

        while (calendarYear < deliveryYear) {
            $x("//div[@class='calendar__arrow calendar__arrow_direction_right calendar__arrow_double']")
                    .click();
            calendarYear++;
        }
        while (currentMonthDigit < deliveryMonthDigit) {
            $x("//div[@class='calendar__arrow calendar__arrow_direction_right']").click();
            currentMonthDigit++;
        }
        while (currentMonthDigit > deliveryMonthDigit) {
            $x("//div[@class='calendar__arrow calendar__arrow_direction_left']").click();
            currentMonthDigit--;
        }
        $x("//td[text()='" + deliveryDay + "']").click();

        nameInput.setValue(personFullName);
        phoneInput.setValue(phone);
        agreementCheckbox.click();
        okButton.click();

        successPopUp
                .shouldHave(Condition.text(successMessage + deliveryDate), Duration.ofSeconds(15))
                .shouldBe(Condition.visible);
    }

    @Test
    public void testReplanningDateForFuture() {
        String city = generateCity();
        int rand = randomPeriod(4, 100);
        String deliveryDate = generateDate(rand,"dd.MM.yyyy");
        String personFullName = generateFirstAndLastNames(locale);
        String phone = generatePhone(locale);

        cityInput.setValue(city);
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(deliveryDate);
        nameInput.setValue(personFullName);
        phoneInput.setValue(phone);
        agreementCheckbox.click();
        okButton.click();

        successPopUp
                .shouldHave(Condition.text(successMessage + deliveryDate), Duration.ofSeconds(15))
                .shouldBe(Condition.visible);

        int newRand = rand + randomPeriod(1, 14);
        String newDeliveryDate = generateDate(newRand,"dd.MM.yyyy");
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(newDeliveryDate);

        okButton.click();

        replanningPopUp
                .shouldHave(Condition.text("Перепланировать"), Duration.ofSeconds(15))
                .shouldBe(Condition.visible);

        okButton.click();

        successPopUp
                .shouldHave(Condition.text(successMessage + newDeliveryDate), Duration.ofSeconds(15))
                .shouldBe(Condition.visible);
    }

    @Test
    public void testInvalidCity() {
        String city = "Нью Йорк";
        String deliveryDate = generateDate(randomPeriod(4, 365),"dd.MM.yyyy");
        String personFullName = generateFirstAndLastNames(locale);
        String phone = generatePhone(locale);

        cityInput.setValue(city);
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(deliveryDate);
        nameInput.setValue(personFullName);
        phoneInput.setValue(phone);
        agreementCheckbox.click();
        okButton.click();

        $x("//span[@data-test-id='city']//span[contains(text(), '" + errorMessageIncorrectCity + "')]")
                .should(appear);
    }

    @Test
    public void testEmptyCityField() {
        String city = "";
        String deliveryDate = generateDate(randomPeriod(4, 365),"dd.MM.yyyy");
        String personFullName = generateFirstAndLastNames(locale);
        String phone = generatePhone(locale);

        cityInput.setValue(city);
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(deliveryDate);
        nameInput.setValue(personFullName);
        phoneInput.setValue(phone);
        agreementCheckbox.click();
        okButton.click();

        $x("//span[@data-test-id='city']//span[contains(text(), '" + errorMessageEmptyField + "')]")
                .should(appear);
    }

    @Test
    public void testValidCityInCapslock() {
        String city = generateCity().toUpperCase();
        String deliveryDate = generateDate(randomPeriod(4, 365),"dd.MM.yyyy");
        String personFullName = generateFirstAndLastNames(locale);
        String phone = generatePhone(locale);

        cityInput.setValue(city);
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(deliveryDate);
        nameInput.setValue(personFullName);
        phoneInput.setValue(phone);
        agreementCheckbox.click();
        okButton.click();

        successPopUp
                .shouldHave(Condition.text(successMessage + deliveryDate), Duration.ofSeconds(15))
                .shouldBe(Condition.visible);
    }

    @Test
    public void testLatinSymbolInCityField() {
        String city = "Moscow";
        String deliveryDate = generateDate(randomPeriod(4, 365),"dd.MM.yyyy");
        String personFullName = generateFirstAndLastNames(locale);
        String phone = generatePhone(locale);

        cityInput.setValue(city);
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(deliveryDate);
        nameInput.setValue(personFullName);
        phoneInput.setValue(phone);
        agreementCheckbox.click();
        okButton.click();

        $x("//span[@data-test-id='city']//span[contains(text(), '" + errorMessageIncorrectCity + "')]")
                .should(appear);
    }

    @Test
    public void testSpecSymbolInCityField() {
        String city = generateCity() + randomSpecSymbol();
        String deliveryDate = generateDate(randomPeriod(4, 365),"dd.MM.yyyy");
        String personFullName = generateFirstAndLastNames(locale);
        String phone = generatePhone(locale);

        cityInput.setValue(city);
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(deliveryDate);
        nameInput.setValue(personFullName);
        phoneInput.setValue(phone);
        agreementCheckbox.click();
        okButton.click();

        $x("//span[@data-test-id='city']//span[contains(text(), '" + errorMessageIncorrectCity + "')]")
                .should(appear);
    }

    @Test
    public void testCurrentDateInDateField() {
        String city = generateCity();
        String deliveryDate = generateDate(0,"dd.MM.yyyy");
        String personFullName = generateFirstAndLastNames(locale);
        String phone = generatePhone(locale);

        cityInput.setValue(city);
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(deliveryDate);
        nameInput.setValue(personFullName);
        phoneInput.setValue(phone);
        agreementCheckbox.click();
        okButton.click();

        $x("//span[@data-test-id='date']//span[contains(text(), '" + errorMessageIncorrectDate + "')]")
                .should(appear);
    }

    @Test
    public void testEmptyDateField() {
        String city = generateCity();
        String deliveryDate = "";
        String personFullName = generateFirstAndLastNames(locale);
        String phone = generatePhone(locale);

        cityInput.setValue(city);
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(deliveryDate);
        nameInput.setValue(personFullName);
        phoneInput.setValue(phone);
        agreementCheckbox.click();
        okButton.click();

        $x("//span[@data-test-id='date']//span[contains(text(), 'Неверно введена')]").should(appear);
    }

    @Test
    public void testPastDateInDateField() {
        String city = generateCity();
        String deliveryDate = generateDate(-1,"dd.MM.yyyy");
        String personFullName = generateFirstAndLastNames(locale);
        String phone = generatePhone(locale);

        cityInput.setValue(city);
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(deliveryDate);
        nameInput.setValue(personFullName);
        phoneInput.setValue(phone);
        agreementCheckbox.click();
        okButton.click();

        $x("//span[@data-test-id='date']//span[contains(text(), '" + errorMessageIncorrectDate + "')]")
                .should(appear);
    }

    @Test
    public void testCurrentPlusTwoDaysInDateField() {
        String city = generateCity();
        String deliveryDate = generateDate(2,"dd.MM.yyyy");
        String personFullName = generateFirstAndLastNames(locale);
        String phone = generatePhone(locale);

        cityInput.setValue(city);
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(deliveryDate);
        nameInput.setValue(personFullName);
        phoneInput.setValue(phone);
        agreementCheckbox.click();
        okButton.click();

        $x("//span[@data-test-id='date']//span[contains(text(), '" + errorMessageIncorrectDate + "')]")
                .should(appear);
    }

    @Test
    public void testCurrentPlusThreeDaysInDateField() {
        String city = generateCity();
        String deliveryDate = generateDate(3,"dd.MM.yyyy");
        String personFullName = generateFirstAndLastNames(locale);
        String phone = generatePhone(locale);

        cityInput.setValue(city);
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(deliveryDate);
        nameInput.setValue(personFullName);
        phoneInput.setValue(phone);
        agreementCheckbox.click();
        okButton.click();

        successPopUp
                .shouldHave(Condition.text(successMessage + deliveryDate), Duration.ofSeconds(15))
                .shouldBe(Condition.visible);
    }

    @Test
    public void testLatinSymbolInNameField() {
        String city = generateCity();
        String deliveryDate = generateDate(randomPeriod(4, 365),"dd.MM.yyyy");
        //String personFullName = "Petrov Petr";
        String personFullName = generateFirstAndLastNames("en");
        String phone = generatePhone(locale);

        cityInput.setValue(city);
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(deliveryDate);
        nameInput.setValue(personFullName);
        phoneInput.setValue(phone);
        agreementCheckbox.click();
        okButton.click();

        $x("//span[@data-test-id='name']//span[contains(text(), '" + errorMessageIncorrectName + "')]")
                .should(appear);
    }

    @Test
    public void testDigitSymbolInNameField() {
        String city = generateCity();
        String deliveryDate = generateDate(randomPeriod(4, 365),"dd.MM.yyyy");
        String personFullName = generateFirstAndLastNames(locale) + "2";
        String phone = generatePhone(locale);

        cityInput.setValue(city);
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(deliveryDate);
        nameInput.setValue(personFullName);
        phoneInput.setValue(phone);
        agreementCheckbox.click();
        okButton.click();

        $x("//span[@data-test-id='name']//span[contains(text(), '" + errorMessageIncorrectName + "')]")
                .should(appear);
    }

    @Test
    public void testSpecSymbolInNameField() {
        String city = generateCity();
        String deliveryDate = generateDate(randomPeriod(4, 365),"dd.MM.yyyy");
        String personFullName = generateFirstName(locale) + randomSpecSymbol() + generateLastName(locale);
        String phone = generatePhone(locale);

        cityInput.setValue(city);
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(deliveryDate);
        nameInput.setValue(personFullName);
        phoneInput.setValue(phone);
        agreementCheckbox.click();
        okButton.click();

        $x("//span[@data-test-id='name']//span[contains(text(), '" + errorMessageIncorrectName + "')]")
                .should(appear);
    }

    @Test
    public void testEmptyNameField() {
        String city = generateCity();
        String deliveryDate = generateDate(randomPeriod(4, 365),"dd.MM.yyyy");
        String personFullName = "";
        String phone = generatePhone(locale);

        cityInput.setValue(city);
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(deliveryDate);
        nameInput.setValue(personFullName);
        phoneInput.setValue(phone);
        agreementCheckbox.click();
        okButton.click();

        $x("//span[@data-test-id='name']//span[contains(text(), '" + errorMessageEmptyField + "')]")
                .should(appear);
    }

    @Test
    public void testEmptyPhoneField() {
        String city = generateCity();
        String deliveryDate = generateDate(randomPeriod(4, 365),"dd.MM.yyyy");
        String personFullName = generateFirstAndLastNames(locale);
        String phone = "";

        cityInput.setValue(city);
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(deliveryDate);
        nameInput.setValue(personFullName);
        phoneInput.setValue(phone);
        agreementCheckbox.click();
        okButton.click();

        $x("//span[@data-test-id='phone']//span[contains(text(), '" + errorMessageEmptyField + "')]")
                .should(appear);
    }

    @Test
    public void testInputWithoutPlusInPhoneField() {
        String city = generateCity();
        String deliveryDate = generateDate(randomPeriod(4, 365),"dd.MM.yyyy");
        String personFullName = generateFirstAndLastNames(locale);
        String phone = generatePhone(locale).replaceAll("\\+", "");

        cityInput.setValue(city);
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(deliveryDate);
        nameInput.setValue(personFullName);
        phoneInput.setValue(phone);
        agreementCheckbox.click();
        okButton.click();

        $x("//span[@data-test-id='phone']//span[contains(text(), '" + errorMessageIncorrectPhone + "')]")
                .should(appear);
    }

    @Test
    public void testInputLess11DigitInPhoneField() {
        String city = generateCity();
        String deliveryDate = generateDate(randomPeriod(4, 365),"dd.MM.yyyy");
        String personFullName = generateFirstAndLastNames(locale);
        String phone = generatePhone(locale).substring(0, 11);

        cityInput.setValue(city);
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(deliveryDate);
        nameInput.setValue(personFullName);
        phoneInput.setValue(phone);
        agreementCheckbox.click();
        okButton.click();

        $x("//span[@data-test-id='phone']//span[contains(text(), '" + errorMessageIncorrectPhone + "')]")
                .should(appear);
    }

    @Test
    public void testInputMore11DigitInPhoneField() {
        String city = generateCity();
        String deliveryDate = generateDate(randomPeriod(4, 365),"dd.MM.yyyy");
        String personFullName = generateFirstAndLastNames(locale);
        String phone = generatePhone(locale) + randomPeriod(0, 9);

        cityInput.setValue(city);
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(deliveryDate);
        nameInput.setValue(personFullName);
        phoneInput.setValue(phone);
        agreementCheckbox.click();
        okButton.click();

        $x("//span[@data-test-id='phone']//span[contains(text(), '" + errorMessageIncorrectPhone + "')]")
                .should(appear);
    }

    /**
     * Стоит валидация поля
     */
    public void testInputSymbolInPhoneField() {
        String city = generateCity();
        String deliveryDate = generateDate(randomPeriod(4, 365),"dd.MM.yyyy");
        String personFullName = generateFirstAndLastNames(locale);
        String phone = generatePhone(locale) + randomSymbol(locale);

        cityInput.setValue(city);
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(deliveryDate);
        nameInput.setValue(personFullName);
        phoneInput.setValue(phone);
        agreementCheckbox.click();
        okButton.click();

        $x("//span[@data-test-id='phone']//span[contains(text(), '" + errorMessageIncorrectPhone + "')]")
                .should(appear);
    }

    @Test
    public void testInputWithDashInPhoneField() {
        String city = generateCity();
        String deliveryDate = generateDate(randomPeriod(4, 365),"dd.MM.yyyy");
        String personFullName = generateFirstAndLastNames(locale);
        String phone = generatePhone(locale) + "-";

        cityInput.setValue(city);
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(deliveryDate);
        nameInput.setValue(personFullName);
        phoneInput.setValue(phone);
        agreementCheckbox.click();
        okButton.click();

        $x("//span[@data-test-id='phone']//span[contains(text(), '" + errorMessageIncorrectPhone + "')]")
                .should(appear);
    }

    @Test
    public void testInputWithBracketsInPhoneField() {
        String city = generateCity();
        String deliveryDate = generateDate(randomPeriod(4, 365),"dd.MM.yyyy");
        String personFullName = generateFirstAndLastNames(locale);
        String phone = generatePhone(locale) + "()";

        cityInput.setValue(city);
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(deliveryDate);
        nameInput.setValue(personFullName);
        phoneInput.setValue(phone);
        agreementCheckbox.click();
        okButton.click();

        $x("//span[@data-test-id='phone']//span[contains(text(), '" + errorMessageIncorrectPhone + "')]")
                .should(appear);
    }

    @Test
    public void testUncheckedAgreement() {
        String city = generateCity();
        String deliveryDate = generateDate(randomPeriod(4, 365),"dd.MM.yyyy");
        String personFullName = generateFirstAndLastNames(locale);
        String phone = generatePhone(locale);

        cityInput.setValue(city);
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(deliveryDate);
        nameInput.setValue(personFullName);
        phoneInput.setValue(phone);

        okButton.click();

        $x("//label[@data-test-id='agreement'][contains(@class, 'input_invalid')]").should(appear);
    }

    @Test
    public void testValidationFieldsAfterCorrectingWrongInput() {
        String city = generateCity();
        String deliveryDate = generateDate(randomPeriod(4, 365),"dd.MM.yyyy");
        String personFullName = generateFirstAndLastNames(locale);
        String phone = generatePhone(locale);

        cityInput.setValue(city + randomSpecSymbol());
        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        String incorrectDate = generateDate(-1,"dd.MM.yyyy");
        dateInput.setValue(incorrectDate);
        nameInput.setValue(personFullName + randomSymbol("en"));
        phoneInput.setValue(phone + randomPeriod(0, 9));
        okButton.click();
        $x("//span[@data-test-id='city'][contains(@class, 'input_invalid')]").should(appear);

        cityInput.clear();
        cityInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        cityInput.setValue(city);
        okButton.click();
        $x("//span[@data-test-id='date']//span[contains(@class, 'input_invalid')]").should(appear);

        dateInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        dateInput.setValue(deliveryDate);
        okButton.click();
        $x("//span[@data-test-id='name'][contains(@class, 'input_invalid')]").should(appear);

        nameInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        nameInput.setValue(personFullName);
        okButton.click();
        $x("//span[@data-test-id='phone'][contains(@class, 'input_invalid')]").should(appear);

        phoneInput.sendKeys(Keys.LEFT_SHIFT, Keys.HOME, Keys.BACK_SPACE);
        phoneInput.setValue(phone);
        okButton.click();
        $x("//label[@data-test-id='agreement'][contains(@class, 'input_invalid')]").should(appear);

        agreementCheckbox.click();
        okButton.click();
        successPopUp
                .shouldHave(Condition.text(successMessage + deliveryDate), Duration.ofSeconds(15))
                .shouldBe(Condition.visible);
    }
}