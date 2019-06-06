package credit.suisse.pojo;

import com.google.gson.Gson;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class EventResult {

    @Id
    private Long pk;

    @Column(nullable = false)
    private String id;

    @Column(nullable = false)
    private Integer duration;

    @Column
    private String type;

    @Column
    private String host;

    @Column(nullable = false)
    private Boolean alert;

    public EventResult(String id, Integer duration, Boolean alert) {
        this.id = id;
        this.duration = duration;
        this.alert = alert;
    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Boolean getAlert() {
        return alert;
    }

    public void setAlert(Boolean alert) {
        this.alert = alert;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
