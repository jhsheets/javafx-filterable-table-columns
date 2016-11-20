# javafx-filterable-table-columns
## Description
Provides a set of JavaFX ```TableColumn```'s widgets that allow the user to create filters.  This library only concerns itself with the UI portion, leaving the filtering up to the user.

![Filter Headers](https://cloud.githubusercontent.com/assets/3843833/20464908/cc2d4e6a-af1f-11e6-9bbd-9ff400e926c9.png)
![Numeric Filter](https://cloud.githubusercontent.com/assets/3843833/20464905/cc2c6ba8-af1f-11e6-9a5a-c96f8af54bca.png)
![Text Filter](https://cloud.githubusercontent.com/assets/3843833/20464907/cc2cfe1a-af1f-11e6-8ad7-c27499da88d3.png)
![Choice Filter](https://cloud.githubusercontent.com/assets/3843833/20464909/cc2e1e12-af1f-11e6-8445-70f5d9a3c093.png)
![DateTime Filter](https://cloud.githubusercontent.com/assets/3843833/20464906/cc2cb888-af1f-11e6-8fc7-689dfe65c48a.png)
![Boolean Filter](https://cloud.githubusercontent.com/assets/3843833/20464904/cc2b7806-af1f-11e6-9430-318cf72789b2.png)

## Use
Preferred Method
The simplest way to use this library is to use the ```FilteredTableView``` class as a drop-in replacement for a ```TableView```.

Then select the type of filtered column you wish to use as a drop-in replacement for the normal ```TableColumn``` class. There are built-in column filters for the following Java types:

* String
* Boolean
* Object Arrays / Enums
* Calendar
* Byte
* Short
* Integer
* Long
* Double
* Float
* BigInteger
* BigDecimal

To react to changes in the table filter, add an ```EventHandler``` of type ```ColumnFilterEvent.FILTER_CHANGED_EVENT``` to your ```FilteredTableColumn```. You may also access the bound filtered columns on the table via exposed properties to see which columns are currently filtered, and to fetch the column filters.

Each returned filter has a Type and a Value. You can use this information to determine how to filter properly filter the data in the ```ObservableList``` backing your ```TableView```.

## Dependencies
* Java 1.7+
* JavaFX 2.2
* SL4FJ http://www.slf4j.org/

## Misc
Calendar widget code taken from Christian Schudt from http://myjavafx.blogspot.com/2012/01/javafx-calendar-control.html

## Example
There's an example GUI in the test folder that shows how to configure and setup a filterable table with a few different column types.

## Download
To download via maven, add the following to your settings.xml or pom.xml
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
And add the dependency to your pom.xml file
```xml
<dependency>
    <groupId>com.github.jhsheets</groupId>
    <artifactId>javafx-filterable-table-columns</artifactId>
    <version>1.0.1</version>
</dependency>
```
