package cg.parser;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Warnings implements Iterable<String> {
	private List<String> warnings;
	private String header = null;
	
	public Warnings() {
		warnings = new LinkedList<>();
	}
	
	public void setHeader(String s) {
		header = s;
	}
	
	public String getHeader() {
		return header;
	}
	
	public void add(String warning) {
		warnings.add(warning);
	}
	
	public void copyFrom(Warnings other) {
		String otherHeader = other.header;
		if (!other.isEmpty() && otherHeader != null) {
			add(otherHeader);
		}

		for (String s : other) {
			add(s);
		}
	}

	@Override
	public Iterator<String> iterator() {
		return warnings.iterator();
	}
	
	public boolean isEmpty() {
		return warnings.isEmpty();
	}
}
