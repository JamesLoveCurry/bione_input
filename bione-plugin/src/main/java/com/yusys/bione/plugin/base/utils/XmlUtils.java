package com.yusys.bione.plugin.base.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.BaseElement;

/**
 * 将XML文本转换为Object对象及将Object对象转换为XML文本，Object对象的成员变量与XML节点是对应关系<br>
 * <br>
 * 转换时，要求Object对象的成员变量支持getter和setter方法，或者有public权限；要求Object对象及其成员变量实现{@link XmlElement}接口；
 * 如果成员变量是{@linkplain java.util.Collection Collection}或一维数组，那么要求其元素实现{@link XmlElement}接口；<br>
 * <br>
 * 不支持{@linkplain java.util.Map Map}和多维数组的成员变量；也不支持父类的成员变量；<br>
 * <br>
 * 对于不支持{@link XmlElement}接口的成员变量，直接作为XML节点的内容字符串值；<br>
 * <br>
 * XML节点属性通过{@link XmlAttribute}接口定义<br>
 * <br>
 * 例如，对以下XML文本：<br>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;<br>
 * &nbsp;&nbsp;&lt;tsResponse&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;project id="project-id" name="project-name"/&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;tags&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;tag label="tag1"&gt;tagConent1&lt;/tag&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;tag label="tag2"&gt;tagConent2&lt;/tag&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/tags&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/project&gt;<br>
 * &nbsp;&nbsp;&lt;/tsResponse&gt;<br>
 * <br>
 * 其对应的类结构为：<br>
 * <br>
 * public class Response implements XmlElement<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;private Project project;<br>
 * <br>
 * public class Project implements XmlAttribute, XmlElement<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;private Tags tags;<br>
 * <br>
 * public class Tags implements XmlElement<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;private Tag[] tags;<br>
 * <br>
 * public class Tag implements XmlAttribute, XmlElement<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;private String content;<br>
 * 
 * @author maxl
 */
public class XmlUtils {

	/**
	 * 尝试为c找到一个非抽象的可实例化的子类
	 */
	private static Class<?> getInstantiableSubClass(Class<?> c) {
		final Class<?>[] instantiableCollectionClasses = {
			ArrayList.class,
			ArrayDeque.class,
			HashSet.class,
			LinkedList.class,
			PriorityQueue.class
		};

		if (! c.isInterface() && ! Modifier.isAbstract(c.getModifiers())) {
			return c;
		}
		for (Class<?> instantiableCollectionClass : instantiableCollectionClasses) {
			if (c.isAssignableFrom(instantiableCollectionClass)) {
				return instantiableCollectionClass;
			}
		}
		return null;
	}

	/**
	 * 如果obj是XmlElemnt，以它生成新节点，加入到父节点上；否则，作为父节点的字符串值
	 */
	private static void addXmlElement(Object obj, Element parentElement) {
		if (obj instanceof XmlElement) {
			Element newElement = new BaseElement(((XmlElement)obj).getElementName());
			parentElement.add(newElement);
			assembleXMLInternal((XmlElement)obj, newElement);
		} else {
			parentElement.setText(obj.toString());
		}
	}

