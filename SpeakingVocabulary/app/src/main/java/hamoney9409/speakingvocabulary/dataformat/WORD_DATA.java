package hamoney9409.speakingvocabulary.dataformat;

/**
 * Created by 상범 on 2017-05-30.
 */

public class WORD_DATA
{
    public String englishWord;
    public String mean;

    public WORD_DATA(String englishWord, String mean)
    {
        this.englishWord = englishWord;
        this.mean = mean;
    }

    public boolean equals(WORD_DATA other)
    {
        return other.englishWord == englishWord && other.mean == mean;
    }
}
