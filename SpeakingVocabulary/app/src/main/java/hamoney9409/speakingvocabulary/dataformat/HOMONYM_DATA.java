package hamoney9409.speakingvocabulary.dataformat;

import org.apache.commons.collections4.map.ListOrderedMap;

import java.util.ArrayList;

/**
 * Created by 상범 on 2018-02-12.
 */

public class HOMONYM_DATA extends ListOrderedMap<String, ArrayList<String>>
{
    public String getEnglishWord(int index)
    {
        return this.get(index);
    }

    public ArrayList<String> getMeans(int index)
    {
        return this.getValue(index);
    }

    public ArrayList<String> getMeans(String englishWord)
    {
        return this.get(englishWord);
    }

    public boolean add(WORD_DATA data)
    {
        ArrayList<String> means = getMeans(data.englishWord);
        if (means == null)
        {
            means = new ArrayList<String>();
            this.put(data.englishWord, means);
        }

        return means.add(data.mean);
    }
}
