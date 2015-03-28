public class Mapper<D> {
	private Mapping<D> map = null;

	public Mapper(Mapping<D> map) {
		this.map = map;
	}

	public D apply(D data) {
		return map.apply(data);
	}
}