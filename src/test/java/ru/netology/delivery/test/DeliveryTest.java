package ru.netology.delivery.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static ru.netology.delivery.data.DataGenerator.*;

class DeliveryTest {
    @BeforeAll
    static void setUp() {
        SelenideLogger.addListener("allure", new AllureSelenide() );
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        Configuration.holdBrowserOpen = true;

        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id =date] input").sendKeys(Keys.CONTROL + "a");
        $("[data-test-id =date] input").sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(firstMeetingDate);
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=phone] input").setValue(validUser.getPhone());
        $("[data-test-id=agreement]").click();
        $(withText("Запланировать")).click();
        $(withText("Успешно!")).shouldBe(Condition.visible, Duration.ofSeconds(10));
        $("[data-test-id=success-notification]")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + firstMeetingDate));
        //$("[data-test-id=success-notification]").shouldBe(visible);
        $("[data-test-id =date] input").sendKeys(Keys.CONTROL + "a");
        $("[data-test-id =date] input").sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(secondMeetingDate);
        $(withText("Запланировать")).click();
        $("[data-test-id=replan-notification]")
                .shouldHave(Condition.text("У вас уже запланирована встреча на другую дату. Перепланировать?"));
        $(withText("Перепланировать")).click();
        $("[data-test-id=success-notification]").shouldBe(Condition.visible, Duration.ofSeconds(10));
        $("[data-test-id=success-notification]").shouldHave(Condition.text("Встреча успешно запланирована на " + secondMeetingDate), Duration.ofSeconds(15))
                .shouldBe(Condition.visible);

    }

}

