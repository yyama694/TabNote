package org.yyama.tabnote2.model;

import java.io.Serializable;

public class Tab implements Serializable {
	private static final long serialVersionUID = 1267037861552622788L;

	public long id;
	public boolean isActivate = false;
	public boolean isReadMode = true;

	public String title;
	public int tabImageId;
	public String value;
	public int tabUnderLineImageId;

	public boolean edited = false;
}
