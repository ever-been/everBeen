$(document).ready(function() {
	$(".tasktogglebutton").click(function() {
		var b = $(this);
		var e = $(this).parent().next();
		if (e.is(":hidden")) {
			e.show();
			b.html("<i class='icon-minus'></i>");
		} else {
			e.hide();
			b.html("<i class='icon-plus'></i>");
		}
		return false;
	});
});
