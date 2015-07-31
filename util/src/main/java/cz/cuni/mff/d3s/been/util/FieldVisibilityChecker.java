package cz.cuni.mff.d3s.been.util;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.introspect.VisibilityChecker;

final class FieldVisibilityChecker<T extends VisibilityChecker<T>> implements VisibilityChecker<T> {

	T self;

	public FieldVisibilityChecker() {
		assignSelf();
	}

	@SuppressWarnings("unchecked")
	private final void assignSelf() {
		self = (T) this;
	}

	@Override
	public T with(JsonAutoDetect ann) {
		return self;
	}

	@Override
	public T with(Visibility v) {
		return self;
	}

	@Override
	public T withVisibility(JsonMethod method, Visibility v) {
		return self;
	}

	@Override
	public T withGetterVisibility(Visibility v) {
		return self;
	}

	@Override
	public T withIsGetterVisibility(Visibility v) {
		return self;
	}

	@Override
	public T withSetterVisibility(Visibility v) {
		return self;
	}

	@Override
	public T withCreatorVisibility(Visibility v) {
		return self;
	}

	@Override
	public T withFieldVisibility(Visibility v) {
		return self;
	}

	@Override
	public boolean isGetterVisible(Method m) {
		return false;
	}

	@Override
	public boolean isGetterVisible(AnnotatedMethod m) {
		return false;
	}

	@Override
	public boolean isIsGetterVisible(Method m) {
		return false;
	}

	@Override
	public boolean isIsGetterVisible(AnnotatedMethod m) {
		return false;
	}

	@Override
	public boolean isSetterVisible(Method m) {
		return false;
	}

	@Override
	public boolean isSetterVisible(AnnotatedMethod m) {
		return false;
	}

	@Override
	public boolean isCreatorVisible(Member m) {
		return false;
	}

	@Override
	public boolean isCreatorVisible(AnnotatedMember m) {
		return false;
	}

	@Override
	public boolean isFieldVisible(Field f) {
		return true;
	}

	@Override
	public boolean isFieldVisible(AnnotatedField f) {
		return true;
	}

}
