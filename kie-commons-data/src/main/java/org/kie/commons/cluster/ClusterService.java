package org.kie.commons.cluster;

import org.kie.commons.lock.LockService;
import org.kie.commons.message.MessageService;

public interface ClusterService extends MessageService,
                                        LockService {

    void dispose();
}
