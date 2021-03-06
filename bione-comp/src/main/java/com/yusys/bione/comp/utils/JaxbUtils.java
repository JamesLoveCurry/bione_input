/**
 * Copyright (c) 2005-2012 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.yusys.bione.comp.utils;


//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBElement;
//import javax.xml.bind.JAXBException;
//import javax.xml.bind.Marshaller;
//import javax.xml.bind.Unmarshaller;
//import javax.xml.bind.annotation.XmlAnyElement;


/**
 * 使用Jaxb2.0实现XML<->Java Object的Mapper.
 * 
 * 在创建时需要设定所有需要序列化的Root对象的Class.
 * 特别支持Root对象是Collection的情形.
 * 
 * @author calvin
 */
public class JaxbUtils {

//	private static ConcurrentMap<Class, JAXBContext> jaxbContexts = new ConcurrentHashMap<Class, JAXBContext>();
//
//	/**
//	 * Java Object->Xml without encoding.
//	 */
//	public static String toXml(Object root) {
//		Class clazz = ReflectionUtils.getUserClass(root);
//		return toXml(root, clazz, null);
//	}
//
//	/**
//	 * Java Object->Xml with encoding.
//	 */
//	public static String toXml(Object root, String encoding) {
//		Class clazz = ReflectionUtils.getUserClass(root);
//		return toXml(root, clazz, encoding);
//	}
//
//	/**
//	 * Java Object->Xml with encoding.
//	 */
//	public static String toXml(Object root, Class clazz, String encoding) {
//		try {
//			StringWriter writer = new StringWriter();
//			createMarshaller(clazz, encoding).marshal(root, writer);
//			return writer.toString();
//		} catch (JAXBException e) {
//			throw ExceptionUtils.unchecked(e);
//		}
//	}
//
//	/**
//	 * Java Collection->Xml without encoding, 特别支持Root Element是Collection的情形.
//	 */
//	public static String toXml(Collection<?> root, String rootName, Class clazz) {
//		return toXml(root, rootName, clazz, null);
//	}
//
//	/**
//	 * Java Collection->Xml with encoding, 特别支持Root Element是Collection的情形.
//	 */
//	public static String toXml(Collection<?> root, String rootName, Class clazz, String encoding) {
//		try {
//			CollectionWrapper wrapper = new CollectionWrapper();
//			wrapper.collection = root;
//
//			JAXBElement<CollectionWrapper> wrapperElement = new JAXBElement<CollectionWrapper>(new QName(rootName),
//					CollectionWrapper.class, wrapper);
//
//			StringWriter writer = new StringWriter();
//			createMarshaller(clazz, encoding).marshal(wrapperElement, writer);
//
//			return writer.toString();
//		} catch (JAXBException e) {
//			throw ExceptionUtils.unchecked(e);
//		}
//	}
//
//	/**
//	 * Xml->Java Object.
//	 */
//	public static <T> T fromXml(String xml, Class<T> clazz) {
//		try {
//			StringReader reader = new StringReader(xml);
//			return (T) createUnmarshaller(clazz).unmarshal(reader);
//		} catch (JAXBException e) {
//			throw ExceptionUtils.unchecked(e);
//		}
//	}
//
//	/**
//	 * 创建Marshaller并设定encoding(可为null).
//	 * 线程不安全，需要每次创建或pooling。
//	 */
//	public static Marshaller createMarshaller(Class clazz, String encoding) {
//		try {
//			JAXBContext jaxbContext = getJaxbContext(clazz);
//
//			Marshaller marshaller = jaxbContext.createMarshaller();
//
//			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//
//			if (StringUtils.isNotBlank(encoding)) {
//				marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
//			}
//
//			return marshaller;
//		} catch (JAXBException e) {
//			throw ExceptionUtils.unchecked(e);
//		}
//	}
//
//	/**
//	 * 创建UnMarshaller.
//	 * 线程不安全，需要每次创建或pooling。
//	 */
//	public static Unmarshaller createUnmarshaller(Class clazz) {
//		try {
//			JAXBContext jaxbContext = getJaxbContext(clazz);
//			return jaxbContext.createUnmarshaller();
//		} catch (JAXBException e) {
//			throw ExceptionUtils.unchecked(e);
//		}
//	}
//
//	protected static JAXBContext getJaxbContext(Class clazz) {
//		Assert.notNull(clazz, "'clazz' must not be null");
//		JAXBContext jaxbContext = jaxbContexts.get(clazz);
//		if (jaxbContext == null) {
//			try {
//				jaxbContext = JAXBContext.newInstance(clazz, CollectionWrapper.class);
//				jaxbContexts.putIfAbsent(clazz, jaxbContext);
//			} catch (JAXBException ex) {
//				throw new HttpMessageConversionException("Could not instantiate JAXBContext for class [" + clazz
//						+ "]: " + ex.getMessage(), ex);
//			}
//		}
//		return jaxbContext;
//	}
//
//	/**
//	 * 封装Root Element 是 Collection的情况.
//	 */
//	public static class CollectionWrapper {
//
//		@XmlAnyElement
//		protected Collection<?> collection;
//	}
}
