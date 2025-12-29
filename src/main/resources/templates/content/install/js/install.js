
const instance = axios.create({
	timeout: 1000,
	headers: {'X-Custom-Header': 'foobar'}
});


function ins_form(){
	test_data();
	if($('#test_conn').val()==0){
		return false;
	}else{
		$("form").submit();
	}
}


/**
 * 测试数据
 */
function test_data(){
	 val = $('#create').attr("checked");
     if(val){
		 create=1;
     }else{
     	create=0;
     }
	axios.post('check.do?action=test_data',{
			dbHost: $('#DB_HOST').val(),
			dbPort: $('#DB_PORT').val(),
			dbName: $('#DB_NAME').val(),
			dbUser: $('#DB_USER').val(),
			dbPassword: $('#DB_PWD').val(),
			create: create
		})
		.then(function (response) {
			let res = response.data;
			if (res.status == 0) {
				$('.msg').html('<span style="color:green">数据库连接成功</span>');
				$('#test_conn').val('1');
			} else {
				$('.msg').html('<span style="color:red">' + res.msg + '</span>');
				$('#test_conn').val('0');
			}
		});


}
