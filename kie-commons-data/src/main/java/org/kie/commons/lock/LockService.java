package org.kie.commons.lock;

public abstract interface LockService {

    void lock();

    void unlock();
}
