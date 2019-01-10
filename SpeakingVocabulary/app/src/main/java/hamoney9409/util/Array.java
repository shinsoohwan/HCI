package hamoney9409.util;

import java.util.Random;

/**
 * Created by 상범 on 2018-01-12.
 */

public class Array
{
    public static <T> T[] shuffle(T[] array)
    {
        Random rand = new Random();
//
//        int[] indices = new int[array.length];
//        for(int i = 0; i<array.length; i++)
//        {
//            indices[i] = i;
//        }

        for(int i = 0; i<array.length-1; i++)
        {
            T temp;
            int swapIndex = i + rand.nextInt(array.length - i);
            temp = array[i];
            array[i] = array[swapIndex];
            array[swapIndex] = temp;
        }

        return array;
    }
}
