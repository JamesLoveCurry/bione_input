package com.yusys.bione.frame.base.decorator;

import org.sitemesh.SiteMeshContext;
import org.sitemesh.content.tagrules.TagRuleBundle;
import org.sitemesh.content.ContentProperty;
import org.sitemesh.tagprocessor.State;

/**
 * {@link TagRuleBundle} for custom SiteMesh tags used for building/applying
 * decorators.
 * 
 * @author Joe Walnes
 */
public class DecoratorDivRuleBundle implements TagRuleBundle {

	public void install(State defaultState, ContentProperty contentProperty,
			SiteMeshContext siteMeshContext) {
		// TODO: Support real XML namespaces.
		defaultState.addRule("sitemesh:div", new SiteMeshDivWriteRule(
				siteMeshContext));
	}

	public void cleanUp(State defaultState, ContentProperty contentProperty,
			SiteMeshContext siteMeshContext) {
		// No op.
	}
}
