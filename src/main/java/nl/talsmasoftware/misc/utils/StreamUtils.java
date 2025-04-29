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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class StreamUtils {

    private StreamUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    /**
     * Stream the elements of a (nullable) {@linkplain Iterable} object.
     *
     * <p>
     * The resulting stream also filters any {@code null} elements from the result.
     *
     * @param iterable The iterable object (optional, may be {@code null}).
     * @param <T>      The type of the iterable elements.
     * @return Non-{@code null} Stream with the elements from the given iterable.
     * Null-values will be filtered out.
     */
    public static <T> Stream<T> streamNullable(Iterable<T> iterable) {
        if (iterable == null) {
            return Stream.empty();
        } else if (iterable instanceof Collection) {
            return ((Collection<T>) iterable).stream().filter(Objects::nonNull);
        }
        return StreamSupport.stream(iterable.spliterator(), false).filter(Objects::nonNull);
    }

    /**
     * Collector for the last <em>n</em> elements of a stream.
     *
     * <p>
     * The returned list is unmodifiable.
     *
     * @param maxSize The maximum size of the list to return.
     * @param <T>     The type of elements to be collected.
     * @return Collector returning the last <em>n</em> elements as a list,
     * or the full stream content if there were at most {@code maxSize} elements.
     */
    public static <T> Collector<T, ArrayList<T>, List<T>> last(final int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Maximum size must be a positive number.");
        }
        return Collector.of(() -> new ArrayList<>(maxSize),
                (accumulator, value) -> {
                    if (accumulator.size() < maxSize) {
                        accumulator.add(value);
                    } else {
                        Collections.rotate(accumulator, -1);
                        accumulator.set(maxSize - 1, value);
                    }
                },
                (acc1, acc2) -> {
                    if (acc2.size() < maxSize) {
                        int skip = acc1.size() + acc2.size() - maxSize;
                        if (skip <= 0) {
                            acc2.addAll(0, acc1);
                        } else {
                            acc2.addAll(0, acc1.subList(skip, acc1.size()));
                        }
                    }
                    return acc2;
                },
                Collections::unmodifiableList);
    }

} 
