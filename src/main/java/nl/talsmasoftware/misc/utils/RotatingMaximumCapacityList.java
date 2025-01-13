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

/**
 * A {@linkplain java.util.List List} with a maximum capacity.
 *
 * <p>
 * The list <em>rotates</em> its elements when new elements are added
 * that would exceed the maximum capacity.<br>
 * This ensures that the maximum number (<em>max</em>) of elements is not exceeded,
 * discarding the first elements, retaining the last elements.
 *
 * <p>
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access a {@code RotatingMaxSizeList} instance concurrently,
 * and at least one of the threads modifies the list structurally, it
 * <i>must</i> be synchronized externally.  (A structural modification is
 * any operation that adds or deletes one or more elements, or explicitly
 * resizes the backing array; merely setting the value of an element is not
 * a structural modification.)  This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the list.
 *
 * @param <T> The type of elements in the list.
 */
public class RotatingMaximumCapacityList<T> extends ArrayList<T> {
    private final int maximumCapacity;

    /**
     * Constructs an empty list with maximum capacity.
     *
     * @param maximumCapacity The maximum capacity of this list.
     */
    public RotatingMaximumCapacityList(int maximumCapacity) {
        this(maximumCapacity, Collections.emptySet());
    }

    /**
     * Constructs a list with maximum capacity, initially adding the elements of the specified
     * collection, in the order they are returned by the collection's iterator.
     *
     * @param maximumCapacity The maximum capacity of this list.
     * @param content
     */
    public RotatingMaximumCapacityList(int maximumCapacity, Collection<T> content) {
        super(maximumCapacity);
        if (maximumCapacity == 0) {
            throw new IllegalArgumentException("Illegal maximum size: " + maximumCapacity);
        }
        this.maximumCapacity = maximumCapacity;
        if (!content.isEmpty()) {
            this.addAll(content);
        }
    }

    /**
     * @return The maximum capacity of this list.
     */
    public int getMaximumCapacity() {
        return maximumCapacity;
    }

    @Override
    public boolean add(T element) {
        return addAll(size(), Collections.singletonList(element));
    }

    @Override
    public void add(int index, T element) {
        addAll(index, Collections.singletonList(element));
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return addAll(size(), c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> other) {
        final int maxSize = getMaximumCapacity();
        final int rotate = maxSize - this.size() - other.size();
        if (rotate >= 0) { // Everything still fits in maxSize.
            return super.addAll(index, other);
        } else {
            int skip = other.size() - maxSize;
            if (skip >= 0) { // The other collection completely fills the maximum capacity.
                index = 0;
                for (T element : other) {
                    if (skip-- <= 0) {
                        setOrAdd(index++, element);
                    }
                }
                return true;
            }
        }
        // First rotate the elements of this list, then set
        Collections.rotate(this, rotate);
        index = maxSize - other.size();
        for (T element : other) {
            setOrAdd(index++, element);
        }
        return true;
    }

    private void setOrAdd(int index, T element) {
        if (index < size()) {
            super.set(index, element);
        } else {
            super.add(index, element);
        }
    }
}
