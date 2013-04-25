package org.kie.commons.message;

import java.util.Map;

import org.kie.commons.data.Pair;

public interface MessageHandler {

    Pair<MessageType, Map<String, String>> handleMessage( final MessageType type,
                                                          final Map<String, String> content );

}
