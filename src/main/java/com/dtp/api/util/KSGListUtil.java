package com.dtp.api.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class KSGListUtil {
	
	public static <T> Collection<List<T>> partition(List<T> collection, int n) {
		AtomicInteger counter = new AtomicInteger();
		return collection.stream()
				.collect(Collectors.groupingBy(it -> counter.getAndIncrement() / n))
				.values();
	}
	public static <T> Predicate<T> distinctByKey( Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> map = new HashMap<>();
		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

}
