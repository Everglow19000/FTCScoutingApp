import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.TimeZone;

public class Utils {
    public static String getCurrentUtcTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(calendar.getTime());
    }

    public static String getCurrentYearRange() {
        return "2024-2025";
    }

    public static String getCurrentYear() {
        return "2024";
    }
}
