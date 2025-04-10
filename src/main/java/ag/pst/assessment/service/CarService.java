package ag.pst.assessment.service;

import ag.pst.assessment.model.Car;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Log4j2
@Service
public class CarService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter DATE_FORMATTER_INPUT = DateTimeFormatter.ofPattern("yyyy,dd,MM");

    private static final Scanner scanner = new Scanner(System.in);

    public void processUserInput() throws Exception {
        System.out.println("\n-------------------Assessment-PST.AG-------------------");

        List<Car> cars = loadDataFromCSV(loadDataFromXML());

        while (true) {
            System.out.println("\nMain Menu:");
            System.out.println("\n1. Filter cars");
            System.out.println("2. Sort cars");
            System.out.println("3. Display cars");
            System.out.println("4. Sort by type with specific currencies");
            System.out.println("5. Exit");
            System.out.print("\nEnter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    filterCars(cars);
                    break;
                case 2:
                    sortCars(cars);
                    break;
                case 3:
                    displayCars(cars);
                    break;
                case 4:
                    sortByTypeWithCurrencies(cars);
                    break;
                case 5:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private List<Car> loadDataFromCSV(List<Car> cars) throws Exception {
        if(!cars.isEmpty()){

            Resource resource = new ClassPathResource("CarsBrand.csv");

            try (InputStream inputStream = resource.getInputStream();
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

                AtomicInteger lineCounter = new AtomicInteger(0);

                bufferedReader.lines()
                    .skip(1) // Skip file header
                    .forEach(line -> {
                        String[] parts = line.replace("\"", "").split(",");
                        if (parts.length == 2) {
                            int index = lineCounter.getAndIncrement();
                            Car car = cars.get(index);
                            car.setBrand(parts[0]);
                            car.setReleaseDate(LocalDate.parse(parts[1], DATE_FORMATTER));
                        }
                    });
            }
        }

        return cars;
    }

    private List<Car> loadDataFromXML() throws Exception {
        List<Car> cars = new ArrayList<>();

        Resource resource = new ClassPathResource("carsType.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(resource.getInputStream());

        NodeList carNodes = document.getElementsByTagName("car");

        for (int i = 0; i < carNodes.getLength(); i++) {
            Node carNode = carNodes.item(i);
            if (carNode.getNodeType() == Node.ELEMENT_NODE) {
                Element carElement = (Element) carNode;
                Car car = new Car();

                car.setType(getElementText(carElement, "type"));
                car.setModel(getElementText(carElement, "model"));

                Map<String, BigDecimal> prices = new HashMap<>();

                // Get main price
                Element priceElement = (Element) carElement.getElementsByTagName("price").item(0);
                String currency = priceElement.getAttribute("currency");
                BigDecimal priceValue = new BigDecimal(priceElement.getTextContent());
                prices.put(currency, priceValue);

                // Get additional prices
                NodeList priceNodes = carElement.getElementsByTagName("prices").item(0).getChildNodes();
                for (int j = 0; j < priceNodes.getLength(); j++) {
                    Node priceNode = priceNodes.item(j);
                    if (priceNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element additionalPriceElement = (Element) priceNode;
                        String additionalCurrency = additionalPriceElement.getAttribute("currency");
                        BigDecimal additionalPriceValue = new BigDecimal(additionalPriceElement.getTextContent());
                        prices.put(additionalCurrency, additionalPriceValue);
                    }
                }

                car.setPrices(prices);
                cars.add(car);
            }
        }

        return cars;
    }

    private String getElementText(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }

    private void filterCars(List<Car> cars) {
        System.out.println("\nFilter Options:");
        System.out.println("1. By Brand and Price");
        System.out.println("2. By Brand and Release Date");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                System.out.print("Enter brand: ");
                String brand = scanner.nextLine();
                System.out.print("Enter minimum price: ");
                BigDecimal minPrice = scanner.nextBigDecimal();
                scanner.nextLine();

                List<Car> filteredByBrandPrice = cars.stream()
                        .filter(c -> c.getBrand().equalsIgnoreCase(brand))
                        .filter(c -> c.getPrices().values().stream().anyMatch(p -> p.compareTo(minPrice) >= 0))
                        .collect(Collectors.toList());

                System.out.println("\nFiltered Results:");
                displayCars(filteredByBrandPrice);
                break;

            case 2:
                System.out.print("Enter brand: ");
                String brand2 = scanner.nextLine();
                System.out.print("Enter date (yyyy,dd,mm e.g.: 2023,31,01): ");
                String dateStr = scanner.nextLine();

                try {
                    LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER_INPUT);

                    List<Car> filteredByBrandDate = cars.stream()
                            .filter(c -> c.getBrand().equalsIgnoreCase(brand2))
                            .filter(c -> c.getReleaseDate().isEqual(date))
                            .collect(Collectors.toList());

                    System.out.println("\nFiltered Results:");
                    displayCars(filteredByBrandDate);
                } catch (Exception e) {
                    System.out.println("Invalid date! Use format: yyyy,dd,mm (e.g., 2023,31,01)");
                }
                break;

            default:
                System.out.println("Invalid choice.");
        }
    }

    private void sortCars(List<Car> cars) {
        System.out.println("\nSort Options:");
        System.out.println("1. By Release Date (Latest to Oldest)");
        System.out.println("2. By Price (Highest to Lowest)");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        List<Car> sortedCars = new ArrayList<>(cars);

        switch (choice) {
            case 1:
                sortedCars.sort((c1, c2) -> c2.getReleaseDate().compareTo(c1.getReleaseDate()));
                System.out.println("\nSorted by Release Date (Latest to Oldest):");
                break;

            case 2:
                sortedCars.sort((c1, c2) -> {
                    BigDecimal maxPrice1 = Collections.max(c1.getPrices().values());
                    BigDecimal maxPrice2 = Collections.max(c2.getPrices().values());
                    return maxPrice2.compareTo(maxPrice1);
                });
                System.out.println("\nSorted by Price (Highest to Lowest):");
                break;

            default:
                System.out.println("Invalid choice.");
                return;
        }

        displayCars(sortedCars);
    }

    private void displayCars(List<Car> cars) {
        System.out.println("\nOutput Format Options:");
        System.out.println("1. Table Format");
        System.out.println("2. XML Format");
        System.out.println("3. JSON Format");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                displayAsTable(cars);
                break;

            case 2:
                displayAsXml(cars);
                break;

            case 3:
                displayAsJson(cars);
                break;

            default:
                System.out.println("Invalid choice.");
        }
    }

    private void displayAsTable(List<Car> cars) {
        System.out.println("\n+-----------------+------------+------------+------------+--------------------------------------------------------------+");
        System.out.printf("| %-15s | %-10s | %-10s | %-10s | %-60s |%n","Brand", "Type", "Model", "Date", "Price");
        System.out.println("+-----------------+------------+------------+------------+--------------------------------------------------------------+");

        for (Car car : cars) {
            String dateStr = car.getReleaseDate().format(DATE_FORMATTER);
            String pricesStr = car.getPrices().entrySet().stream()
                    .map(e -> e.getKey() + ": " + e.getValue())
                    .collect(Collectors.joining(", "));

            System.out.printf("| %-15s | %-10s | %-10s | %-10s | %-60s |%n",
                    car.getBrand(), car.getType(), car.getModel(), dateStr, pricesStr);
        }

        System.out.println("+-----------------+------------+------------+------------+--------------------------------------------------------------+");
    }

    private void displayAsXml(List<Car> cars) {
        System.out.println("<cars>");
        for (Car car : cars) {
            System.out.println("  <car>");
            System.out.println("    <brand>" + car.getBrand() + "</brand>");
            System.out.println("    <type>" + car.getType() + "</type>");
            System.out.println("    <model>" + car.getModel() + "</model>");
            System.out.println("    <releaseDate>" + car.getReleaseDate().format(DATE_FORMATTER) + "</releaseDate>");
            System.out.println("    <prices>");
            for (Map.Entry<String, BigDecimal> entry : car.getPrices().entrySet()) {
                System.out.println("      <price currency=\"" + entry.getKey() + "\">" + entry.getValue() + "</price>");
            }
            System.out.println("    </prices>");
            System.out.println("  </car>");
        }
        System.out.println("</cars>");
    }

    private void displayAsJson(List<Car> cars) {
        System.out.println("[");
        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            System.out.println("  {");
            System.out.println("    \"brand\": \"" + car.getBrand() + "\",");
            System.out.println("    \"type\": \"" + car.getType() + "\",");
            System.out.println("    \"model\": \"" + car.getModel() + "\",");
            System.out.println("    \"releaseDate\": \"" + car.getReleaseDate().format(DATE_FORMATTER) + "\",");
            System.out.println("    \"prices\": {");

            int priceCount = 0;
            for (Map.Entry<String, BigDecimal> entry : car.getPrices().entrySet()) {
                System.out.print("      \"" + entry.getKey() + "\": " + entry.getValue());
                if (++priceCount < car.getPrices().size()) {
                    System.out.println(",");
                } else {
                    System.out.println();
                }
            }

            System.out.println("    }");
            System.out.print("  }");
            if (i < cars.size() - 1) {
                System.out.println(",");
            } else {
                System.out.println();
            }
        }
        System.out.println("]");
    }

    private void sortByTypeWithCurrencies(List<Car> cars) {
        List<Car> sortedCars = new ArrayList<>(cars);

        sortedCars.sort((c1, c2) -> {
            // sort all SUV in EUR, all Sedan in JPY, All Truck in USD
            BigDecimal price1 = getPriceByType(c1);
            BigDecimal price2 = getPriceByType(c2);
            return price2.compareTo(price1);
        });

        System.out.println("\nSorted by Type with specific currencies (SUV-EUR, Sedan-JPY, Truck-USD):");
        displayCars(sortedCars);
    }

    private BigDecimal getPriceByType(Car car) {
        return switch (car.getType()) {
            case "SUV" -> car.getPrices().getOrDefault("EUR", BigDecimal.ZERO);
            case "Sedan" -> car.getPrices().getOrDefault("JPY", BigDecimal.ZERO);
            case "Truck" -> car.getPrices().getOrDefault("USD", BigDecimal.ZERO);
            default -> BigDecimal.ZERO;
        };
    }
}