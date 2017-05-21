package ai.agusibrahim.xhrlog;
import android.app.Application;
import cat.ereza.customactivityoncrash.*;

public class App extends Application
{

	@Override
	public void onCreate() {
		CustomActivityOnCrash.install(this);
		super.onCreate();
	}
	
}
