package com.yusys.bione.frame.base.decorator;

import org.apache.commons.lang3.StringUtils;
import org.sitemesh.tagprocessor.BasicBlockRule;
import org.sitemesh.tagprocessor.Tag;
import org.sitemesh.SiteMeshContext;
import org.sitemesh.content.ContentProperty;
import org.sitemesh.content.Content;

import java.io.IOException;

public class SiteMeshDivWriteRule extends BasicBlockRule<Object> {

	private final SiteMeshContext siteMeshContext;

	public SiteMeshDivWriteRule(SiteMeshContext siteMeshContext) {
		this.siteMeshContext = siteMeshContext;
	}

	@Override
	protected Object processStart(Tag tag) throws IOException {
		String propertyPath = tag.getAttributeValue("property", true);
		Content contentToMerge = siteMeshContext.getContentToMerge();
		// 增加div操作
		if (contentToMerge != null) {
			ContentProperty property = contentToMerge.getExtractedProperties()
					.getChild("div").getChild(propertyPath);
			property.writeValueTo(tagProcessorContext.currentBuffer());

		}
		tagProcessorContext.pushBuffer();
		return null;
	}

	protected ContentProperty getProperty(Content content, String propertyPath) {
		ContentProperty currentProperty = content.getExtractedProperties();
		for (String childPropertyName : StringUtils.split(propertyPath, '.')) {
			currentProperty = currentProperty.getChild(childPropertyName);
		}
		return currentProperty;
	}

	@Override
	protected void processEnd(Tag tag, Object data) throws IOException {
		CharSequence defaultContents = tagProcessorContext
				.currentBufferContents();
		tagProcessorContext.popBuffer();
		if (siteMeshContext.getContentToMerge() == null) {
			tagProcessorContext.currentBuffer().append(defaultContents);
		}
	}
}
