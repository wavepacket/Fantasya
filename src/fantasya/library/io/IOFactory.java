package fantasya.library.io;

public class IOFactory {
	
	public static final String PATH_SEP = System.getProperties().getProperty("file.separator");
	public static final String APP_DIR = System.getProperties().getProperty("user.dir") + PATH_SEP;
	
	public static final String PROPERTIES_DIR = APP_DIR + "properties" + PATH_SEP;
	public static final String CONFIG_DIR = APP_DIR + "config" + PATH_SEP;
	public static final String REPORTS_DIR = APP_DIR + "reports" + PATH_SEP;
	public static final String ORDERS_DIR = APP_DIR + "orders" + PATH_SEP;
	
}
