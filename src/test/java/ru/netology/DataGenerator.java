package ru.netology;

import com.github.javafaker.Faker;
import lombok.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataGenerator {
    private static Faker faker;
    private static final Random random = new Random();

    private DataGenerator() {
    }

    public static int randomPeriod(int begin, int end) {
        return random.nextInt(end + 1 - begin) + begin;
    }

    public static char randomSpecSymbol() {
        String specSymbols = "!@#$%^&*()_+~`;:.,<>/|?№";

        return specSymbols.charAt(random.nextInt(specSymbols.length()));
    }

    public static char randomSymbol(String locale) {
        String symbols;
        if (locale.equals("ru") || locale.equals("RU") || locale.equals("ру")) {
            symbols = "йцукенгшщзхъфывапролджэячсмитьбюёЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮЁ";
        } else {
            symbols = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
        }

        return symbols.charAt(random.nextInt(symbols.length()));
    }

    public static String generateDate(int plusDaysToCurrent, String pattern) {
        return LocalDate.now().plusDays(plusDaysToCurrent).format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String generateCity() {
        List<String> cities = new ArrayList<>();

        cities.add("Казань");
        cities.add("Нижний Новгород");
        cities.add("Нальчик");
        cities.add("Санкт-Петербург");
        cities.add("Вологда");
        cities.add("Москва");
        cities.add("Ханты-Мансийск");

        return cities.get(random.nextInt(cities.size()));
    }

    public static String generateCity(String locale) {
        faker = new Faker(new Locale(locale));
        return faker.address().city();
    }

    public static String generateFullName(String locale) {
        faker = new Faker(new Locale(locale));
        return faker.name().fullName().replaceAll("ё", "е").replaceAll("Ё", "Е");
    }

    public static String generateFirstName(String locale) {
        faker = new Faker(new Locale(locale));
        return faker.name().firstName().replaceAll("ё", "е").replaceAll("Ё", "Е");
    }

    public static String generateLastName(String locale) {
        faker = new Faker(new Locale(locale));
        return faker.name().lastName().replaceAll("ё", "е").replaceAll("Ё", "Е");
    }

    public static String generateFirstAndLastNames(String locale) {
        return generateFirstName(locale) + " " + generateLastName(locale);
    }

    public static String generatePhone(String locale) {
        faker = new Faker(new Locale(locale));

        return faker.phoneNumber().phoneNumber()
                .replaceAll("\\)", "")
                .replaceAll("\\(", "")
                .replaceAll("-", "");
    }

    public static class Registration {
        private Registration() {
        }

        public static UserInfo generateUser(String locale) {
            return new UserInfo(
                    generateCity(locale), generateFirstAndLastNames(locale), generatePhone(locale));
        }
    }

    @Value
    public static class UserInfo {
        String city;
        String name;
        String phone;
    }
}

