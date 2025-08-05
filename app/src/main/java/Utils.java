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
        if (Calendar.getInstance(TimeZone.getTimeZone("UTC")).get(Calendar.MONTH) >= 8) {
            return Integer.toString(Calendar.getInstance(TimeZone.getTimeZone("UTC")).get(Calendar.YEAR)) + "-" + Integer.toString(Calendar.getInstance(TimeZone.getTimeZone("UTC")).get(Calendar.YEAR)+1);
        }
        return Integer.toString(Calendar.getInstance(TimeZone.getTimeZone("UTC")).get(Calendar.YEAR)-1) + "-" + Integer.toString(Calendar.getInstance(TimeZone.getTimeZone("UTC")).get(Calendar.YEAR));
    }
}
