/*
 * Copyright 2022-2025 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.talsmasoftware.misc.utils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FirstLastComparatorTest {
    @Test
    void must_delegate_normal_without_values() {
        // prepare
        final Comparator<Character> delegate = Comparator.naturalOrder();
        final String value = "cbda";

        // execute
        final String result1 = sortString(FirstLastComparator.compareFirst(delegate, emptyList()), value);
        final String result2 = sortString(FirstLastComparator.compareLast(delegate, emptyList()), value);

        // verify
        assertThat(result1, equalTo("abcd"));
        assertThat(result2, equalTo("abcd"));
    }

    @Test
    void must_sort_single_value_first() {
        // prepare
        final Comparator<Character> subject = FirstLastComparator.compareFirst(Comparator.naturalOrder(), 'o');

        // execute
        final String result = sortString(subject, "The quick brown fox jumps over the lazy dog");

        // verify
        assertThat(result, equalTo("oooo        Tabcdeeefghhijklmnpqrrstuuvwxyz"));
    }

    @Test
    void must_sort_single_value_last() {
        // prepare
        final Comparator<Character> subject = FirstLastComparator.compareLast(Comparator.reverseOrder(), 'o');

        // execute
        final String result = sortString(subject, "The quick brown fox jumps over the lazy dog");

        // verify
        assertThat(result, equalTo("zyxwvuutsrrqpnmlkjihhgfeeedcbaT        oooo"));
    }

    @Test
    void first_values_are_in_given_order() {
        // prepare
        final Comparator<Character> subject = FirstLastComparator.compareFirst(Comparator.naturalOrder(), 'o', 'r', 'a');

        // execute
        final String result = sortString(subject, "The quick brown fox jumps over the lazy dog");

        // verify
        assertThat(result, equalTo("oooorra        Tbcdeeefghhijklmnpqstuuvwxyz"));
    }

    @Test
    void last_values_are_in_given_order() {
        // prepare
        final Comparator<Character> subject = FirstLastComparator.compareLast(Comparator.naturalOrder(), 'o', 'r', 'a');

        // execute
        final String result = sortString(subject, "The quick brown fox jumps over the lazy dog");

        // verify
        assertThat(result, equalTo("        Tbcdeeefghhijklmnpqstuuvwxyzoooorra"));
    }

    @Test
    void test_nullsafe_compare() {
        // prepare
        final List<String> values = Arrays.asList("zzzz", null, "last", "aaaa", "first", null, "    ");

        // execute
        List<String> result1 = sortCopy(FirstLastComparator.compareFirst(Comparator.nullsFirst(Comparator.naturalOrder()), "first"), values);
        List<String> result2 = sortCopy(FirstLastComparator.compareFirst(Comparator.naturalOrder(), null, "first"), values);
        List<String> result3 = sortCopy(FirstLastComparator.compareLast(Comparator.nullsLast(Comparator.naturalOrder()), "last"), values);
        List<String> result4 = sortCopy(FirstLastComparator.compareLast(Comparator.naturalOrder(), "last", null), values);

        // verify
        assertThat(result1, contains("first", null, null, "    ", "aaaa", "last", "zzzz"));
        assertThat(result2, contains(null, null, "first", "    ", "aaaa", "last", "zzzz"));
        assertThat(result3, contains("    ", "aaaa", "first", "zzzz", null, null, "last"));
        assertThat(result4, contains("    ", "aaaa", "first", "zzzz", "last", null, null));
    }

    @Test
    void all_parameters_to_factorymethods_are_required() {
        final Comparator<String> natural = Comparator.naturalOrder();
        // Check nulls for compareFirst
        assertThat(assertThrows(NullPointerException.class, () -> FirstLastComparator.compareFirst(null, "")),
                hasProperty("message", equalTo("Delegate comparator is <null>.")));
        assertThat(assertThrows(NullPointerException.class, () -> FirstLastComparator.compareFirst(natural, (String[]) null)),
                hasProperty("message", equalTo("firstValues is <null>.")));
        assertThat(assertThrows(NullPointerException.class, () -> FirstLastComparator.compareFirst(null, Collections.singleton(""))),
                hasProperty("message", equalTo("Delegate comparator is <null>.")));
        assertThat(assertThrows(NullPointerException.class, () -> FirstLastComparator.compareFirst(natural, (List<String>) null)),
                hasProperty("message", equalTo("firstValues is <null>.")));

        // Check nulls for compareLast
        assertThat(assertThrows(NullPointerException.class, () -> FirstLastComparator.compareLast(null, "")),
                hasProperty("message", equalTo("Delegate comparator is <null>.")));
        assertThat(assertThrows(NullPointerException.class, () -> FirstLastComparator.compareLast(natural, (String[]) null)),
                hasProperty("message", equalTo("lastValues is <null>.")));
        assertThat(assertThrows(NullPointerException.class, () -> FirstLastComparator.compareLast(null, Collections.singleton(""))),
                hasProperty("message", equalTo("Delegate comparator is <null>.")));
        assertThat(assertThrows(NullPointerException.class, () -> FirstLastComparator.compareLast(natural, (List<String>) null)),
                hasProperty("message", equalTo("lastValues is <null>.")));
    }

    static String sortString(Comparator<Character> comparator, String characters) {
        char[] chars = characters.toCharArray();
        List<Character> values = new ArrayList<>(chars.length);
        for (char ch : chars) {
            values.add(ch);
        }
        return sortCopy(comparator, values).stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
    }

    static <T> List<T> sortCopy(Comparator<? super T> comparator, Collection<? extends T> values) {
        final List<T> sortedCopy = new ArrayList<>(values);
        sortedCopy.sort(comparator);
        return sortedCopy;
    }
}
