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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toCollection;

/**
 * Class extending the standard {@linkplain Random} with richer generators.
 *
 * <p>
 * This can for instance be used in unit tests to more easily generate random test data.
 *
 * <p>
 * All base methods from the {@link Random} class remain available.
 * Besides that, there are additional methods available for example {@code nextEnum(enumType)}.
 */
public class RandomGenerator extends Random {
    /**
     * The lowercase letters {@code a..z}.
     *
     * <p>
     * Useful for generating {@linkplain #nextString(int, CharSequence) random strings}.
     */
    public static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";

    /**
     * The uppercase letters {@code A..Z}.
     *
     * <p>
     * Useful for generating {@linkplain #nextString(int, CharSequence) random strings}.
     */
    public static final String CAPITAL_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * The numbers {@code 0..9}.
     *
     * <p>
     * Useful for generating {@linkplain #nextString(int, CharSequence) random strings}.
     */
    public static final String NUMBERS = "0123456789";

    /**
     * The letters {@code a..z} and {@code A..Z}.
     *
     * <p>
     * Useful for generating {@linkplain #nextString(int, CharSequence) random strings}.
     *
     * @see #LOWERCASE_LETTERS
     * @see #CAPITAL_LETTERS
     */
    public static final String LETTERS = LOWERCASE_LETTERS + CAPITAL_LETTERS;

    /**
     * {@linkplain #LETTERS Letters} {@code a..z + A..Z} and spaces {@code ' '}.
     *
     * <p>
     * Useful for generating {@linkplain #nextString(int, CharSequence) random strings}.
     */
    public static final String LETTERS_AND_SPACES = LETTERS + "     ";

    /**
     * The {@linkplain #NUMBERS numbers} {@code 0..9} and {@linkplain #LETTERS letters} {@code a..z + A..Z}.
     *
     * <p>
     * Useful for generating {@linkplain #nextString(int, CharSequence) random strings}.
     *
     * @see #NUMBERS
     * @see #LETTERS
     */
    public static final String NUMBERS_AND_LETTERS = NUMBERS + LETTERS;

    /**
     * {@linkplain #NUMBERS Numbers} {@code 0..9}, {@linkplain #LETTERS letters} {@code a..z + A..Z} and spaces {@code ' '}.
     *
     * <p>
     * Useful for generating {@linkplain #nextString(int, CharSequence) random strings}.
     *
     * @see #NUMBERS
     * @see #LETTERS_AND_SPACES
     */
    public static final String NUMBERS_LETTERS_AND_SPACES = NUMBERS + LETTERS_AND_SPACES;

    /**
     * The hexadecimal characters: {@code 0..9} + {@code A..F}.
     *
     * <p>
     * Useful for generating {@linkplain #nextString(int, CharSequence) random strings}.
     */
    public static final String HEXADECIMALS = NUMBERS + CAPITAL_LETTERS.substring(0, 6);

    /**
     * Randomly choose one of the specified values.
     *
     * <p>
     * At least one value must be specified to choose from,
     * although that would still be a rather predictable random choice ;-).
     *
     * @param values The values to randomly choose from.
     * @param <T>    The type of the objects to choose.
     * @return One of the given values.
     * @see #nextValueFrom(Iterable)
     */
    @SafeVarargs
    public final <T> T nextValueFrom(T... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("No values to randomly choose from.");
        }
        return values[super.nextInt(values.length)];
    }

    /**
     * Randomly choose one of the specified values.
     *
     * <p>
     * At least one value must be specified to choose from,
     * although that would still be a rather predictable random choice ;-).
     *
     * @param values The values to randomly choose from.
     * @param <T>    The type of the objects to choose.
     * @return One of the given values.
     * @see #nextValueFrom(Object[])
     */
    @SuppressWarnings("unchecked")
    public final <T> T nextValueFrom(Iterable<? extends T> values) {
        return (T) nextValueFrom(values instanceof Collection
                ? ((Collection<Object>) values).toArray(new Object[0])
                : StreamSupport.stream(values.spliterator(), false).toArray(Object[]::new));
    }

    /**
     * Random enum of a given type.
     *
     * @param enumType The enum type to choose a constant from.
     * @param <E>      The enum type.
     * @return One of the enum constants.
     */
    public <E extends Enum<E>> E nextEnum(Class<E> enumType) {
        return nextValueFrom(enumType.getEnumConstants());
    }

    /**
     * A next random value that is not one of the given exceptions.
     *
     * @param generator  Generator for the next random value (bijv. {@code RND::nextEnum}).
     * @param exceptions Values that must be excluded from the results.
     * @param <T>        The result type.
     * @return Next random waarde that is not one of the given exceptions.
     * @see #nextValueExcept(Supplier, Collection)
     */
    @SafeVarargs
    public final <T> T nextValueExcept(Supplier<T> generator, T... exceptions) {
        return nextValueExcept(generator, Arrays.asList(exceptions));
    }

    /**
     * A next random value that is not one of the given exceptions.
     *
     * @param generator  Generator for the next random value (bijv. {@code RND::nextEnum}).
     * @param exceptions Values that must be excluded from the results.
     * @param <T>        The result type.
     * @return Next random waarde that is not one of the given exceptions.
     * @see #nextValueExcept(Supplier, Object[])
     */
    public final <T> T nextValueExcept(Supplier<T> generator, Collection<? extends T> exceptions) {
        T randomValue = generator.get();
        int remainingAttempts = 100000;
        while (exceptions.contains(randomValue)) {
            if (remainingAttempts-- <= 0) { // 'emergency brake' to prevent endless loop
                throw new IllegalStateException("No suitable random value generated in a reasonable amount of attempts.");
            }
            randomValue = generator.get();
        }
        return randomValue;
    }

    /**
     * Random String of a fixed length.
     *
     * @param length     The length the resulting String must have.
     * @param characters The characters that may occur in the random String.
     * @return A random String of the specified {@code length},
     * consisting of the specified {@code characters}.
     * @see #nextString(int, int, CharSequence)
     */
    public String nextString(int length, CharSequence characters) {
        return nextString(length, length, characters);
    }

    /**
     * Random String.
     *
     * @param minLength  The minimum length the resulting String must have (inclusive).
     * @param maxLength  The maximum length the resulting String must have (inclusive).
     * @param characters The characters that may occur in the random String.
     * @return A random String of length between {@code minLength} and {@code maxLength} (both inclusive),
     * consisting of the specified {@code characters}.
     * @see #nextString(int, CharSequence)
     */
    public String nextString(int minLength, int maxLength, CharSequence characters) {
        final int min = Math.max(0, Math.min(minLength, maxLength)); // 0 <= minLength <= [MIN]
        final int max = Math.max(min, Math.max(minLength, maxLength)); // [min] <= [MAX] <= maxLength
        char[] chars = new char[min + nextInt(1 + max - min)];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = nextCharacterFrom(characters);
        }
        return new String(chars);
    }

    /**
     * Random character from a sequence.
     *
     * @param characters The characters to choose from randomly (required, may not be null or empty).
     * @return A random character from the specified sequence.
     */
    public char nextCharacterFrom(CharSequence characters) {
        return characters.charAt(nextInt(characters.length()));
    }

    /**
     * Shuffle characters and returns them in random order as a new String.
     *
     * @param characters The characters to be shuffled.
     * @return The specified characters in random order as a new String.
     */
    public String shuffleCharacters(CharSequence characters) {
        List<Character> chars = characters.chars().mapToObj(ch -> (char) ch).collect(toCollection(ArrayList::new));
        Collections.shuffle(chars, this);
        return chars.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
    }
}
