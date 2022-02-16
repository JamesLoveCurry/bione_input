window.TableRemark={
	global:{
		//数据源
		dsId:"注意：在修改补录表页面，修改数据源时，将重建补录表和接口表!",
		//表名称
		tableName:"表名称只能含有英文字符，数字，'_'和'$'组成，只能以英文字符开头，且不能用关键字(如字段类型),insert,create等作为表名,且表名称长度不能超过26个字符。同一数据源，不能出现重复的表名。",
		//表中文名称
		tableComment:'表中文名称长度不能超过32个字符',
		//字段名称
		colName:"表字段名称只能含有英文字符，数字，'_'和'$'组成，只能以英文字符开头，且不能用关键字(如字段类型),insert等作为表名,且表名称长度不能超过30个字符,删除字段后请同时删除主键索引中该字段。",
		//表中文字段名称
		comments:'表中文字段名称长度不能超过32个字符。',
		//字段长度
		typelength:'表字段长度必须是整数，且不能为负数或者零。修改表字段长度时，如果表字段有数据，不能减少字段长度。',
		//可否为空
		nullable:'勾选可否为空按钮，则代表该字段值可以为空，不勾选，则代表一定要有值。',
		//主键唯一性说明
		keyTypeValue:'只能添加一个主键，且添加主键和唯一性约束后的字段，无需再重新添加索引，因为已经包含索引。',
		//选择字段类型
		colType:'选择指定的字段类型。',
		//表类型
		tableType:'选择某个表类型，代表该表分别用作补录表(前缀INPUT_)，数据字典表(前缀LIB_)和其他表(无前缀)，必须选择一个。'
	}
};