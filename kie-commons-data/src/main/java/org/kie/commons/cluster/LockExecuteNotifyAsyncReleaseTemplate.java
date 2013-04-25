package org.kie.commons.cluster;

public abstract class LockExecuteNotifyAsyncReleaseTemplate<V> extends BaseLockExecuteNotifyReleaseTemplate<V> {

    @Override
    public void sendMessage( final ClusterService clusterService ) {
        clusterService.broadcast( getMessageType(), buildContent() );
    }
}
