package me.texward.customenchantment.api;

public interface ITrade<T> {
	public void importFrom(T source);

	public T exportTo();
}
