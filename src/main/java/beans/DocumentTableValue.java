package beans;

public class DocumentTableValue {
    private Long id;
    private Stock stock;
    private Float amount;
    private Integer position;

    public DocumentTableValue() {

    }

    public DocumentTableValue(Long id, Stock stock, Float amount, Integer position) {
        this.id = id;
        this.stock = stock;
        this.amount = amount;
        this.position = position;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocumentTableValue that = (DocumentTableValue) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (stock != null ? !stock.equals(that.stock) : that.stock != null) return false;
        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        return !(position != null ? !position.equals(that.position) : that.position != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (stock != null ? stock.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (position != null ? position.hashCode() : 0);
        return result;
    }
}
