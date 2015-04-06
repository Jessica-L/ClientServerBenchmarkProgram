/*
 * Project: Client Server Performance Measurement Program
 * Authors: Jessica Lynch and Andrew Arnopoulos
 * Date:    27-Apr-2015
 */
package clientserver;

public interface Mapping<D>
{
    public D apply(D data);
}
