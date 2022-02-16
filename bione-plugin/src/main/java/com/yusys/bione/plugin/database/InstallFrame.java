package com.yusys.bione.plugin.database;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * 首页安装系统的工具界面.因为第一次没有库的时候，无法启动系统，所有只能在外面搞个单独程序进行安装
 * @author xch
 *
 */
public class InstallFrame extends JFrame implements ActionListener, ListSelectionListener {

	private JTextField text_driver, text_url, text_user, text_pwd, text_xmls; //数据库的参数
	private JCheckBox checkBox_base, checkBox_east, checkBox_crrs, checkBox_fsrspisa, checkBox_pocdemo; //基础,east,客户风险,金标/PISA

	private String database_type = null; //

	private JButton btn_loadXml, btn_execSQL; //加载xml数据,执行SQL,两个按钮!!

	private JTable table_createSQL, table_initData; //建表SQL与初始化SQL的界面
	private JTextArea textArea_createSQL, textArea_initData; //显示结果的详细

	private JTabbedPane tabb; //

	private Font font = new Font("宋体", Font.PLAIN, 12); //
	private Font font2 = new Font("宋体", Font.PLAIN, 16); //

	private DataBaseDMO dataBaseDMO = new DataBaseDMO(); //数据库DMO

	private StringBuilder sb_loadXmlLMsg = new StringBuilder(); //
	private StringBuilder sb_execSQLMsg = new StringBuilder(); //

	public InstallFrame() {
		this.setTitle("首次安装系统"); //
		this.setSize(1400, 700); //
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //
		initialize(); //
		this.setVisible(true); //
	}

	//初始化界面.
	private void initialize() {
		//driver
		JLabel label_driver = new JLabel("驱动", SwingConstants.RIGHT); //
		text_driver = new JTextField("com.mysql.jdbc.Driver"); //
		label_driver.setPreferredSize(new Dimension(40, 20));
		text_driver.setPreferredSize(new Dimension(150, 20));
		label_driver.setFont(font);
		text_driver.setFont(font);

		//url
		JLabel label_url = new JLabel("URL", SwingConstants.RIGHT); //
		text_url = new JTextField("jdbc:mysql://127.0.0.1:5555/frs_1?useUnicode=true&characterEncoding=utf-8"); //
		label_url.setPreferredSize(new Dimension(40, 20));
		text_url.setPreferredSize(new Dimension(250, 20));
		label_url.setFont(font);
		text_url.setFont(font);

		//User
		JLabel label_user = new JLabel("用户", SwingConstants.RIGHT); //
		text_user = new JTextField("root"); //
		label_user.setPreferredSize(new Dimension(40, 20));
		text_user.setPreferredSize(new Dimension(60, 20));
		label_user.setFont(font);
		text_user.setFont(font);

		//密码
		JLabel label_pwd = new JLabel("密码", SwingConstants.RIGHT); //
		text_pwd = new JTextField("111111"); //
		label_pwd.setPreferredSize(new Dimension(40, 20));
		text_pwd.setPreferredSize(new Dimension(60, 20));
		label_pwd.setFont(font);
		text_pwd.setFont(font);

		//处理哪些xml
		checkBox_base = new JCheckBox("基础产品", true); //
		checkBox_base.setPreferredSize(new Dimension(80, 20));
		checkBox_base.setFont(font);
		checkBox_base.setEnabled(false); //

		checkBox_east = new JCheckBox("East", false); //
		checkBox_east.setPreferredSize(new Dimension(60, 20));
		checkBox_east.setFont(font);

		checkBox_crrs = new JCheckBox("客户风险", false); //
		checkBox_crrs.setPreferredSize(new Dimension(80, 20));
		checkBox_crrs.setFont(font);

		checkBox_fsrspisa = new JCheckBox("金标&PISA", false); //
		checkBox_fsrspisa.setPreferredSize(new Dimension(80, 20));
		checkBox_fsrspisa.setFont(font);

		checkBox_pocdemo = new JCheckBox("POC模型表", true); //
		checkBox_pocdemo.setPreferredSize(new Dimension(80, 20));
		checkBox_pocdemo.setFont(font);

		//按钮..
		btn_loadXml = new JButton("加载xml安装数据"); //
		btn_execSQL = new JButton("执行SQL"); //

		btn_loadXml.addActionListener(this);
		btn_execSQL.addActionListener(this);

		btn_loadXml.setFont(font);
		btn_execSQL.setFont(font);

		//组装..
		JPanel panel_north = new JPanel(new FlowLayout(FlowLayout.LEFT)); //

		panel_north.add(label_driver); //
		panel_north.add(text_driver); //

		panel_north.add(label_url); //
		panel_north.add(text_url); //

		panel_north.add(label_user); //
		panel_north.add(text_user); //

		panel_north.add(label_pwd); //
		panel_north.add(text_pwd); //

		//加入面板。。
		panel_north.add(checkBox_base); //
		panel_north.add(checkBox_east); //
		panel_north.add(checkBox_crrs); //
		panel_north.add(checkBox_fsrspisa); //
		panel_north.add(checkBox_pocdemo); //

		panel_north.add(btn_loadXml); //
		panel_north.add(btn_execSQL); //

		//建表SQL
		table_createSQL = new JTable(new DefaultTableModel(new String[0][3], new String[] { "表名", "表名说明", "建表SQL", "执行结果" })); //
		table_createSQL.setRowHeight(25); // 设置行高
		table_createSQL.getColumn("表名").setPreferredWidth(150); // 设置列宽
		table_createSQL.getColumn("表名说明").setPreferredWidth(150); // 设置列宽
		table_createSQL.getColumn("建表SQL").setPreferredWidth(600); // 设置列宽
		table_createSQL.getColumn("执行结果").setPreferredWidth(250); //

		table_createSQL.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 单选
		table_createSQL.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // 宽度不是自动调整
		table_createSQL.getSelectionModel().addListSelectionListener(this); // 选择表格监听
		table_createSQL.setFont(font); //

		//初始化数据的SQL
		table_initData = new JTable(new DefaultTableModel(new String[0][3], new String[] { "表名", "插入数据SQL", "执行结果" })); //
		table_initData.setRowHeight(25); // 设置行高
		table_initData.getColumn("表名").setPreferredWidth(150); // 设置列宽
		table_initData.getColumn("插入数据SQL").setPreferredWidth(500); // 设置列宽
		table_initData.getColumn("执行结果").setPreferredWidth(250); //列宽

		table_initData.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 单选
		table_initData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // 宽度不是自动调整
		table_initData.getSelectionModel().addListSelectionListener(this); // 选择表格监听
		table_initData.setFont(font); //

		//
		textArea_createSQL = new JTextArea(); //
		textArea_createSQL.setFont(font); //
		textArea_createSQL.setEditable(false);

		//
		textArea_initData = new JTextArea(); //
		textArea_initData.setFont(font); //
		textArea_initData.setEditable(false);
		textArea_initData.setLineWrap(true);

		JSplitPane split_1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(table_createSQL), new JScrollPane(textArea_createSQL)); //
		split_1.setDividerLocation(450); //

