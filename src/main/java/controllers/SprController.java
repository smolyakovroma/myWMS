package controllers;

import beans.DocumentIncome;
import beans.DocumentTableValue;
import beans.Stock;
import db.Database;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ManagedBean
@SessionScoped
public class SprController implements Serializable {

    private static final long serialVersionUID = 3900311423514518344L;

    private List<Stock> stockList;
    private Stock selectedStock;

    public SprController() {
        fillStockListAll();
    }

    public void fillStockListAll(){
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;

        stockList = new ArrayList<Stock>();

        try {
            conn = Database.getConnection();

            stmt = conn.createStatement();
            rs = stmt.executeQuery("select * from spr_stock order by id");

            while (rs.next()) {
                Stock value = new Stock();
                value.setId(rs.getLong("id"));
                value.setName(rs.getString("name"));
                value.setService(rs.getInt("isService") != 0);

                stockList.add(value);
            }

        } catch (SQLException ex) {
            Logger.getLogger(DocumentIncome.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DocumentIncome.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public List<Stock> getStockList() {
        return stockList;
    }

    public void setStockList(List<Stock> stockList) {
        this.stockList = stockList;
    }

    public Stock getSelectedStock() {
        return selectedStock;
    }

    public void setSelectedStock(Stock selectedStock) {
        this.selectedStock = selectedStock;
    }


}
