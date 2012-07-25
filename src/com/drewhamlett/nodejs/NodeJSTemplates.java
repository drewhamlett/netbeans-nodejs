package com.drewhamlett.nodejs;

import org.netbeans.spi.project.ui.PrivilegedTemplates;
import org.netbeans.spi.project.ui.RecommendedTemplates;

/**
 *
 * @author drewh
 */
public class NodeJSTemplates implements PrivilegedTemplates, RecommendedTemplates {

	private static final String[] TYPES = new String[]{
		"simple-files", // NOI18N
	};
	private static final String[] PRIVILEGED_NAMES = new String[]{
		"Templates/Other/javascript.js",
		"Templates/Other/json.json",
		"Templates/Other/html.html",
		"Templates/Other/CascadeStyleSheet.css",
		"Templates/Other/Folder"
	};

	@Override
	public String[] getRecommendedTypes() {
		return TYPES;
	}

	@Override
	public String[] getPrivilegedTemplates() {
		return PRIVILEGED_NAMES;
	}
}
