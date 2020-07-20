package com.deutsche.service;

import com.deutsche.domain.Trade;
import com.deutsche.errors.BadRequestAlertException;
import com.deutsche.service.TradeStore;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;
import org.mockito.junit.jupiter.MockitoExtension;
//import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.junit.runner.RunWith;


/**
 * Integration tests for the Trade Store.
 */
public class TradeStoreTest {

    private TradeStore tradeStore = new TradeStore();
    private Trade trade;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    
    @BeforeEach
    public void primeDataInStore() throws Exception {
        Trade trade = new Trade()
                        .version(10)
                        .tradeId("T1")
                        .counterPartyId("CP1")
                        .bookId("B1")
                        .maturityDate(LocalDate.parse("08-15-2020", formatter));
        tradeStore.storeTrade(trade);
    }

    @ParameterizedTest(name = "storeExistingTradeForLowerVersion")
	@CsvSource({
			"4,    'T1',   'CP1', 'B1', '08-15-2020'"
	})
    public void storeExistingTradeForLowerVersion(int version, String tradeId, String counterPartyId, String bookId, String maturityDate) throws Exception {

        Trade trade = new Trade()
                        .version(version)
                        .tradeId(tradeId)
                        .counterPartyId(counterPartyId)
                        .bookId(bookId)
                        .maturityDate(LocalDate.parse(maturityDate, formatter));

        int databaseSizeBeforeCreate = tradeStore.findAll().size();
        System.out.println("Size is " + databaseSizeBeforeCreate );
        assertThrows(
            BadRequestAlertException.class,
                () -> tradeStore.storeTrade(trade),
                "Lower Version Test Expected to throw, but it didn't"
            );
           
        int databaseSizeAfterCreate = tradeStore.findAll().size();
        assertEquals(databaseSizeBeforeCreate, databaseSizeAfterCreate, () -> "Lower Version Test Failed");
    }

    @ParameterizedTest(name = "storeExistingTradeForEqualVersion")
	@CsvSource({
			"10,    'T1',   'CP2', 'B2', '08-15-2020'"
	})
    public void storeExistingTradeForEqualVersion(int version, String tradeId, String counterPartyId, String bookId, String maturityDate) throws Exception {
        
        Trade trade = new Trade()
        .version(version)
        .tradeId(tradeId)
        .counterPartyId(counterPartyId)
        .bookId(bookId)
        .maturityDate(LocalDate.parse(maturityDate, formatter));

        int databaseSizeBeforeCreate = tradeStore.findAll().size();
        tradeStore.storeTrade(trade);
        Trade updatedTrade = tradeStore.getTradeByIdAndVersion(version, tradeId);
        assertEquals(true,trade.equals(updatedTrade));
        int databaseSizeAfterCreate = tradeStore.findAll().size();
        assertEquals(databaseSizeBeforeCreate, databaseSizeAfterCreate, () -> "Equal Version Failed");
    }

    @ParameterizedTest(name = "storeExistingTradeForGreaterVersion")
	@CsvSource({
			"15,    'T1',   'CP1', 'B1', '08-15-2020'"
	})
    public void storeExistingTradeForGreaterVersion(int version, String tradeId, String counterPartyId, String bookId, String maturityDate) throws Exception {
        Trade trade = new Trade()
        .version(version)
        .tradeId(tradeId)
        .counterPartyId(counterPartyId)
        .bookId(bookId)
        .maturityDate(LocalDate.parse(maturityDate, formatter));

        int databaseSizeBeforeCreate = tradeStore.findAll().size();
        tradeStore.storeTrade(trade);
        Trade updatedTrade = tradeStore.getTradeByIdAndVersion(version, tradeId);
        assertEquals(true,trade.equals(updatedTrade));
        int databaseSizeAfterCreate = tradeStore.findAll().size();
        assertEquals(databaseSizeBeforeCreate + 1, databaseSizeAfterCreate, () -> "Greater Version Test Passed");
    }

