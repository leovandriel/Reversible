Reversible
==========

*Reversible is a Java library of undo datastructures.*

About
-----
Reversible offers undoable datastructures, e.g. java.util.List, by storing undo information for every atomic action. It thereby abstracts this undo behind the individual datastructure interfaces, allowing integration with exiting code bases with minimal impact.

Build using Ant
---------------
Reversible can be build using [Apache Ant](http://ant.apache.org/). The build script is located in `build.xml` and can be configured in `build.properties`.

An executable jar can be built by running: `ant all`

License
-------
Reversible is licensed under the terms of the Apache License version 2.0, see the included LICENSE file.

