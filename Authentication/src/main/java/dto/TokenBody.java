package dto;

import domain.Authority;

import java.util.List;

/**
 * Created by yoon on 15. 9. 1..
 */
public class TokenBody {

    private Long id;
    private List<Authority> authorityList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Authority> getAuthorityList() {
        return authorityList;
    }

    public void setAuthorityList(List<Authority> authorityList) {
        this.authorityList = authorityList;
    }

    @Override
    public String toString() {
        return "TokenBody{" +
                "id=" + id +
                ", authorityList=" + authorityList +
                '}';
    }
}
