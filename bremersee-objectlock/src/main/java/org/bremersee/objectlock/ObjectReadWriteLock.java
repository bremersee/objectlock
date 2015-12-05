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

import java.util.concurrent.locks.ReadWriteLock;

/**
 * <p>
 * This interface defines methods for using a <tt>ReadWriteLock</tt> mechanism
 * for a specified object.
 * </p>
 * <p>
 * The specified object may be for example the user name of a person entity and
 * the method, that updates this person is only locked for this person:<br/>
 * </p>
 * <pre>
 * public void update(Person person) {
 *     final String userName = person.getUserName();
 *     objectReadWriteLock.lockWriting(userName);
 *     try {
 *         // update person
 * 
 *     } finally {
 *         objectReadWriteLock.unlockWriting(userName);
 *     }
 * }
 * </pre>
 * 
 * @see ReadWriteLock
 * 
 * @author Christian Bremer
 */
public interface ObjectReadWriteLock {

    /**
     * Creates a read lock for the specified object.
     * 
     * @param obj
     *            an object
     */
    void lockReading(Object obj);

    /**
     * Releases the read lock for the specified object.
     * 
     * @param obj
     *            on object
     */
    void unlockReading(Object obj);

    /**
     * Creates a write lock for the specified object.
     * 
     * @param obj
     *            on object
     */
    void lockWriting(Object obj);

    /**
     * Releases a write lock for the specified object.
     * 
     * @param obj
     *            an object
     */
    void unlockWriting(Object obj);

}
