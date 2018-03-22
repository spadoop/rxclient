# rxclient

<html>
	<head>
		<style type="text/css">
        /*垂直居中 transform: translate(-50%,-50%);*/
		ul {position: absolute;width: 200px;height: 200px;top: 50%;left: 50%;}
		/*画3个圆代表红绿灯*/
		ul >li {width: 40px;height: 40px;border-radius:50%;opacity: 0.2;display: inline-block;}
		/*执行时改变透明度*/
		ul.red >#red, ul.green >#green,ul.yellow >#yellow{opacity: 1.0;}
		/*红绿灯的三个颜色*/
		#red {background: red;}
		#yellow {background: yellow;}
		#green {background: green;}
   		</style>
	</head>
	<body>
	<ul id="traffic" class="">
	      <li id="green"></li>
	      <li id="yellow"></li>
	      <li id="red"></li>
    </ul>
	</body>
    
   		
   	<script type="text/javascript"> 
   		 
   		function green2red2yellow( ){
        	return function(){ 
	            // var arr = [].slice.apply(arguments)
	            var ms = arguments[0];
	            // var self = this;
	            return new Promise(
	            	//function(resolve,reject){
	                // arr.unshift(resolve)
	                // setTimeout.apply(self,arr);   
	                // console.log(arr[0], ms);
	                (resolveFunc) => setTimeout(resolveFunc, ms)
	            );
	        }
		}

		var green = green2red2yellow( ).bind(null, 111);
		var yellow = green2red2yellow( ).bind(null, 222);
		var red = green2red2yellow( ).bind(null, 333);
		var traffic = document.getElementById("traffic");

		(function restart(){
		    // 'use strict'                     //严格模式
		    trace(20)
		    console.log("绿灯"+new Date().getSeconds())  //绿灯执行三秒 
		    traffic.className = 'green';
		    
		    green()
		    .then(function(){
		        console.log("黄灯"+new Date().getSeconds())  //黄灯执行四秒
		        traffic.className = 'yellow';
		        return yellow();
		    })
		    .then(function(){
		        console.log("红灯"+new Date().getSeconds())   //红灯执行五秒
		        traffic.className = 'red';
		        return red();
		    }).then(function(){
		        restart()
		    })
		})(); 

	</script>
</html>
