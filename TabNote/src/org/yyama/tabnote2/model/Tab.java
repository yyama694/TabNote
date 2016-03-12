package org.yyama.tabnote2.model;

import java.io.Serializable;

import org.yyama.tabnote2.service.TabColorEnum;

public class Tab implements Serializable {
	private static final long serialVersionUID = 1267037861552622788L;

	public long id;
	public boolean isActivate = false;
	public boolean isReadMode = true;

	public String title;
	public String value;
	public TabColorEnum color;
	public long noteId;

	public boolean edited = false;
}
