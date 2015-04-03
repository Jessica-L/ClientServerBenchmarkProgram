/*
 * Project: Client Server Performance Measurement Program
 * Authors: Jessica Lynch and Andrew Arnopoulos
 * Date:    27-Apr-2015
 */

public class Mapper<D>
{
    private Mapping<D> map = null;

    public Mapper(Mapping<D> map) {
            this.map = map;
    }

    public D apply(D data) {
            return map.apply(data);
    }
}
