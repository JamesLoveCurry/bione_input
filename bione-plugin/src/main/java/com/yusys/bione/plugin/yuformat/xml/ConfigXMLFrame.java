package com.yusys.bione.plugin.yuformat.xml;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.yusys.bione.comp.utils.FilepathValidateUtils;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil;

/**
 * 配置XML模板的工具
 * @author xch
 *
 */
public class ConfigXMLFrame extends JFrame implements ActionListener {

	private Font font = new Font("宋体", Font.PLAIN, 12); //
	private Font font_b = new Font("宋体", Font.PLAIN, 14); //

	//驱动
	private String str_driver, str_url, str_user, str_pwd; //4个参数!
	private JTextField text_tableName = null; //
	private JButton btn_createXml = null; //
	private JTextArea textArea_createXml = null;//创建Xml的多行文本框

	//文本框
	private JTextField text_dictxml = null, text_dictxml_tabName; //数字字典文本框
	private JButton btn_createXmlByDict = null; //根据数据字典创建xml
	private JTextArea textArea_createXmlByDict = null;//创建Xml的多行文本框

	private JTextField text_path3 = new JTextField(); //
	private JButton btn_updateXmlItem = new JButton("修改XML项"); //
	private JTextArea textArea3_1 = new JTextArea(); //命令格式
	private JTextArea textArea3_2 = new JTextArea(); //

	private YuFormatUtil bsUtil = new YuFormatUtil(); //

	public ConfigXMLFrame() {
		this.setTitle("XML模板配置"); //
		this.setSize(1400, 768);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //
		initialize();
		this.setVisible(true); //
	}

	private void initialize() {
		JTabbedPane tabb = new JTabbedPane(); //
		tabb.addTab("配置模板", new JLabel("")); //
		tabb.addTab("根据表结构创建模板", getCreateXmlPanelByDBTable()); //
		tabb.addTab("根据数据字典创建模板", getCreateXmlPanelByXmlDict()); //
		tabb.addTab("修改模板项", getUpdateXmlItemPanel()); //
		tabb.setFont(font); //

		this.getContentPane().add(tabb); //
	}

	//创建XML..
	private JPanel getCreateXmlPanelByDBTable() {
		str_driver = System.getProperty("driver"); //驱动
		str_url = System.getProperty("url"); //驱动
		str_user = System.getProperty("user"); //驱动
		str_pwd = System.getProperty("pwd"); //驱动

		JTextField textField = new JTextField(); //
		textField.setPreferredSize(new Dimension(900, 25)); //
		textField.setEditable(false); //不可编辑
		textField.setBackground(Color.WHITE); //
		textField.setFont(this.font); //设置字体

		//数据库存连接信息
		String str_dbConnInto = "【driver=" + str_driver + "】【url=" + str_url + "】【user=" + str_user + "】【pwd=" + str_pwd + "】";
		if (str_driver == null || str_url == null || str_user == null || str_pwd == null) {
			str_dbConnInto = str_dbConnInto + "请在启动参数中设置-Ddriver=XXX -Durl=XXX"; //
		}
		textField.setText(str_dbConnInto); //

		text_tableName = new JTextField("表名");
		text_tableName.setPreferredSize(new Dimension(150, 25)); //
		text_tableName.setFont(this.font); //

		//上面的按钮.
		btn_createXml = new JButton("创建XML");
		btn_createXml.setPreferredSize(new Dimension(85, 25)); //
		btn_createXml.setFont(this.font); //
		btn_createXml.addActionListener(this); //

		//上面的按钮面板..
		JPanel panel_north = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
		panel_north.add(textField);
		panel_north.add(text_tableName);
		panel_north.add(btn_createXml);

		//下面的输出Xml的多行文本框!
		textArea_createXml = new JTextArea();
		textArea_createXml.setFont(this.font_b);
		textArea_createXml.setEditable(false); //
		textArea_createXml.setForeground(Color.BLUE); //

		//最后组装!
		JPanel allPanel = new JPanel(new BorderLayout()); //
		allPanel.add(panel_north, BorderLayout.NORTH); //
		allPanel.add(new JScrollPane(textArea_createXml), BorderLayout.CENTER); //

		return allPanel;
	}

