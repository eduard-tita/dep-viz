package ca.objectscape.depviz.config


import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class ConfigurationTest
    extends Specification
{
  @Rule
  TemporaryFolder temporaryFolder = new TemporaryFolder()

  def 'load works fine'() {
    given: 'a test config file'
      String configFile = getClass().getResource('/config/config-test.yml').getFile()

    when:
      def configuration = Configuration.load(configFile)

    then: 'repost section is fine'
      configuration
      configuration.workDirectory == '/home/user/tmp/dep-viz'
      configuration.repos.size() == 2
      configuration.repos[0].url == 'https://github.com/org/repo-1'
      !configuration.repos[0].directory
      configuration.repos[1].url == 'https://github.com/org/repo-2'
      configuration.repos[1].directory == 'parent-dir'

    and: 'gitConfig section is fine'
      configuration.gitConfig.executable == '/usr/bin/git'
      configuration.gitConfig.username == 'valid-username-here'
      configuration.gitConfig.token == 'valid-token-here'

    and: 'mavenConfig section is fine'
      configuration.mavenConfig.executable == '/usr/bin/mvn'
      configuration.mavenConfig.goal == 'dependency:tree'
      configuration.mavenConfig.scope == 'compile'
      configuration.mavenConfig.includes == 'com.target.package.*::jar,org.target.package.*::jar'

    and: 'graphvizConfig section is fine'
      configuration.graphvizConfig.executable == '/usr/bin/dot'
      configuration.graphvizConfig.format == 'svg'
      configuration.graphvizConfig.output == 'dep-graph.svg'

    and: 'features section is fine'
      configuration.features
      configuration.features.get('ignoreSingle') == false
  }

  def 'load handles errors'() {
    given: 'a non-existent config file'
      String configFile = 'non-existent-config.yml'

    when:
      Configuration.load(configFile)

    then:
      thrown(FileNotFoundException.class)
  }
}
