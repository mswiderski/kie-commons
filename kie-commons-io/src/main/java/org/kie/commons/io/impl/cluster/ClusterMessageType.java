package org.kie.commons.io.impl.cluster;

import org.kie.commons.message.MessageType;

public enum ClusterMessageType implements MessageType {
    NEW_FS, SYNC_FS, QUERY_FOR_FS, QUERY_FOR_FS_RESULT;
}
