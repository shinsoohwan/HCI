package hamoney9409.util;

import com.ibm.icu.text.CharsetDetector;

/**
 * Created by 상범 on 2017-07-02.
 */

public class Locale
{
    public static String[] getAllDetectableEncodings()
    {
        return CharsetDetector.getAllDetectableCharsets();
    }

    public static String[] getPriorityEncodings(java.util.Locale locale)
    {
        final String language = locale.getLanguage();
        if (language.equals(java.util.Locale.KOREAN.getLanguage()))
        {
            return new String[] {"EUC-KR", "ISO-2022-KR"};
        }
        if (language.equals(java.util.Locale.CHINESE.getLanguage()))
        {
            return new String[] {"Big5", "ISO-2022-CN"};
        }
        else if (language.equals(java.util.Locale.JAPANESE.getLanguage()))
        {
            return new String[] {"EUC-JP", "ISO-2022-JP", "Shift_JIS"};
        }

        for(String isoLanguage : java.util.Locale.getISOLanguages())
        {
            if (language.equals(isoLanguage))
            {
                return new String[] {"ISO-8859-1", "ISO-8859-2", "ISO-8859-5", "ISO-8859-6", "ISO-8859-7", "ISO-8859-8", "ISO-8859-8-I"};
            }
        }

        return new String[0];
    }

    public static boolean isEnglish(char character)
    {
        return ('A' <= character && character <= 'Z') || ('a' <= character && character <= 'z');
    }

    public static boolean isForeign(char character)
    {
        return 0xff < character;
    }

    public static int indexOfEnglish(CharSequence charSequences)
    {
        return indexOfEnglish(charSequences, 0);
    }

    public static int indexOfEnglish(CharSequence charSequences, int fromIndex)
    {
        // 컴퓨터의 비트별로 로직을 다르게하면 빨라지게 만들수있을텐데 ㅎㅎ
        final int length = charSequences.length();

        for(int i=fromIndex; i<length; i++)
        {
            if (isEnglish(charSequences.charAt(i)))
            {
                return i;
            }
        }

        return -1;
    }

    public static int lastIndexOfEnglish(CharSequence charSequences)
    {
        return lastIndexOfEnglish(charSequences,  charSequences.length()-1);
    }

    public static int lastIndexOfEnglish(CharSequence charSequences, int fromIndex)
    {
        // 컴퓨터의 비트별로 로직을 다르게하면 빨라지게 만들수있을텐데 ㅎㅎ
        for(int i=fromIndex; 0<=i; i--)
        {
            if (isEnglish(charSequences.charAt(i)))
            {
                return i;
            }
        }

        return -1;
    }

    public static int indexOfForeign(CharSequence charSequences)
    {
        return indexOfForeign(charSequences, 0);
    }

    public static int indexOfForeign(CharSequence charSequences, int fromIndex)
    {
        // 컴퓨터의 비트별로 로직을 다르게하면 빨라지게 만들수있을텐데 ㅎㅎ
        final int length = charSequences.length();

        for(int i=fromIndex; i<length; i++)
        {
            if (isForeign(charSequences.charAt(i)))
            {
                return i;
            }
        }

        return -1;
    }

    public static int lastIndexOfForeign(CharSequence charSequences)
    {
        return lastIndexOfForeign(charSequences,  charSequences.length()-1);
    }

    public static int lastIndexOfForeign(CharSequence charSequences, int fromIndex)
    {
        // 컴퓨터의 비트별로 로직을 다르게하면 빨라지게 만들수있을텐데 ㅎㅎ
        for(int i=fromIndex; 0<=i; i--)
        {
            if (isForeign(charSequences.charAt(i)))
            {
                return i;
            }
        }

        return -1;
    }

    private static final char[] CHO =
			/*ㄱ ㄲ ㄴ ㄷ ㄸ ㄹ ㅁ ㅂ ㅃ ㅅ ㅆ ㅇ ㅈ ㅉ ㅊ ㅋ ㅌ ㅍ ㅎ */
            {0x3131, 0x3132, 0x3134, 0x3137, 0x3138, 0x3139, 0x3141, 0x3142, 0x3143, 0x3145,
                    0x3146, 0x3147, 0x3148, 0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e};
    private static final char[] JUN =
			/*ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ*/
            {0x314f, 0x3150, 0x3151, 0x3152, 0x3153, 0x3154, 0x3155, 0x3156, 0x3157, 0x3158,
                    0x3159, 0x315a, 0x315b, 0x315c, 0x315d, 0x315e, 0x315f, 0x3160,	0x3161,	0x3162,
                    0x3163};
    /*X ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ*/
    private static final char[] JON =
            {0x0000, 0x3131, 0x3132, 0x3133, 0x3134, 0x3135, 0x3136, 0x3137, 0x3139, 0x313a,
                    0x313b, 0x313c, 0x313d, 0x313e, 0x313f, 0x3140, 0x3141, 0x3142, 0x3144, 0x3145,
                    0x3146, 0x3147, 0x3148, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e};

    public static CharSequence getAbbreviationString(java.util.Locale locale, String input)
    {
        final String language = locale.getLanguage();
        if (language.equals(java.util.Locale.KOREAN.getLanguage())) {
            return koreanChosung(input);
        }
        else
        {
            return input;
        }
    }
    public static CharSequence koreanChosung(String input)
    {
        StringBuffer buf = new StringBuffer(input.length());
        for(int i = 0 ; i < input.length(); i++)
        {
            char ch = input.charAt(i);
            if(0xAC00 <= ch)
            {
                // 초성 변환
                char uniVal = (char) (ch - 0xAC00);
                ch = CHO[((uniVal - (uniVal % 28))/28)/21];
            }
            buf.append(ch);
        }

        return buf;
    }
}
