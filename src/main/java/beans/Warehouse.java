package beans;

import db.Database;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@ManagedBean
@RequestScoped
public class Warehouse implements Serializable {
    private static final long serialVersionUID = 3163660284381583060L;

    private Long id;
    private String name;

    public Warehouse() {
    }

    public Warehouse(Long id, String name) {
        this.id = id;
        this.name = name;
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

    public void saveWarehouse(){

        Connection conn = null;
        PreparedStatement preparedStatement = null;


        String insertWarehouseSQL = "INSERT INTO spr_warehouse (name) VALUES (?)";

        try {
            conn = Database.getConnection();

            preparedStatement = conn.prepareStatement(insertWarehouseSQL);
            preparedStatement.setString(1, this.name);

            preparedStatement .executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
