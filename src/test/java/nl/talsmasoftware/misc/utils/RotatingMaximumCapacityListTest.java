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

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RotatingMaximumCapacityListTest {

    @Test
    void addLessThanMaxSize() {
        // given
        List<String> subject = new RotatingMaximumCapacityList<>(10);

        // when
        for (int i = 1; i < 10; i++) {
            subject.add("" + i);
        }

        // then
        assertThat(subject)
                .hasSize(9)
                .contains("1", "2", "3", "4", "5", "6", "7", "8", "9");
    }

    @Test
    void addExactlyMaxSize() {
        // given
        List<String> subject = new RotatingMaximumCapacityList<>(10);

        // when
        for (int i = 1; i <= 10; i++) {
            subject.add("" + i);
        }

        // then
        assertThat(subject)
                .hasSize(10)
                .contains("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
    }

    @Test
    void addMoreThanMaxSize() {
        // given
        List<String> subject = new RotatingMaximumCapacityList<>(10);

        // when
        for (int i = 1; i <= 11; i++) {
            subject.add("" + i);
        }

        // then
        assertThat(subject)
                .hasSize(10)
                .contains("2", "3", "4", "5", "6", "7", "8", "9", "10", "11");
    }

    @Test
    void addAllMoreThanMaxSize() {
        // given
        List<String> subject = new RotatingMaximumCapacityList<>(10, Arrays.asList("a", "b", "c", "d"));

        // when
        subject.addAll(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"));

        // then
        assertThat(subject)
                .hasSize(10)
                .contains("2", "3", "4", "5", "6", "7", "8", "9", "10", "11");
    }
}
