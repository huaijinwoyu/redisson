/**
 * Copyright 2016 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.redisson.core;

import io.netty.util.concurrent.Future;

/**
 * Distributed alternative to the {@link java.util.concurrent.CountDownLatch}
 *
 * It has an advantage over {@link java.util.concurrent.CountDownLatch} --
 * count can be set via {@link #trySetCount} method.
 *
 * @author Nikita Koksharov
 *
 */
public interface RCountDownLatchAsync extends RObjectAsync {

    /**
     * Decrements the count of the latch, releasing all waiting threads if
     * the count reaches zero.
     *
     * <p>If the current count is greater than zero then it is decremented.
     * If the new count is zero then all waiting threads are re-enabled for
     * thread scheduling purposes.
     *
     * <p>If the current count equals zero then nothing happens.
     */
    Future<Void> countDownAsync();

    /**
     * Returns the current count.
     *
     * <p>This method is typically used for debugging and testing purposes.
     *
     * @return the current count
     */
    Future<Long> getCountAsync();

    /**
     * Sets new count value only if previous count already has reached zero
     * or is not set at all.
     *
     * @param count - number of times {@link #countDown} must be invoked
     *        before threads can pass through {@link #await}
     * @return <code>true</code> if new count setted
     *         <code>false</code> if previous count has not reached zero
     */
    Future<Boolean> trySetCountAsync(long count);

}
