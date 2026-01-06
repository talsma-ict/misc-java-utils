/*
 * Copyright 2022-2026 Talsma ICT
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Comparator to sort certain values first or last.
 *
 * <p>
 * For example:
 * <pre>{@code
 * public static final Comparator<String> ADJUSTED_ORDER =
 *     FirstLastComparator.compareLast(String.CASE_INSENSITIVE_ORDER, "Always last");
 * }</pre>
 * In the example above, {@code ADJUSTED_ORDER} will sort Strings case-insensitive
 * where {@code "Always last"} is always sorted last.
 *
 * @param <T> The type to be sorted.
 * @author Sjoerd Talsma
 */
public final class FirstLastComparator<T> implements Comparator<T>, Serializable {
    private final Comparator<T> delegate;

    private final boolean compareFirst;

    private final List<T> explicitValues;

    private FirstLastComparator(Comparator<T> delegate, boolean compareFirst, Collection<? extends T> explicitValues) {
        this.delegate = Objects.requireNonNull(delegate, "Delegate comparator is <null>.");
        this.compareFirst = compareFirst;
        this.explicitValues = new ArrayList<>(explicitValues);
    }

    /**
     * Sort a limited collection of values first, delegating main sorting to another comparator.
     *
     * @param delegate    The main sorting delegate for all 'other' values.
     * @param firstValues The values to be sorted first.
     * @param <T>         The type to be sorted.
     * @return A comparator sorting the given values first.
     */
    public static <T> Comparator<T> compareFirst(Comparator<T> delegate, Collection<? extends T> firstValues) {
        return new FirstLastComparator<>(delegate, true,
                Objects.requireNonNull(firstValues, "firstValues is <null>."));
    }

    /**
     * Sort one or more values first, delegating main sorting to another comparator.
     *
     * @param delegate    The main sorting delegate for all 'other' values.
     * @param firstValues The values to be sorted first.
     * @param <T>         The type to be sorted.
     * @return A comparator sorting the given values first.
     */
    @SafeVarargs
    public static <T> Comparator<T> compareFirst(Comparator<T> delegate, T... firstValues) {
        return compareFirst(delegate, firstValues != null ? Arrays.asList(firstValues) : null);
    }

    /**
     * Sort a limited collection of values last, delegating main sorting to another comparator.
     *
     * @param delegate   The main sorting delegate for all 'other' values.
     * @param lastValues The values to be sorted last.
     * @param <T>        The type to be sorted.
     * @return A comparator sorting the given values last.
     */
    public static <T> Comparator<T> compareLast(Comparator<T> delegate, Collection<? extends T> lastValues) {
        return new FirstLastComparator<>(delegate, false,
                Objects.requireNonNull(lastValues, "lastValues is <null>."));
    }

    /**
     * Sort one or more values last, delegating main sorting to another comparator.
     *
     * @param delegate   The main sorting delegate for all 'other' values.
     * @param lastValues The values to be sorted last.
     * @param <T>        The type to be sorted.
     * @return A comparator sorting the given values last.
     */
    @SafeVarargs
    public static <T> Comparator<T> compareLast(Comparator<T> delegate, T... lastValues) {
        return compareLast(delegate, lastValues != null ? Arrays.asList(lastValues) : null);
    }

    /**
     * Compare two objects.
     * <p>
     * Check whether the compared objects are in the limited set of specified values.
     * If this is the case, the corresponding object(s) is sorted either first or last,
     * depending on the used factory method ({@code compareFirst} vs. {@code compareLast}).<br>
     * If neither of the compared objects are explicitly sorted first or last,
     * the specified {@code delegate} Comparator is used.
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer
     * as the first argument is less than, equal to, or greater than the second.
     */
    @Override
    public int compare(T o1, T o2) {
        final int i1 = explicitValues.indexOf(o1);
        final int i2 = explicitValues.indexOf(o2);

        if (i1 < 0) {
            if (i2 < 0) return delegate.compare(o1, o2);
            else return compareFirst ? 1 : -1;
        } else if (i2 < 0) {
            return compareFirst ? -1 : 1;
        }
        // i1 >= 0 && i2 >= 0, return explicit values in the order they are specified.
        return i1 - i2;
    }
}
