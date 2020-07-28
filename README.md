# ColorTable
Таблица цветов

Библиотека основана на упрощённой реализации метода вёдер kd-дерева. Возможно будет дорабатываться, но это не точно.

Чтобы подключить репозиторий через Maven добавьте в pom.xml

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
    
    а также добавить в зависимости саму библиотеку:
    
    <dependencies>
        <dependency>
            <groupId>ru.folko85</groupId>
            <artifactId>ColorTable</artifactId>
            <version>1.0.3</version>
        </dependency>
    </dependencies>
    
После этого следует в IDEA, в контекстном меню файла pom.xml выбрать Maven -> reimport
Внимание!!! Версии ниже 1.0.3 работают некорректно.
P.S. Зависимость может подсвечиваться красным, пока не выполнен импорт библиотеки.
Но если она остаётся красным и после импорта - проверьте правильность ввода данных.
