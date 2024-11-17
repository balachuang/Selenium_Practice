package bala.tools.model;

import lombok.Data;


@Data
public class ComicInfo
{
    String Name;
    String loginUrl;
    String coverUrl;
    String username;
    String password;
    String[] specialChaps;
    int id;
    int chapStart;
    int chapEnd;
    int pageStart;
    int pageEnd;
    boolean needLogin;
}
