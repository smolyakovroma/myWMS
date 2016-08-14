package controllers;

import beans.DocumentIncome;
import beans.DocumentTableValue;
import beans.Stock;
import db.Database;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@ManagedBean
@SessionScoped
public class DocumentController implements Serializable {

    private static final long serialVersionUID = 1124287688342100443L;

    private List<DocumentIncome> documentList;
    private DocumentIncome selectedDocument;

    private List<DocumentTableValue> tableDoc;
    private DocumentTableValue selectedTableValue;

    private List<Stock> stockList;
    private Stock selectedStock;


    public DocumentController() {
        fillDocumentListAll();
        //удалить после теста
        fillTableDocumentAll();
    }

    public void fillTableDocumentAll() {

        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;

        tableDoc = new ArrayList<DocumentTableValue>();

        try {
            conn = Database.getConnection();

            stmt = conn.createStatement();
            rs = stmt.executeQuery("select t.id, t.id_stock, spr.name, spr.isService, t.position, t.amount from doc_income_table as t inner join spr_stock as spr on t.id_stock = spr.id where t.id_doc = "
                    + " 1 "
// + selectedDocument.getId()
                    + " order by t.position ");

            //select t.id, t.id_stock, spr.name, t.position, t.amount from doc_income_table as t inner join spr_stock as spr on t.id_stock = spr.id where t.id_doc = 1 order by t.position
            while (rs.next()) {
                DocumentTableValue value = new DocumentTableValue();
                value.setId(rs.getLong("t.id"));
                value.setPosition(rs.getInt("t.position"));
                value.setAmount(rs.getFloat("t.amount"));
                value.setStock(new Stock(rs.getLong("t.id_stock"), rs.getString("spr.name"), rs.getInt("isService") != 0));
                tableDoc.add(value);
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

    public void fillDocumentListAll() {
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;

        documentList = new ArrayList<DocumentIncome>();

        try {
            conn = Database.getConnection();

            stmt = conn.createStatement();
            rs = stmt.executeQuery("select * from doc_income order by date_doc");
            while (rs.next()) {
                DocumentIncome doc = new DocumentIncome();
//                doc.setDateDoc(rs.getDate("name"));
                doc.setId(rs.getLong("id"));
                doc.setComment(rs.getString("comment"));
                documentList.add(doc);
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

    public List<DocumentIncome> getDocumentList() {
        return documentList;
    }

    public DocumentIncome getSelectedDocument() {
        return selectedDocument;

    }

    public void setSelectedDocument(DocumentIncome selectedDocument) {
        this.selectedDocument = selectedDocument;
    }

    public DocumentTableValue getSelectedTableValue() {
        return selectedTableValue;
    }

    public void setSelectedTableValue(DocumentTableValue selectedTableValue) {
        this.selectedTableValue = selectedTableValue;
    }


    public void deleteDocument() {
        if (selectedDocument != null) {
            documentList.remove(selectedDocument);
            selectedDocument = null;
        }
    }

    public void deleteTableValue(){
        if(selectedTableValue!=null & tableDoc.size()>0){
            tableDoc.remove(selectedTableValue);
        }
    }

    public void moveUp(){
        if(selectedTableValue!=null & tableDoc.size()>0 & selectedTableValue.getPosition()>1){
            for (DocumentTableValue value : tableDoc) {
                if(value.equals(selectedTableValue)){
                     selectedTableValue.setPosition(selectedTableValue.getPosition()-1);
                }
            }

        }
    }

    public void moveDown(){
        if(selectedTableValue!=null & tableDoc.size()>0 & selectedTableValue.getPosition()<tableDoc.size()){
            for (DocumentTableValue value : tableDoc) {
                if(value.equals(selectedTableValue)){
                    selectedTableValue.setPosition(selectedTableValue.getPosition()+1);
                }
            }

        }
    }

    public void openDocument() throws IOException, ServletException {
        fillTableDocumentAll();

        FacesContext fc = FacesContext.getCurrentInstance();
        NavigationHandler nh = fc.getApplication().getNavigationHandler();
        nh.handleNavigation(fc, null, "doc_income");


//        if (selectedDocument != null) {
//            System.out.println(selectedDocument.getId());
//            return "doc_income";
//        }
//        return "doc_income";

//        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
//        RequestDispatcher dispatcher = ((ServletRequest) context.getRequest()).getRequestDispatcher("/pages/doc_income.xhtml");
//        dispatcher.forward((ServletRequest) context.getRequest(), (ServletResponse) context.getResponse());
//        FacesContext.getCurrentInstance().responseComplete();
//        return null;
    }

    public void onRowSelect(SelectEvent event) {
        FacesMessage msg = new FacesMessage("Document Selected", ((DocumentIncome) event.getObject()).getId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
//        openDocument();
    }

    public void onRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage("Car Unselected", ((DocumentIncome) event.getObject()).getId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public List<DocumentTableValue> getTableDoc() {

        return tableDoc;
    }

    public void setTableDoc(List<DocumentTableValue> tableDoc) {
        this.tableDoc = tableDoc;
    }

    public void pickStockValue() {
        if (selectedStock != null & selectedTableValue != null) {
//            DocumentTableValue documentTableValue = tableDoc.get(tableDoc.indexOf(selectedTableValue));
            selectedTableValue.setStock(selectedStock);
            tableDoc.set(tableDoc.indexOf(selectedTableValue), selectedTableValue);
        }

    }


    public void fillStockListAll() {
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
        fillStockListAll();
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

    public void closeFromDocument() {
        fillDocumentListAll();
        FacesContext fc = FacesContext.getCurrentInstance();
        NavigationHandler nh = fc.getApplication().getNavigationHandler();
        nh.handleNavigation(fc, null, "main");
    }

    public void saveFromDocument() {
        saveDocument();
        fillDocumentListAll();
        FacesContext fc = FacesContext.getCurrentInstance();
        NavigationHandler nh = fc.getApplication().getNavigationHandler();
        nh.handleNavigation(fc, null, "main");
    }

    public void saveDocument() {

        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;

        String update_doc = "UPDATE doc_income SET comment = ? WHERE id = "+selectedDocument.getId();
        String update_doc_table = "UPDATE doc_income_table SET id_stock = ?, position = ?, amount = ? WHERE id = ?";
        PreparedStatement preparedStatement = null;
        try {
            conn = Database.getConnection();
            preparedStatement = conn.prepareStatement(update_doc);
            preparedStatement.setString(1, selectedDocument.getComment());
            preparedStatement.executeUpdate();
            for (DocumentTableValue value : tableDoc) {
                preparedStatement = conn.prepareStatement(update_doc_table);
                preparedStatement.setInt(1, value.getStock().getId().intValue());
                preparedStatement.setInt(2, value.getPosition());
                preparedStatement.setInt(3, value.getAmount().intValue());
                preparedStatement.setInt(4, value.getId().intValue());
                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
