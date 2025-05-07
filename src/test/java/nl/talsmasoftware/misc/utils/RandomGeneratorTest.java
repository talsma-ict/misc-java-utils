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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RandomGeneratorTest {
    /**
     * Reusable random instance for tests.
     */
    static final RandomGenerator RND = new RandomGenerator();

    @Test
    @DisplayName("nextValueFrom: May not be called without values")
    void nextValueFrom_mayNotBeCalledWithoutValues() {
        assertThatThrownBy(RND::nextValueFrom)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No values to randomly choose from.");
    }

    @Test
    @DisplayName("nextValueFrom: Chooses from all values")
    void nextValueFrom_choosesFromAllValues() {
        String[] allValues = new String[]{"a", "b", "c"};
        Set<String> remaining = new LinkedHashSet<>(Arrays.asList(allValues));

        long deadline = System.currentTimeMillis() + Duration.ofMinutes(1).toMillis();
        while (deadline > System.currentTimeMillis() && !remaining.isEmpty()) {
            remaining.remove(RND.nextValueFrom(allValues));
        }

        assertThat(remaining).as("Remaining values").isEmpty();
    }

    @Test
    @DisplayName("nextValueFromCollection: Chooses from all values")
    void nextValueFromCollection_choosesFromAllValues() {
        Collection<String> allValues = Arrays.asList("a", "b", "c");
        Set<String> remaining = new LinkedHashSet<>(allValues);

        long deadline = System.currentTimeMillis() + Duration.ofMinutes(1).toMillis();
        while (deadline > System.currentTimeMillis() && !remaining.isEmpty()) {
            remaining.remove(RND.nextValueFrom(allValues));
        }

        assertThat(remaining).as("Remaining values").isEmpty();
    }

    @Test
    @DisplayName("nextValueFromIterable: Chooses from all values")
    void nextValueFromIterable_choosesFromAllValues() {
        final List<String> list = Arrays.asList("a", "b", "c");
        Iterable<String> alleWaarden = list::iterator;
        Set<String> remaining = new LinkedHashSet<>(list);

        long deadline = System.currentTimeMillis() + Duration.ofMinutes(1).toMillis();
        while (deadline > System.currentTimeMillis() && !remaining.isEmpty()) {
            remaining.remove(RND.nextValueFrom(alleWaarden));
        }

        assertThat(remaining).as("Remaining values").isEmpty();
    }

    @Test
    @DisplayName("nextEnum: Chooses 1 of the enum values")
    void nextEnum_choosesOneOfTheEnumValues() {
        Set<ExampleEnum> allValues = EnumSet.allOf(ExampleEnum.class);
        for (int i = 0; i < 100; i++) {
            ExampleEnum result = RND.nextEnum(ExampleEnum.class);
            assertThat(result).isIn(allValues);
        }
    }

    @Test
    @DisplayName("nextValueExcept: Generator returns unique values")
    void nextValueExcept_generatorReturnsUniqueValues() {
        List<Long> values = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            long random = RND.nextValueExcept(RND::nextLong, values.toArray(new Long[0]));
            assertThat(random).isNotIn(values);
            values.add(random);
        }
    }

    @Test
    @DisplayName("nextValueExcept: Generator keeps trying with excluded value")
    void nextValueExcept_generatorKeepsTryingWithExcludedValue() {
        for (int i = 0; i < 100; i++) {
            boolean result = RND.nextValueExcept(RND::nextBoolean, Boolean.TRUE);
            assertThat(result).isFalse();
        }
    }

    @Test
    @DisplayName("nextValueExcept: Generator will give up to escape infinite loop")
    void nextValueExcept_generatorWillGiveUpToEscapeInfiniteLoop() {
        assertThatThrownBy(() -> RND.nextValueExcept(RND::nextBoolean, Boolean.TRUE, Boolean.FALSE))
                .hasMessage("No suitable random value generated in a reasonable amount of attempts.");
    }

    @Test
    @DisplayName("nextString: Fixed length matches with generated string")
    void nextString_fixedLengthMatchesWithGeneratedString() {
        final int length = 5 + RND.nextInt(10);
        for (int i = 0; i < 100; i++) {
            String result = RND.nextString(length, RandomGenerator.CAPITAL_LETTERS);
            assertThat(result).hasSize(length).matches("^[A-Z]+$");
        }
    }
}
