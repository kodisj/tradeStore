package com.deutsche.service;

import com.deutsche.domain.Trade;
import com.deutsche.repository.TradeRepository;
import com.deutsche.errors.BadRequestAlertException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.time.format.DateTimeFormatter;


public class TradeStore {

    private TradeRepository tradeRepository = new TradeRepository();

    private final Logger log = LoggerFactory.getLogger(TradeStore.class);

    private static final String ENTITY_NAME = "TradeStore";

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    //DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;

    public TradeStore(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    public TradeStore(){
    }

    /**
     *  Check and expire the trade in the store
     */

    public void expireTrade()
    {
        
        List<Trade> trades = tradeRepository.findAll();
        trades.stream().forEach(t -> {
            String currentDateTimeString = LocalDate.now().atStartOfDay().format(formatter);  //.atStartOfDay(ZoneId.systemDefault()).toString();//  .now().atStartOfDay(ZoneId.systemDefault()).;
            if(t.getMaturityDate().isBefore(LocalDate.parse(currentDateTimeString,formatter)))
            {  
                    t.setExpired("Y");
            }            
        });
    } 

    public boolean checkExpiredTradeInStore()
    {
        return tradeRepository.checkExpiredTradeInStore();
    }

    /*
    There is a scenario where thousands of trades are flowing into one store, assume any way of 
    transmission of trades. We need to create a one trade store, which stores the trade in the 
    following order

    1.	During transmission if the lower version is being received by the store it will reject 
    the trade and throw an exception. If the version is same it will override the existing record.
    2.	Store should not allow the trade which has less maturity date then today date.
    3.	Store should automatically update the expire flag if in a store the trade crosses the 
    maturity date.

    */
    public Trade storeTrade(Trade trade) throws BadRequestAlertException {
        log.debug("Request to store Trade : {}", trade);
        // The result to return
        Trade result = null;
        LocalDate tradeDate = trade.getMaturityDate();
        System.out.println(tradeDate.toString());
        String currentDateTimeString = LocalDate.now().atStartOfDay().format(formatter);
        
        if(tradeDate.isBefore(LocalDate.parse(currentDateTimeString,formatter)))
        {
          throw new BadRequestAlertException("A new trade cannot have maturity date less than today's date ", ENTITY_NAME, "Earlier Maturity Date"); 
        }
        
        // Check if the trade id exists, if exists then compare version and store accordingly
        List<Trade> existingTradeObj = tradeRepository.findByTradeId(trade.getTradeId());

        if(!existingTradeObj.isEmpty())
        {
            //Get the max version trade object for comparison
            Trade maxVersionTradeObj = existingTradeObj.stream().max(Comparator.comparing(Trade::getVersion)).get();
            System.out.println("Max Version is " + maxVersionTradeObj.getVersion());
            
            //Check if the trade has lower version
            if(trade.getVersion() < maxVersionTradeObj.getVersion())
            {
                throw new BadRequestAlertException("A new trade cannot have lesser version than existing one", ENTITY_NAME, "Earlier Maturity Date"); 

            }
            //Set the Expiration Flag and Created Date before saving the trade
            trade.setExpired("N");
            trade.setCreatedDate(LocalDate.now(ZoneId.systemDefault()));
            
            //Update the existing trade object
            if(trade.getVersion() == maxVersionTradeObj.getVersion())
            {
                result = tradeRepository.update(trade);
            }
            //Insert a new trade object
            if(trade.getVersion() > maxVersionTradeObj.getVersion())
            {
                result = tradeRepository.save(trade);
            }
        }
        // New trade, just store it
        else{
            result = tradeRepository.save(trade);
        }
     
        return result;
    
    }
    public Trade getTradeByIdAndVersion(int version, String tradeId) {
        return tradeRepository.findByTradeIdAndVersion(version,tradeId);
    }

    public List<Trade> getTradeById(String tradeId) {
        return tradeRepository.findByTradeId(tradeId);
    }

    public List<Trade> findAll() {
        return tradeRepository.findAll();
    }

}
