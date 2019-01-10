package hamoney9409.speakingvocabulary;

import android.content.res.Resources;
import android.util.Log;

import java.util.Date;

/**
 * Created by 상범 on 2018-03-03.
 */

public class TextUtil
{
    public static CharSequence getDateDiffSimple(Date date1, Date date2, Resources res)
    {
        long diff = date2.getTime() - date1.getTime();
        int afterBefore;

        if (diff < 0)
        {
            afterBefore = R.string.sentenseBefore;
            diff *= -1;
        }
        else
        {
            afterBefore = R.string.sentenseAfter;
        }

        int id;
        if (diff < 1000)
        {
            if (diff == 1)
                id = R.string.wordMilliSecond;
            else
                id = R.string.wordMilliSeconds;
        }
        else
        {
            diff /= 1000;
            if (diff < 120)
            {
                if (diff == 1)
                    id = R.string.wordSecond;
                else
                    id = R.string.wordSeconds;
            }
            else
            {
                diff /= 60;
                if (diff < 120)
                {
                    if (diff == 1)
                        id = R.string.wordMinute;
                    else
                        id = R.string.wordMinutes;
                }
                else
                {
                    diff /= 60;
                    if (diff < 100)
                    {
                        if (diff == 1)
                            id = R.string.wordHour;
                        else
                            id = R.string.wordHours;
                    }
                    else
                    {
                        diff /= 24;
                        if (diff < 28)
                        {
                            if (diff == 1)
                                id = R.string.wordDay;
                            else
                                id = R.string.wordDays;
                        }
                        else if (diff < 92)
                        {
                            diff /= 7;
                            if (diff == 1)
                                id = R.string.wordWeek;
                            else
                                id = R.string.wordWeeks;
                        }
                        else if (diff < 730)
                        {
                            diff = diff * 12 / 365;
                            if (diff == 1)
                                id = R.string.wordMonth;
                            else
                                id = R.string.wordMonths;
                        }
                        else
                        {
                            diff /= 365;
                            if (diff == 1)
                                id = R.string.wordYear;
                            else
                                id = R.string.wordYears;
                        }
                    }
                }
            }
        }

        StringBuffer buf = new StringBuffer();
        buf.append(diff);
        buf.append(' ');
        buf.append(res.getString(id));
        return String.format(res.getString(afterBefore), buf);

        //return buf;
    }
}
