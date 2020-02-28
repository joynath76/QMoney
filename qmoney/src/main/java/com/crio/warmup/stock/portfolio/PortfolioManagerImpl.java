
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.PortfolioManagerApplication;
import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {



  RestTemplate restTemplate = new RestTemplate();
  
  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // Now we want to convert our code into a module, so we will not call it from main anymore.
  // Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  // into #calculateAnnualizedReturn function here and make sure that it
  // follows the method signature.
  // Logic to read Json file and convert them into Objects will not be required further as our
  // clients will take care of it, going forward.
  // Test your code using Junits provided.
  // Make sure that all of the tests inside PortfolioManagerTest using command below -
  // ./gradlew test --tests PortfolioManagerTest
  // This will guard you against any regressions.
  // run ./gradlew build in order to test yout code, and make sure that
  // the tests and static code quality pass.

  //CHECKSTYLE:OFF


  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo thirdparty APIs to a separate function.
  //  It should be split into fto parts.
  //  Part#1 - Prepare the Url to call Tiingo based on a template constant,
  //  by replacing the placeholders.
  //  Constant should look like
  //  https://api.tiingo.com/tiingo/daily/<ticker>/prices?startDate=?&endDate=?&token=?
  //  Where ? are replaced with something similar to <ticker> and then actual url produced by
  //  replacing the placeholders with actual parameters.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
    String url = buildUri(symbol, from, to);
    String result = restTemplate.getForObject(url, String.class);
    TiingoCandle[] data = PortfolioManagerApplication
        .getObjectMapper().readValue(result, TiingoCandle[].class);
    return Arrays.asList(data);
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String url = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?startDate=" + startDate + "&endDate=" + endDate + "&token=7a3b3f7b9628373e7b858b51a3ce0f17fde30d46";
    return url;
  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate) throws JsonMappingException, JsonProcessingException {
    List<AnnualizedReturn> annualizedReturns = new ArrayList<>();
    for (int i = 0; i < portfolioTrades.size(); i++) {
      
      
      List<Candle> tiingoCandle = getStockQuote(portfolioTrades.get(i).getSymbol(),
          portfolioTrades.get(i).getPurchaseDate(), endDate);
      int l = tiingoCandle.size();
      Double buyprice = tiingoCandle.get(0).getOpen();
      Double sellprice = tiingoCandle.get(l-1).getClose();
      annualizedReturns
      .add(PortfolioManagerApplication.calculateAnnualizedReturns(endDate, portfolioTrades.get(i), buyprice, sellprice));
    }
    for (int i = 0; i < annualizedReturns.size() - 1; i++) {
      for (int j = 0; j < annualizedReturns.size() - i - 1; j++) {
        if (annualizedReturns.get(j).getAnnualizedReturn() < annualizedReturns.get(j + 1)
            .getAnnualizedReturn()) {
          Collections.swap(annualizedReturns, j, j + 1);
        }
    }
  }
    return annualizedReturns;
  }




  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }




}
