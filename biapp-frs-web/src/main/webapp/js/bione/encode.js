(function() {
	// 防范XSS攻击，对HTML文本进行编码
	// 合法字符：a-z A-Z 0-9 SPACE , .
	htmlEncode = function($str, $default) {
		if($str == null || $str.length == 0) {
			$str = ($default == null ? '' : $default);
		}
		var $out = '';
		var $len = $str.length;
		// Allow: a-z A-Z 0-9 SPACE , .
		// Allow (dec): 97-122 65-90 48-57 32 44 46
		for (var $cnt = 0; $cnt < $len; $cnt++) {
			var $c = $str.charCodeAt($cnt);
			if ($c >= 97 && $c <= 122 || $c >= 65 && $c <= 90 || $c >= 48 && $c <= 57 ||
				$c == 32 || $c == 44 || $c == 46) {
				$out += $str.charAt($cnt);
			} else {
				$out += '&#' + $c + ';';
			}
		}
		return $out;
	}

	// 防范XSS攻击，对HTML属性值进行编码
	// 合法字符：a-z A-Z 0-9
	htmlAttributeEncode = function($str, $default) {
		if($str == null || $str.length == 0) {
			$str = ($default == null ? '' : $default);
		}
		var $out = '';
		var $len = $str.length;
		// Allow: a-z A-Z 0-9
		// Allow (dec): 97-122 65-90 48-57
		for (var $cnt = 0; $cnt < $len; $cnt++) {
			var $c = $str.charCodeAt($cnt);
			if ($c >= 97 && $c <= 122 || $c >= 65 && $c <= 90 || $c >= 48 && $c <= 57) {
				$out += $str.charAt($cnt);
			} else {
				$out += '&#' + $c + ';';
			}
		}
		return $out;
	}
})();