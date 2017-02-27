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

    private static void log(Type type, String tag, String msg){
        if(type.isEnabled()){
            System.out.println(type.name()+": *"+tag+"* "+msg);
        }
    }

    public static void debug(String tag, String msg) {
        log(Type.DEBUG, tag, msg);
    }

    public static void info(String tag,String msg) {
        log(Type.INFO, tag, msg);
    }

    public static void message(String tag, String msg) {
        log(Type.MSG, tag, msg);
    }

    public static void warn(String tag, String msg) {
        log(Type.WARN, tag, msg);
    }

    public static void error(String tag, String msg) {
        log(Type.ERROR, tag, msg);
    }
}
