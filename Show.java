
public class Show {
    private String ShowName;
    private String genre;

    Show(String ShowName, String genre){
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
