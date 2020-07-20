package com.deutsche.repository;

import com.deutsche.domain.Trade;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.stream.Collectors; 

/**
 * Spring Data  repository for the Trade entity.
 */

public class TradeRepository  {
    List<Trade> tradeStore = new ArrayList<Trade>();

    public List<Trade> findByTradeId(String tradeId)
    {
        List<Trade> matchingTrade = tradeStore.stream().filter(t-> t.getTradeId().equals(tradeId)).collect(Collectors.toList());
        return matchingTrade;
    }

    public Trade findByTradeIdAndVersion(int version,String tradeId)
    {
        List<Trade> trades = findByTradeId(tradeId);
         //Get the matching version trade object for update
        List<Trade> matchingTrade = trades.stream().filter(t-> t.getVersion() == version).limit(1).collect(Collectors.toList());
        return matchingTrade.get(0);
    }

    public List<Trade> findAll()
    {
        List<String> list = tradeStore.stream().map(t->t.getTradeId()).collect(Collectors.toList());
        System.out.println(list);
        List<Integer> versions = tradeStore.stream().map(t->t.getVersion()).collect(Collectors.toList());
        System.out.println(versions);
        List<String> bookIds = tradeStore.stream().map(t->t.getBookId()).collect(Collectors.toList());
        System.out.println(bookIds);
        List<String> cpIds = tradeStore.stream().map(t->t.getCounterPartyId()).collect(Collectors.toList());
        System.out.println(cpIds);
        List<LocalDate> mDates = tradeStore.stream().map(t->t.getMaturityDate()).collect(Collectors.toList());
        System.out.println(mDates);
        List<String> expFlag = tradeStore.stream().map(t->t.getExpired()).collect(Collectors.toList());
        System.out.println(expFlag);
        return tradeStore;
    }

    public Trade save(Trade trade)
    {
        tradeStore.add(trade);
        return trade;
    }

    public Trade update(Trade trade)
    {
        List<Trade> trades = findByTradeId(trade.getTradeId());
         //Get the matching version trade object for update
         List<Trade> tradeToBeUpdated = trades.stream().filter(t-> t.getVersion() == trade.getVersion()).limit(1).collect(Collectors.toList());
         tradeStore.remove(tradeToBeUpdated.get(0));
         tradeStore.add(trade);
         return trade;

    }

    public boolean checkExpiredTradeInStore(){
        List<Trade> list = tradeStore.stream().filter(t->t.getExpired().equals("Y")).collect(Collectors.toList());
        if(list.size() == 0) return true; else return false;
    }

}