	/**
	 * obj的成员变量作为子节点加到element上，并设置element的节点属性
	 */
	private static void assembleXMLInternal(XmlElement obj, Element element) {
		Field[] fields = obj.getClass().getDeclaredFields();
		if (obj instanceof XmlAttribute) {
			// 设置XML节点属性
			Map<String, String> attrs = ((XmlAttribute)obj).getAttrs();
			if (attrs != null) {
				for (Iterator<Entry<String, String>> it = attrs.entrySet().iterator(); it.hasNext(); ) {
					Entry<String, String> entry = it.next();
					element.addAttribute(entry.getKey(), entry.getValue());
				}
			}
		}
		try {
			for (int i = 0; i < fields.length; i ++) {
				// 跳过Map类型成员变量
				if (Map.class.isAssignableFrom(fields[i].getType())) {
					continue;
				}
				String fieldName = fields[i].getName();
				// 获取成员变量值
				Object fieldVal = null;
				try {
					Method method = obj.getClass().getMethod("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
					fieldVal = method.invoke(obj);
				} catch (NoSuchMethodException e) {
					fieldVal = fields[i].get(obj);
				}
				if (fieldVal == null) {
					fieldVal = "";
				}
				// 处理成员变量值
				if (fieldVal instanceof Collection) {
					for (Iterator<?> it = ((Collection<?>)fieldVal).iterator(); it.hasNext(); ) {
						addXmlElement(it.next(), element);
					}
				} else if (fieldVal instanceof Object[]) {
					for (int k = 0; k < ((Object[])fieldVal).length; k ++) {
						addXmlElement(((Object[])fieldVal)[k], element);
					}
				} else {
					addXmlElement(fieldVal, element);
				}
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

 	/**
 	 * 将XmlElement对象转换为XML文本；使用方法和限制见{@link XmlUtils}
 	 * 
 	 * @param obj 待转换的XmlElement对象
 	 * @return XML文本
 	 * @throws DocumentException
 	 * @throws IOException
 	 */
	private static String assembleXMLInternal(XmlElement obj) {
		Document doc = DocumentHelper.createDocument();
		doc.setXMLEncoding("UTF8");
		
		// add root element
		Element rootElement = new BaseElement(obj.getElementName());
		doc.add(rootElement);
		
		assembleXMLInternal(obj, rootElement);
		return doc.asXML();
	}

	/**
 	 * 将XmlElement对象转换为XML文本；使用方法和限制见{@link XmlUtils}
 	 * 
 	 * @param obj 待转换的XmlElement对象
 	 * @return XML文本
 	 * @throws DocumentException
 	 * @throws IOException
 	 */
	public static String assembleXML(XmlElement obj) {
		return assembleXMLInternal(obj);
	}

	/**
	 * 找到setter方法，支持子类对象向父类对象/接口赋值
	 */
	private static Method getSetMethod(String fieldName, Class<?> objClass, Class<?> valueClass) {
		String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		Method[] methods = objClass.getMethods();
		for (int i = 0; i < methods.length; i ++) {
			if (methods[i].getName().equals(methodName) && methods[i].getParameterTypes().length == 1 &&
					methods[i].getParameterTypes()[0].isAssignableFrom(valueClass)) {
				return methods[i];
			}
		}
		return null;
	}

	/**
	 * 将成员变量值设置到成员变量上
	 * 
	 * @param obj 成员变量所属对象
	 * @param field 成员变量对象定义
	 * @param value 成员变量值
	 */
	private static void setObjectValue(Object obj, Field field, Object value) {
		String fieldName = field.getName();
		Class<?> fieldType = field.getType();
		String fieldTypeName = fieldType.getCanonicalName();
		Method method = getSetMethod(fieldName, obj.getClass(), value.getClass());
		try {
			if (method != null) {
				method.invoke(obj, value);
			} else if (value instanceof String) {
				if (fieldTypeName.equals("boolean")) {
					field.setBoolean(obj, Boolean.parseBoolean((String)value));
				} else if (fieldTypeName.equals("byte")) {
					field.setByte(obj, Byte.parseByte((String)value));
				} else if (fieldTypeName.equals("char") || fieldTypeName.equals("java.lang.Character")) {
					field.setChar(obj, ((String)value).charAt(0));
				} else if (fieldTypeName.equals("short")) {
					field.setShort(obj, Short.parseShort((String)value));
				} else if (fieldTypeName.equals("int")) {
					field.setInt(obj, Integer.parseInt((String)value));
				} else if (fieldTypeName.equals("long")) {
					field.setLong(obj, Long.parseLong((String)value));
				} else if (fieldTypeName.equals("float")) {
					field.setFloat(obj, Float.parseFloat((String)value));
				} else if (fieldTypeName.equals("double")) {
					field.setDouble(obj, Double.parseDouble((String)value));
				} else if (fieldTypeName.equals("java.lang.String")) {
					field.set(obj, value);
				} else {
					Constructor<?> constructor = fieldType.getDeclaredConstructor(String.class);
					if (constructor != null) {
						field.set(obj, constructor.newInstance(value));
					} else {
						field.set(obj, value);
					}
				}
			} else {
				field.set(obj, value);
			}
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 根据field成员变量扫描parentElement的子节点，找到与成员变量相符的子节点，生成对应对象
	 * 
	 * @param parentElement 父节点
	 * @param field 成员变量对象定义
	 * @return 子节点对象
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@SuppressWarnings("unchecked")
	private static Object assembleObjectInternal(Element parentElement, Field field)
			throws InstantiationException, IllegalAccessException {
		Class<?> fieldType = field.getType();
		if (Collection.class.isAssignableFrom(fieldType) || fieldType.isArray()) {
			// 找到集合／数组内元素类型
			Class<?> itemType;
			if (fieldType.isArray()) {
				itemType = fieldType.getComponentType();
			} else {
				Type t = field.getGenericType();
				if (t instanceof ParameterizedType) {
					// 有泛型定义
					ParameterizedType parameterizedType = (ParameterizedType)t;
					itemType = (Class<?>)parameterizedType.getActualTypeArguments()[0];
				} else {
					throw new RuntimeException("Do not know the ParameterizedType of field: " + field.getName()
							+ " on element: " + parentElement.getName());
				}
			}
			if (XmlElement.class.isAssignableFrom(itemType)) {
				// 尝试创建一个XmlElement对象，以获取其对应的XML节点名称
				XmlElement testObj = (XmlElement)itemType.newInstance();
				String elementName = testObj.getElementName();
				List<Object> list = new ArrayList<Object>();
				for (Iterator<Element> it = (Iterator<Element>)parentElement.elementIterator(); it.hasNext(); ) {
					// 扫描父节点，找到所有其名称与XmlElement对象的XML节点名称一致的子节点
					Element element = it.next();
					if (element.getName().equals(elementName)) {
						// 第一次加入list时，可以重用testObj
						list.add(assembleObjectInternal(element, itemType, testObj));
						// 但list后面加入的对象必须重新创建
						testObj = null;
					}
				}
				// 如果为空列表，直接返回null
				return list.size() > 0 ? list : null;
			} else {
				throw new RuntimeException("Do not know how to process the element: " + parentElement.getName()
						+ " and field: " + field.getName());
			}
		}
		if (XmlElement.class.isAssignableFrom(fieldType)) {
			// 不是集合／数组，尝试创建一个XmlElement对象，以获取其对应的XML节点名称
			XmlElement testObj = (XmlElement)fieldType.newInstance();
			for (Iterator<Element> it = (Iterator<Element>)parentElement.elementIterator(); it.hasNext(); ) {
				// 扫描父节点，找到一个其名称与XmlElement对象的XML节点名称一致的子节点
				Element element = it.next();
				if (element.getName().equals(testObj.getElementName())) {
					// 可以重用testObj
					return assembleObjectInternal(element, fieldType, testObj);
				}
			}
			return null;
		}
		return parentElement.getText();
	}

	/**
 	 * 将XML元素转换为Object对象
 	 * 
	 * @param element 待转换的XML元素
	 * @param c 目标Object对象类
	 * @param obj 已创建的目标Object对象，可能为null
	 * @return 转换后的Object对象
	 */
	@SuppressWarnings("unchecked")
	private static Object assembleObjectInternal(Element element, Class<?> c, Object obj) {
		try {
			if (obj == null) {
				obj = c.newInstance();
			}
			Field[] fields = obj.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i ++) {
				// 跳过Map类成员变量
				if (Map.class.isAssignableFrom(fields[i].getType())) {
					continue;
				}
				Object subObj = assembleObjectInternal(element, fields[i]);
				if (subObj == null) {
					continue;
				}
				Class<?> fieldType = fields[i].getType();
				if (Collection.class.isAssignableFrom(fieldType)) {
					// fieldType是集合
					if (fieldType.isAssignableFrom(ArrayList.class)) {
						// fieldType可由ArrayList赋值，那么由assembleObjectInternal返回的就是ArrayList，可以直接赋值
						setObjectValue(obj, fields[i], subObj);
					} else {
						// 找到可实例化的集合类
						Class<?> instantiableFieldType = getInstantiableSubClass(fieldType);
						if (instantiableFieldType == null) {
							throw new InstantiationException("Instantiation Fail: " + fieldType.getCanonicalName());
						}
						// 并创建实例
						Collection<Object> fieldValue = (Collection<Object>)instantiableFieldType.newInstance();
						// 将assembleObjectInternal返回内容加入到新实例中
						fieldValue.addAll((Collection<?>)subObj);
						// 以新实例赋值
						setObjectValue(obj, fields[i], fieldValue);
					}
				} else if (fieldType.isArray()) {
					// 当成员变量是数组时，逐项拷贝
					List<?> list = (List<?>)subObj;
					// subObj不为null，就说明返回的列表长度大于0
					Object[] array = (Object[])Array.newInstance(list.get(0).getClass(), list.size());
					for (int k = 0; k < array.length; k ++) {
						array[k] = list.get(k);
					}
					setObjectValue(obj, fields[i], array);
				} else {
					setObjectValue(obj, fields[i], subObj);
				}
			}
			if (XmlAttribute.class.isAssignableFrom(c)) {
				Map<String, String> attrs = new HashMap<String, String>();
				for (Iterator<Attribute> it = (Iterator<Attribute>)element.attributeIterator(); it.hasNext(); ) {
					Attribute attr = it.next();
					attrs.put(attr.getName(), attr.getValue());
				}
				((XmlAttribute)obj).setAttrs(attrs);
			}
			return obj;
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
 	 * 将XML元素转换为Object对象；使用方法和限制见{@link XmlUtils}
 	 * 
	 * @param in 待转换的XML文本输入流
	 * @param c 目标对象类
	 * @return 转换后的Object对象
	 */
	private static Object assembleObjectInternal(InputStream in, Class<?> c) {
		SAXReader reader = new SAXReader();
		Document doc;
		try {
			doc = reader.read(in);
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		}
		return assembleObjectInternal(doc.getRootElement(), c, null);
	}

	/**
 	 * 将XML元素转换为Object对象；使用方法和限制见{@link XmlUtils}
 	 * 
	 * @param in 待转换的XML文本输入流
	 * @param c 目标对象类
	 * @return 转换后的Object对象
	 */
	public static Object assembleObject(InputStream in, Class<?> c) {
		return assembleObjectInternal(in, c);
	}

	/**
 	 * 将XML元素转换为Object对象；使用方法和限制见{@link XmlUtils}
 	 * 
	 * @param content 待转换的XML文本字符串
	 * @param c 目标对象类
	 * @return 转换后的Object对象
	 */
	public static Object assembleObject(String content, Class<?> c) throws IOException {
		return assembleObject(new ByteArrayInputStream(content.getBytes("UTF-8")), c);
	}
}
