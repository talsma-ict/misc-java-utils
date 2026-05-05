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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/// Class for [Stream] utility methods.
public final class StreamUtils {
    /// Private constructor to avoid utility class instantiation.
    private StreamUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    /// Stream the elements of a (nullable) [Iterable] object.
    ///
    /// The resulting [Stream] also filters any `null` elements from the result.
    ///
    /// @param iterable The iterable object (optional, may be `null`).
    /// @param <T>      The type of the iterable elements.
    /// @return Non-`null` [Stream] with the elements from the given [Iterable]. All iterated `null` values will be filtered out.
    public static <T> Stream<T> streamNullable(Iterable<T> iterable) {
        if (iterable == null) {
            return Stream.empty();
        } else if (iterable instanceof Collection) {
            return ((Collection<T>) iterable).stream().filter(Objects::nonNull);
        }
        return StreamSupport.stream(iterable.spliterator(), false).filter(Objects::nonNull);
    }

    /// Stream the elements of a (nullable) [Map] object.
    ///
    /// The resulting [Stream] also filters any [entries][Map.Entry] with `null` values.
    ///
    /// @param map The map object (optional, may be `null`).
    /// @param <K> The 'key' type of the map.
    /// @param <V> The 'value' type of the map.
    /// @return Non-`null` [Stream] of [map entries][Map.Entry] from the given [Map]. All iterated entries with `null` values will be filtered out.
    /// @see #streamNullable(Iterable)
    public static <K, V> Stream<Map.Entry<K, V>> streamNullable(Map<K, V> map) {
        if (map == null) {
            return Stream.empty();
        }
        return map.entrySet().stream().filter(entry -> entry.getValue() != null);
    }

    /// Collector for the last _n_ elements of a [Stream].
    ///
    /// **Note:** Obvious caveat: The stream to be collected **must** be a limited stream,
    /// in other words, actually _have_ a last element.
    ///
    /// The returned list is unmodifiable.
    ///
    /// @param maxSize The maximum size of the [List] to return.
    /// @param <T>     The type of elements to be collected.
    /// @return Collector returning the last _n_ elements as a [List],
    /// or the entire stream content if there were at most `maxSize` elements.
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
