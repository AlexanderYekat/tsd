import android.app.Application;

public class GlobalClass extends Application {
    private String Version = "1.22";

    public String getVersion(){
        return Version;
    }
}
