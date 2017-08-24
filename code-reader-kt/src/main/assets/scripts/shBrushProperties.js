;(function()
{
	// CommonJS
	typeof(require) != 'undefined' ? SyntaxHighlighter = require('shCore').SyntaxHighlighter : null;

	function Brush()
	{

		this.regexList = [
			{ regex: /^=(.*)$/g,				                            css: 'keyword' },			// numbers
			{ regex: /#.*$/gm,		                                    css: 'color1' },		// strings
			];

	};

	Brush.prototype	= new SyntaxHighlighter.Highlighter();
	Brush.aliases	= ['properties'];

	SyntaxHighlighter.brushes.Properties = Brush;

	// CommonJS
	typeof(exports) != 'undefined' ? exports.Brush = Brush : null;
})();
