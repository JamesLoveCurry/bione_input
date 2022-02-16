package org.sitemesh.content.tagrules.decorate;

import org.apache.commons.lang3.StringUtils;
import org.sitemesh.tagprocessor.BasicBlockRule;
import org.sitemesh.tagprocessor.Tag;
import org.sitemesh.SiteMeshContext;
import org.sitemesh.content.ContentProperty;
import org.sitemesh.content.Content;

import java.io.IOException;

/**
 * Replaces tags that look like {@code <sitemesh:write property='foo'/>} with
 * the {@link ContentProperty} being merged into the current document. The body
 * contents of the tag will be discarded. To get child properties, use a dot
 * notation, e.g. {@code foo.child.grandchild}.
 * 
 * @author Joe Walnes
 * @see SiteMeshContext#getContentToMerge()
 */
public class SiteMeshWriteRule extends BasicBlockRule<Object> {

	private final SiteMeshContext siteMeshContext;

	public SiteMeshWriteRule(SiteMeshContext siteMeshContext) {
		this.siteMeshContext = siteMeshContext;
	}

	@Override
	protected Object processStart(Tag tag) throws IOException {
		String propertyPath = tag.getAttributeValue("property", true);
		Content contentToMerge = siteMeshContext.getContentToMerge();
		// 增加div操作
		if (contentToMerge != null) {
			if (propertyPath!=null&&propertyPath.length()>4&&propertyPath.substring(0, 3).equals("div")) {
				 ContentProperty property=contentToMerge.getExtractedProperties().getChild("div").getChild(propertyPath.substring(4));
				 property.writeValueTo(tagProcessorContext.currentBuffer());
			} else {
				 ContentProperty property = getProperty(contentToMerge, propertyPath);
				 property.writeValueTo(tagProcessorContext.currentBuffer());
			}
		}
		// if (contentToMerge != null) {
		// ContentProperty property = getProperty(contentToMerge, propertyPath);
		// property.writeValueTo(tagProcessorContext.currentBuffer());
		// }
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
