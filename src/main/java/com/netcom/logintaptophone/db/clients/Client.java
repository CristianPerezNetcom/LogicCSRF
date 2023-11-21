package com.netcom.logintaptophone.db.clients;

import com.netcom.logintaptophone.db.key.AllowedKeys;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Client")
public class Client {

    @Id
    private Integer id;

    private String clientName;

    private String clientAddress;

    private Integer clientPhone;

    private String clientAlias;

    private Timestamp creationDateTime;

    private boolean enable;

    @OneToMany(mappedBy = "client")
    private List<AllowedKeys> allowedKeys;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public Integer getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(Integer clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getClientAlias() {
        return clientAlias;
    }

    public void setClientAlias(String clientAlias) {
        this.clientAlias = clientAlias;
    }

    public Timestamp getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(Timestamp creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
