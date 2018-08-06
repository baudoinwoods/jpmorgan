#J.P Morgan Java Technical Test

##Daily Trade Reporting Engine

Code produced in the recruitment process for J.P Morgan.

To package the application you will need Maven and Java 8.
Run `mvn clean install` at the root level of the project.

The executable jar file is generated in the target subfolder.

To start the application you will need Java 8 or higher JRE installed.

```
Usage of ReportBuilder:
java -jar reportBuilder.jar -f settlementFileName [OPTIONS]
OPTIONS:
 -c {propertiesFileName} : a currency property file with configuration for currency settlement dates
 -df {format} : the date format used in the settlement file - default dd MMM yyyy (see https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html)
 -s {separator} : the filed separator used in the settlement file - default " " (single space)
```  

Format for the currency property file:
```
# AED settlement on Sunday(1) to Thursday(4)
currency.settlementDays.AED=1,2,3,4,5
# SAR settlement on Sunday(1) to Thursday(4)
currency.settlementDays.SAR=1,2,3,4,5
# Default settlement days : Monday(2) to Friday(5)
currency.default=2,3,4,5,6

# Add other currencies as needed 
# with the key being currency.settlementDays.CURRENCY_CODE 
# and the value being a comma separated list of day numbers starting at 1 for Sunday 
```

Example default format for the settlement file

```
foo B 0.50 SGP 01 Jan 2016 02 Jan 2016 200 100.25
foo B 0.70 AED 01 Jan 2016 02 Jan 2016 200 100.25
foo S 0.40 SAR 01 Jan 2016 09 Jan 2016 200 100.25
bar B 1.20 GBP 01 Jan 2016 02 Jan 2016 200 100.25
foo S 0.50 SGP 01 Jan 2016 02 Jan 2016 200 100.25
```

Example format for settlement file with yyyyMMdd date format (use `-df yyyyMMdd` on the command line)

```
foo B 0.50 SGP 20160101 20160102 200 100.25
foo B 0.70 AED 20160101 20160102 200 100.25
foo S 0.40 SAR 20160101 20160109 200 100.25
bar B 1.20 GBP 20160101 20160102 200 100.25
foo S 0.50 SGP 20160101 20160102 200 100.25
```

Example format for settlement file with yyyyMMdd date format and ',' as field separator (use `-df yyyyMMdd -s ,` on the command line)

```
foo,B,0.50,SGP,20160101,20160102,200,100.25
foo,B,0.70,AED,20160101,20160102,200,100.25
foo,B,0.40,SAR,20160101,20160109,200,100.25
bar,B,1.20,GBP,20160101,20160102,200,100.25
foo,B,0.50,SGP,20160101,20160102,200,100.25
```