	//根据数字字典创建XML..
	private JPanel getCreateXmlPanelByXmlDict() {
		text_dictxml = new JTextField("E:/YuWorkSpace20190615/FRS/biapp-frs/src/main/java/com/yusys/biapp/frs/base/database/crrs.xml"); //
		text_dictxml.setPreferredSize(new Dimension(900, 25));
		text_dictxml.setFont(this.font); //

		text_dictxml_tabName = new JTextField(""); //
		text_dictxml_tabName.setPreferredSize(new Dimension(150, 25));
		text_dictxml_tabName.setFont(this.font); //

		btn_createXmlByDict = new JButton("创建xml");
		btn_createXmlByDict.setPreferredSize(new Dimension(85, 25)); //
		btn_createXmlByDict.setFont(this.font); //
		btn_createXmlByDict.addActionListener(this); //

		//上面的按钮面板..
		JPanel panel_north = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
		panel_north.add(text_dictxml);
		panel_north.add(text_dictxml_tabName); //表名
		panel_north.add(btn_createXmlByDict);

		//下面的输出Xml的多行文本框!
		textArea_createXmlByDict = new JTextArea();
		textArea_createXmlByDict.setFont(this.font_b);
		textArea_createXmlByDict.setEditable(false); //
		textArea_createXmlByDict.setForeground(Color.BLUE); //

		//最后组装!
		JPanel allPanel = new JPanel(new BorderLayout()); //
		allPanel.add(panel_north, BorderLayout.NORTH); //
		allPanel.add(new JScrollPane(textArea_createXmlByDict), BorderLayout.CENTER); //

		return allPanel;
	}

	//批量修改XML中的某一项!
	//
	private JPanel getUpdateXmlItemPanel() {
		JPanel panel = new JPanel(new BorderLayout()); //

		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); //

		text_path3.setText("K:\\123");
		text_path3.setPreferredSize(new Dimension(800, 25));
		btn_updateXmlItem.setPreferredSize(new Dimension(125, 25));

		text_path3.setFont(font);
		btn_updateXmlItem.setFont(font);

		btn_updateXmlItem.addActionListener(this); //
		btnPanel.add(text_path3); //
		btnPanel.add(btn_updateXmlItem); //

		panel.add(btnPanel, BorderLayout.NORTH);

		//
		textArea3_1.setFont(font);
		textArea3_2.setFont(font);

