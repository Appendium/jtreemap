[![Build Status](https://secure.travis-ci.org/Appendium/jtreemap.png?branch=master)](http://travis-ci.org/Appendium/jtreemap) [![Stories in Ready](https://badge.waffle.io/Appendium/jtreemap.png?label=ready)](https://waffle.io/Appendium/jtreemap)
[![SonarQube](https://sonarcloud.io/api/project_badges/measure?project=net.sf.jtreemap:jtreemap-parent&metric=bugs)](https://sonarcloud.io/dashboard/index/net.sf.jtreemap:jtreemap-parent)
[![Sonar Reliability](https://sonarcloud.io/api/project_badges/measure?project=net.sf.jtreemap:jtreemap-parent&metric=reliability_rating)](https://sonarcloud.io/dashboard/index/net.sf.jtreemap:jtreemap-parent)
[![Sonar Security](https://sonarcloud.io/api/project_badges/measure?project=net.sf.jtreemap:jtreemap-parent&metric=security_rating)](https://sonarcloud.io/dashboard/index/net.sf.jtreemap:jtreemap-parent)
[![Maven Central jtreemap](https://maven-badges.herokuapp.com/maven-central/net.sf.jtreemap/jtreemap/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.sf.jtreemap/jtreemap)
![GitHub license](https://img.shields.io/github/license/appendium/jtreemap.svg?style=flat-square)

 [![Quality Gate](https://sonarcloud.io/api/project_badges/quality_gate?project=net.sf.jtreemap:jtreemap-parent)](https://sonarcloud.io/dashboard/index/net.sf.jtreemap:jtreemap-parent)

# JTreeMap

JTreeMap is a powerful and versatile Java library for creating heatmap charts. It can be used in various applications, including desktop applications, applets, and for generating JPG images.

## Features

*   **Multiple Algorithms**: Implements various treemap algorithms, including Squarified, Slice, and Strip.
*   **Customizable**: Provides a rich set of options for customizing the appearance of the treemap, such as colors, fonts, and borders.
*   **Swing and SWT Support**: Offers components for both Swing and SWT, making it easy to integrate into existing applications.
*   **Data Sources**: Can load data from various sources, including XML files and custom data models.
*   **Export**: Supports exporting the treemap to various formats, including JPG, PNG, and SVG.

## Getting Started

To get started with JTreeMap, you'll need to have Java 21 or later installed.

### Building the Project

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/Appendium/jtreemap.git
    ```
2.  **Navigate to the project directory**:
    ```bash
    cd jtreemap
    ```
3.  **Build the project using Maven**:
    ```bash
    mvn clean install
    ```

This will build the project and install the artifacts into your local Maven repository.

### Running the Examples

The `JTreeMapExample` module contains several examples that demonstrate how to use the library. To run the examples, you can use the following command:

```bash
mvn exec:java -pl JTreeMapExample
```

## Usage

Here's a simple example of how to create a treemap using JTreeMap:

```java
import net.sf.jtreemap.swing.JTreeMap;
import net.sf.jtreemap.swing.TreeMapNode;
import net.sf.jtreemap.swing.provider.SquarifiedAlgorithm;

import javax.swing.*;
import java.awt.*;

public class SimpleTreeMap extends JFrame {

    public SimpleTreeMap() {
        super("Simple TreeMap");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTreeMap treeMap = new JTreeMap(createData());
        treeMap.setAlgorithm(new SquarifiedAlgorithm());

        getContentPane().add(treeMap, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

    private TreeMapNode createData() {
        TreeMapNode root = new TreeMapNode("Root");
        root.add(new TreeMapNode("Node 1", 10));
        root.add(new TreeMapNode("Node 2", 20));
        root.add(new TreeMapNode("Node 3", 30));
        return root;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimpleTreeMap().setVisible(true));
    }
}
```

## API Changes

This version of JTreeMap has been upgraded to Java 21. The following are the main API changes:

*   **Java 21**: The library now requires Java 21 or later.
*   **Generics**: The codebase has been updated to use generics, which improves type safety.
*   **Removed Deprecated APIs**: Some deprecated APIs have been removed. Please refer to the Javadoc for more details.

## Contributing

Contributions are welcome! If you'd like to contribute to the project, please follow these steps:

1.  Fork the repository.
2.  Create a new branch for your changes.
3.  Make your changes and commit them.
4.  Push your changes to your fork.
5.  Create a pull request.

## License

JTreeMap is licensed under the Apache License, Version 2.0. See the `LICENSE.txt` file for more details.
