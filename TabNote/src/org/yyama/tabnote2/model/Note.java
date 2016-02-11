package org.yyama.tabnote2.model;

public class Note {
	public long id;
	public String name;
	public long order;

	public Note(String name, long order) {
		this.name = name;
		this.order = order;
	}

	public Note(long id, String name, long order) {
		this.id = id;
		this.name = name;
		this.order = order;
	}

}
