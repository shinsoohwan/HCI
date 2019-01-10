package hamoney9409.util;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by 상범 on 2018-03-03.
 */

public class Time
{
    public static Date getAverageDate(Date[] dates)
    {
        BigInteger total = BigInteger.ZERO;
        for (Date date : dates) {
            total = total.add(BigInteger.valueOf(date.getTime()));
        }
        BigInteger averageMillis = total.divide(BigInteger.valueOf(dates.length));
        return new Date(averageMillis.longValue());
    }

    public static Date getAverageDate(Iterable<Date> dates)
    {
        BigInteger total = BigInteger.ZERO;
        int i = 0;
        for (Date date : dates) {
            total = total.add(BigInteger.valueOf(date.getTime()));
            i++;
        }
        BigInteger averageMillis = total.divide(BigInteger.valueOf(i));
        return new Date(averageMillis.longValue());
    }
}
