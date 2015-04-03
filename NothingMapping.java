/*
 * Project: Client Server Performance Measurement Program
 * Authors: Jessica Lynch and Andrew Arnopoulos
 * Date:    27-Apr-2015
 */

/**
 * Implements mapping of message.
 */
public class NothingMapping implements Mapping<String>
{
    public String apply(String str)
    {
        return str;
    }
}
