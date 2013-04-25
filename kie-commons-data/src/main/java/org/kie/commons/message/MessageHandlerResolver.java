package org.kie.commons.message;

public interface MessageHandlerResolver {

    public MessageHandler resolveHandler( final MessageType type );

}
