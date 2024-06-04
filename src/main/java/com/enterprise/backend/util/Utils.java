package com.enterprise.backend.util;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.beanutils.BeanUtilsBean;

import java.text.Normalizer;
import java.util.List;
import java.util.Random;

@Log4j2
@UtilityClass
public class Utils {
    public static final String DATE_TIME_FORMATTER = "dd/MM/yyyy HH:mm:ss";
    public static final String TIME_START = " 00:00:00";
    public static final String TIME_END = " 23:59:59";
    private static final Random RANDOM = new Random();

    /**
     * Copies properties from source to target, ignoring null values.
     *
     * @param source the source object
     * @param target the target object
     */
    public static void copyPropertiesNotNull(Object source, Object target) {
        try {
            BeanUtilsBean notNull = new NullAwareBeanUtilsBean();
            notNull.copyProperties(target, source);
        } catch (Exception e) {
            log.error("Error copying properties: {}", e.getMessage(), e);
        }
    }

    /**
     * Converts a string to its ASCII equivalent by removing diacritical marks and
     * converting specific Vietnamese characters.
     *
     * @param input the input string
     * @return the ASCII equivalent of the input string
     */
    public static String toEn(String input) {
        if (input == null) return null;
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replace("Đ", "D")
                .replace("đ", "d");
    }

    /**
     * Generates a random alphanumeric string of the specified length.
     *
     * @param length the length of the generated string
     * @return a random alphanumeric string
     */
    public static String getAlphaNumericString(int length) {
        String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(alphaNumericString.length());
            sb.append(alphaNumericString.charAt(index));
        }

        return sb.toString();
    }

    /**
     * Calculates the average of a list of integers.
     *
     * @param numbers the list of integers
     * @return the average of the integers
     */
    public static Double calculateAverage(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            return 0.0;
        }
        double sum = 0;
        for (Integer number : numbers) {
            sum += number;
        }
        return sum / numbers.size();
    }
}
