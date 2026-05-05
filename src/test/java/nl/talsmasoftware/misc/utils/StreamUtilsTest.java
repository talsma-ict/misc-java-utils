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

import org.assertj.core.data.MapEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StreamUtilsTest {
    @Test
    @SuppressWarnings({
            "java:S1874" // We use java8, where canAccess is not yet available to replace isAccessible.
    })
    void streamUtils_utility_class_has_unsupported_constructor() throws ReflectiveOperationException {
        Constructor<StreamUtils> constructor = StreamUtils.class.getDeclaredConstructor();
        assertThat(constructor.isAccessible()).isFalse();
        constructor.setAccessible(true);
        assertThatThrownBy(constructor::newInstance)
                .isInstanceOf(InvocationTargetException.class)
                .cause()
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("This is a utility class and cannot be instantiated.");
    }

    @Test
    @DisplayName("streamNullable: null iterable returns empty stream.")
    void streamNullable_null_iterable_returns_empty_stream() {
        // when
        Stream<?> result = StreamUtils.streamNullable((Iterable<?>) null);

        // then
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("streamNullable: null map returns empty stream.")
    void streamNullable_null_map_returns_empty_stream() {
        // when
        var result = StreamUtils.streamNullable((Map<?, ?>) null);

        // then
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("streamNullable: empty collection returns empty stream.")
    void streamNullable_empty_returns_empty_stream() {
        // when
        Stream<?> result = StreamUtils.streamNullable(Collections.emptyList());

        // then
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("streamNullable: empty iterable returns empty stream.")
    void streamNullable_empty_Iterable_returns_empty_stream() {
        // given
        Iterable<?> emptyIterable = Collections::emptyIterator;

        // when
        Stream<?> result = StreamUtils.streamNullable(emptyIterable);

        // then
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("streamNullable: empty map returns empty stream.")
    void streamNullable_empty_map_returns_empty_stream() {
        var result = StreamUtils.streamNullable(Collections.emptyMap());
        assertThat(result).as("Stream from empty map")
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("streamNullable: collection is streamed with filtered null values.")
    void streamNullable_returns_stream_from_collection() {
        // when
        Stream<Integer> result = StreamUtils.streamNullable(Arrays.asList(1, null, 2, null, 3));

        // then
        assertThat(result).isNotNull().isNotEmpty().hasSize(3).containsExactly(1, 2, 3);
    }

    @Test
    @DisplayName("streamNullable: iterable is streamed with filtered null values.")
    void streamNullable_returns_stream_from_iterable() {
        // given
        Iterable<Integer> iterable = Arrays.asList(1, null, 2, null, 3)::iterator;

        // when
        Stream<Integer> result = StreamUtils.streamNullable(iterable);

        // then
        assertThat(result).isNotNull().isNotEmpty().hasSize(3).containsExactly(1, 2, 3);
    }

    @Test
    @DisplayName("streamNullable: map is streamed with filtered null values.")
    void streamNullable_returns_stream_from_map() {
        Map<String, String> map = new HashMap<>() {{
            put("key1", null);
            put("key2", "value2");
        }};

        var result = StreamUtils.streamNullable(map);

        assertThat(result).as("Stream from map")
                .isNotNull()
                .hasSize(1)
                .containsExactly(MapEntry.entry("key2", "value2"));
    }

    @Test
    @DisplayName("last: Count must be a positive number.")
    void last_count_must_be_positive() {
        assertThatThrownBy(() -> StreamUtils.last(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Maximum size must be a positive number.");
    }

    @Test
    @DisplayName("last: Test last 2 of 10 items.")
    void last_2_of_10() {
        // given
        Stream<String> stream = IntStream.range(1, 11).mapToObj(Integer::toString);

        // when
        List<String> result = stream.collect(StreamUtils.last(2));

        // then
        assertThat(result).hasSize(2).containsExactly("9", "10");
    }

    @Test
    @DisplayName("last: Test last 10 of 2 items.")
    void last_10_of_2() {
        // given
        Stream<String> stream = Stream.of("1", "2");

        // when
        List<String> result = stream.collect(StreamUtils.last(10));

        // then
        assertThat(result).hasSize(2).containsExactly("1", "2");
    }

    @Test
    @DisplayName("last: Test last 10 of 10 items.")
    void last_10_of_10() {
        // given
        Stream<String> stream = IntStream.range(1, 11).mapToObj(Integer::toString);

        // when
        List<String> result = stream.collect(StreamUtils.last(10));

        // then
        assertThat(result).hasSize(10).containsExactly("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
    }

    @Test
    @DisplayName("last: Test combiner when acc2 is full.")
    void last_combiner_acc2_is_full() {
        // given
        Collector<String, ArrayList<String>, List<String>> collector = StreamUtils.last(10);
        ArrayList<String> acc1 = IntStream.range(1, 11).mapToObj(Integer::toString).collect(toCollection(ArrayList::new));
        ArrayList<String> acc2 = IntStream.range(11, 21).mapToObj(Integer::toString).collect(toCollection(ArrayList::new));

        // when
        List<String> result = collector.combiner().apply(acc1, acc2);

        // then
        assertThat(result)
                .hasSize(10)
                .containsExactly("11", "12", "13", "14", "15", "16", "17", "18", "19", "20")
                .isSameAs(acc2);
    }

    @Test
    @DisplayName("last: Test combiner when acc2 still has some space available.")
    void last_combiner_acc2_has_some_space() {
        // given
        Collector<String, ArrayList<String>, List<String>> collector = StreamUtils.last(10);
        ArrayList<String> acc1 = IntStream.range(1, 11).mapToObj(Integer::toString).collect(toCollection(ArrayList::new));
        ArrayList<String> acc2 = IntStream.range(11, 16).mapToObj(Integer::toString).collect(toCollection(ArrayList::new));

        // when
        List<String> result = collector.combiner().apply(acc1, acc2);

        // then
        assertThat(result)
                .hasSize(10)
                .containsExactly("6", "7", "8", "9", "10", "11", "12", "13", "14", "15")
                .isSameAs(acc2);
    }

    @Test
    @DisplayName("last: Test combiner when acc1 and acc2 both fit.")
    void last_combiner_acc1_and_acc2_both_fit() {
        // given
        Collector<String, ArrayList<String>, List<String>> collector = StreamUtils.last(10);
        ArrayList<String> acc1 = IntStream.range(1, 5).mapToObj(Integer::toString).collect(toCollection(ArrayList::new));
        ArrayList<String> acc2 = IntStream.range(5, 11).mapToObj(Integer::toString).collect(toCollection(ArrayList::new));

        // when
        List<String> result = collector.combiner().apply(acc1, acc2);

        // then
        assertThat(result)
                .hasSize(10)
                .containsExactly("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
                .isSameAs(acc2);
    }
}
