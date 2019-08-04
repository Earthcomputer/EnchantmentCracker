package enchcracker;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {

	private Log() {
	}

	private static FileHandler fileHandler = null;
	static {
		try {
			fileHandler = new FileHandler("enchcracker.log");
		} catch (Exception e) {
			System.err.println("Exception creating log file");
			e.printStackTrace();
		}
	}

	private static final Logger LOGGER = getLogger();

	public static void cleanupLogging() {
		fileHandler.close();
	}

	private static Logger getLogger() {
		Logger logger = Logger.getLogger("");
		logger.setUseParentHandlers(false);
		logger.addHandler(fileHandler);
		SimpleFormatter formatter = new SimpleFormatter() {
			private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			@Override
			public String format(LogRecord record) {
				String thrown;
				if (record.getThrown() == null) {
					thrown = "";
				} else {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					pw.println();
					record.getThrown().printStackTrace(pw);
					thrown = sw.toString();
				}
				return String.format("[%s] [%s/%s]: %s%s%n", dateFormat.format(record.getMillis()),
						record.getLoggerName(), record.getLevel(), record.getMessage(), thrown);
			}
		};
		for (Handler handler : logger.getHandlers()) {
			handler.setFormatter(formatter);
		}

		return logger;
	}

	public static void info(String message) {
		LOGGER.info(message);
	}

	public static void warn(String message) {
		LOGGER.log(Level.WARNING, message);
	}

	public static void warn(String message, Throwable thrown) {
		LOGGER.log(Level.WARNING, message, thrown);
	}

	public static void fatal(String message) {
		LOGGER.log(Level.SEVERE, message);
	}

	public static void fatal(String message, Throwable thrown) {
		LOGGER.log(Level.SEVERE, message, thrown);
	}

}
