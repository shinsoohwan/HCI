package hamoney9409.speakingvocabulary.dataformat;

import java.util.Date;

/**
 * Created by 상범 on 2018-02-19.
 */

public class DEACTIVE_WORD_INFO {
    public WORD_DATA mWordData;
    public Date mLastUpdatedDate;
    public int mScore;
    public DEACTIVE_WORD_INFO(WORD_DATA wordData, Date lastUpdatedDate, int score)
    {
        mWordData = wordData;
        mLastUpdatedDate = lastUpdatedDate;
        mScore = score;
    }
}
