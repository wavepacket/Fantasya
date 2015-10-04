package fantasya.library.io;

public class IOFactory {
	
	public static final String PATH_SEP = System.getProperties().getProperty("file.separator");
	public static final String APP_DIR = System.getProperties().getProperty("user.dir") + PATH_SEP;
	
	public static final String RULES_DIR = APP_DIR + "rules" + PATH_SEP;
	public static final String CONFIG_DIR = APP_DIR + "config" + PATH_SEP;
	public static final String REPORT_DIR = APP_DIR + "report" + PATH_SEP;
	public static final String ORDER_DIR = APP_DIR + "order" + PATH_SEP;
	
}
