package org.kie.commons.admin.impl;

import org.kie.commons.admin.Repository;

import java.util.HashMap;
import java.util.Map;

import static org.kie.commons.validation.Preconditions.checkNotNull;

public class GitRepository implements Repository {

    private String alias;
    private boolean bootstrap;
    private Map<String, String> environment = new HashMap<String, String>();

    public static final String SCHEME = "git";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String ORIGIN = "origin";

    public GitRepository() {
    }

    public GitRepository( final String alias ) {
        checkNotNull( "alias", alias );
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return this.alias;
    }

    @Override
    public String getScheme() {
        return SCHEME;
    }

    @Override
    public Map<String, String> getEnvironment() {
        return this.environment;
    }

    @Override
    public void addEnvironmentParameter( final String key,
                                         final String value ) {
        checkNotNull( "key", key );
        checkNotNull( "value", value );
        this.environment.put( key, value );
    }

    @Override
    public boolean getBootstrap() {
        return this.bootstrap;
    }

    @Override
    public void setBootstrap( boolean bootstrap ) {
        this.bootstrap = bootstrap;
    }

    @Override
    public boolean isValid() {
        final String username = environment.get( USERNAME );
        final String password = environment.get( PASSWORD );
        final String origin = environment.get( ORIGIN );
        return alias != null &&
                username != null &&
                password != null &&
                origin != null;
    }

}
