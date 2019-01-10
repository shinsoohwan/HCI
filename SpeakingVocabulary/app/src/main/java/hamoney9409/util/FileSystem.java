package hamoney9409.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

/*
 * Created by 상범 on 2017-07-02.
 *  compile 'com.ibm.icu:icu4j:52.1' 을 gradie에 반드시 추가할 것.
 */

public class FileSystem
{
    private static CharsetMatch[] DetectCharset(InputStream inputStream) throws
            FileNotFoundException
    {
        byte[] header = new byte[32];
        try
        {
            inputStream.read(header);
        }
        catch (IOException e)
        {

        }
        CharsetDetector charsetDetector = new CharsetDetector();
        charsetDetector.setText(header);
        return charsetDetector.detectAll();
    }

    public static InputStreamReader getTextFileReader(Context base, Uri uri, Locale locale) throws FileNotFoundException
    {
        InputStream inputStream = base.getContentResolver().openInputStream(uri);

        CharsetMatch[] matches = DetectCharset(inputStream);

        String finalEncoding;
        if (matches.length == 0)
        {
            finalEncoding = hamoney9409.util.Locale.getPriorityEncodings(locale)[0];
        }
        else
        {
            finalEncoding = matches[0].getName();
        }

        for (String priorityEncoding : hamoney9409.util.Locale.getPriorityEncodings(locale))
        {
            for(CharsetMatch match : matches)
            {
                if (match.getName().equalsIgnoreCase(priorityEncoding))
                {
                    finalEncoding = match.getName();
                    break;
                }
            }
        }

        // 파일 닫고 다시 읽는다
        try
        {
            inputStream.close();
        }
        catch(IOException e)
        {

        }
        inputStream =  base.getContentResolver().openInputStream(uri);

        try
        {
            return new InputStreamReader(inputStream, finalEncoding);
        }
        catch(UnsupportedEncodingException e)
        {
            return new InputStreamReader(inputStream);
        }
    }

    public static File getPathWithoutFilename(File file)
    {
        if (file != null)
        {
            if (file.isDirectory())
            {
                // no file to be split off. Return everything
                return file;
            }
            else
            {
                String filename = file.getName();
                String filepath = file.getAbsolutePath();

                // Construct path without file name.
                String pathwithoutname = filepath.substring(0, filepath.length() - filename.length());
                if (pathwithoutname.endsWith("/"))
                {
                    pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length() - 1);
                }
                return new File(pathwithoutname);
            }
        }
        return null;
    }

    public static String getExtension(String uri)
    {
        if (uri == null)
        {
            return null;
        }

        int dot = uri.lastIndexOf(".");
        if (dot >= 0)
        {
            return uri.substring(dot);
        }
        else
        {
            return "";
        }
    }

    public static String getMimeType(File file)
    {
        String extension = getExtension(file.getName());

        if (extension.length() > 0)
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1));

        return "application/octet-stream";
    }

    public static boolean isExternalStorageDocument(Uri uri)
    {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    public static boolean isDownloadsDocument(Uri uri)
    {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri)
    {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri)
    {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
            String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try
        {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst())
            {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        }
        finally
        {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isLocal(String url)
    {
        if (url != null && !url.startsWith("http://") && !url.startsWith("https://"))
        {
            return true;
        }
        return false;
    }

}

