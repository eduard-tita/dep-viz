package ca.objectscape.depviz.git;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ca.objectscape.depviz.config.GitConfig;
import ca.objectscape.depviz.util.Digester;
import ca.objectscape.depviz.util.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.unmodifiableList;

public class GitRunner
{
  private static final Logger log = LoggerFactory.getLogger(GitRunner.class);

  private final GitConfig gitConfig;

  private final String workdir;

  private final String[] environment;

  public GitRunner(final GitConfig gitConfig, final String workdir) throws IOException {
    this.workdir = workdir;
    this.gitConfig = gitConfig;
    // git auth file
    File authFile = createAuthCredentialFile();
    // setup git cmd env
    List<String> env = new LinkedList<>();
    env.add("GIT_TOKEN" + "=" + gitConfig.getToken());
    env.add("GIT_USERNAME" + "=" + gitConfig.getUsername());
    env.add("GIT_TERMINAL_PROMPT" + "=0");
    env.add("GIT_ASKPASS" + "=" + authFile.getAbsolutePath());
    env.add("SSH_ASKPASS" + "=" + authFile.getAbsolutePath());
    environment = env.toArray(new String[]{});
  }

  public File execute(String repositoryUrl) {
    log.info("Processing " + repositoryUrl + " ...");
    try {
      String localDirName = Digester.sha1Digest(repositoryUrl);
      File repoDir = new File(workdir, localDirName);
      if (!repoDir.exists()) {
        log.debug("Repository dir '{}' does not exist; creating it...", repoDir);
        repoDir.mkdirs();
        cloneRepository(repositoryUrl, repoDir);
      } else {
        updateRepository(repoDir);
      }

      return repoDir;
    } catch (Exception e) {
      log.error("Cannot execute git command", e);
    }
    return null;
  }

  private void cloneRepository(final String repositoryUrl, final File directory) throws IOException {
    Stopwatch stopwatch = new Stopwatch();
    String cmd = "clone --depth 1 --no-checkout --branch master " + repositoryUrl + " .";
    runGitCommand(cmd, directory);
    runGitCommand("config core.sparseCheckout true", directory);
    createSparseCheckout(directory);
    runGitCommand("checkout master", directory);
    long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
    log.info("Cloned into directory '{}' in {}ms", directory, elapsed);
  }

  private static final List<String> UPDATE_REPO_CMDS = Arrays.asList(
      "clean --force -d -x",
      "checkout master",
      "fetch --depth 1 origin",
      "reset --hard origin/master"
  );

  private void updateRepository(final File directory) throws IOException {
    Stopwatch stopwatch = new Stopwatch();
    for (String cmd : UPDATE_REPO_CMDS) {
      runGitCommand(cmd, directory);
    }
    long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
    log.info("Updated directory '{}' in {}ms", directory, elapsed);
  }

  private static final List<String> SPARSE_CHECKOUT_FILES = unmodifiableList(
      Arrays.asList("pom.xml", "MANIFEST.MF", "feature.xml", "*.target"));

  private void createSparseCheckout(final File target) throws IOException {
    Path path = Paths.get(target.getAbsolutePath(), ".git", "info", "sparse-checkout");
    path.getParent().toFile().mkdirs();
    Files.write(path, SPARSE_CHECKOUT_FILES, StandardCharsets.UTF_8);
  }

  private int runGitCommand0(final String command, final File directory) {
    try {
      log.trace("Executing [{}] in {} ...", command, workdir);
      Process process = Runtime.getRuntime().exec(gitConfig.getExecutable() + " " + command, environment, directory);
      return process.waitFor();
    } catch (IOException | InterruptedException e) {
      log.error("Cannot execute git command", e);
    }
    return -1;
  }

  private void runGitCommand(final String command, final File directory) throws IOException {
    int retCode = runGitCommand0(command, directory);
    if (retCode != 0) {
      throw new IOException("Git command [" + command + "] returned " + retCode);
    }
  }

  /**
   * Create a git credential helper in .git_auth folder
   */
  private File createAuthCredentialFile() throws IOException {
    boolean isWindows = System.getProperty("os.name").startsWith("Windows");
    Path path = Paths.get(workdir, ".git_auth", "git_auth" + (isWindows ? ".bat" : ".sh"));
    path.getParent().toFile().mkdirs();

    File file = path.toFile();
    byte[] authContent = isWindows ? WINDOWS_AUTH_FILE_CONTENT.getBytes() : UNIX_AUTH_FILE_CONTENT.getBytes();
    Files.write(path, authContent);
    file.setExecutable(true);
    return file;
  }

  private static final String WINDOWS_AUTH_FILE_CONTENT =
      "@set arg=%~1\r\n" +
      "@if (%arg:~0,8%)==(Password) echo %GIT_TOKEN%\r\n" +
      "@if (%arg:~0,8%)==(Username) echo %GIT_USERNAME%\r\n";

  private static final String UNIX_AUTH_FILE_CONTENT =
      "#!/bin/sh\n" +
      "case \"$1\" in\n" +
      "Username*) echo \"$GIT_USERNAME\" ;;\n" +
      "Password*) echo \"$GIT_TOKEN\" ;;\n" +
      "esac\n";
}
