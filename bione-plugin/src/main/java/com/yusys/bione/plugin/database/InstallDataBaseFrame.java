package com.yusys.bione.plugin.database;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.dom4j.Document;
import org.dom4j.Element;

import com.yusys.bione.comp.utils.FilepathValidateUtils;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil2;

public class InstallDataBaseFrame extends JFrame implements ActionListener {

	private String str_initXml = "/com/yusys/biapp/frs/base/database/1_filelist.xml";

	private JTextField text_SQLFile; //SQL文件路径
	private JTextField text_driver, text_url, text_user, text_pwd; //连接数据库的参数!
	private JButton btn_install; //

	private JTextArea textArea_install; //安装结果

	private Font font = new Font("宋体", Font.PLAIN, 12); //

	private ConcurrentLinkedQueue<String> textList = new ConcurrentLinkedQueue<String>(); //

	private YuFormatUtil2 bsUtil = new YuFormatUtil2(); //

	public InstallDataBaseFrame() {
		this.setTitle("安装数据库【" + str_initXml + "】");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //
		this.setSize(1200, 600); //

		initialize(); //初始化界面!!

		this.setVisible(true); //

		new Timer().schedule(new MyTask(), 0, 20); //
	}

	//初始化界面..
	private void initialize() {
		text_SQLFile = new JTextField("D:/A_CreateTableSQL.txt"); //
		text_SQLFile.setFont(font); //
		text_SQLFile.setPreferredSize(new Dimension(200, 25)); //

		//驱动
		JLabel label_driver = new JLabel("驱动类", SwingConstants.RIGHT); //
		label_driver.setFont(font); //
		label_driver.setPreferredSize(new Dimension(50, 25)); //

		text_driver = new JTextField("com.mysql.jdbc.Driver"); //
		text_driver.setFont(font); //
		text_driver.setPreferredSize(new Dimension(150, 25)); //

		//URL
		JLabel label_url = new JLabel("URL", SwingConstants.RIGHT); //
		label_url.setFont(font); //
		label_url.setPreferredSize(new Dimension(30, 25)); //

		text_url = new JTextField("jdbc:mysql://127.0.0.1:5555/frs_new?characterEncoding=utf8"); //
		text_url.setFont(font); //
		text_url.setPreferredSize(new Dimension(400, 25)); //

		//用户名
		JLabel label_user = new JLabel("用户名", SwingConstants.RIGHT); //
		label_user.setFont(font); //
		label_user.setPreferredSize(new Dimension(50, 25)); //

		text_user = new JTextField("root"); //
		text_user.setFont(font); //
		text_user.setPreferredSize(new Dimension(50, 25)); //

		//密码
		JLabel label_pwd = new JLabel("密码", SwingConstants.RIGHT); //
		label_pwd.setFont(font); //
		label_pwd.setPreferredSize(new Dimension(50, 25)); //

		text_pwd = new JTextField("111111"); //
		text_pwd.setFont(font); //
		text_pwd.setPreferredSize(new Dimension(50, 25)); //

		//执行按钮!!
		btn_install = new JButton("安装"); //
		btn_install.setFont(font); //
		btn_install.setPreferredSize(new Dimension(80, 25)); //
		btn_install.addActionListener(this); //

		JPanel panel_north = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
		panel_north.add(text_SQLFile); //

		panel_north.add(label_driver); //
		panel_north.add(text_driver); //

		panel_north.add(label_url); //
		panel_north.add(text_url); //

		panel_north.add(label_user); //
		panel_north.add(text_user); //

		panel_north.add(label_pwd); //
		panel_north.add(text_pwd); //

		panel_north.add(btn_install); //

		this.getContentPane().add(panel_north, BorderLayout.NORTH); //

		textArea_install = new JTextArea(); //
		this.getContentPane().add(new JScrollPane(textArea_install), BorderLayout.CENTER); //
	}

	private Connection getConn(String _driver, String _url, String _user, String _pwd) throws Exception {
		Class.forName(_driver); //加载JDBC驱动
		Connection conn = (Connection) DriverManager.getConnection(_url, _user, _pwd); //创建连接
		return conn; //
	}

	@Override
	public void actionPerformed(ActionEvent _evt) {
		Connection conn = null; //
		Statement stmt = null; //
		try {
			Document doc = readXml(this.getClass().getResourceAsStream(str_initXml));
			List<Element> list = doc.getRootElement().elements("xmlfile"); //所有文件内容
			for (int i = 0; i < list.size(); i++) {
				Element ele = list.get(i); //
				String str_xmlfileName = ele.attributeValue("initdataxml"); //初始化数据的SQL,是【bione_data.xml】 的样子!
				System.err.println("【" + str_xmlfileName + "】"); //
			}

			String str_driver = text_driver.getText(); //
			String str_url = text_url.getText(); //
			String str_user = text_user.getText(); //
			String str_pwd = text_pwd.getText(); //

			//创建连接!
			conn = getConn(str_driver, str_url, str_user, str_pwd); //
			stmt = conn.createStatement(); //

			//读取文件!
			String str_filePath = text_SQLFile.getText(); //
			if(FilepathValidateUtils.validateFilepath(str_filePath)) {
				String str_AllSQL = bsUtil.readFromInputStreamToStr(new FileInputStream(str_filePath), "UTF-8"); //
				String[] str_SQLs = bsUtil.split(str_AllSQL, ";"); //
				for (int i = 0; i < str_SQLs.length; i++) {
					String str_sql = str_SQLs[i]; //
					try {
						stmt.executeUpdate(str_sql); //
						System.out.println("[" + (i + 1) + "]成功建表!"); //
					} catch (Exception _sqlEx) {
						_sqlEx.printStackTrace(); //
						textList.add("执行SQL异常:" + str_sql + "[" + _sqlEx.getMessage() + "]\r\n"); //
					}
				}
			}
		} catch (Exception _ex) {
			_ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "发生异常:" + _ex.getMessage());
		} finally {
			try {
				conn.close(); //
			} catch (Exception _exx) {
				_exx.printStackTrace();
			}
		}
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

	//任务!每隔0.1秒跑一下
	class MyTask extends TimerTask {

		@Override
		public void run() {
			String str_text = textList.poll();
			if (str_text != null) {
				textArea_install.append(str_text);
			}
		}
	}
}
