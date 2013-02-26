package org.kie.commons.admin.impl;

import org.kie.commons.admin.ConfigGroup;
import org.kie.commons.admin.ConfigItem;
import org.kie.commons.admin.Repository;
import org.kie.commons.admin.RepositoryFactory;

import javax.enterprise.context.ApplicationScoped;

import static org.kie.commons.validation.Preconditions.checkNotNull;

@ApplicationScoped
public class RepositoryFactoryImpl implements RepositoryFactory {

    @Override
    public Repository newRepository(ConfigGroup repoConfig) {
        Repository repository = null;
        checkNotNull( "config", repoConfig );
        ConfigItem<String> scheme = repoConfig.getConfigItem("scheme");

        checkNotNull( "scheme", scheme );
        if ( GitRepository.SCHEME.equals( scheme.getValue() ) ) {
            repository = new GitRepository(repoConfig.getName());
            repository.addEnvironmentParameter(GitRepository.ORIGIN, repoConfig.getConfigItemValue("origin"));
            repository.addEnvironmentParameter(GitRepository.USERNAME, repoConfig.getConfigItemValue("username"));
            repository.addEnvironmentParameter(GitRepository.PASSWORD, repoConfig.getConfigItemValue("password"));

            if (!repository.isValid()) {
                throw new IllegalStateException("Repository " + repoConfig.getName() + " not valid");
            }
        } else {
            throw new IllegalArgumentException( "Unrecognized scheme '" + scheme.getValue() + "'." );
        }
        return repository;
    }
}
