package pojo;

public class Show {
    private String ShowName;
    private String genre;

    public Show(String ShowName, String genre){
        this.ShowName=ShowName;
        this.genre=genre;
    }

    public String getShowName() {
        return ShowName;
    }

    public String getGenre() {
        return genre;
    }
}

