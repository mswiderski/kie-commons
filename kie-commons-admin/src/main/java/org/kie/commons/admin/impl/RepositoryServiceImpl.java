package org.kie.commons.admin.impl;

import org.kie.commons.admin.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class RepositoryServiceImpl implements RepositoryService {

    @Inject
    private ConfigurationService configurationService;
    @Inject
    private RepositoryFactory repoFactory;

    private Map<String, Repository> knownRepos = new HashMap<String, Repository>();

    @PostConstruct
    public void loadRepositories() {
        List<ConfigGroup> repoConfigs = configurationService.getConfiguration(ConfigType.REPOSITORY);

        if (repoConfigs != null) {
            for (ConfigGroup config : repoConfigs) {
                Repository repository = repoFactory.newRepository(config);
                knownRepos.put(repository.getAlias(), repository);
            }
        }
    }

    @Override
    public Repository getRepository(String alias) {

        return knownRepos.get(alias);
    }

    @Override
    public Collection<Repository> getRepositories() {

        return Collections.unmodifiableCollection(knownRepos.values());
    }

    @Override public void addRepository(ConfigGroup repositoryConfig) {

        Repository repository = repoFactory.newRepository(repositoryConfig);

        configurationService.addConfiguration(repositoryConfig);
        knownRepos.put(repository.getAlias(), repository);
    }

}
