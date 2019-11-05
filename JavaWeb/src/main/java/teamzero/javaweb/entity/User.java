package teamzero.javaweb.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {

    @Id
    @Column(columnDefinition = "int(11) COMMENT 'user id'")
    private Integer uid;
    @Column(columnDefinition = "varchar(255) COMMENT 'user name'")
    private String uname;
    @Column(columnDefinition = "varchar(255) COMMENT 'user email'")
    private String email;
    @Column(columnDefinition = "varchar(255) COMMENT 'user password'")
    private String pwd;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
