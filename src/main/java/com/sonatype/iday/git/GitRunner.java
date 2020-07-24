package com.sonatype.iday.git;

import java.io.File;
import java.util.concurrent.TimeUnit;

import com.sonatype.iday.config.GitConfig;
import com.sonatype.nexus.git.utils.api.GitApi;
import com.sonatype.nexus.git.utils.api.GitException;
import com.sonatype.nexus.git.utils.api.NativeGitApi;
import com.sonatype.nexus.scm.GitApiClientFactory;
import com.sonatype.nexus.scm.SourceControlProvider;
import com.sonatype.nexus.scm.api.GitApiClientUtils;
import com.sonatype.nexus.scm.api.model.ProjectUri;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitRunner
{
  private static final Logger log = LoggerFactory.getLogger(GitRunner.class);

  private final GitConfig gitConfig;

  private final String workdir;

  private final GitApiClientUtils gitApiClientUtils;

  public GitRunner(final GitConfig gitConfig, final String workdir) {
    this.workdir = workdir;
    this.gitConfig = gitConfig;

    GitApiClientFactory gitApiClientFactory = new GitApiClientFactory();
    gitApiClientUtils = gitApiClientFactory.getGitApiClientUtils(SourceControlProvider.GITHUB);
  }

  public File execute(String repositoryUrl) {
    log.info("Processing " + repositoryUrl + " ...");
    try {
      GitApi gitApi =
          new NativeGitApi(repositoryUrl, gitConfig.getToken(), gitConfig.getUsername(), gitConfig.getExecutable());

      ProjectUri projectUri = gitApiClientUtils.createProjectUri(repositoryUrl);
      File repository = new File(workdir, projectUri.getProject());
      if (!repository.exists()) {
        log.debug("Clone dir '{}' does not exist; creating it...", repository);
        repository.mkdirs();
      }

      syncRepository(gitApi, repository);
      return repository;
    } catch (GitException e) {
      log.error("Cannot execute mvn command", e);
    }
    return null;
  }

  private void syncRepository(final GitApi gitApi, final File repository) throws GitException {
    Stopwatch stopwatch = Stopwatch.createStarted();
    gitApi.cloneOrPullRepository(repository, "master");
    long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
    log.info("Checkout into directory '{}' in {}ms", repository, elapsed);
  }
}
