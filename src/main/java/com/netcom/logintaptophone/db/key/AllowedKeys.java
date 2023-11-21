package com.netcom.logintaptophone.db.key;

import com.netcom.logintaptophone.db.clients.Client;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AllowedKeys {

    @Id
    @GeneratedValue
    private Integer idKey;

    private String aesKey;
    private String ivKey;

    @Length(min = 13, max = 1000)
    private String hosts;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    public Client client;

    public Integer getIdKey() {
        return idKey;
    }

    public void setIdKey(Integer idKey) {
        this.idKey = idKey;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public String getIvKey() {
        return ivKey;
    }

    public void setIvKey(String ivKey) {
        this.ivKey = ivKey;
    }

    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }
}
