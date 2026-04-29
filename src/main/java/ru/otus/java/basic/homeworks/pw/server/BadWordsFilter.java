package ru.otus.java.basic.homeworks.pw.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

//16	Фильтр плохих слов
public class BadWordsFilter {
    private static final String FILE_NAME = "badwords.txt";

    // Список с сортировкой по длине
    private static List<String> sortedBadWords = new ArrayList<>();
    static {
        loadWords();
    }

    private static void loadWords() {
        Set<String> rawWords = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    rawWords.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Файл плохих слов не найден, используется стандартный набор.");
            rawWords.addAll(Set.of("дурак", "идиот", "тупой", "дебил", "дура", "мразь", "сволочь"));
        }
        // Сортируем по убыванию длины, чтобы длинные заменялись первыми
        // была проблема с короткими словами, были первыми в списке
        List<String> list = new ArrayList<>(rawWords);
        list.sort((a, b) -> Integer.compare(b.length(), a.length()));
        sortedBadWords = list;
    }

    public static String filter(String message) {
        String result = message;
        for (String word : sortedBadWords) {
            if (message.toLowerCase().contains(word)) {
                result = result.replaceAll("(?iu)" + Pattern.quote(word), "*".repeat(word.length()));
            }
        }
        return result;
    }
}
