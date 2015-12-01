/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bremersee.objectlock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.pool.BasePoolableObjectFactory;

/**
 * <p>
 * Factory for creating {@link ReentrantReadWriteLock} instances of the pool.
 * </p>
 * 
 * @author Christian Bremer
 */
class ReentrantReadWriteLockFactory extends
        BasePoolableObjectFactory<ReentrantReadWriteLock> {

    private final boolean fair;

    /**
     * Default constructor.
     */
    public ReentrantReadWriteLockFactory() {
        this(false);
    }

    /**
     * Construct a factory with the specified ordering policy.
     * 
     * @param fair
     *            {@code true} if this lock should use a fair ordering policy
     */
    public ReentrantReadWriteLockFactory(boolean fair) {
        this.fair = fair;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.pool.BasePoolableObjectFactory#makeObject()
     */
    @Override
    public ReentrantReadWriteLock makeObject() throws Exception {
        return new ReentrantReadWriteLock(fair);
    }

}
