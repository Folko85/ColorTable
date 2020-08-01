# ColorTable
Table of Colors

The library is based on a simplified implementation of the KD-tree bucket method.
It may be further developed, but this is not accurate.

To connect the repository via Maven, add to pom.xml

    <repositories>
        <repository>
            <id>ColorTable-mvn-repo</id>
            <url>https://raw.github.com/Folko85/ColorTable/mvn-repo/</url>
            <snapshots>
                <enabled>true</enabled>
                <updatePolicy>always</updatePolicy>
            </snapshots>
        </repository>
    </repositories>
    
also add to dependencies library:
    
    <dependencies>
        <dependency>
            <groupId>ru.folko85</groupId>
            <artifactId>ColorTable</artifactId>
            <version>1.0.5</version>
        </dependency>
    </dependencies>
    
After that you must in IDEA,  in context menu of pom.xml choose Maven -> reimport

P.S. The dependency may be highlighted in red until the library is imported.
If it remains red even after import, check the data is entered correctly.

Example:

    String hexCode = FF00FF;
    ColorTable colorTable = new ColorTable(new Locale("en"));
    String color = colorTable.findNamedColorFromHex(hexCode);
    System.out.println(color);