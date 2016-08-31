package controllers;

import beans.DocumentIncome;
import beans.DocumentTableValue;
import beans.Stock;
import beans.Warehouse;
import db.Database;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
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

    private List<Warehouse> warehouseList;
    private Warehouse selectedWarehouse;

    private String emptyPosition;

    private String docType;

    public DocumentController() {
//        setDocumentType();
//        fillDocumentListAll();
        this.emptyPosition = "empty";

    }

    public void fillTableDocumentAll() {

        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;

        tableDoc = new ArrayList<DocumentTableValue>();

        try {
            conn = Database.getConnection();

            stmt = conn.createStatement();
            rs = stmt.executeQuery("select t.id, t.id_stock, spr.name, spr.isService, t.position, t.amount from "+docType+"_table as t inner join spr_stock as spr on t.id_stock = spr.id where t.id_doc = "
                    + selectedDocument.getId().intValue()
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

//        setDocumentType();

        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;

        documentList = new ArrayList<DocumentIncome>();

        try {
            conn = Database.getConnection();

            stmt = conn.createStatement();
            rs = stmt.executeQuery("select * from "+docType+" order by date_doc");
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


    public void addTableValue(){
            tableDoc.add(new DocumentTableValue(null, null, 0.0f, tableDoc.size()+1));
    }

    public void deleteTableValue() {
        if (selectedTableValue != null & tableDoc.size() > 0) {
            tableDoc.remove(selectedTableValue);
        }
    }

    public void moveUp() {
        if (selectedTableValue != null & tableDoc.size() > 0 & selectedTableValue.getPosition() > 1) {
//            for (DocumentTableValue value : tableDoc) {
//                if(value.equals(selectedTableValue)){
//                     selectedTableValue.setPosition(selectedTableValue.getPosition()-1);
//                }
//            }
            int pos = tableDoc.indexOf(selectedTableValue) - 1;
            tableDoc.set(tableDoc.indexOf(selectedTableValue), tableDoc.get(pos));
            tableDoc.set(pos, selectedTableValue);
            tableDocIndex();
        }
    }

    public void moveDown() {
        if (selectedTableValue != null & tableDoc.size() > 0 & selectedTableValue.getPosition() < tableDoc.size()) {
//            for (DocumentTableValue value : tableDoc) {
//                if(value.equals(selectedTableValue)){
//                    selectedTableValue.setPosition(selectedTableValue.getPosition()+1);
//                }
//            }
            int pos = tableDoc.indexOf(selectedTableValue) + 1;
            tableDoc.set(tableDoc.indexOf(selectedTableValue), tableDoc.get(pos));
            tableDoc.set(pos, selectedTableValue);
            tableDocIndex();
        }
    }

    public void tableDocIndex() {
        int i = 0;
        for (DocumentTableValue value : tableDoc) {
            i++;
            value.setPosition(i);
        }

    }

    public void openDocument() throws IOException, ServletException {
        fillTableDocumentAll();

        FacesContext fc = FacesContext.getCurrentInstance();
        NavigationHandler nh = fc.getApplication().getNavigationHandler();
        nh.handleNavigation(fc, null, docType);

    }

    public void newDocument() {
        this.selectedDocument = new DocumentIncome();
        this.tableDoc = new ArrayList<DocumentTableValue>();

        FacesContext fc = FacesContext.getCurrentInstance();
        NavigationHandler nh = fc.getApplication().getNavigationHandler();
        nh.handleNavigation(fc, null, docType);

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

    public void pickWarehouseValue() {


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

    public void fillWarehouseListAll() {
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;

        warehouseList = new ArrayList<Warehouse>();

        try {
            conn = Database.getConnection();

            stmt = conn.createStatement();
            rs = stmt.executeQuery("select * from spr_warehouse order by id");

            while (rs.next()) {
                Warehouse value = new Warehouse();
                value.setId(rs.getLong("id"));
                value.setName(rs.getString("name"));

                warehouseList.add(value);
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
        //TODO really need?
//        this.selectedStock = null;
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
        PreparedStatement preparedStatement = null;



//        String update_doc_table = "UPDATE "+docType+"_table SET id_stock = ?, position = ?, amount = ? WHERE id = ?";
        String insert_doc_table_SQL = "INSERT INTO "+docType+"_table (id_doc, id_stock, amount, position) VALUES (?,?,?,?)";
        String insert_new_doc_SQL = "INSERT INTO "+docType+" (date_doc, comment, id_warehouse_to) VALUES (?, ?,?)";
        String get_last_id_SQL = "SELECT max(id) as last_id FROM "+docType;
        try {
            conn = Database.getConnection();

            if(selectedDocument.getId()==null){
                preparedStatement = conn.prepareStatement(insert_new_doc_SQL);
                preparedStatement.setLong(1, selectedDocument.getDateDoc().getTime());
                preparedStatement.setString(2, selectedDocument.getComment());
                preparedStatement.setInt(3, selectedWarehouse.getId().intValue());
                preparedStatement.executeUpdate();

                stmt = conn.createStatement();
                rs = stmt.executeQuery(get_last_id_SQL);

                while (rs.next()) {
                    selectedDocument.setId(rs.getLong(1));
                }

            }else {
                String delete_doc_table_SQL = "DELETE FROM "+docType+"_table WHERE id_doc = " + selectedDocument.getId().intValue();
                String update_doc_SQL = "UPDATE "+docType+" SET date_doc = ?, comment = ?, id_warehouse_to =? WHERE id = " + selectedDocument.getId().intValue();

                preparedStatement = conn.prepareStatement(delete_doc_table_SQL);
                preparedStatement.executeUpdate();

                preparedStatement = conn.prepareStatement(update_doc_SQL);
                preparedStatement.setLong(1, selectedDocument.getDateDoc().getTime());
                preparedStatement.setString(2, selectedDocument.getComment());
                preparedStatement.setInt(3, selectedWarehouse.getId().intValue());
                preparedStatement.executeUpdate();
            }
            for (DocumentTableValue value : tableDoc) {
                preparedStatement = conn.prepareStatement(insert_doc_table_SQL);
                preparedStatement.setInt(1, selectedDocument.getId().intValue());
                preparedStatement.setInt(2, value.getStock().getId().intValue());
                preparedStatement.setInt(3, value.getAmount().intValue());
                preparedStatement.setInt(4, value.getPosition());
                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public String getEmptyPosition() {
        return emptyPosition;
    }

    public void setEmptyPosition(String emptyPosition) {
        this.emptyPosition = emptyPosition;
    }

    public List<Warehouse> getWarehouseList() {
        //TODO really need?
//        this.selectedWarehouse = null;
        fillWarehouseListAll();
        return warehouseList;
    }

    public void setWarehouseList(List<Warehouse> warehouseList) {
        this.warehouseList = warehouseList;
    }

    public Warehouse getSelectedWarehouse() {
        return selectedWarehouse;
    }

    public void setSelectedWarehouse(Warehouse selectedWarehouse) {
        this.selectedWarehouse = selectedWarehouse;
    }

    public String openDocumentList(String docType){
        this.docType = docType;
        fillDocumentListAll();
        return docType+"_list";
    }

    protected void setDocumentType(){
//        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
//        this.docType = params.get("docType");
//        if(docType==null){
//            this.docType="doc_income";
//        }
    }
}
