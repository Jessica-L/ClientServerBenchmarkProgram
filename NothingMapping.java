/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpclientserver;

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