		textArea3_1.setText("类型=主表;字段=autoquery;新值=N\n类型=子表;表单项=CJRQ;字段=query_itemtype;新值=日历");

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(textArea3_1), new JScrollPane(textArea3_2)); //
		split.setDividerLocation(500);
		panel.add(split, BorderLayout.CENTER);

		return panel; //
	}

	@Override
	public void actionPerformed(ActionEvent _evt) {
		if (_evt.getSource() == btn_updateXmlItem) {
			onUpdateXmlItem();
		} else if (_evt.getSource() == btn_createXml) {
			onCreateXml();
		} else if (_evt.getSource() == btn_createXmlByDict) {
			onCreateXmlByDict();
		}
	}

	//创建xml..
	private void onCreateXml() {
		if (str_driver == null || str_url == null || str_user == null || str_pwd == null) {
			JOptionPane.showMessageDialog(this, getMsgLabel("数据库存连接的4个参数不能为空!请在启动参数中设置-Ddriver=XX -Durl=XXX")); //
			return;
		}

		String str_tableName = text_tableName.getText(); //
		if (str_tableName == null || str_tableName.trim().equals("")) {
			JOptionPane.showMessageDialog(this, getMsgLabel("表名不能为空!!")); //
			return;
		}

		Connection dbConn = null; //
		java.sql.Statement stmt = null; //
		ResultSet rs = null; //
		try {
			StringBuilder sb_xml = new StringBuilder(); //
			sb_xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
			sb_xml.append("<root>\r\n");

			sb_xml.append("\r\n");

			sb_xml.append("<templet>\r\n");
			sb_xml.append("  <templetname>" + str_tableName + "</templetname>\r\n");
			sb_xml.append("  <fromtable>" + str_tableName + "</fromtable>\r\n");
			sb_xml.append("  <savetable>" + str_tableName + "</savetable>\r\n");
			sb_xml.append("  <pkname>rid</pkname>\r\n");
			sb_xml.append("  <autoquery>Y</autoquery>\r\n");
			sb_xml.append("  <autocondition></autocondition>\r\n");
			sb_xml.append("  <querycontion></querycontion>\r\n");
			sb_xml.append("  <ishavebillquery>Y</ishavebillquery>\r\n");
			sb_xml.append("  <list_btns>$INSERT;$UPDATE;$DELETE;</list_btns>\r\n");
			sb_xml.append("  <list_ispagebar>Y</list_ispagebar>\r\n");
			sb_xml.append("  <list_ismultisel>N</list_ismultisel>\r\n");
			sb_xml.append("  <list_ischeckstyle>N</list_ischeckstyle>\r\n");
			sb_xml.append("</templet>\r\n");

			sb_xml.append("\r\n");

			Properties props = new Properties();
			props.put("user", str_user);
			props.put("password", str_pwd);

			//之前高并发时,报socket time out，网上查资料说要加上这几个参数!
			props.put("oracle.net.CONNECT_TIMEOUT", "10000000");
			props.put("oracle.net.READ_TIMEOUT", "300000");
			props.put("oracle.jdbc.ReadTimeout", "300000");

			Class.forName(str_driver);
			dbConn = DriverManager.getConnection(str_url, props);

			stmt = dbConn.createStatement(); //
			rs = stmt.executeQuery("select * from " + str_tableName + " where 1=2"); //

			//计算字段名.
			ResultSetMetaData rsmd = rs.getMetaData();
			int li_columncount = rsmd.getColumnCount(); // 总共有几列

			//遍历所有数据
			for (int i = 1; i < li_columncount + 1; i++) { // 遍历各列!
				String str_colName = rsmd.getColumnLabel(i).toLowerCase(); // 统一转小写!

				sb_xml.append("<templet_item>\r\n");
				sb_xml.append("  <itemkey>" + str_colName + "</itemkey>\r\n");
				sb_xml.append("  <itemname>" + str_colName + "</itemname>\r\n");
				sb_xml.append("  <itemtype>文本框</itemtype>\r\n");
				sb_xml.append("  <itemdefine></itemdefine>\r\n");
				sb_xml.append("  <issave>Y</issave>\r\n");
				sb_xml.append("  <ismust>N</ismust>\r\n");
				sb_xml.append("  <card_isshow>Y</card_isshow>\r\n");
				sb_xml.append("  <card_width>300</card_width>\r\n");
				sb_xml.append("  <card_iswrap>Y</card_iswrap>\r\n");
				sb_xml.append("  <list_isshow>Y</list_isshow>\r\n");
				sb_xml.append("  <list_width>100</list_width>\r\n");
				sb_xml.append("  <list_align>中</list_align>\r\n");
				sb_xml.append("  <query_isshow>N</query_isshow>\r\n");
				sb_xml.append("  <query_width>200</query_width>\r\n");
				sb_xml.append("</templet_item>\r\n");

				sb_xml.append("\r\n");
			}

			sb_xml.append("</root>\r\n");

			textArea_createXml.setText(sb_xml.toString());
			textArea_createXml.setSelectionStart(0);
			textArea_createXml.setSelectionEnd(0); //
		} catch (Exception _ex) {
			_ex.printStackTrace(); //
			textArea_createXml.setText("发生异常," + _ex.getMessage() + "," + _ex.getClass());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception _exx) {
					_exx.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception _exx) {
					_exx.printStackTrace();
				}
			}

			if (dbConn != null) {
				try {
					dbConn.close();
				} catch (Exception _exx) {
					_exx.printStackTrace();
				}
			}
		}
	}

	//根据xml字典创建xml模板。。
	private void onCreateXmlByDict() {
		String str_xmlFile = text_dictxml.getText(); //
		if (FilepathValidateUtils.validateFilepath(str_xmlFile)) {
			if (str_xmlFile == null || str_xmlFile.trim().equals("")) {
				JOptionPane.showMessageDialog(this, getMsgLabel("xml文件路径不能为空!")); //
				return;
			}
	
			File file = new File(str_xmlFile); //
			if (!file.exists()) {
				JOptionPane.showMessageDialog(this, getMsgLabel("文件【" + str_xmlFile + "】不存在!")); //
				return;
			}
	
			String str_tabName = text_dictxml_tabName.getText(); //
			if (str_tabName == null || str_tabName.trim().equals("")) {
				JOptionPane.showMessageDialog(this, getMsgLabel("表名不能为空!")); //
				return;
			}
	
			Document doc = readXml(file); //
			List<Element> list = doc.getRootElement().elements("table"); //
			Element findElement = null; //
			for (int i = 0; i < list.size(); i++) {
				Element ele = list.get(i); //
				String str_table = ele.attributeValue("name"); //
				if (str_tabName.equals(str_table)) {
					findElement = ele; //找到了
					break; //
				}
			}
	
			//是否从xml..
			if (findElement == null) {
				JOptionPane.showMessageDialog(this, getMsgLabel("从xml中没有找到【" + str_tabName + "】表")); //
				return;
			}
	
			//所有的列
			String str_tab_descr = findElement.attributeValue("descr"); //表说明
	
			StringBuilder sb_xml = new StringBuilder(); //
			sb_xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
			sb_xml.append("<root>\r\n");
	
			sb_xml.append("\r\n");
	
			sb_xml.append("<templet>\r\n");
			sb_xml.append("  <templetname>" + str_tab_descr + "</templetname>\r\n");
			sb_xml.append("  <fromtable>" + str_tabName + "</fromtable>\r\n");
			sb_xml.append("  <savetable>" + str_tabName + "</savetable>\r\n");
			sb_xml.append("  <pkname>rid</pkname>\r\n");
			sb_xml.append("  <autoquery>Y</autoquery>\r\n");
			sb_xml.append("  <autocondition></autocondition>\r\n");
			sb_xml.append("  <querycontion></querycontion>\r\n");
			sb_xml.append("  <ishavebillquery>Y</ishavebillquery>\r\n");
			sb_xml.append("  <list_btns>$INSERT;$UPDATE;$DELETE;</list_btns>\r\n");
			sb_xml.append("  <list_ispagebar>Y</list_ispagebar>\r\n");
			sb_xml.append("  <list_ismultisel>N</list_ismultisel>\r\n");
			sb_xml.append("  <list_ischeckstyle>N</list_ischeckstyle>\r\n");
			sb_xml.append("</templet>\r\n");
	
			sb_xml.append("\r\n");
	
			List<Element> colList = findElement.element("columns").elements("col"); //多少个列!
			for (int i = 0; i < colList.size(); i++) {
				Element colElement = colList.get(i); //某一列
				String str_colName = colElement.attributeValue("name"); //列名
				String str_colDescr = colElement.attributeValue("descr"); //列说明
	
				sb_xml.append("<templet_item>\r\n");
				sb_xml.append("  <itemkey>" + str_colName + "</itemkey>\r\n");
				sb_xml.append("  <itemname>" + str_colDescr + "</itemname>\r\n");
				sb_xml.append("  <itemtype>文本框</itemtype>\r\n");
				sb_xml.append("  <itemdefine></itemdefine>\r\n");
				sb_xml.append("  <issave>Y</issave>\r\n");
				sb_xml.append("  <ismust>N</ismust>\r\n");
				sb_xml.append("  <card_isshow>Y</card_isshow>\r\n");
				sb_xml.append("  <card_width>300</card_width>\r\n");
				sb_xml.append("  <card_iswrap>Y</card_iswrap>\r\n");
				sb_xml.append("  <list_isshow>Y</list_isshow>\r\n");
				sb_xml.append("  <list_width>100</list_width>\r\n");
				sb_xml.append("  <list_align>中</list_align>\r\n");
				sb_xml.append("  <query_isshow>N</query_isshow>\r\n");
				sb_xml.append("  <query_width>200</query_width>\r\n");
				sb_xml.append("</templet_item>\r\n");
	
				sb_xml.append("\r\n");
			}
	
			sb_xml.append("</root>\r\n");
	
			textArea_createXmlByDict.setText(sb_xml.toString()); //
			textArea_createXmlByDict.setSelectionStart(0);
			textArea_createXmlByDict.setSelectionEnd(0); //
		}
	}

	//修改表单项
	private void onUpdateXmlItem() {
		String str_path = text_path3.getText(); //
		if (FilepathValidateUtils.validateFilepath(str_path)) {
			if (str_path == null || str_path.trim().equals("")) {
				JOptionPane.showMessageDialog(this, getMsgLabel("文件路径不能为空!")); //
				return; //
			}
	
			ArrayList fileList = new ArrayList(); //
			File file = new File(str_path); //
			File[] childFiles = file.listFiles(); //
			for (int i = 0; i < childFiles.length; i++) {
				String str_FileName = childFiles[i].getName(); //
				if (str_FileName.endsWith(".xml")) {
					fileList.add(childFiles[i]); //
				}
			}
	
			if (fileList.size() <= 0) {
				JOptionPane.showMessageDialog(this, getMsgLabel("路径【" + str_path + "】下没有一个XML!")); //
				return; //
			}
	
			//处理命令!!
			String str_cmd = textArea3_1.getText(); //
			if (str_cmd == null || str_cmd.trim().equals("")) {
				JOptionPane.showMessageDialog(this, getMsgLabel("处理命令为空!")); //
				return;
			}
	
			//命令解析成几条
			String[] str_cmds = bsUtil.split(str_cmd, "\n"); //
			int li_rt = JOptionPane.showConfirmDialog(this, getMsgLabel("你是否真的想批量为【" + fileList.size() + "】个XM进行【" + str_cmds.length + "】种处理L吗?"));
			if (li_rt != 0) {
				return; //
			}
	
			//循环处理各个文件
			for (int i = 0; i < fileList.size(); i++) {
				File oneFile = (File) fileList.get(i); //
				dealFile(oneFile, str_cmds);
			}
	
			JOptionPane.showMessageDialog(this, getMsgLabel("处理所有XML成功!")); //
		}
	}

	//处理一个文件
	private void dealFile(File _file, String[] _cmds) {
		Document doc = readXml(_file);

		//模板主表
		Element templetEle = doc.getRootElement().element("templet"); //取得所有结点!

		//模板子表!
		List templetItemList = doc.getRootElement().elements("templet_item"); //取得所有结点!

		//遍历各个规则!
		for (int i = 0; i < _cmds.length; i++) {
			HashMap<String, String> cmdMap = bsUtil.convertStrToHashMap(_cmds[i]); //
			String str_type = cmdMap.get("类型"); //字段=autoquery;新值=N
			if (str_type.equals("主表")) { //
				String str_field = cmdMap.get("字段");
				String str_newValue = cmdMap.get("新值");
				updateElementNewValue(templetEle, str_field, str_newValue); //
			} else if (str_type.equals("子表")) {
				String str_itemkey = cmdMap.get("表单项");
				String str_field = cmdMap.get("字段");
				String str_newValue = cmdMap.get("新值");
				updateElementNewValue2(templetItemList, str_itemkey, str_field, str_newValue); //
			} else if (str_type.equals("删除子表项")) {
				String str_itemkey = cmdMap.get("表单项");
				deleteItemElement(templetItemList, str_itemkey); //
			}
		}

		//写文件!!
		writeXml(_file, doc); //
	}

	//修改模板
	private void updateElementNewValue(Element _ele, String _field, String _newValue) {
		List<Element> list = _ele.elements(); //

		Element findEle = null; //
		for (int i = 0; i < list.size(); i++) {
			Element childEle = list.get(i); //
			String str_name = childEle.getName(); //
			if (str_name.equalsIgnoreCase(_field)) {
				findEle = childEle; //
				break; //
			}
		}

		//
		if (findEle != null) {
			findEle.setText(_newValue); //
		} else { //如果没有找到结点,则创建一个结点!
			Element newEle = _ele.addElement(_field);
			newEle.setText(_newValue);
		}
	}

	//修改模板子表
	private void updateElementNewValue2(List<Element> _itemList, String _itemkey, String _field, String _newValue) {
		Element itemEle = findItemEleByItemkey(_itemList, _itemkey);
		if (itemEle == null) {
			return; //
		}

		//找出结点中的所有字段,
		List<Element> list = itemEle.elements(); //
		Element findEle = null; //
		for (int i = 0; i < list.size(); i++) {
			Element childEle = list.get(i); //
			String str_name = childEle.getName(); //
			if (str_name.equalsIgnoreCase(_field)) {
				findEle = childEle; //
				break; //
			}
		}

		//
		if (findEle != null) {
			findEle.setText(_newValue); //
		} else { //如果没有找到结点,则创建一个结点!
			Element newEle = itemEle.addElement(_field);
			newEle.setText(_newValue);
		}
	}

	//删除子表
	private void deleteItemElement(List<Element> _itemList, String _itemkey) {
		Element itemEle = findItemEleByItemkey(_itemList, _itemkey);
		if (itemEle == null) {
			return; //
		}

		//删除子表项
		_itemList.remove(itemEle); //
	}

	//寻找模板子表..
	private Element findItemEleByItemkey(List<Element> _itemList, String _itemkey) {
		for (int i = 0; i < _itemList.size(); i++) {
			Element ele = _itemList.get(i); //

			List<Element> list = ele.elements(); //子表List
			Element findEle = null; //
			for (int j = 0; j < list.size(); j++) {
				Element childEle = list.get(j); //
				String str_name = childEle.getName(); //
				if (str_name.equalsIgnoreCase("itemkey")) {
					String str_itemkeyValue = childEle.getText(); //
					if (str_itemkeyValue.equalsIgnoreCase(_itemkey)) { //如果itemkey的值等于指定的值!
						return ele; //
					}
				}
			}
		}

		return null;
	}

	private Document readXml(File _file) {
		InputStream ins = null; //
		try {
			ins = new FileInputStream(_file); //
			org.dom4j.io.SAXReader reader = new org.dom4j.io.SAXReader();
			Document doc = reader.read(ins);
			return doc; //
		} catch (Exception _ex) {
			_ex.printStackTrace();
			return null;
		} finally {
			try {
				ins.close(); //
			} catch (Exception _exx) {
				_exx.printStackTrace(); //
			}
		}
	}

	//写文件!
	private void writeXml(File _file, Document _doc) {
		XMLWriter writer = null;
		try {
			OutputFormat outputFormat = OutputFormat.createPrettyPrint();
			outputFormat.setEncoding("UTF-8");
			outputFormat.setIndent(true);
			outputFormat.setIndent("  ");
			outputFormat.setNewlines(true);

			writer = new XMLWriter(new FileOutputStream(_file), outputFormat);
			writer.setIndentLevel(2);
			writer.setEscapeText(true);
			writer.write(_doc);
		} catch (Exception _ex) {
			_ex.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (Exception _exx) {
				_exx.printStackTrace(); //
			}
		}
	}

	private JLabel getMsgLabel(String _text) {
		JLabel label = new JLabel(_text); //
		label.setFont(font);
		return label;
	}
}
