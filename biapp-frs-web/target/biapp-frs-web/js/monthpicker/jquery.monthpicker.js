(function($, undefined) {
  $.fn.monthpicker = function() {
	  var _this = $(this);
	  var options = {
		  years: [2010,2011,2012,2013,2014,2015,2016,2017,2018,2019,2020,2021,2022,2023,2024,2025,2026,2027,2028,2029,2030,2031,2032,2033,2034,2035,2036,2037,2038,2039,2040,2041,2042,2043,2044,2045,2046,2047,2048,2049,2050],
		  topOffset: 6,
		  onMonthSelect: function(m, y) {
		  console.log('Month: ' + m + ', year: ' + y);
		  }
	  };
      var months = options.months || [{text:'1月',value:'01'}, {text:'2月',value:'02'}, {text:'3月',value:'03'}, {text:'4月',value:'04'}, {text:'5月',value:'05'}, {text:'6月',value:'06'}, {text:'7月',value:'07'}, {text:'8月',value:'08'}, {text:'9月',value:'09'}, {text:'10月',value:'10'}, {text:'11月',value:'11'}, {text:'12月',value:'12'}];

	  showData = function() {
	    var dt = new Date();
	    _this.val(dt.getFullYear() + '-' + (((dt.getMonth()+1)<10)?("0"+(dt.getMonth()+1)):(dt.getMonth()+1)) );
	  };
    
      Monthpicker = function(el) {
        this._el = $(el);
        this._init();
        this._render();
        this._renderYears();
        this._renderMonths();
        this._bind();
        showData();
      };

    Monthpicker.prototype = {
      destroy: function() {
        this._el.off('click');
        this._yearsSelect.off('click');
        this._container.off('click');
        $(document).off('click', $.proxy(this._hide, this));
        this._container.remove();
      },

      _init: function() {
        this._el.val(options.years[0] + '-' + months[0].value);
        this._el.data('monthpicker', this);
      },

      _bind: function() {
        this._el.on('click', $.proxy(this._show, this));
        $(document).on('click', $.proxy(this._hide, this));
        this._yearsSelect.on('click', function(e) { e.stopPropagation(); });
        this._container.on('click', 'button', $.proxy(this._selectMonth, this));
      },

      _show: function(e) {
        e.preventDefault();
        e.stopPropagation();
        this._container.css('display', 'inline-block');
      },

      _hide: function() {
        this._container.css('display', 'none');
      },

      _selectMonth: function(e) {
        var monthIndex = $(e.target).data('value'),
          month = months[monthIndex],
          year = this._yearsSelect.val();
        this._el.val(year + '-' + month.value);
        if (options.onMonthSelect) {
          options.onMonthSelect(monthIndex, year);
        }
      },

      _render: function() {
        var linkPosition = this._el.position(),
          cssOptions = {
            display:  'none',
            position: 'absolute',
            top:55,
            left:80,
            background: '#fff'
          };
        this._id = (new Date).valueOf();
        this._container = $('<div class="monthpicker" id="monthpicker-' + this._id +'">')
          .css(cssOptions)
          .appendTo($('body'));
      },

      _renderYears: function() {
        var markup = $.map(options.years, function(year) {
          return '<option>' + year + '</option>';
        });
        var yearsWrap     = $('<div class="years">').appendTo(this._container);
        this._yearsSelect = $('<select>').html(markup.join('')).appendTo(yearsWrap);
      },

      _renderMonths: function() {
        var markup = ['<table>', '<tr>'];
        $.each(months, function(i, month) {
          if (i > 0 && i % 4 === 0) {
            markup.push('</tr>');
            markup.push('<tr>');
          }
          markup.push('<td><button data-value="' + i + '">' + month.text +'</button></td>');
        });
        markup.push('</tr>');
        markup.push('</table>');
        this._container.append(markup.join(''));
      }
    };

    var methods = {
      destroy: function() {
        var monthpicker = this.data('monthpicker');
        if (monthpicker) monthpicker.destroy();
        return this;
      }
    }

    if ( methods[options] ) {
        return methods[ options ].apply( this, Array.prototype.slice.call( arguments, 1 ));
    } else if ( typeof options === 'object' || ! options ) {
      return this.each(function() {
        return new Monthpicker(this);
      });
    } else {
      $.error( 'Method ' +  options + ' does not exist on monthpicker' );
    }

  };
}(jQuery));
