package com.actualize.mortgage.pdfbuilder;

import java.io.IOException;

public interface Section {
	public void draw(Page page, Object data) throws IOException;
}
