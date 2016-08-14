package beans;

import db.Database;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import java.io.Serializable;
import java.sql.*;

@ManagedBean
@RequestScoped
public class Stock implements Serializable{

    private static final long serialVersionUID = -8773553341290758796L;

    private Long id;
    private String name;
    private boolean service;

    public Stock() {

    }

    public Stock(Long id, String name, boolean service) {
        this.id = id;
        this.name = name;
        this.service = service;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isService() {
        return service;
    }

    public void setService(boolean service) {
        this.service = service;
    }

    public void saveStock(){

        Connection conn = null;
        PreparedStatement preparedStatement = null;


        String insertStockSQL = "INSERT INTO spr_stock (name, isService) VALUES (?,?)";

        try {
            conn = Database.getConnection();

            preparedStatement = conn.prepareStatement(insertStockSQL);
            preparedStatement.setString(1, this.name);
            preparedStatement.setInt(2, isService() ? 1 : 0);

            preparedStatement .executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
