package org.excelProject.pojo;

import java.math.BigDecimal;

public class ExcelDto {
    private Integer thedate;

    private String stockid;

    private BigDecimal openprice;

    private BigDecimal highprice;

    private BigDecimal lowprice;

    private BigDecimal closeprice;

    private Long matchqty;

    private Long matchvalue;

    public ExcelDto(Integer thedate, String stockid, BigDecimal openprice, BigDecimal highprice, BigDecimal lowprice, BigDecimal closeprice, Long matchqty, Long matchvalue) {
        this.thedate = thedate;
        this.stockid = stockid;
        this.openprice = openprice;
        this.highprice = highprice;
        this.lowprice = lowprice;
        this.closeprice = closeprice;
        this.matchqty = matchqty;
        this.matchvalue = matchvalue;
    }

    public ExcelDto() {
        super();
    }

    public Integer getThedate() {
        return thedate;
    }

    public void setThedate(Integer thedate) {
        this.thedate = thedate;
    }

    public String getStockid() {
        return stockid;
    }

    public void setStockid(String stockid) {
        this.stockid = stockid;
    }

    public BigDecimal getOpenprice() {
        return openprice;
    }

    public void setOpenprice(BigDecimal openprice) {
        this.openprice = openprice;
    }

    public BigDecimal getHighprice() {
        return highprice;
    }

    public void setHighprice(BigDecimal highprice) {
        this.highprice = highprice;
    }

    public BigDecimal getLowprice() {
        return lowprice;
    }

    public void setLowprice(BigDecimal lowprice) {
        this.lowprice = lowprice;
    }

    public BigDecimal getCloseprice() {
        return closeprice;
    }

    public void setCloseprice(BigDecimal closeprice) {
        this.closeprice = closeprice;
    }

    public Long getMatchqty() {
        return matchqty;
    }

    public void setMatchqty(Long matchqty) {
        this.matchqty = matchqty;
    }

    public Long getMatchvalue() {
        return matchvalue;
    }

    public void setMatchvalue(Long matchvalue) {
        this.matchvalue = matchvalue;
    }
}
