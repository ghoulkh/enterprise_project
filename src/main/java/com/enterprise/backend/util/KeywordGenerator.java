package com.enterprise.backend.util;


import lombok.experimental.UtilityClass;

import java.text.Normalizer;

@UtilityClass
public final class KeywordGenerator {

    public static String genStringQuerySearchAddress(String keyword, Long minPrice, Long maxPrice, String[] types, Integer[] numberOfBedrooms) {
        StringBuilder stringBuilder = new StringBuilder();
        keyword = Normalizer.normalize(keyword, Normalizer.Form.NFC);
        stringBuilder.append(genSolrQuerySearchAddress(keyword));
        appendPriceAndType(minPrice, maxPrice, types, stringBuilder);
        appendNumberOfBedrooms(numberOfBedrooms, stringBuilder);
        return stringBuilder.toString();
    }

    public static String genStringQuerySearchAddressAndTitle(String keyword, Long minPrice, Long maxPrice, String[] types, Integer[] numberOfBedrooms) {
        StringBuilder stringBuilder = new StringBuilder();
        keyword = Normalizer.normalize(keyword, Normalizer.Form.NFC);
        stringBuilder.append(genSolrQuerySearchAddressAndTitle(keyword));
        appendPriceAndType(minPrice, maxPrice, types, stringBuilder);
        appendNumberOfBedrooms(numberOfBedrooms, stringBuilder);
        return stringBuilder.toString();
    }


    private static void appendNumberOfBedrooms(Integer[] numberOfBedrooms, StringBuilder stringBuilder) {
        if (numberOfBedrooms != null && numberOfBedrooms.length > 0) {
            if (numberOfBedrooms.length == 1) {
                if (numberOfBedrooms[0] == 4) {
                    stringBuilder.append(" AND numberOfBedrooms:[").append(numberOfBedrooms[0]).append(" TO *]");
                } else {
                    stringBuilder.append(" AND  numberOfBedrooms:").append(numberOfBedrooms[0]);
                }
                return;
            }
            stringBuilder.append("AND (");
            if (numberOfBedrooms[0] == 4) {
                stringBuilder.append("numberOfBedrooms:[").append(numberOfBedrooms[0]).append(" TO *]");
            } else {
                stringBuilder.append("numberOfBedrooms:").append(numberOfBedrooms[0]);
            }
            for (int i = 1; i < numberOfBedrooms.length; i ++) {
                appendNumberOfBedroomsOption(numberOfBedrooms[i], stringBuilder);
            }
            stringBuilder.append(")");
        }
    }

    private static void appendNumberOfBedroomsOption(Integer numberOfBedrooms, StringBuilder stringBuilder) {
        if (numberOfBedrooms == 4) {
            stringBuilder.append(" OR numberOfBedrooms:[").append(numberOfBedrooms).append(" TO *]");
        } else {
            stringBuilder.append(" OR numberOfBedrooms:").append(numberOfBedrooms);
        }
    }


    private static void appendPriceAndType(Long minPrice, Long maxPrice, String[] types, StringBuilder stringBuilder) {
        if (maxPrice != null || minPrice != null) {
            stringBuilder.append(" AND price:[");
            if (minPrice != null)
                stringBuilder.append(minPrice);
            else
                stringBuilder.append("*");
            stringBuilder.append(" TO ");
            if (maxPrice != null)
                stringBuilder.append(maxPrice);
            else
                stringBuilder.append("*");
            stringBuilder.append("]");
        }
        if (types != null) {
            stringBuilder.append(" AND type:(");
            for (int i = 0; i < types.length; i ++) {
                stringBuilder.append(types[i] + " ");
            }
            stringBuilder.append(")");
        }
    }
    private static String genSolrQuerySearchAddress(String keyword) {
        StringBuilder builder = new StringBuilder();
        builder.append("searchData:*").append(toSearchKeyword(keyword)).append("*");
        return builder.toString();
    }

    private static String genSolrQuerySearchAddressAndTitle(String keyword) {
        StringBuilder builder = new StringBuilder();
        builder.append("addressAndTitle:*").append(toSearchKeyword(keyword)).append("*");
        return builder.toString();
    }

    private static String toSearchKeyword(String keyword) {
        return keyword.replace(" ", "-").toLowerCase();
    }
}
