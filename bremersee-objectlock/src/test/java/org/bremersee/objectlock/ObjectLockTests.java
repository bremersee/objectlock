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

import java.util.Date;
import org.junit.Test;
import junit.framework.TestCase;

/**
 * <p>
 * JUnit tests for {@link ObjectReadWriteLock} implementation.
 * </p>
 * 
 * @author Christian Bremer
 */
public class ObjectLockTests {

    @Test
    public void testWriteLockWithId() throws Exception {

        Object id = "1234";
        int threadSize = 10;
        long sleepMillis = 1000L;
        ExampleClassWithLockedMethod m = new ExampleClassWithLockedMethod(sleepMillis);
        WriteThread[] t = new WriteThread[threadSize];
        for (int i = 0; i < threadSize; i++) {
            t[i] = new WriteThread(m, id, i, true);
        }
        System.out.println("Test started: " + new Date());
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < threadSize; i++) {
            t[i].start();
        }
        for (int i = 0; i < threadSize; i++) {
            t[i].join();
        }
        long stopMillis = System.currentTimeMillis();
        System.out.println("Test stopped: " + new Date(stopMillis));
        long duration = stopMillis - startMillis;
        System.out.println("Duration: " + duration);
        TestCase.assertTrue(duration >= threadSize * sleepMillis);
    }
    
    @Test
    public void testWriteLockWithNo() throws Exception {

        Object id = "1234";
        int threadSize = 10;
        long sleepMillis = 1000L;
        ExampleClassWithLockedMethod m = new ExampleClassWithLockedMethod(sleepMillis);
        WriteThread[] t = new WriteThread[threadSize];
        for (int i = 0; i < threadSize; i++) {
            t[i] = new WriteThread(m, id, i, false);
        }
        System.out.println("Test started: " + new Date());
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < threadSize; i++) {
            t[i].start();
        }
        for (int i = 0; i < threadSize; i++) {
            t[i].join();
        }
        long stopMillis = System.currentTimeMillis();
        System.out.println("Test stopped: " + new Date(stopMillis));
        long duration = stopMillis - startMillis;
        System.out.println("Duration: " + duration);
        TestCase.assertTrue(duration >= sleepMillis && duration < 2 * sleepMillis);
    }
    
    @Test
    public void testReadLockWithId() throws Exception {

        Object id = "1234";
        int threadSize = 10;
        long sleepMillis = 1000L;
        ExampleClassWithLockedMethod m = new ExampleClassWithLockedMethod(sleepMillis);
        ReadThread[] t = new ReadThread[threadSize];
        for (int i = 0; i < threadSize; i++) {
            t[i] = new ReadThread(m, id, i, true);
        }
        System.out.println("Test read lock started: " + new Date());
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < threadSize; i++) {
            t[i].start();
        }
        for (int i = 0; i < threadSize; i++) {
            t[i].join();
        }
        long stopMillis = System.currentTimeMillis();
        System.out.println("Test read lock stopped: " + new Date(stopMillis));
        long duration = stopMillis - startMillis;
        System.out.println("Duration: " + duration);
        TestCase.assertTrue(duration >= sleepMillis && duration < 2 * sleepMillis);
    }
    
    private static class ExampleClassWithLockedMethod {
        
        private final ObjectReadWriteLock readWriteLock = new ObjectReadWriteLockImpl();
        
        private final long sleepMillis;
        
        private ExampleClassWithLockedMethod(long sleepMillis) {
            this.sleepMillis = sleepMillis;
        }
        
        private void write(Object id) {
            
            readWriteLock.lockWriting(id);
            try {
                System.out.println("Running write: " + new Date() + " [" + Thread.currentThread().getName() + "]");
                try {
                    Thread.sleep(sleepMillis);
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + " was interrupted.");
                    Thread.currentThread().interrupt();
                    return;
                }
                
            } finally {
                readWriteLock.unlockWriting(id);
            }
        }
        
        private void read(Object id) {
            
            readWriteLock.lockReading(id);
            try {
                System.out.println("Running read: " + new Date() + " [" + Thread.currentThread().getName() + "]");
                try {
                    Thread.sleep(sleepMillis);
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + " was interrupted.");
                    Thread.currentThread().interrupt();
                    return;
                }
                
            } finally {
                readWriteLock.unlockReading(id);
            }
        }
        
    }
    
    private static class WriteThread extends Thread {
        
        private ExampleClassWithLockedMethod m;
        
        private Object id;
        
        private Object no;
        
        private boolean useId;
        
        private WriteThread(ExampleClassWithLockedMethod m, Object id, int no, boolean useId) {
            super("Thread No. " + no);
            this.m = m;
            this.id = id;
            this.no = no;
            this.useId = useId;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            if (useId){
                m.write(id);
            } else {
                m.write(no);
            }
        }
    }

    private static class ReadThread extends Thread {
        
        private ExampleClassWithLockedMethod m;
        
        private Object id;
        
        private Object no;
        
        private boolean useId;
        
        public ReadThread(ExampleClassWithLockedMethod m, Object id, int no, boolean useId) {
            super("Thread No. " + no);
            this.m = m;
            this.id = id;
            this.no = no;
            this.useId = useId;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            if (useId){
                m.read(id);
            } else {
                m.read(no);
            }
        }
    }

}
