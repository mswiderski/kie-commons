package org.kie.commons.cluster;

import org.kie.commons.message.MessageHandlerResolver;

public interface ClusterServiceFactory {

    ClusterService build( final MessageHandlerResolver resolver );
}
