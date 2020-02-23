
package com.crio.warmup.stock;

import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {


  // Read the json file provided in the argument[0]. The file will be avilable in
  // the classpath.
  // 1. Use #resolveFileFromResources to get actual file from classpath.
  // 2. parse the json file using ObjectMapper provided with #getObjectMapper,
  // and extract symbols provided in every trade.
  // return the list of all symbols in the same order as provided in json.
  // Test the function using gradle commands below
  // ./gradlew run --args="trades.json"
  // Make sure that it prints below String on the console -
  // ["AAPL","MSFT","GOOGL"]
  // Now, run
  // ./gradlew build and make sure that the build passes successfully
  // There can be few unused imports, you will need to fix them to make the build
  // pass.

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    File file = resolveFileFromResources(args[0]);
    PortfolioTrade[] data = getObjectMapper().readValue(file, PortfolioTrade[].class);
    List<String> syslist = new ArrayList<String>();
    for (int i = 0; i < data.length; i++) {
      syslist.add(data[i].getSymbol());
    }
    return syslist;
  }

  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(Thread.currentThread().getContextClassLoader()
        .getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  //
  // Follow the instructions provided in the task documentation and fill up the
  // correct values for
  // the variables provided. First value is provided for your reference.
  // A. Put a breakpoint on the first line inside mainReadFile() which says
  // return Collections.emptyList();
  // B. Then Debug the test #mainReadFile provided in
  // PortfoliomanagerApplicationTest.java
  // following the instructions to run the test.
  // Once you are able to run the test, perform following tasks and record the
  // output as a
  // String in the function below.
  // Use this link to see how to evaluate expressions -
  // https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  // 1. evaluate the value of "args[0]" and set the value
  // to the variable named valueOfArgument0 (This is implemented for your
  // reference.)
  // 2. In the same window, evaluate the value of expression below and set it
  // to resultOfResolveFilePathArgs0
  // expression ==> resolveFileFromResources(args[0])
  // 3. In the same window, evaluate the value of expression below and set it
  // to toStringOfObjectMapper.
  // You might see some garbage numbers in the output. Dont worry, its expected.
  // expression ==> getObjectMapper().toString()
  // 4. Now Go to the debug window and open stack trace. Put the name of the
  // function you see at
  // second place from top to variable functionNameFromTestFileInStackTrace
  // 5. In the same window, you will see the line number of the function in the
  // stack trace window.
  // assign the same to lineNumberFromTestFileInStackTrace
  // Once you are done with above, just run the corresponding test and
  // make sure its working as expected. use below command to do the same.
  // ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> debugOutputs() {
    String path = "~/workspace/jborman76-ME_QMONEY/qmoney/bin/main/trades.json";

    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 = path;
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@5a9d6f02";
    String functionNameFromTestFileInStackTrace = "mainReadFile()";
    String lineNumberFromTestFileInStackTrace = "22";

    return Arrays.asList(new String[] { 
      valueOfArgument0, resultOfResolveFilePathArgs0, toStringOfObjectMapper,
        functionNameFromTestFileInStackTrace, lineNumberFromTestFileInStackTrace });
  }






  // TODO: CRIO_TASK_MODULE_REST_API
  //  Copy the relavent code from #mainReadFile to parse the Json into PortfolioTrade list.
  //  Now That you have the list of PortfolioTrade already populated in module#1
  //  For each stock symbol in the portfolio trades,
  //  Call Tiingo api (https://api.tiingo.com/tiingo/daily/<ticker>/prices?startDate=&endDate=&token=)
  //  with
  //   1. ticker = symbol in portfolio_trade
  //   2. startDate = purchaseDate in portfolio_trade.
  //   3. endDate = args[1]
  //  Use RestTemplate#getForObject in order to call the API,
  //  and deserialize the results in List<Candle>
  //  Note - You may have to register on Tiingo to get the api_token.
  //    Please refer the the module documentation for the steps.
  //  Find out the closing price of the stock on the end_date and
  //  return the list of all symbols in ascending order by its close value on endDate
  //  Test the function using gradle commands below
  //   ./gradlew run --args="trades.json 2020-01-01"
  //   ./gradlew run --args="trades.json 2019-07-01"
  //   ./gradlew run --args="trades.json 2019-12-03"
  //  And make sure that its printing correct results.

  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    File file = resolveFileFromResources(args[0]);
    RestTemplate restTemplate = new RestTemplate();
    PortfolioTrade[] data = getObjectMapper().readValue(file, PortfolioTrade[].class);
    List<Double> closePrice = new ArrayList<Double>();
    List<String> symlist = new ArrayList<String>();
    for (int i = 0; i < data.length; i++) {
      String url = "https://api.tiingo.com/tiingo/daily/" + data[i].getSymbol() + "/prices?startDate=" + data[i].getPurchaseDate() + "&endDate=" + args[1] + "&token=7a3b3f7b9628373e7b858b51a3ce0f17fde30d46";
      String result = restTemplate.getForObject(url, String.class);
      TiingoCandle[] tiingoCandle = getObjectMapper().readValue(result, TiingoCandle[].class);
      int l = tiingoCandle.length;
      if (tiingoCandle[l - 1].getDate().toString().equals(args[1])) {
        closePrice.add(tiingoCandle[l - 1].getClose());
        symlist.add(data[i].getSymbol());
      }
          
    }
    for (int i = 0; i < closePrice.size() - 1; i++) {
      for (int j = 0; j < closePrice.size() - i - 1; j++) {
        if (closePrice.get(j) > closePrice.get(j + 1)) {
          Collections.swap(closePrice, j, j + 1);
          Collections.swap(symlist, j, j + 1);
        }
      }
    }
    
    return symlist;
  }









  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    //printJsonObject(mainReadFile(args));

    printJsonObject(mainReadQuotes(args));


  }
}

