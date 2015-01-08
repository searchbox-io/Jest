package io.searchbox.core.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * @author happyprg
 */

public class DocumentExclusionStrategy implements ExclusionStrategy {

	private final Class<?> typeToSkip;

	private DocumentExclusionStrategy() {
		this.typeToSkip = null;
	}

	private DocumentExclusionStrategy(Class<?> typeToSkip) {
		this.typeToSkip = typeToSkip;
	}

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return (clazz == typeToSkip);
	}

	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		return f.getAnnotation(Exclude.class) != null;
	}

	public static DocumentExclusionStrategy createWithSkipType(Class<?> typeToSkip) {
		return new DocumentExclusionStrategy(typeToSkip);
	}

	public static DocumentExclusionStrategy create() {
		return new DocumentExclusionStrategy();
	}
}
