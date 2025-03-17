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
import java.util.Set;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
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
        assertThat(result1).isEqualTo("abcd");
        assertThat(result2).isEqualTo("abcd");
    }

    @Test
    void must_sort_single_value_first() {
        // prepare
        final Comparator<Character> subject = FirstLastComparator.compareFirst(Comparator.naturalOrder(), 'o');

        // execute
        final String result = sortString(subject, "The quick brown fox jumps over the lazy dog");

        // verify
        assertThat(result).isEqualTo("oooo        Tabcdeeefghhijklmnpqrrstuuvwxyz");
    }

    @Test
    void must_sort_single_value_last() {
        // prepare
        final Comparator<Character> subject = FirstLastComparator.compareLast(Comparator.reverseOrder(), 'o');

        // execute
        final String result = sortString(subject, "The quick brown fox jumps over the lazy dog");

        // verify
        assertThat(result).isEqualTo("zyxwvuutsrrqpnmlkjihhgfeeedcbaT        oooo");
    }

    @Test
    void first_values_are_in_given_order() {
        // prepare
        final Comparator<Character> subject = FirstLastComparator.compareFirst(Comparator.naturalOrder(), 'o', 'r', 'a');

        // execute
        final String result = sortString(subject, "The quick brown fox jumps over the lazy dog");

        // verify
        assertThat(result).isEqualTo("oooorra        Tbcdeeefghhijklmnpqstuuvwxyz");
    }

    @Test
    void last_values_are_in_given_order() {
        // prepare
        final Comparator<Character> subject = FirstLastComparator.compareLast(Comparator.naturalOrder(), 'o', 'r', 'a');

        // execute
        final String result = sortString(subject, "The quick brown fox jumps over the lazy dog");

        // verify
        assertThat(result).isEqualTo("        Tbcdeeefghhijklmnpqstuuvwxyzoooorra");
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
        assertThat(result1).containsExactly("first", null, null, "    ", "aaaa", "last", "zzzz");
        assertThat(result2).containsExactly(null, null, "first", "    ", "aaaa", "last", "zzzz");
        assertThat(result3).containsExactly("    ", "aaaa", "first", "zzzz", null, null, "last");
        assertThat(result4).containsExactly("    ", "aaaa", "first", "zzzz", "last", null, null);
    }

    @Test
    void all_parameters_to_factorymethods_are_required() {
        final Comparator<String> natural = Comparator.naturalOrder();
        // Check nulls for compareFirst
        Set<String> singleEmptyString = Collections.singleton("");
        assertThat(assertThrows(NullPointerException.class, () -> FirstLastComparator.compareFirst(null, "")))
                .hasFieldOrPropertyWithValue("message", "Delegate comparator is <null>.");
        assertThat(assertThrows(NullPointerException.class, () -> FirstLastComparator.compareFirst(natural, (String[]) null)))
                .hasFieldOrPropertyWithValue("message", "firstValues is <null>.");
        assertThat(assertThrows(NullPointerException.class, () -> FirstLastComparator.compareFirst(null, singleEmptyString)))
                .hasFieldOrPropertyWithValue("message", "Delegate comparator is <null>.");
        assertThat(assertThrows(NullPointerException.class, () -> FirstLastComparator.compareFirst(natural, (List<String>) null)))
                .hasFieldOrPropertyWithValue("message", "firstValues is <null>.");

        // Check nulls for compareLast
        assertThat(assertThrows(NullPointerException.class, () -> FirstLastComparator.compareLast(null, "")))
                .hasFieldOrPropertyWithValue("message", "Delegate comparator is <null>.");
        assertThat(assertThrows(NullPointerException.class, () -> FirstLastComparator.compareLast(natural, (String[]) null)))
                .hasFieldOrPropertyWithValue("message", "lastValues is <null>.");
        assertThat(assertThrows(NullPointerException.class, () -> FirstLastComparator.compareLast(null, singleEmptyString)))
                .hasFieldOrPropertyWithValue("message", "Delegate comparator is <null>.");
        assertThat(assertThrows(NullPointerException.class, () -> FirstLastComparator.compareLast(natural, (List<String>) null)))
                .hasFieldOrPropertyWithValue("message", "lastValues is <null>.");
    }

    static String sortString(Comparator<Character> comparator, String characters) {
        return characters.chars()
                .mapToObj(ch -> (char) ch)
                .sorted(comparator)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
    }

    static <T> List<T> sortCopy(Comparator<? super T> comparator, Collection<? extends T> values) {
        final List<T> sortedCopy = new ArrayList<>(values);
        sortedCopy.sort(comparator);
        return sortedCopy;
    }
}
