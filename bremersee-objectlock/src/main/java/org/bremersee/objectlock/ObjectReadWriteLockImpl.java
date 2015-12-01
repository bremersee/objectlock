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

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * <p>
 * Default implementation of the {@link ObjectReadWriteLock}.
 * </p>
 * 
 * @author Christian Bremer
 */
public class ObjectReadWriteLockImpl implements ObjectReadWriteLock {

    private final ObjectPool<ReentrantReadWriteLock> pool;

    private final HashMap<Object, ReentrantReadWriteLock> locks = new HashMap<Object, ReentrantReadWriteLock>();

    /**
     * Default constructor.
     */
    public ObjectReadWriteLockImpl() {
        this(false, -1, -1);
    }

    /**
     * Construct a read write lock with the specified ordering policy.
     * 
     * @param fair
     *            {@code true} if this lock should use a fair ordering policy
     */
    public ObjectReadWriteLockImpl(boolean fair) {
        this(fair, -1, -1);
    }

    /**
     * Construct a read write lock with the specified ordering policy.
     * 
     * @param fair
     *            {@code true} if this lock should use a fair ordering policy
     * @param minIdle
     *            the minimum number of objects allowed in the pool
     * @param maxIdle
     *            the cap on the number of idle instances in the pool, use a
     *            negative value to indicate an unlimited number of idle
     *            instances
     */
    public ObjectReadWriteLockImpl(boolean fair, int minIdle, int maxIdle) {
        GenericObjectPool.Config config = new GenericObjectPool.Config();
        config.maxActive = -1;
        config.minIdle = minIdle >= 8 ? minIdle : 8;
        config.maxIdle = maxIdle < 0 ? maxIdle : (maxIdle >= 32 ? maxIdle : 32);
        this.pool = new GenericObjectPool<ReentrantReadWriteLock>(
                new ReentrantReadWriteLockFactory(fair), config);
    }

    private ReentrantReadWriteLock borrowLock() {
        try {
            return pool.borrowObject();

        } catch (NoSuchElementException e) {
            throw e;
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new ObjectLockException(e);
        }
    }

    private void returnLock(ReentrantReadWriteLock lock) {
        if (lock != null) {
            try {
                pool.returnObject(lock);

            } catch (Exception e) {
                throw new ObjectLockException(e);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.bremersee.objectlock.ObjectReadWriteLock#lockReading(java.lang.Object)
     */
    @Override
    public void lockReading(Object obj) {

        if (obj == null) {
            return;
        }
        ReentrantReadWriteLock l;
        synchronized (locks) {
            l = locks.get(obj);
            if (l == null) {
                l = borrowLock();
                locks.put(obj, l);
            }
        }
        l.readLock().lock();

    }

    /* (non-Javadoc)
     * @see org.bremersee.objectlock.ObjectReadWriteLock#unlockReading(java.lang.Object)
     */
    @Override
    public void unlockReading(Object obj) {

        if (obj == null) {
            return;
        }
        synchronized (locks) {
            ReentrantReadWriteLock l = locks.get(obj);
            if (l != null) {
                l.readLock().unlock();
                if (l.getQueueLength() == 0) {
                    l = locks.remove(obj);
                    returnLock(l);
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.bremersee.objectlock.ObjectReadWriteLock#lockWriting(java.lang.Object)
     */
    @Override
    public void lockWriting(Object obj) {

        if (obj == null) {
            return;
        }
        ReentrantReadWriteLock l;
        synchronized (locks) {
            l = locks.get(obj);
            if (l == null) {
                l = borrowLock();
                locks.put(obj, l);
            }
        }
        l.writeLock().lock();
    }

    /* (non-Javadoc)
     * @see org.bremersee.objectlock.ObjectReadWriteLock#unlockWriting(java.lang.Object)
     */
    @Override
    public void unlockWriting(Object obj) {

        if (obj == null) {
            return;
        }
        synchronized (locks) {
            ReentrantReadWriteLock l = locks.get(obj);
            if (l != null) {
                l.writeLock().unlock();
                if (l.getQueueLength() == 0) {
                    l = locks.remove(obj);
                    returnLock(l);
                }
            }
        }
    }

}
