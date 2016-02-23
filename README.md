# javafx-filterable-table-columns
## Description
Provides a set of JavaFX ```TableColumn```'s widgets that allow the user to create filters.  This library only concerns itself with the UI portion, leaving the filtering up to the user.

<img align="right" width="200" src="https://javafx-filterable-table-columns.googlecode.com/files/calendar-filter.png">
![UI Example-1](https://javafx-filterable-table-columns.googlecode.com/files/demo.png)
![UI Example-2](https://javafx-filterable-table-columns.googlecode.com/files/text-filter.png)
![UI Example-3](https://javafx-filterable-table-columns.googlecode.com/files/enum-filter.png)
![UI Example-3](https://javafx-filterable-table-columns.googlecode.com/files/number-filter.png)

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
