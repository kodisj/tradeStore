package com.deutsche.domain;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * A Trade.
 */

public class Trade implements Serializable {

    private static final long serialVersionUID = 1L;

    
    private Long id;

    
    private Integer version;

    
    private String tradeId;

    
    private String counterPartyId;

    
    private String bookId;

    private String expired;

    
    private LocalDate maturityDate;

    
    private LocalDate createdDate;

   
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public Trade version(Integer version) {
        this.version = version;
        return this;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getTradeId() {
        return tradeId;
    }

    public Trade tradeId(String tradeId) {
        this.tradeId = tradeId;
        return this;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getCounterPartyId() {
        return counterPartyId;
    }

    public Trade counterPartyId(String counterPartyId) {
        this.counterPartyId = counterPartyId;
        return this;
    }

    public void setCounterPartyId(String counterPartyId) {
        this.counterPartyId = counterPartyId;
    }

    public String getBookId() {
        return bookId;
    }

    public Trade bookId(String bookId) {
        this.bookId = bookId;
        return this;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getExpired() {
        return expired;
    }

    public Trade expired(String expired) {
        this.expired = expired;
        return this;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    public Trade maturityDate(LocalDate maturityDate) {
        this.maturityDate = maturityDate;
        return this;
    }

    public void setMaturityDate(LocalDate maturityDate) {
        this.maturityDate = maturityDate;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public Trade createdDate(LocalDate createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }
    

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Trade)) {
            return false;
        }
        return id != null && id.equals(((Trade) o).id);
    }


    public int hashCode() {
        return 31;
    }

    public String toString() {
        return "Trade{" +
            "id=" + getId() +
            ", version=" + getVersion() +
            ", tradeId='" + getTradeId() + "'" +
            ", counterPartyId='" + getCounterPartyId() + "'" +
            ", bookId='" + getBookId() + "'" +
            ", expired='" + getExpired() + "'" +
            ", maturityDate='" + getMaturityDate() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            "}";
    }
}
