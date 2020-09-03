# Dependencies Visualizer

![Java CI with Maven](https://github.com/eduard-tita/dep-viz/workflows/Java%20CI%20with%20Maven/badge.svg)
[![DepShield Badge](https://depshield.sonatype.org/badges/eduard-tita/dep-viz/depshield.svg)](https://depshield.github.io)

---

`Dep-Viz` is a small tool that help visualize inter-dependencies in a set of interconnected projects. 

The tool generates a dependency graph. The graph generation starts from a set of artifact/artifact groups 
(specified in the configuration file) and contains their dependency trees. In consequence, it may not always be a full dependency graph.

Artifact versions are nodes in this graph and multiple versions of the same artifact are grouped in clusters 
(shown with randomly colored backgrounds).

Released artifact versions are shown in blue. Snapshot artifact versions are shown in black. 
The tool determines which artifact versions are outdated, based on the information available (see note below).
Outdated artifact versions and their dependency paths(graph edges) are marked in red.     

In case the dependency graph is too large there is an option to skip rendering of the single version, not SNAPHSOT clusters 
(see the configuration template for details).

**Note:**
> Depending on the project set selected, there might be cases when there is not enough information to determine all outdated artifact versions.  
> In such cases not all outdated artifact versions will be red.    

## Requirements

- [ ] The user must be able to **clone** all the projects of interest with the same **credentials**.
- [ ] **Git** must be installed locally - use your OS package manager, or go to: https://git-scm.com/downloads
- [ ] **Maven** must be installed locally - use your OS package manager, or go to: https://maven.apache.org/download.cgi
- [ ] **Dot**, part of GraphViz Software Suite, must be installed locally - use your OS package manager, or go to: https://www.graphviz.org/download/ 

## Usage

```bash
java -jar dep-viz.jar config.yml
```
Replace `dep-viz.jar` with the proper jar name, including version number. e.g. `dep-viz-1.0.jar`

A template configuration file is available at `/src/main/resources/config.yml`. 
Make a copy of it, update it as needed, and pass it as the only program argument above.

## To Do

- [ ] Add tests. :smiley:
- [ ] Add Gradle support.