    @ParameterizedTest(name = "storeNonExistingTrade")
	@CsvSource({
			"1,    'T100',   'CP1', 'B1', '08-15-2020'",
			"1,    'T101',   'CP1', 'B1', '08-15-2020'",
			"1,    'T102',   'CP1', 'B1', '08-15-2020'",
			"1,    'T103',   'CP1', 'B1', '08-15-2020'",
	})
    public void storeNonExistingTrade(int version, String tradeId, String counterPartyId, String bookId, String maturityDate) throws Exception {
        Trade trade = new Trade()
        .version(version)
        .tradeId(tradeId)
        .counterPartyId(counterPartyId)
        .bookId(bookId)
        .maturityDate(LocalDate.parse(maturityDate, formatter));

        int databaseSizeBeforeCreate = tradeStore.findAll().size();
        tradeStore.storeTrade(trade);
        List<Trade> updatedTrade = tradeStore.getTradeById(tradeId);
        assertEquals(1,updatedTrade.size());
        assertEquals(version,updatedTrade.get(0).getVersion());
        assertEquals(tradeId,updatedTrade.get(0).getTradeId());
        int databaseSizeAfterCreate = tradeStore.findAll().size();
        assertEquals(databaseSizeBeforeCreate + 1, databaseSizeAfterCreate, () -> "Non Existing Trade Test Passed");
    }

    @ParameterizedTest(name = "storeExistingTradeForEarlierMaturityDate")
	@CsvSource({
			"10,    'T1',   'CP1', 'B1', '07-14-2020'",
			"10,    'T1',   'CP1', 'B1', '07-13-2020'",
			"10,    'T1',   'CP1', 'B1', '07-12-2020'",
			"10,    'T1',   'CP1', 'B1', '07-11-2020'",
	})
    public void storeExistingTradeForEarlierMaturityDate(int version, String tradeId, String counterPartyId, String bookId, String maturityDate) throws Exception {
    Trade trade = new Trade()
                        .version(version)
                        .tradeId(tradeId)
                        .counterPartyId(counterPartyId)
                        .bookId(bookId)
                        .maturityDate(LocalDate.parse(maturityDate, formatter));

        int databaseSizeBeforeCreate = tradeStore.findAll().size();
        
        assertThrows(
            BadRequestAlertException.class,
                () -> tradeStore.storeTrade(trade),
                "storeExistingTradeForEarlierMaturityDate Expected to throw, but it didn't"
            );
           
        int databaseSizeAfterCreate = tradeStore.findAll().size();
        assertEquals(databaseSizeBeforeCreate, databaseSizeAfterCreate, () -> "Earlier Maturity Date for existing Trade Test Passed");
    }

    @ParameterizedTest(name = "storeNonExistingTradeForEarlierMaturityDate")
	@CsvSource({
			"10,    'T200',   'CP1', 'B1', '07-15-2019'",
			"10,    'T201',   'CP1', 'B1', '07-15-2019'",
			"10,    'T202',   'CP1', 'B1', '07-15-2019'",
			"10,    'T203',   'CP1', 'B1', '07-15-2019'",
	})
    public void storeNonExistingTradeForEarlierMaturityDate(int version, String tradeId, String counterPartyId, String bookId, String maturityDate) throws Exception {
        Trade trade = new Trade()
                        .version(version)
                        .tradeId(tradeId)
                        .counterPartyId(counterPartyId)
                        .bookId(bookId)
                        .maturityDate(LocalDate.parse(maturityDate, formatter));

        int databaseSizeBeforeCreate = tradeStore.findAll().size();
        
        assertThrows(
            BadRequestAlertException.class,
                () -> tradeStore.storeTrade(trade),
                "storeNonExistingTradeForEarlierMaturityDate Expected to throw, but it didn't"
            );
           
        int databaseSizeAfterCreate = tradeStore.findAll().size();
        assertEquals(databaseSizeBeforeCreate, databaseSizeAfterCreate, () -> "storeNonExistingTradeForEarlierMaturityDate Test Passed");
    }

