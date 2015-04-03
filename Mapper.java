/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpclientserver;

/**
 *
 * @author Jessica
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
