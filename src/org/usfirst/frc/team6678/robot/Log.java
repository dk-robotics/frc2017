package org.usfirst.frc.team6678.robot;

/**
 * Haandtere logging til konsolen paa computeren
 * Created by viktorstrate on 2/27/17.
 */
public class Log {
	
    public enum Type {
        DEBUG(true), INFO(true), MSG(true), WARN(true), ERROR(true);

        private boolean enabled;

        Type(boolean show) {
            this.enabled = show;
        }

        public boolean isEnabled() {
            return enabled;
        }
    }

	Type loggingLevel = Type.ERROR;
    
    public void setLoggingLevel(Type level) {
    	loggingLevel = level;
    }

    private static void log(Type type, String msg){
        if(type.isEnabled()){
            System.out.println(type.name()+": "+msg);
        }
        
        switch(type) {
        case DEBUG:
        	
        }
    }

    public static void debug(String msg) {
        log(Type.DEBUG, msg);
    }

    public static void info(String msg) {
        log(Type.INFO, msg);
    }

    public static void message(String msg) {
        log(Type.MSG, msg);
    }

    public static void warn(String msg) {
        log(Type.WARN, msg);
    }

    public static void error(String msg) {
        log(Type.ERROR, msg);
    }
}
