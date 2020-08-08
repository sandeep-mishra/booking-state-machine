package mishra.sandeep.acceptancestatemachine.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Acceptance {
    @Id
    private
    String id;
    private String type;
    private AcceptanceStates state;
    private String timestamp;
    private String faId;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    private String currency;

    public String getFaId() {
        return faId;
    }

    public void setFaId(String faId) {
        this.faId = faId;
    }

    public Acceptance() {
    }

    public Acceptance(String id) {
        this.setId(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AcceptanceStates getState() {
        return state;
    }

    public void setState(AcceptanceStates state) {
        this.state = state;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
