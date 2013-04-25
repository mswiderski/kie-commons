package org.kie.commons.lock;

import java.util.concurrent.RunnableFuture;

import org.kie.commons.lock.LockService;

public class LockExecuteReleaseTemplate<V> {

    public V execute( final LockService lock,
                      final RunnableFuture<V> task ) {
        try {
            lock.lock();

            task.run();

            return task.get();
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        } finally {
            lock.unlock();
        }
    }
}
