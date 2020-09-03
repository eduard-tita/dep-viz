# Dependencies Visualizer

![Java CI with Maven](https://github.com/eduard-tita/dep-viz/workflows/Java%20CI%20with%20Maven/badge.svg)

`Dep-Viz` is a small tool that help visualize interdependencies in a set of interconnected projects.

A dependency graph is generated. Artifact versions are nodes in this graph and multiple versions of the same artifact are grouped in clusters.
Outdated artifact versions and their dependencies(graph edges) are marked in red (see note below).     

In case the dependency graph is too large there is an option to skip rendering of the single version, not SNAPHSOT clusters 
(see the configuration template for details).

**Note:**
> Depending on the project set selected, there might be cases when not all outdated artifact versions are detected.  
> In such cases not all outdated artifact versions will be red. 
> Users will have to visually inspect the graph and decide for themselves.   

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
