# PST.AG-Assessment

A Spring Boot application that parses and processes car data from both XML and CSV files with advanced filtering, sorting, and output formatting capabilities.

## Features

### Data Processing
- Reads car information from:
    - XML files (structured car data)
    - CSV files (brand and release dates)
- Combines data from both sources

### Filtering Options
1. By Brand and Price Range
2. By Brand and Release Date (supports yyyy,dd,mm format)

### Sorting Options
1. By Release Date (newest to oldest)
2. By Price (highest to lowest)
3. Special Currency Sorting (Optional):
    - SUVs sorted by EUR price
    - Sedans sorted by JPY price
    - Trucks sorted by USD price

### Output Formats
- Table view (console)
- XML
- JSON

## Technology Stack
- Framework: Spring Boot 3.4.4
- Java: 17
- Build Tool: Maven
- Dependencies:
    - Spring Boot Starter
    - Lombok (for code simplification)
    - Spring Boot Test (for testing)
- XML Processing: Jackson XML (implied by Spring Boot)
- Date Handling: Java Time API

## Getting Started

### Prerequisites
- JDK 17
- Maven 3.6+

### Installation
1. git clone https://github.com/zakizainul93/PST.AG-Assessment.git
2. cd car-data-processor
3. mvn clean install

### Usage
Run the application:
mvn spring-boot:run

Follow the interactive menu to:
1. Load data files
2. Apply filters
3. Choose sorting options
4. Select output format

## Project Structure
- Group ID: ag.pst
- Artifact ID: assessment
- Version: 0.0.1-SNAPSHOT

## Data Format Examples

### CSV Format
```csv
Brand,ReleaseDate
Toyota,01/15/2023
Honda,11/20/2022
```

### XML Format
```xml
<cars>
    <car>
        <type>SUV</type>
        <model>RAV4</model>
        <price currency="USD">25000.00</price>
    </car>
</cars>
```

## Building the Project
The project uses Spring Boot's Maven plugin:
mvn package
