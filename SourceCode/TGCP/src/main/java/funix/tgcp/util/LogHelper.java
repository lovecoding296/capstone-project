package funix.tgcp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogHelper {
    private final Logger logger;

    public LogHelper(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    private String getMethodName() {
        return Thread.currentThread().getStackTrace()[3].getMethodName(); // Lấy method gọi log
    }

    public void info(String message) {
        logger.info("[{}] {}", getMethodName(), message);
    }

    public void error(String message, Throwable throwable) {
        logger.error("[{}] {}", getMethodName(), message, throwable);
    }

    public void debug(String message) {
        logger.debug("[{}] {}", getMethodName(), message);
    }

    public void warn(String message) {
        logger.warn("[{}] {}", getMethodName(), message);
    }
	
	public void info(String format, Object... arguments) {
		logger.info(format, arguments);
	}
	
	public void error(String format, Object... arguments) {
		logger.error(format, arguments);
	}
	
	public void debug(String format, Object... arguments) {
		logger.debug(format, arguments);
	}
	
	public void warn(String format, Object... arguments) {
		logger.warn(format, arguments);
	}
	
}