		JSplitPane split_2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(table_initData), new JScrollPane(textArea_initData)); //
		split_2.setDividerLocation(450); //

		tabb = new JTabbedPane(); //
		tabb.setFont(font);
		tabb.addTab("帮助说明", new JScrollPane(getHepTextArea())); //
		tabb.addTab("表结构", split_1); //
		tabb.addTab("初始数据", split_2); //

		this.getContentPane().add(panel_north, BorderLayout.NORTH);
		this.getContentPane().add(tabb, BorderLayout.CENTER);
	}

	//帮助说明
	private JTextArea getHepTextArea() {
		JTextArea textArea = new JTextArea();

		StringBuilder sb_help = new StringBuilder(); //
		sb_help.append("1.这是系统首次安装数据库的工具,先点击【加载xml安装数据】把xml中的数据解析成SQL,并插入到界面表中\r\n\r\n"); //
		sb_help.append("2.点击【执行SQL】，会界面中的SQL提交到上面设置的数据库中\r\n\r\n"); //
		sb_help.append("3.xml数据是在WEB-INT/lib/biapp-frs-1.0.0-SNAPSHOT.jar中的com.yusys.biapp.frs.base.database目录下\r\n\r\n"); //CREATE DATABASE `mydb` CHARACTER SET utf8 
		sb_help.append("4.数据库字符集必须是utf-8，MySQL的建库脚本是CREATE DATABASE frs_1 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;\r\n\r\n"); //

		textArea.setFont(font2);
		textArea.setText(sb_help.toString());
		textArea.setEditable(false);
		return textArea;
	}

	@Override
	public void actionPerformed(ActionEvent _evt) {
		if (_evt.getSource() == btn_loadXml) {
			onLoadXml(); //加载Xml数据
		} else if (_evt.getSource() == btn_execSQL) {
			onExecuteSQL(); //执行SQL
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent _evt) {
		if (!_evt.getValueIsAdjusting()) {
			javax.swing.DefaultListSelectionModel selModel = (DefaultListSelectionModel) _evt.getSource();
			if (selModel == table_createSQL.getSelectionModel()) {
				int li_row = table_createSQL.getSelectedRow(); //
				if (li_row < 0) {
					return;
				}
				String str_sql = (String) table_createSQL.getValueAt(li_row, 2); //
				String str_result = (String) table_createSQL.getValueAt(li_row, 3); //
				textArea_createSQL.setText(str_sql + "\r\n" + (str_result == null ? "" : str_result)); //
				textArea_createSQL.setSelectionStart(0);
				textArea_createSQL.setSelectionEnd(0);
			} else if (selModel == table_initData.getSelectionModel()) {
				int li_row = table_initData.getSelectedRow(); //
				if (li_row < 0) {
					return;
				}
				String str_sql = (String) table_initData.getValueAt(li_row, 1); //
				textArea_initData.setText(str_sql); //
				textArea_initData.setSelectionStart(0);
				textArea_initData.setSelectionEnd(0);
			}
		}
	}

	//执行SQL
	private void onExecuteSQL() {
		if (database_type == null) {
			JOptionPane.showMessageDialog(this, getMsgLabel("请先加载XML!"));
			return;
		}

		String str_driver = text_driver.getText(); //
		String str_url = text_url.getText(); //
		String str_user = text_user.getText(); //
		String str_pwd = text_pwd.getText(); //

		if (str_driver == null || str_driver.equals("")) {
			JOptionPane.showMessageDialog(this, "驱动程序不能为空!");
			return;
		}

		if (str_url == null || str_url.equals("")) {
			JOptionPane.showMessageDialog(this, "URL不能为空!");
			return;
		}

		if (str_user == null || str_user.equals("")) {
			JOptionPane.showMessageDialog(this, "用户不能为空!");
			return;
		}

		int li_rt = JOptionPane.showConfirmDialog(this, getMsgLabel("你真的要把生成的SQL插入到数据库:\r\nURL:【" + str_url + "】\r\n数据库类型：【" + database_type + "】\r\n中么?这一般需要5分钟左右！"), "提示", JOptionPane.YES_NO_OPTION); //
		if (li_rt != 0) {
			return;
		}

		long ll_1 = System.currentTimeMillis(); //
		sb_execSQLMsg.delete(0, sb_execSQLMsg.length()); //
		Connection conn = null; //
		Statement stmt = null; //
		try {
			Class.forName(str_driver); //加载JDBC驱动
			conn = (Connection) DriverManager.getConnection(str_url, str_user, str_pwd); //创建连接
			conn.setAutoCommit(true);  //自动提交
			stmt = (Statement) conn.createStatement(); //打开游标
			executeSQL_createSQL(stmt); //建表SQL执行
			executeSQL_initData(stmt); //初始数据!!

			long ll_2 = System.currentTimeMillis(); //
			sb_execSQLMsg.append("共耗时【" + ((ll_2 - ll_1) / 1000) + "】秒!"); //

			System.out.println("执行SQL结束!!"); //
			JOptionPane.showMessageDialog(this, getMsgLabel(sb_execSQLMsg.toString())); //
		} catch (Exception _ex) {
			_ex.printStackTrace(); //
		} finally {
			try {
				if (stmt != null) {
					stmt.close(); //
				}
			} catch (Exception _exx) {
				_exx.printStackTrace();
			}

			try {
				if (conn != null) {
					conn.close(); //
				}
			} catch (Exception _exx) {
				_exx.printStackTrace();
			}
		}
	}

	//建表!!
	private void executeSQL_createSQL(Statement _stmt) {
		int li_rowCount = table_createSQL.getRowCount();
		int li_count_1 = 0;
		int li_count_2 = 0;
		for (int i = 0; i < li_rowCount; i++) {
			String str_sql = (String) table_createSQL.getValueAt(i, 2); //
			try {
				_stmt.execute(str_sql); //
				table_createSQL.setValueAt("成功!", i, 3); //设置错误
				li_count_1++;
			} catch (Exception _ex) {
				li_count_2++;
				table_createSQL.setValueAt("失败,原因:" + _ex.getMessage(), i, 3); //设置错误
				//_ex.printStackTrace();
				System.err.println("SQL异常【" + _ex.getMessage() + "】【" + _ex.getClass().getName() + "】【" + str_sql + "】"); //
			}
		}

		sb_execSQLMsg.append("建表SQL成功【" + li_count_1 + "】条,失败【" + li_count_2 + "】条!\r\n"); //
		System.out.println("建库成功....");  //
	}

	//初始数据!!
	private void executeSQL_initData(Statement _stmt) {
		int li_rowCount = table_initData.getRowCount();
		int li_count_1 = 0;
		int li_count_2 = 0;
		for (int i = 0; i < li_rowCount; i++) {
			String str_sql = (String) table_initData.getValueAt(i, 1); //
			try {
				_stmt.execute(str_sql); //
				table_initData.setValueAt("成功!", i, 2); //设置错误
				li_count_1++;
			} catch (Exception _ex) {
				li_count_2++;
				table_initData.setValueAt("失败,原因:" + _ex.getMessage(), i, 2); //设置错误
				System.err.println("SQL异常【" + _ex.getMessage() + "】【" + _ex.getClass().getName() + "】【" + str_sql + "】"); //
				//_ex.printStackTrace();
			}
		}

		sb_execSQLMsg.append("初始数据SQL成功【" + li_count_1 + "】条,失败【" + li_count_2 + "】条!\r\n"); //
	}

	//加载Xml数据至前端!
	private void onLoadXml() {
		String str_driver = text_driver.getText(); //
		if (str_driver == null || str_driver.trim().equals("")) {
			JOptionPane.showMessageDialog(this, getMsgLabel("驱动程序不能为空!")); //
			return; //
		}

		if (str_driver.indexOf("oracle") > 0) {
			database_type = "oracle"; //
		} else if (str_driver.indexOf("mysql") > 0) {
			database_type = "mysql"; //
		} else if (str_driver.indexOf("db2") > 0) {
			database_type = "db2"; //
		} else {
			JOptionPane.showMessageDialog(this, getMsgLabel("根据驱动程序类名【" + database_type + "】判断不出是何种数据库!\r\n无法继续!")); //
			return; //
		}

		//解析索引文件...
		Document doc = readXml(this.getClass().getResourceAsStream("/com/yusys/biapp/frs/base/database/1_filelist.xml"));
		ArrayList<String> allXmlTabNameList = new ArrayList<String>(); //
		List<Element> list = doc.getRootElement().elements("xmlfile"); //所有文件内容

		//
		ArrayList<String> chooseXmlsList = new ArrayList<String>(); //
		if (checkBox_base.isSelected()) {
			chooseXmlsList.add("bione.xml"); //
			chooseXmlsList.add("rpt.xml"); //
			chooseXmlsList.add("qrtz.xml"); //
			chooseXmlsList.add("act.xml"); //
		}

		if (checkBox_east.isSelected()) {
			chooseXmlsList.add("east.xml"); //
		}

		//客户风险
		if (checkBox_crrs.isSelected()) {
			chooseXmlsList.add("crrs.xml"); //
		}

		//金标与Pisa
		if (checkBox_fsrspisa.isSelected()) {
			chooseXmlsList.add("fsrs.xml"); //
			chooseXmlsList.add("pisa.xml"); //
		}

		//poc演示模型表
		if (checkBox_pocdemo.isSelected()) {
			chooseXmlsList.add("pocdemo.xml"); //
		}

		sb_loadXmlLMsg.delete(0, sb_loadXmlLMsg.length()); //

		//建表
		onLoadXml_createSQL(list, database_type, chooseXmlsList);

		//初始数据
		onLoadXml_initData(list, database_type, chooseXmlsList); //

		tabb.setSelectedIndex(1); //

		JOptionPane.showMessageDialog(this, getMsgLabel("加载XML数据生成SQL成功!\r\n" + sb_loadXmlLMsg.toString() + "请再执行SQL!")); //
	}

	//加载Xml--建表SQL
	private void onLoadXml_createSQL(List<Element> list, String database_type, ArrayList<String> _chooseXmlsList) {
		//处理建表
		table_createSQL.clearSelection();
		DefaultTableModel model = (DefaultTableModel) (table_createSQL.getModel());
		clearTableModelData(model);

		int li_count = 0;
		for (int i = 0; i < list.size(); i++) {
			Element ele = list.get(i); //
			String str_xmlfileName = ele.attributeValue("tabxml"); //表结构的XML文件,是rpt.xml的样子!!
			if (!_chooseXmlsList.contains(str_xmlfileName)) { //如果不参与安装,则跳过!!
				continue;
			}

			String str_filePath = "/com/yusys/biapp/frs/base/database/" + str_xmlfileName;
			System.err.println("加载文件【" + str_filePath + "】..."); //

			Document doc_item = readXml(this.getClass().getResourceAsStream(str_filePath)); //读取文件内容
			if (doc_item == null) {
				continue;
			}

			List<Element> tabList = doc_item.getRootElement().elements("table"); //
			for (int j = 0; j < tabList.size(); j++) {
				Element tabEle = tabList.get(j); //
				String str_tableName = tabEle.attributeValue("name"); //
				String str_desc = tabEle.attributeValue("descr", ""); //表的注释!
				String str_sql = dataBaseDMO.getCreateTableSQL(tabEle, database_type, false); //计算出该表的建表SQL
				model.addRow(new String[] { str_tableName, str_desc, str_sql });
				li_count++;
			}
		}

		sb_loadXmlLMsg.append("建表SQL【" + li_count + "】条!\r\n"); //
	}

	//加载Xml--初始化数据
	private void onLoadXml_initData(List<Element> list, String database_type, ArrayList<String> _chooseXmlsList) {
		table_initData.clearSelection(); //
		DefaultTableModel model = (DefaultTableModel) (table_initData.getModel());
		clearTableModelData(model);
		int li_count = 0;
		for (int i = 0; i < list.size(); i++) {
			Element ele = list.get(i); //
			String str_xmlfileName = ele.attributeValue("tabxml"); //表结构的XML文件,是rpt.xml的样子!!
			if (!_chooseXmlsList.contains(str_xmlfileName)) { //如果不参与安装,则跳过!!
				continue;
			}

			String str_dataXmlFileName = ele.attributeValue("initdataxml"); //初始化数据的SQL,是【bione_data.xml】 的样子!
			if (str_dataXmlFileName == null || str_dataXmlFileName.equals("")) {
				continue;
			}

			String str_xmlfileName_prefix = str_dataXmlFileName.substring(0, str_dataXmlFileName.indexOf(".")); //
			String str_filePath = "/com/yusys/biapp/frs/base/database/" + str_xmlfileName_prefix + "_1" + ".xml"; //文件
			//System.err.println("加载文件路径【" + str_filePath + "】"); //

			Document doc_item = readXml(this.getClass().getResourceAsStream(str_filePath)); //读取文件内容
			if (doc_item == null) {
				continue;
			}

			//表结构的xml
			String str_tabxml = ele.attributeValue("tabxml"); //表结构的xml
			Document doc_tabStruct = readXml(this.getClass().getResourceAsStream("/com/yusys/biapp/frs/base/database/" + str_tabxml)); //表结构

			List<Element> recordList = doc_item.getRootElement().elements("record"); //
			for (int j = 0; j < recordList.size(); j++) {
				Element recordEle = recordList.get(j); //初始化的值
				String str_tableName = recordEle.attributeValue("tabname"); //表名!
				Element tabStructElement = findTableStructElement(doc_tabStruct, str_tableName); //找出这个表的结构的element

				String str_sql = dataBaseDMO.getCreateInitDataSQLFromXml(recordEle, tabStructElement, database_type); //从数据库计算出该表的初始化SQL
				model.addRow(new String[] { str_tableName, str_sql });
				li_count++;
			}
		}

		sb_loadXmlLMsg.append("初始数据SQL【" + li_count + "】条!\r\n"); //
	}

	//找出一个表的结构的xml
	private Element findTableStructElement(Document doc_tabStruct, String _tableName) {
		List<Element> tabList = doc_tabStruct.getRootElement().elements("table"); //
		for (int i = 0; i < tabList.size(); i++) {
			Element tabEle = tabList.get(i); //
			String str_tabname = tabEle.attributeValue("name"); //
			if (str_tabname.equalsIgnoreCase(_tableName)) { //
				return tabEle;
			}
		}
		return null;
	}

	//
	private void clearTableModelData(DefaultTableModel _model) {
		//		for (int i = _model.getRowCount() - 1; i >= 0; i--) {
		//			_model.removeRow(i); //
		//		}
		_model.setRowCount(0);
	}

	private JTextArea getMsgLabel(String _msg) {
		JTextArea label = new JTextArea(_msg); //
		label.setOpaque(false); //
		label.setEditable(false);
		label.setBorder(BorderFactory.createEmptyBorder());
		label.setFont(font); //
		return label;
	}

	//读文件!
	private Document readXml(InputStream _ins) {
		if (_ins == null) {
			return null;
		}
		try {
			org.dom4j.io.SAXReader reader = new org.dom4j.io.SAXReader();
			Document doc = reader.read(_ins);
			return doc; //
		} catch (Exception _ex) {
			_ex.printStackTrace();
			return null;
		} finally {
			try {
				if (_ins != null) {
					_ins.close(); //
				}
			} catch (Exception _exx) {
				_exx.printStackTrace(); //
			}
		}
	}
}
