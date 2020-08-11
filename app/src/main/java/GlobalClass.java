import android.app.Application;

public class GlobalClass extends Application {
    private String Version = "1.23";

    public String getVersion(){
        return Version;
    }
}
