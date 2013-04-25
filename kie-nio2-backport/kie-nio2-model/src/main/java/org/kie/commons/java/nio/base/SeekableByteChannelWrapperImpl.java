package org.kie.commons.java.nio.base;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.kie.commons.java.nio.IOException;
import org.kie.commons.java.nio.channels.SeekableByteChannel;

import static org.kie.commons.validation.Preconditions.*;

/**
 *
 */
public class SeekableByteChannelWrapperImpl
        implements SeekableByteChannel {

    private final SeekableByteChannel channel;

    public SeekableByteChannelWrapperImpl( final SeekableByteChannel channel ) {
        this.channel = checkNotNull( "channel", channel );
    }

    @Override
    public long position() throws IOException {
        return channel.position();
    }

    @Override
    public SeekableByteChannel position( final long newPosition ) throws IOException {
        return channel.position( newPosition );
    }

    @Override
    public long size() throws IOException {
        return channel.size();
    }

    @Override
    public SeekableByteChannel truncate( final long size ) throws IOException {
        return channel.truncate( size );
    }

    @Override
    public int read( final ByteBuffer dst ) throws java.io.IOException {
        return channel.read( dst );
    }

    @Override
    public int write( final ByteBuffer src ) throws java.io.IOException {
        return channel.write( src );
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    @Override
    public void close() throws java.io.IOException {
        channel.close();
    }
}