    @ParameterizedTest(name = "storeMultipleExistingTradeForEarlierMaturityDate")
	@CsvSource({
			"10,    'T1',   'CP1', 'B1', '07-15-2019'",
			"10,    'T1',   'CP1', 'B1', '07-15-2019'",
			"10,    'T1',   'CP1', 'B1', '07-15-2019'",
			"10,    'T1',   'CP1', 'B1', '07-15-2019'",
	})
    public void storeMultipleExistingTradeForEarlierMaturityDate(int version, String tradeId, String counterPartyId, String bookId, String maturityDate) throws Exception {
        Trade trade = new Trade()
                        .version(version)
                        .tradeId(tradeId)
                        .counterPartyId(counterPartyId)
                        .bookId(bookId)
                        .maturityDate(LocalDate.parse(maturityDate, formatter));

        int databaseSizeBeforeCreate = tradeStore.findAll().size();
        
        assertThrows(
            BadRequestAlertException.class,
                () -> tradeStore.storeTrade(trade),
                "storeMultipleExistingTradeForEarlierMaturityDate Expected to throw, but it didn't"
            );
                   
        int databaseSizeAfterCreate = tradeStore.findAll().size();
        assertEquals(databaseSizeBeforeCreate, databaseSizeAfterCreate, () -> "storeMultipleExistingTradeForEarlierMaturityDate Test Passed");

    }

    @ParameterizedTest(name = "storeExistingTradeForLowerVersion")
	@CsvSource({
			"0,    'T1',   'CP1', 'B1', '08-15-2020'",
			"0,    'T1',   'CP1', 'B1', '08-15-2020'",
			"0,    'T1',   'CP1', 'B1', '08-15-2020'",
			"0,    'T1',   'CP1', 'B1', '08-15-2020'",
	})
    public void storeMultipleExistingTradeForLowerVersion(int version, String tradeId, String counterPartyId, String bookId, String maturityDate) throws Exception {
        Trade trade = new Trade()
                        .version(version)
                        .tradeId(tradeId)
                        .counterPartyId(counterPartyId)
                        .bookId(bookId)
                        .maturityDate(LocalDate.parse(maturityDate, formatter));

        int databaseSizeBeforeCreate = tradeStore.findAll().size();
        
        assertThrows(
            BadRequestAlertException.class,
                () -> tradeStore.storeTrade(trade),
                "storeMultipleExistingTradeForLowerVersion Expected to throw, but it didn't"
            );
           
        int databaseSizeAfterCreate = tradeStore.findAll().size();
        assertEquals(databaseSizeBeforeCreate, databaseSizeAfterCreate, () -> "storeMultipleExistingTradeForLowerVersion Test Passed");
        
    }

    // @ExtendWith(PowerMockRunner.class)
    // @PrepareForTest({ LocalDate.class })
    // @ParameterizedTest(name = "checkExpiredTradeInStore")
	// @CsvSource({
	// 		"11,    'T1',   'CP100', 'B1', '07-15-2020'"
	// })    
    // public void checkExpiredTradeInStore(int version, String tradeId, String counterPartyId, String bookId, String maturityDate) throws Exception {
    //     Trade trade = new Trade()
    //                     .version(version)
    //                     .tradeId(tradeId)
    //                     .counterPartyId(counterPartyId)
    //                     .bookId(bookId)
    //                     .maturityDate(LocalDate.parse(maturityDate, formatter));

    //     DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    //     String instantExpected = "2017-01-03T10:15:30Z";
    //     Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("Asia/Calcutta"));

    //     LocalDate localDate = LocalDate.now(clock);
    //     mockStatic(LocalDate.class);
    //     when(LocalDate.now()).thenReturn(localDate);

    //     LocalDate now = LocalDate.now();
 
    //     assertEquals(now.atStartOfDay().format(format),"2017-01-03",()-> "Mocking Date failed");
        
    //     System.out.println(trade.getMaturityDate().toString());
    //     int databaseSizeBeforeCreate = tradeStore.findAll().size();
    //     tradeStore.storeTrade(trade);
    //     tradeStore.expireTrade();
    //     tradeStore.findAll();
    //     assertFalse(tradeStore.checkExpiredTradeInStore());
    // }
}
