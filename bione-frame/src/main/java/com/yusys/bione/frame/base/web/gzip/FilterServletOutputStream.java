/* Copyright 2005 Tacit Knowledge LLC
 * 
 * Licensed under the Tacit Knowledge Open License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at http://www.tacitknowledge.com/licenses-1.0.
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yusys.bione.frame.base.web.gzip;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

/**
 * A servlet output stream implementation that can be used to store output
 * 
 * @author Mike Hardy <mhardy@tacitknowledge.com/>
 * @version $Id: FilterServletOutputStream.java,v 1.6 2005/03/10 01:41:31 mike
 *          Exp $
 */
public class FilterServletOutputStream extends ServletOutputStream {

	private OutputStream output;

	public FilterServletOutputStream(OutputStream output) {
		this.output = output;
	}

	@Override
	public void write(int b) throws IOException {
		output.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		output.write(b, off, len);
	}
}
